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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Declaration;
import org.jsweet.input.typescriptdef.ast.DeclarationHelper;
import org.jsweet.input.typescriptdef.ast.FullFunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.ModuleDeclaration;
import org.jsweet.input.typescriptdef.ast.ParameterDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeParameterDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;
import org.jsweet.input.typescriptdef.util.NameUtils;
import org.jsweet.input.typescriptdef.util.Util;

/**
 * This scanner hides duplicate method (real duplicates but also the ones with
 * the same type erasure).
 * 
 * @author Renaud Pawlak
 */
public class DuplicateMethodsCleaner extends Scanner {

	private int hiddenMethodCount = 0;

	private boolean dump = false;

	private final static String NO_OVERRIDE = "";

	public enum Strategy {
		USER_FRIENDLY, FULL;
	}

	private final static int MAX_NAME_LENGTH = 40;

	public DuplicateMethodsCleaner(Context context) {
		super(context);
	}
	
	@Override
	public void visitModuleDeclaration(ModuleDeclaration moduleDeclaration) {
		if (context.isInDependency(moduleDeclaration)) {
			return;
		}
		
		super.visitModuleDeclaration(moduleDeclaration);
	}

	private void hideDuplicatesInDeclarations(TypeDeclaration originalDeclaringType,
			FunctionDeclaration functionDeclaration, TypeDeclaration declaringType) {
		Declaration[] declarations = declaringType.getMembers();
		if (declarations == null) {
			return;
		}
		for (Declaration d : declarations) {
			if (d instanceof FunctionDeclaration) {
				FunctionDeclaration f = (FunctionDeclaration) d;
				if (f != functionDeclaration && !f.isHidden()
						&& (f.getName().equals(functionDeclaration.getName())
								|| (f.isConstructor() && functionDeclaration.isConstructor()))
						&& f.getParameters().length == functionDeclaration.getParameters().length) {
					boolean duplicate = true;
					boolean checkReturnType = true;
					// static methods can never be overloaded
					// if (!(!functionDeclaration.hasModifier("static") &&
					// f.hasModifier("static"))) {

					for (int i = 0; i < f.getParameters().length; i++) {
						ParameterDeclaration d1 = functionDeclaration.getParameters()[i];
						ParameterDeclaration d2 = f.getParameters()[i];
						String erasedName1 = context.getTypeNameErased(d1.getType());
						String erasedName2 = context.getTypeNameErased(d2.getType());

						try {
							if (!erasedName1.equals(erasedName2)) {
								duplicate = false;
								break;
							} else {
								checkReturnType = context.getFullTypeNameNoErasure(d1.getType())
										.equals(context.getFullTypeNameNoErasure(d2.getType()));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					boolean incompatibleReturnType = false;
					if (duplicate && checkReturnType && originalDeclaringType != declaringType
							&& !f.hasModifier("static")) {
						// String name1 =
						// functionDeclaration.getType().getName();
						// String name2 = f.getType().getName();
						// if (name1 != null && !name1.equals(name2)) {
						// duplicate = false;
						// }
						try {
							if (!Object.class.getName().equals(f.getType().getName())
									&& !"Object".equals(f.getType().getName())
									&& !functionDeclaration.getType().equals(f.getType())
									&& !functionDeclaration.getType().isSubtypeOf(f.getType())) {
								// duplicate = false;
								incompatibleReturnType = true;
								logger.debug("incompatible return types: " + functionDeclaration + " - "
										+ functionDeclaration.getType() + " / " + f);
							}
						} catch (Exception e) {
							context.reportError("unattributed type ref for " + f + " at " + f.getLocation()
									+ ", declaring type: " + declaringType, declaringType.getToken(), e);
						}
					}
					if (duplicate) {
						boolean isErased = false;
						for (int i = 0; i < functionDeclaration.getParameters().length; i++) {
							ParameterDeclaration d1 = functionDeclaration.getParameters()[i];
							ParameterDeclaration d2 = f.getParameters()[i];
							if (!context.getFullTypeNameNoErasure(d1.getType())
									.equals(context.getFullTypeNameNoErasure(d2.getType()))) {
								isErased = true;
							}
						}
						boolean staticDuplicate = (f.hasModifier("static") != functionDeclaration
								.hasModifier("static"));
						if (!incompatibleReturnType && originalDeclaringType != declaringType && !staticDuplicate) {
							context.addOverride(
									new FullFunctionDeclaration(null, originalDeclaringType, functionDeclaration),
									new FullFunctionDeclaration(null, declaringType, f));
						} else if (isErased && !staticDuplicate) {
							context.addDuplicate(
									new FullFunctionDeclaration(null, originalDeclaringType, functionDeclaration),
									new FullFunctionDeclaration(null, declaringType, f));
						} else {
							if (context.verbose) {
								logger.debug("WARNING: removing duplicate " + f
										+ (f.getToken() == null ? "" : " at " + f.getToken().getLocation()) + ", type "
										+ declaringType.getName());
							}
							hiddenMethodCount++;
							functionDeclaration.setHidden(true);
						}
					}
				}
			}
		}
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
		TypeDeclaration declaringType = getParent(TypeDeclaration.class);
		TypeDeclaration orgType = declaringType;
		while (declaringType != null) {
			hideDuplicatesInDeclarations(orgType, functionDeclaration, declaringType);
			if (!functionDeclaration.isConstructor()) {
				if (declaringType.getSuperTypes() != null && declaringType.getSuperTypes().length > 0) {
					declaringType = (TypeDeclaration) lookupType(declaringType.getSuperTypes()[0], null);
				} else if ("class".equals(declaringType.getKind())
						&& !JSweetDefTranslatorConfig.getObjectClassName().equals(context.getTypeName(declaringType))) {
					declaringType = context.getTypeDeclaration(JSweetDefTranslatorConfig.getObjectClassName());
				} else {
					declaringType = null;
				}
			} else {
				declaringType = null;
			}
		}
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
	}

	@Override
	public void onScanEnded() {
		handleDuplicates();
		if (hiddenMethodCount > 0) {
			logger.debug(hiddenMethodCount + " method(s) hidden.");
		}
	}

	private void dumpDuplicates(List<Set<FullFunctionDeclaration>> duplicatesList) {
		if (dump) {
			for (Set<FullFunctionDeclaration> s : duplicatesList) {
				TypeDeclaration type = s.iterator().next().declaringType;
				logger.info(" -- " + context.getTypeName(type));
				for (FullFunctionDeclaration d : s) {
					logger.info(" * " + d);
					if (context.overrides.get(d) != null) {
						logger.info("    -> overrides: " + context.overrides.get(d));
					}
					if (context.overridens.get(d) != null) {
						logger.info("    -> overridens: " + context.overridens.get(d));
					}
				}
			}
		}
	}

	private void handleDuplicates() {
		dumpDuplicates(context.duplicates);
		List<Set<FullFunctionDeclaration>> newDuplicatesList = new ArrayList<Set<FullFunctionDeclaration>>();
		for (Set<FullFunctionDeclaration> duplicates : context.duplicates) {
			Set<FullFunctionDeclaration> newDuplicates = new HashSet<FullFunctionDeclaration>(duplicates);
			for (FullFunctionDeclaration f : duplicates) {
				if (context.overrides.containsKey(f)) {
					for (Set<FullFunctionDeclaration> duplicates2 : context.duplicates) {
						if (duplicates2.contains(context.overrides.get(f))) {
							newDuplicates.addAll(duplicates2);
							newDuplicatesList.remove(duplicates2);
						}
					}
				}
			}
			newDuplicatesList.add(newDuplicates);
		}
		dumpDuplicates(newDuplicatesList);

		for (Set<FullFunctionDeclaration> duplicates : newDuplicatesList) {
			Map<FullFunctionDeclaration, List<String>> nameMatrix = null;
			// logger.info("disambiguation for " + duplicates);
			nameMatrix = calculateNames(duplicates, Strategy.USER_FRIENDLY);
			dumpNameMatrix(nameMatrix);
			if (hasDuplicate(nameMatrix)) {
				// logger.info("found duplicate!");
				nameMatrix = calculateNames(duplicates, Strategy.FULL);
				dumpNameMatrix(nameMatrix);
			}
			applyDisambiguation(duplicates, nameMatrix);
		}
	}

	private void dumpNameMatrix(Map<FullFunctionDeclaration, List<String>> nameMatrix) {
		if (dump) {
			logger.info("Dumping name matrix:");
			for (Entry<FullFunctionDeclaration, List<String>> e : nameMatrix.entrySet()) {
				logger.info("# " + e.getKey() + " ---> " + e.getValue());
			}
		}
	}

	private boolean hasDuplicate(Map<FullFunctionDeclaration, List<String>> nameMatrix) {
		Map<FullFunctionDeclaration, List<String>> m = new HashMap<FullFunctionDeclaration, List<String>>(nameMatrix);
		for (FullFunctionDeclaration f : nameMatrix.keySet()) {
			if (context.overridens.get(f) != null && m.containsKey(context.overridens.get(f))) {
				m.remove(f);
			}
		}
		// logger.info("TESTING DUPLICATES ON:");
		// dumpNameMatrix(m);
		List<String> signatures = new ArrayList<String>();
		for (List<String> l : m.values()) {
			String s = l.toString();
			if (signatures.contains(s)) {
				return true;
			}
			signatures.add(s);
		}
		return false;
	}

	private Map<FullFunctionDeclaration, List<String>> calculateNames(Set<FullFunctionDeclaration> duplicates,
			Strategy strategy) {
		Map<FullFunctionDeclaration, List<String>> nameMatrix = new HashMap<FullFunctionDeclaration, List<String>>();
		List<FullFunctionDeclaration> l = new ArrayList<FullFunctionDeclaration>(duplicates);
		TypeDeclaration highestTypeDeclaration = getHighestSuperType(duplicates);

		for (int paramIndex = 0; paramIndex < l.get(0).function.getParameters().length; paramIndex++) {
			final int i = paramIndex;
			l.sort(new Comparator<FullFunctionDeclaration>() {
				@Override
				public int compare(FullFunctionDeclaration f1, FullFunctionDeclaration f2) {
					int diff = context.getShortTypeNameNoErasure(f1.function.getParameters()[i].getType()).length()
							- context.getShortTypeNameNoErasure(f2.function.getParameters()[i].getType()).length();
					if (diff == 0) {
						return context.getShortTypeNameNoErasure(f1.function.getParameters()[i].getType())
								.compareTo(context.getShortTypeNameNoErasure(f2.function.getParameters()[i].getType()));
					} else {
						return diff;
					}
				}
			});

			List<String> names;
			boolean functionalDisambiguation = isFunctionalTypeReference(
					l.get(0).function.getParameters()[i].getType());
			if (functionalDisambiguation) {
				if (!isFunctionalTypeReference(l.get(0).function.getParameters()[i].getType())
						|| context.getShortTypeNameNoErasure(l.get(0).function.getParameters()[i].getType())
								.equals(context.getShortTypeNameNoErasure(
										l.get(l.size() - 1).function.getParameters()[i].getType()))) {
					// no erasure conflict comes form parameter i (by convention
					// we set an empty name)
					names = new ArrayList<String>(Collections.nCopies(l.size(), NO_OVERRIDE));
				} else {
					names = calculateNames(highestTypeDeclaration, strategy, functionalDisambiguation, l,
							l.get(0).function, i);
				}
			} else {
				if (context.getShortTypeNameNoErasure(l.get(0).function.getParameters()[i].getType()).equals(
						context.getShortTypeNameNoErasure(l.get(l.size() - 1).function.getParameters()[i].getType()))) {
					// no erasure conflict comes form parameter i (by convention
					// we set an empty name)
					names = new ArrayList<String>(Collections.nCopies(l.size(), NO_OVERRIDE));
				} else {
					names = calculateNames(highestTypeDeclaration, strategy, functionalDisambiguation, l,
							l.get(0).function, i);
				}
			}
			for (int j = 0; j < l.size(); j++) {
				List<String> paramNames = nameMatrix.get(l.get(j));
				if (paramNames == null) {
					paramNames = new ArrayList<String>();
					nameMatrix.put(l.get(j), paramNames);
				}
				paramNames.add(names.get(j));
			}
		}
		return nameMatrix;
	}

	private void applyDisambiguation(Set<FullFunctionDeclaration> duplicates,
			Map<FullFunctionDeclaration, List<String>> nameMatrix) {
		TypeDeclaration highestTypeDeclaration = getHighestSuperType(duplicates);
		List<FullFunctionDeclaration> l = new ArrayList<FullFunctionDeclaration>(duplicates);
		for (int paramIndex = 0; paramIndex < l.get(0).function.getParameters().length; paramIndex++) {
			final int i = paramIndex;
			for (FullFunctionDeclaration f : l) {
				String newTypeName = nameMatrix.get(f).get(i);
				if (newTypeName == NO_OVERRIDE) {
					continue;
				}

				ParameterDeclaration parameter = f.function.getParameters()[i];
				TypeDeclaration newType = null;
				boolean alreadyCreated = false;

				if (newType == null && (newType = highestTypeDeclaration.findType(newTypeName)) != null) {
					alreadyCreated = true;
				}

				boolean functionalDisambiguation = isFunctionalTypeReference(
						l.get(0).function.getParameters()[i].getType());
				if (functionalDisambiguation) {
					if (f.function.isConstructor()) {
						if (!alreadyCreated) {
							boolean hasResult = ((TypeDeclaration) parameter.getType().getDeclaration()).getName()
									.contains("Function");
							newType = DeclarationHelper.createFunctionalType(newTypeName,
									parameter.getType().getTypeArguments().length - (hasResult ? 1 : 0), hasResult,
									true);
							newType.addStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_ERASED);
							highestTypeDeclaration.addMember(newType);
							context.registerType(context.getTypeName(highestTypeDeclaration) + "." + newType.getName(),
									newType);
						}
						parameter.setType(new TypeReference(null, newType,
								DeclarationHelper.copyReferences(parameter.getType().getTypeArguments())));
					} else {
						if (!f.function.hasStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_NAME)) {
							f.function.addStringAnnotation(
									JSweetDefTranslatorConfig.ANNOTATION_NAME + "(\"" + f.function.getName() + "\")");
							f.function.setName(f.function.getName() + newTypeName);
						} else {
							if (!StringUtils.isBlank(newTypeName)) {
								f.function.setName(StringUtils.uncapitalize(f.function.getName()) + newTypeName);
							}
						}
					}
				} else {
					TypeParameterDeclaration[] typeParameters = Util.findTypeParameters(this, parameter);
					if (typeParameters.length == 0) {
						typeParameters = null;
					}
					if (!alreadyCreated) {
						FunctionDeclaration newConstructor = new FunctionDeclaration(null, "constructor", null,
								new ParameterDeclaration[] { parameter.copy() }, null);
						newType = new TypeDeclaration(null, "class", newTypeName,
								DeclarationHelper.copy(typeParameters), null, new Declaration[] { newConstructor });
						newType.setDocumentation(
								"/** This class was automatically generated for disambiguating erased method signatures. */");
						if (newType.getTypeParameters() != null) {
							for (TypeParameterDeclaration t : newType.getTypeParameters()) {
								t.setUpperBound(null);
							}
						}
						newType.addModifier("static");
						newType.addStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_ERASED);
						highestTypeDeclaration.addMember(newType);
						context.registerType(context.getTypeName(highestTypeDeclaration) + "." + newType.getName(),
								newType);
					}
					parameter.setType(new TypeReference(null, newType, DeclarationHelper.toReferences(typeParameters)));
				}

			}
		}
	}

