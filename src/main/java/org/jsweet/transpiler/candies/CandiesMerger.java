/* 
 * JSweet - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsweet.transpiler.candies;

import static java.util.Arrays.asList;
import static org.jsweet.JSweetConfig.ANNOTATION_MIXIN;
import static org.jsweet.JSweetConfig.ANNOTATION_ROOT;
import static org.jsweet.JSweetConfig.STRING_TYPES_INTERFACE_NAME;
import static org.jsweet.JSweetConfig.UTIL_PACKAGE;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.jsweet.JSweetConfig;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.DuplicateMemberException;

/**
 * This class implements a JSweet bytecode-level candy merger. When mixin
 * classes are applied to target classes, the merger generates new classes that
 * contain all the target and mixin classes members.
 * 
 * @author Renaud Pawlak
 */
public class CandiesMerger {

	private static final Logger logger = Logger.getLogger(CandiesMerger.class);

	private static final List<String> BUILTIN_MIXINS = asList(UTIL_PACKAGE + "." + STRING_TYPES_INTERFACE_NAME);

	private File targetDir;
	private URLClassLoader candyClassLoader;
	private ClassPool classPool;

	private Map<File, ClassPool> candyClassPools;

	/**
	 * Overrides the default behavior so that it never uses any other class
	 * loaders to look up classes.
	 */
	private static class CandyClassLoader extends URLClassLoader {

		public CandyClassLoader(URL[] urls) {
			super(urls);
		}

