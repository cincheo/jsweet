/* 
 * TypeScript definitions to Java translator - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jsweet.input.typescriptdef.visitor;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.Token;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeReference;

/**
 * @author Renaud Pawlak
 */
public class TypeMerger extends Scanner {

	private int mergedTypeCount = 0;

	public TypeMerger(Context context) {
		super(context);
	}

	private boolean isCoreMergeCandidate(String moduleName) {
		if (moduleName.endsWith("." + JSweetDefTranslatorConfig.GLOBALS_PACKAGE_NAME)
				|| moduleName.equals(JSweetDefTranslatorConfig.LANG_PACKAGE)
				|| moduleName.equals(JSweetDefTranslatorConfig.DOM_PACKAGE)) {
			return true;
		} else if (context.libModules.contains(moduleName)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void visitTypeDeclaration(TypeDeclaration typeDeclaration) {
		if (typeDeclaration.isExternal()) {
			return;
		}
		String modName = getCurrentModuleName();
		String libModule = context.getLibModule(modName);
		if (typeDeclaration.getName() != null) {
			if (isCoreMergeCandidate(modName)) {
				TypeDeclaration targetType = null;
				targetType = context
						.getTypeDeclaration(JSweetDefTranslatorConfig.LANG_PACKAGE + "." + typeDeclaration.getName());
				if (targetType == null) {
					targetType = context.getTypeDeclaration(
							JSweetDefTranslatorConfig.DOM_PACKAGE + "." + typeDeclaration.getName());
				}
				boolean coreTarget = targetType != null;
				if (targetType == null) {
					// this is for cross lib merge (will not be applied on a
					// per-lib
					// generation strategy)
					if (libModule != null) {
						String libRelativePath = context.getLibRelativePath(modName);

						List<String> dependencies = context.dependencyGraph.getDestinationElements(libModule);
						if (dependencies == null) {
							context.reportError("cannot find dependency for module " + libModule + " (type "
									+ typeDeclaration + "): check dependecy graph initialization", (Token) null);
						} else {
							for (String targetLibModule : dependencies) {
								if (targetLibModule == null) {
									continue;
								}
								if (modName.equals(libRelativePath)) {
									targetType = context
											.getTypeDeclaration(targetLibModule + "." + typeDeclaration.getName());
								} else {
									targetType = context.getTypeDeclaration(
											targetLibModule + "." + libRelativePath + "." + typeDeclaration.getName());
								}
								if (targetType != null) {
									break;
								}
							}
						}
					}
				}

				if (targetType == typeDeclaration) {
					return;
				}

				if (targetType != null) {
					if (libModule == null && coreTarget || libModule != null
							&& libModule.equals(context.getLibModule(context.getTypeName(targetType)))) {
						mergeTypes(typeDeclaration, targetType);
					} else {
						logger.debug("add @Mixin annotation for cross-lib type: " + modName + "."
								+ typeDeclaration.getName() + "@" + typeDeclaration.getLocation() + " ==> "
								+ context.getTypeName(targetType) + "@" + targetType.getLocation());
						typeDeclaration.addStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_MIXIN + "(target="
								+ context.getTypeName(targetType) + ".class)");
						typeDeclaration.setSuperTypes(
								new TypeReference[] { new TypeReference(null, context.getTypeName(targetType), null) });
						context.resiterMixin(libModule, typeDeclaration);
						// TODO: redirect references
					}
				}
			} else {
				if (Collections.frequency(context.getTypeNames().values(), context.getTypeName(typeDeclaration)) > 1) {
					if (!"interface".equals(typeDeclaration.getOriginalKind())) {
						context.reportWarning("duplicate type is not an interface: " + typeDeclaration + " at "
								+ typeDeclaration.getLocation());
						return;
					}
					for (Entry<TypeDeclaration, String> e : new HashSet<>(context.getTypeNames().entrySet())) {
						if (e.getKey() == typeDeclaration
								|| !e.getValue().equals(context.getTypeName(typeDeclaration))) {
							continue;
						}
						if (!"interface".equals(e.getKey().getOriginalKind())) {
							context.reportWarning("duplicate type is not an interface: " + e.getKey() + " at "
									+ e.getKey().getLocation());
							return;
						}
						mergeTypes(e.getKey(), typeDeclaration);
					}
				}
			}
		}

	}

	private void mergeTypes(TypeDeclaration source, TypeDeclaration target) {
		if (source == target) {
			return;
		}
		logger.debug("merging types: " + context.getTypeName(source) + "@" + source.getLocation() + " ==> "
				+ context.getTypeName(target) + "@" + target.getLocation());
		mergedTypeCount++;
		target.addMembers(source.getMembers());
		if (source.getSuperTypes() != null) {
			if (target.getSuperTypes() != null) {
				target.setSuperTypes(ArrayUtils.addAll(target.getSuperTypes(), source.getSuperTypes()));
			} else {
				target.setSuperTypes(source.getSuperTypes());
			}
		}
		source.setHidden(true);
		context.unregisterType(source);

	}

	@Override
	public void onScanEnded() {
		if (mergedTypeCount > 0) {
			logger.debug(mergedTypeCount + " type(s) merged.");
		}
	}

}