	private String resolveNameClash(boolean functionalDisambiguation, TypeDeclaration highestTypeDeclaration,
			TypeReference parameterType, String newTypeName) {
		TypeDeclaration resolved = highestTypeDeclaration.findType(newTypeName);
		if (resolved == null) {
			return newTypeName;
		} else {
			if (resolved.getTypeParameters() == null && parameterType.getTypeArguments() == null) {
				return newTypeName;
			}
			if (!functionalDisambiguation) {
				if (resolved.getTypeParameters() != null && parameterType.getTypeArguments() != null
						&& resolved.getTypeParameters().length == parameterType.getTypeArguments().length) {
					return newTypeName;
				}
				FunctionDeclaration constructor = resolved.findFirstConstructor();
				if (constructor != null && constructor.getParameters() != null
						&& constructor.getParameters().length == 1
						&& constructor.getParameters()[0].getType().equals(parameterType)) {
					return newTypeName;
				}
			} else {
				FunctionDeclaration apply = resolved
						.findFirstFunction(JSweetDefTranslatorConfig.ANONYMOUS_FUNCTION_NAME);
				if (apply != null && resolved.getTypeParameters() != null && parameterType.getTypeArguments() != null
						&& resolved.getTypeParameters().length == parameterType.getTypeArguments().length) {
					if (parameterType.getName().endsWith("Consumer") && "void".equals(apply.getType().getName())) {
						return newTypeName;
					}
					if (parameterType.getName().endsWith("Provider") && apply.getParameters().length == 0) {
						return newTypeName;
					}
					if (parameterType.getName().endsWith("Function") && apply.getParameters().length > 0
							&& !"void".equals(apply.getType().getName())) {
						return newTypeName;
					}
				}
			}
			Map.Entry<String, Integer> intSuffix = Util.splitIntSuffix(newTypeName);
			if (intSuffix == null) {
				return resolveNameClash(functionalDisambiguation, highestTypeDeclaration, parameterType,
						newTypeName + 2);
			} else {
				return resolveNameClash(functionalDisambiguation, highestTypeDeclaration, parameterType,
						intSuffix.getKey() + (intSuffix.getValue() + 1));
			}

		}
	}