		@Override
		protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
			Class<?> c = findLoadedClass(name);
			if (c == null) {
				try {
					c = findClass(name);
					resolveClass(c);
				} catch (ClassNotFoundException e) {
					return super.loadClass(name, resolve);
				}
			}
			return c;
		}

	}

	/**
	 * Creates a new candies merger that will merge the candies found in the
	 * classpath and output the result to the given target directory.
	 */
	public CandiesMerger(File targetDir, List<File> candyClassPathEntries) {
		logger.debug("new candies merger");
		logger.debug("targetDir: " + targetDir.getAbsolutePath());
		logger.debug("candies classpath entries: " + candyClassPathEntries);
		this.targetDir = targetDir;
		this.candyClassLoader = new CandyClassLoader(candyClassPathEntries.stream().map(entry -> {
			try {
				return entry.toURI().toURL();
			} catch (Exception e) {
				logger.error("wrong class path entry " + entry, e);
				return null;
			}
		}).toArray(size -> new URL[size]));
		logger.debug("classloader URLs: " + Arrays.asList(candyClassLoader.getURLs()));

		this.candyClassPools = new HashMap<>();
		this.classPool = new ClassPool(ClassPool.getDefault());
		for (File entry : candyClassPathEntries) {
			try {
				this.classPool.appendClassPath(entry.getAbsolutePath());

				ClassPool candyClassPool = new ClassPool();
				candyClassPool.appendClassPath(entry.getAbsolutePath());
				candyClassPools.put(entry, candyClassPool);
			} catch (Exception e) {
				logger.error("wrong class path entry " + entry, e);
			}
		}

		logger.debug("candies class pools: " + candyClassPools);
	}

	private Annotation getAnnotation(AnnotatedElement element, String annotationName) {
		try {
			return element.getAnnotation(getAnnotationClass(annotationName));
		} catch (Exception e) {
			logger.error("error retreiving annotation " + annotationName, e);
			return null;
		}
	}

	private Class<? extends Annotation> getAnnotationClass(String annotationName) {
		try {
			@SuppressWarnings("unchecked")
			Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) candyClassLoader.loadClass(annotationName);
			return annotationClass;
		} catch (Exception e) {
			logger.error("error retreiving annotation class " + annotationName, e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T getProperty(Annotation annotation, String property) {
		try {
			return (T) annotation.annotationType().getDeclaredMethod(property).invoke(annotation);
		} catch (Exception e) {
			logger.error("error retreiving annotation property " + property, e);
			return null;
		}
	}

	public Map<Class<?>, List<Class<?>>> merge() {
		File defDir = new File(targetDir, JSweetConfig.LIBS_PACKAGE);
		List<Class<?>> mixinsClasses = new ArrayList<Class<?>>();
		findMixinClasses(defDir, mixinsClasses);

		logger.debug("mixin classes: " + mixinsClasses);
		Map<Class<?>, List<Class<?>>> toBeMerged = new HashMap<>();
		// adds mixins from @Root annotation infos
		for (Class<?> mixin : mixinsClasses) {
			Class<?> target = getProperty(getAnnotation(mixin, ANNOTATION_MIXIN), "target");
			List<Class<?>> mixins = toBeMerged.get(target);
			if (mixins == null) {
				mixins = new ArrayList<>();
				toBeMerged.put(target, mixins);
			}
			mixins.add(mixin);
		}
		logger.debug("mixins to be merged: " + toBeMerged);
		for (Entry<Class<?>, List<Class<?>>> e : toBeMerged.entrySet()) {
			mergeMixins(e.getValue(), e.getKey());
		}

		for (String builtinMixinClassName : BUILTIN_MIXINS) {
			try {
				List<CtClass> mixinClasses = new LinkedList<>();
				for (ClassPool classPool : candyClassPools.values()) {
					try {
						CtClass mixinCtClass = classPool.get(builtinMixinClassName);
						if (mixinCtClass != null) {
							mixinClasses.add(mixinCtClass);
						}
					} catch (NotFoundException e) {
						// mixin class not found, not a problem
					}
				}
				logger.info(mixinClasses.size() + " classes found for " + builtinMixinClassName);
				if (mixinClasses.size() > 1) {
					CtClass ctTarget = mixinClasses.get(0);
					for (CtClass ctMixin : mixinClasses.subList(1, mixinClasses.size())) {
						mergeMixin(ctTarget, ctMixin);
					}
					ctTarget.debugWriteFile(targetDir.getPath());
				}
			} catch (Exception e) {
				logger.warn("error merging mixin " + builtinMixinClassName, e);
			}
		}

		return toBeMerged;
	}

	private void findMixinClasses(File dir, List<Class<?>> mixinClasses) {
		String packageName = dir.getPath().substring(targetDir.getPath().length() + 1).replace(File.separatorChar, '.');
		Class<?> packageInfo = null;
		try {
			// trick to force the package to load (we are only interested in the
			// packages with the Root annotation, so the package-info class must
			// exist
			packageInfo = candyClassLoader.loadClass(packageName + ".package-info");
		} catch (ClassNotFoundException e) {
			// swallow
		}
		if (packageInfo != null) {
			for (Map.Entry<File, ClassPool> candyClassPoolEntry : candyClassPools.entrySet()) {
				try {
					ClassPool candyClassPool = candyClassPoolEntry.getValue();
					CtClass packageCtClass = candyClassPool.get(packageInfo.getName());

					if (packageCtClass.hasAnnotation(getAnnotationClass(ANNOTATION_ROOT))) {

						Class<?> packageClass = candyClassPool.toClass(packageCtClass,
								new URLClassLoader(new URL[] { candyClassPoolEntry.getKey().toURI().toURL() }, candyClassLoader), null);
						Annotation rootAnno = packageClass.getAnnotation(getAnnotationClass(ANNOTATION_ROOT));
						List<Class<?>> candyMixins = asList(getProperty(rootAnno, "mixins"));
						mixinClasses.addAll(candyMixins);
						logger.debug(candyClassPoolEntry.getKey() + ":" + packageInfo.getName() + " mixins: " + candyMixins);
					}
				} catch (NotFoundException e) {
					// not found in candy, not a problem
				} catch (Exception e) {
					logger.error("cannot read mixins info for " + packageInfo + " in " + candyClassPoolEntry.getKey(), e);
				}
			}
		}
		File[] children = dir.listFiles();
		if (children != null) {
			for (File f : children) {
				if (f.isDirectory()) {
					findMixinClasses(f, mixinClasses);
				}
			}
		}
	}

	private void mergeMixins(List<Class<?>> mixins, Class<?> target) {
		try {
			logger.debug("merging: " + mixins + " -> " + target);
			CtClass ctTarget = classPool.get(target.getName());
			for (Class<?> mixin : mixins) {
				CtClass ctMixin = classPool.get(mixin.getName());
				mergeMixin(ctTarget, ctMixin);
			}
			ctTarget.debugWriteFile(targetDir.getPath());
		} catch (Exception e) {
			logger.warn("error merging mixin", e);
		}
	}

	private void mergeMixin(CtClass ctTarget, CtClass ctMixin) throws NotFoundException {
		mergeMixin(ctTarget, ctMixin, true);
	}

	private boolean hasField(CtClass clazz, String name) {
		try {
			return clazz.getDeclaredField(name) != null;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean hasMethod(CtClass clazz, CtMethod candidateMethod) {
		try {
			CtMethod existing = clazz.getDeclaredMethod(candidateMethod.getName());
			if (existing == null) {
				return false;
			}

			CtClass[] existingParamTypes = existing.getParameterTypes();
			CtClass[] candidateParamTypes = candidateMethod.getParameterTypes();
			if (existingParamTypes.length != candidateParamTypes.length) {
				return false;
			}

			for (int i = 0; i < existingParamTypes.length; i++) {
				if (!existingParamTypes[i].getName().equals(candidateParamTypes[i].getName())) {
					return false;
				}
			}

			return true;

		} catch (Exception e) {
			return false;
		}
	}

	private void mergeMixin(CtClass ctTarget, CtClass ctMixin, boolean verbose) throws NotFoundException {
		if (verbose)
			logger.debug("merging: " + ctMixin.getName() + " -> " + ctTarget.getName());
		int memberCount = 0;
		int ignoredDuplicates = 0;
		int innerClassCount = 0;
		for (CtClass ctInnerClass : ctMixin.getDeclaredClasses()) {
			try {
				String innerClassSimpleName = ctInnerClass.getSimpleName();
				if (innerClassSimpleName.lastIndexOf('$') != -1) {
					innerClassSimpleName = innerClassSimpleName.substring(innerClassSimpleName.lastIndexOf('$') + 1);
				}

				if (!ctTarget.getSimpleName().equals(JSweetConfig.STRING_TYPES_INTERFACE_NAME) || !hasField(ctTarget, innerClassSimpleName)) {
					// classic inner class merge of string types field does not
					// exist

					String innerClassName = ctTarget.getName() + "$" + innerClassSimpleName;

					boolean newInnerClass = false;
					CtClass ctTargetInner;
					try {
						ctTargetInner = classPool.get(innerClassName);
					} catch (NotFoundException e) {
						newInnerClass = true;
						logger.debug("adding " + innerClassName + " to pool");
						ctTargetInner = ctTarget.makeNestedClass(innerClassSimpleName, true);
						// RP: This line did not work for me.
						// ctTargetInner = classPool.makeClass(innerClassName);
					}

					mergeMixin(ctTargetInner, ctInnerClass, false);

					if (newInnerClass) {
						ctTargetInner.debugWriteFile(targetDir.getAbsolutePath());
						logger.debug("inner class file written to " + targetDir.getAbsolutePath());
					}

					memberCount++;
					innerClassCount++;
				}
			} catch (Exception e) {
				logger.warn("error merging inner class: " + ctInnerClass, e);
			}
		}
		if (innerClassCount > 0) {
			logger.debug("merged " + innerClassCount + " inner classes");
		}
		for (CtMethod ctMethod : ctMixin.getDeclaredMethods()) {
			try {
				if (!hasMethod(ctTarget, ctMethod)) {
					CtMethod copy = CtNewMethod.copy(ctMethod, ctTarget, null);
					try {
						copy.setGenericSignature(ctMethod.getGenericSignature());
					} catch (Exception e) {
						// swallow
					}
					ctTarget.addMethod(copy);
					memberCount++;
				} else {
					// ignore duplicate members
					ignoredDuplicates++;
				}
			} catch (DuplicateMemberException e) {
				// ignore duplicate members
				ignoredDuplicates++;
			} catch (Exception e) {
				logger.warn("error merging method", e);
			}
		}
		for (CtField ctField : ctMixin.getDeclaredFields()) {
			try {
				if (!hasField(ctTarget, ctField.getName())) {
					ctTarget.addField(new CtField(ctField, ctTarget));
					memberCount++;
				} else {
					// ignore duplicate members
					ignoredDuplicates++;
				}
			} catch (DuplicateMemberException e) {
				// ignore duplicate members
				ignoredDuplicates++;
			} catch (Exception e) {
				logger.warn("error merging field", e);
			}
		}
		for (CtConstructor ctConstructor : ctMixin.getDeclaredConstructors()) {
			try {
				ctTarget.addConstructor(CtNewConstructor.copy(ctConstructor, ctTarget, null));
				memberCount++;
			} catch (DuplicateMemberException e) {
				// ignore duplicate members
				ignoredDuplicates++;
			} catch (Exception e) {
				logger.warn("error merging constructor", e);
			}
		}
		if (verbose)
			logger.debug("merged " + memberCount + " member(s) and ignored " + ignoredDuplicates + " duplicates");
	}
}