	private List<String> calculateNames(TypeDeclaration highestTypeDeclaration, Strategy strategy,
			boolean functionalDisambiguation, List<FullFunctionDeclaration> l, FunctionDeclaration reference,
			int paramIndex) {
		List<String> names = new ArrayList<String>();
		switch (strategy) {
		case USER_FRIENDLY:
			if (functionalDisambiguation) {
				// apply a diff strategy (more user friendly in some cases)
				for (FullFunctionDeclaration f : l) {
					String newInterfaceName = StringUtils.capitalize(reference.getParameters()[paramIndex].getName())
							+ NameUtils.getDiff(
									context.getShortTypeNameNoErasure(reference.getParameters()[paramIndex].getType()),
									context.getShortTypeNameNoErasure(
											f.function.getParameters()[paramIndex].getType()));
					if (newInterfaceName.length() > MAX_NAME_LENGTH) {
						newInterfaceName = StringUtils.capitalize(reference.getParameters()[paramIndex].getName())
								+ names.size();
					}
					TypeReference parameterType = reference.getParameters()[paramIndex].getType();
					if (parameterType.getName().contains("Consumer")) {
						switch (parameterType.getTypeArguments().length) {
						case 1:
							newInterfaceName += "Consumer";
							break;
						case 2:
							newInterfaceName += "BiConsumer";
							break;
						case 3:
							newInterfaceName += "TriConsumer";
							break;
						default:
							newInterfaceName += "Consumer";
							newInterfaceName += parameterType.getTypeArguments().length;
							break;
						}
					}
					if (parameterType.getName().contains("Function")) {
						switch (parameterType.getTypeArguments().length) {
						case 2:
							newInterfaceName += "Function";
							break;
						case 3:
							newInterfaceName += "BiFunction";
							break;
						case 4:
							newInterfaceName += "TriFunction";
							break;
						default:
							newInterfaceName += "Function";
							newInterfaceName += parameterType.getTypeArguments().length - 1;
							break;
						}
					}
					if (parameterType.getName().contains("Supplier")) {
						newInterfaceName += "Supplier";
					}
					// this naming convention seems to avoid all name clashes
					// newInterfaceName =
					// resolveNameClash(functionalDisambiguation,
					// highestTypeDeclaration,
					// f.function.getParameters()[paramIndex].getType(),
					// newInterfaceName);
					names.add(newInterfaceName);
				}
			} else {
				for (FullFunctionDeclaration f : l) {
					// TODO: the parameter happens to clash sometime (so full
					// strategy from scratch until we find a better way)
					// SEE: underscore: _._Chain.map(...) methods
					// String newInterfaceName =
					// StringUtils.capitalize(reference.getParameters()[paramIndex].getName());

					String newInterfaceName = null;
					if (DeclarationHelper.getTypeOrComponentType(
							reference.getParameters()[paramIndex].getType()) instanceof TypeParameterDeclaration) {
						newInterfaceName = StringUtils.capitalize(reference.getParameters()[paramIndex].getName());
					} else {
						newInterfaceName = StringUtils.capitalize(
								context.getShortTypeNameErased(reference.getParameters()[paramIndex].getType())
										.replace("[]", "s"));
					}
					if (f.function.getParameters()[paramIndex].getType().getTypeArguments() != null) {
						for (TypeReference t : f.function.getParameters()[paramIndex].getType().getTypeArguments()) {
							String[] tokens = context.getShortTypeNameNoErasure(t).split("[<>,]");
							for (String token : tokens) {
								newInterfaceName += StringUtils.capitalize(token).replace("[]", "s");
							}
						}
					} else {
						String[] tokens = context
								.getShortTypeNameNoErasure(f.function.getParameters()[paramIndex].getType())
								.split("[<>,]");
						for (String token : tokens) {
							newInterfaceName += StringUtils.capitalize(token).replace("[]", "s");
						}
					}
					if (newInterfaceName.length() > MAX_NAME_LENGTH) {
						newInterfaceName = StringUtils.capitalize(reference.getParameters()[paramIndex].getName())
								+ names.size();
					}
					newInterfaceName = resolveNameClash(functionalDisambiguation, highestTypeDeclaration,
							f.function.getParameters()[paramIndex].getType(), newInterfaceName);
					names.add(newInterfaceName);
				}
			}
			break;
		case FULL:
			// apply a whole name strategy
			if (functionalDisambiguation) {
				for (FullFunctionDeclaration f : l) {
					String newInterfaceName = StringUtils.capitalize(reference.getParameters()[paramIndex].getName());
					if (f.function.getParameters()[paramIndex].getType().getTypeArguments() != null) {
						for (TypeReference t : f.function.getParameters()[paramIndex].getType().getTypeArguments()) {
							String[] tokens = context.getShortTypeNameNoErasure(t).split("[<>,]");
							for (String token : tokens) {
								newInterfaceName += StringUtils.capitalize(token).replace("[]", "s");
							}
						}
					} else {
						String[] tokens = context
								.getShortTypeNameNoErasure(f.function.getParameters()[paramIndex].getType())
								.split("[<>,]");
						for (String token : tokens) {
							newInterfaceName += StringUtils.capitalize(token).replace("[]", "s");
						}
					}
					if (newInterfaceName.length() > MAX_NAME_LENGTH) {
						newInterfaceName = StringUtils.capitalize(reference.getParameters()[paramIndex].getName())
								+ names.size();
					}
					newInterfaceName = resolveNameClash(functionalDisambiguation, highestTypeDeclaration,
							f.function.getParameters()[paramIndex].getType(), newInterfaceName);
					names.add(newInterfaceName);
				}
			} else {
				for (FullFunctionDeclaration f : l) {
					String newInterfaceName = StringUtils
							.capitalize(context.getShortTypeNameErased(reference.getParameters()[paramIndex].getType())
									.replace("[]", "s"));
					if (f.function.getParameters()[paramIndex].getType().getTypeArguments() != null) {
						for (TypeReference t : f.function.getParameters()[paramIndex].getType().getTypeArguments()) {
							String[] tokens = context.getShortTypeNameNoErasure(t).split("[<>,]");
							for (String token : tokens) {
								newInterfaceName += StringUtils.capitalize(token).replace("[]", "s");
							}
						}
					} else {
						String[] tokens = context
								.getShortTypeNameNoErasure(f.function.getParameters()[paramIndex].getType())
								.split("[<>,]");
						for (String token : tokens) {
							newInterfaceName += StringUtils.capitalize(token).replace("[]", "s");
						}
					}
					if (newInterfaceName.length() > MAX_NAME_LENGTH) {
						newInterfaceName = StringUtils.capitalize(reference.getParameters()[paramIndex].getName())
								+ names.size();
					}
					newInterfaceName = resolveNameClash(functionalDisambiguation, highestTypeDeclaration,
							f.function.getParameters()[paramIndex].getType(), newInterfaceName);
					names.add(newInterfaceName);
				}
			}
		}
		return names;
	}

	private boolean isTypePresent(TypeDeclaration t, Set<FullFunctionDeclaration> duplicates) {
		if (t == null) {
			return false;
		}
		for (FullFunctionDeclaration f : duplicates) {
			if (t.equals(f.declaringType)) {
				return true;
			}
		}
		return false;
	}

	private TypeDeclaration getHighestSuperType(Set<FullFunctionDeclaration> duplicates) {
		FullFunctionDeclaration first = duplicates.iterator().next();
		TypeDeclaration hightest = first.declaringType;
		TypeDeclaration t = hightest;
		while (t != null && t.getSuperTypes() != null && t.getSuperTypes().length > 0) {
			TypeDeclaration st = (TypeDeclaration) lookupType(t.getSuperTypes()[0], null);
			if (st == null && t.getToken() != null) {
				context.reportError("undefined type " + t.getSuperTypes()[0], t.getToken());
			}
			if (isTypePresent(st, duplicates)) {
				hightest = st;
			}
			t = st;
		}
		return hightest;
	}

}
