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

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.join;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.ast.AbstractPrinter;
import org.jsweet.input.typescriptdef.ast.ArrayTypeReference;
import org.jsweet.input.typescriptdef.ast.CompilationUnit;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Declaration;
import org.jsweet.input.typescriptdef.ast.DeclarationHelper;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.FunctionalTypeReference;
import org.jsweet.input.typescriptdef.ast.ModuleDeclaration;
import org.jsweet.input.typescriptdef.ast.ParameterDeclaration;
import org.jsweet.input.typescriptdef.ast.QualifiedDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.Type;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeParameterDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.ast.UnionTypeReference;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;
import org.jsweet.input.typescriptdef.util.Util;

/**
 * This final scanner generates the Java code.
 * 
 * @author Renaud Pawlak
 */
public class JavaDefModelPrinter extends AbstractPrinter {

	private static Map<String, String> typeMap = new HashMap<String, String>();

	static {
		typeMap.put("any", "Object");
		typeMap.put("string", "String");
		typeMap.put("number", "double");
		typeMap.put("symbol", "String");
	}

	private static String toJavaTypeName(String typeName) {
		if (typeMap.containsKey(typeName)) {
			return typeMap.get(typeName);
		} else {
			return typeName;
		}
	}

	private File outputDir;

	public JavaDefModelPrinter(Context context, File outputDir) {
		super(context);
		this.outputDir = outputDir;
	}

	private AbstractPrinter printIdentifier(String identifier) {
		print(DeclarationHelper.toJavaIdentifier(identifier));
		return this;
	}

	private AbstractPrinter printFunctionName(String functionName) {
		return printIdentifier(functionName);
	}

	@Override
	public void visitCompilationUnit(CompilationUnit compilationUnit) {
		if (context.isDependency(compilationUnit)) {
			return;
		}

		super.visitCompilationUnit(compilationUnit);
	}

	String currentModuleName;

	@Override
	public void visitModuleDeclaration(ModuleDeclaration moduleDeclaration) {

		logger.trace("PRINT " + moduleDeclaration.getName());

		currentModuleName = getCurrentModuleName();
		String dependenciesNamesString = null;
		if (context.libModules.contains(currentModuleName)) {

			List<String> dependenciesNames = context.dependencyGraph
					.getDestinationElements(moduleDeclaration.getName());
			dependenciesNamesString = "";
			if (dependenciesNames != null) {
				for (int i = 0; i < dependenciesNames.size(); i++) {
					dependenciesNames.set(i, "\"" + dependenciesNames.get(i) + "\"");
				}
				dependenciesNamesString = join(dependenciesNames, ",");
			}
		}
		File f = new File(outputDir, currentModuleName.replace('.', '/') + "/package-info.java");
		f.getParentFile().mkdirs();
		try {
			if (dependenciesNamesString != null) {
				FileUtils.write(f, //
						"/** This package contains the " + moduleDeclaration.getName()
								+ " library (source: Definitely Typed). */\n");

				String mixins = "";
				if (context.getMixins(currentModuleName) != null) {
					mixins = StringUtils.join(
							context.getMixins(currentModuleName).stream().map(t -> context.getTypeName(t)).toArray(),
							".class,") + ".class";
				}
				FileUtils.write(f, "@" + JSweetDefTranslatorConfig.ANNOTATION_ROOT + "(dependencies={"
						+ dependenciesNamesString + "}, mixins={" + mixins + "})\n", true);
			} else {
				FileUtils.write(f, //
						"/** (source: Definitely Typed) */\n");
			}
			CharSequence annosDecls = annotationsToString(moduleDeclaration);
			if (!isBlank(annosDecls)) {
				FileUtils.write(f, annosDecls, true);
			}
			if (context.externalModules.keySet().contains(currentModuleName)) {
				FileUtils.write(f, "@" + JSweetDefTranslatorConfig.ANNOTATION_MODULE + "(\""
						+ context.externalModules.get(currentModuleName) + "\")\n", true);
			}
			FileUtils.write(f, "package " + currentModuleName + ";\n", true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		scan(moduleDeclaration.getMembers());
	}

	private void printDocumentation(Declaration declaration) {
		if (declaration.getDocumentation() != null) {
			printIndent().print(declaration.getDocumentation()).println();
		}
	}

	@Override
	public void visitTypeDeclaration(TypeDeclaration typeDeclaration) {
		// typeDeclaration.setName(toJavaDeclarationName(typeDeclaration.getName()));
		if (typeDeclaration.isExternal()) {
			return;
		}
		boolean innerType = getParent() instanceof TypeDeclaration;

		StringBuilder mergedSuperTypeList = new StringBuilder();
		if (typeDeclaration.getMergedSuperTypes() != null) {
			for (TypeReference type : typeDeclaration.getMergedSuperTypes()) {
				mergedSuperTypeList.append(type.getName() + ".class,");
			}
			if (mergedSuperTypeList.length() > 1) {
				mergedSuperTypeList.deleteCharAt(mergedSuperTypeList.length() - 1);
				typeDeclaration.addStringAnnotation(
						JSweetDefTranslatorConfig.ANNOTATION_EXTENDS + "({" + mergedSuperTypeList.toString() + "})");
			}
		}
		if (!innerType) {
			clearOutput();
			currentModuleName = getCurrentModuleName();

			print("package " + currentModuleName + ";").println();

			// dynamically create imports
			Set<String> imports = new HashSet<String>();
			new Scanner(this) {
				@Override
				public void visitTypeReference(TypeReference typeReference) {
					TypeDeclaration enclosingTypeDecl = getParent(TypeDeclaration.class);
					if (typeReference.getName() != null && enclosingTypeDecl != null
							&& typeReference.getName().equals(enclosingTypeDecl.getName())) {
						return;
					}

					Type t = lookupType(typeReference, null);
					if (t instanceof TypeDeclaration) {
						TypeDeclaration typeDeclaration = (TypeDeclaration) t;
						String name = context.getTypeName(typeDeclaration);
						if (!typeDeclaration.isExternal() && name != null
								&& !(typeReference.getName().contains(".") && typeReference.getName().equals(name))) {
							imports.add(name);
						}
					}
					super.visitTypeReference(typeReference);
				}
			}.visitTypeDeclaration(typeDeclaration);
			List<String> importShortNames = imports.stream().map(name -> Util.getSimpleName(name))
					.collect(Collectors.toList());
			List<String> clashes = new ArrayList<>();
			for (String typeName : imports) {
				if (!typeName.startsWith(currentModuleName + ".")) {
					if (typeName.contains(".")) {
						if (Collections.frequency(importShortNames, Util.getSimpleName(typeName)) > 1) {
							clashes.add(typeName);
						} else {
							print("import ").print(typeName).print(";").println();
						}
					}
				}
			}
			for (String clash : clashes) {
				new Scanner(this) {
					@Override
					public void visitTypeReference(TypeReference typeReference) {
						TypeDeclaration enclosingTypeDecl = getParent(TypeDeclaration.class);
						if (typeReference.getName() != null && enclosingTypeDecl != null
								&& typeReference.getName().equals(enclosingTypeDecl.getName())) {
							return;
						}

						Type t = lookupType(typeReference, null);
						if (t instanceof TypeDeclaration) {
							TypeDeclaration typeDeclaration = (TypeDeclaration) t;
							String name = context.getTypeName(typeDeclaration);
							if (clash.equals(name)) {
								typeReference.setName(name);
							}
						}
						super.visitTypeReference(typeReference);
					}
				}.visitTypeDeclaration(typeDeclaration);
			}

			printDocumentation(typeDeclaration);
			printAnnotations(typeDeclaration);
			print("public ");
		} else {
			printDocumentation(typeDeclaration);
			printAnnotations(typeDeclaration);
			printIndent().print("public ");
		}
		if (typeDeclaration.hasModifier("static")) {
			print("static ");
		}
		if (typeDeclaration.hasModifier("abstract")) {
			print("abstract ");
		}
		if (JSweetDefTranslatorConfig.GLOBALS_CLASS_NAME.equals(typeDeclaration.getName())) {
			print("final ");
		}
		print(typeDeclaration.getKind() + " ");
		printIdentifier(typeDeclaration.getName());
		printTypeParameters(typeDeclaration.getTypeParameters(), false);
		if ("interface".equals(typeDeclaration.getKind())) {
			if (typeDeclaration.getSuperTypes() != null && typeDeclaration.getSuperTypes().length > 0) {
				for (TypeReference r : typeDeclaration.getSuperTypes()) {
					TypeDeclaration t = (TypeDeclaration) lookupType(r, null);
					if ("interface".equals(t.getKind())) {
						print(" extends ").print(typeDeclaration.getSuperTypes()[0]);
					} else {
						context.reportError(
								"wrong subclassing link between " + typeDeclaration.getName() + " and " + t.getName(),
								typeDeclaration.getToken());
					}
				}
			}
		} else if ("class".equals(typeDeclaration.getKind())) {
			List<TypeReference> extendList = new ArrayList<TypeReference>();
			List<TypeReference> implementList = new ArrayList<TypeReference>();
			if (typeDeclaration.getSuperTypes() != null && typeDeclaration.getSuperTypes().length > 0) {
				for (TypeReference r : typeDeclaration.getSuperTypes()) {
					TypeDeclaration t = (TypeDeclaration) lookupType(r, null);
					if (t != null) {
						if ("interface".equals(t.getKind())) {
							implementList.add(r);
						} else {
							extendList.add(r);
						}
					} else {
						logger.warn("unresolved inheritance reference: " + r);
					}
				}
			}

			if (extendList.size() > 1) {

				context.reportError("multiple inheritance should not appear at this stage for "
						+ typeDeclaration.getName() + " extends=" + extendList, typeDeclaration.getToken());
			} else if (extendList.size() == 1) {
				print(" extends ").print(extendList.get(0));
			} else if (!JSweetDefTranslatorConfig.isJDKReplacementMode()
					&& !((JSweetDefTranslatorConfig.getObjectClassName())
							.equals(currentModuleName + "." + typeDeclaration.getName()))
					&& !typeDeclaration.getName().equals(JSweetDefTranslatorConfig.GLOBALS_CLASS_NAME)) {
				print(" extends ").print(JSweetDefTranslatorConfig.getObjectClassName());
			}
			if (!implementList.isEmpty()) {
				print(" implements ");
				for (TypeReference r : implementList) {
					print(r);
					print(", ");
				}
				removeLastChars(2);
			}
		}

		print(" {");
		println().startIndent();
		scan(typeDeclaration.getMembers());
		endIndent();
		printIndent().print("}").println();
		if (!innerType) {
			String typeName = currentModuleName + "." + typeDeclaration.getName();
			File outputFile = new File(outputDir, typeName.replace('.', '/') + ".java");
			writeToFile(outputFile, getResult());
		}
	}

	private void writeToFile(File f, String output) {
		f.getParentFile().mkdirs();
		if (f.exists()) {
			logger.warn(f + " already exists!!");
		}

		try {
			try (PrintWriter out = new PrintWriter(f.getPath())) {
				out.println(output);
				out.close();
				logger.trace("wrote " + f);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private JavaDefModelPrinter printTypeParameters(TypeParameterDeclaration[] typeParameters, boolean whitespace) {
		if (typeParameters != null && typeParameters.length > 0) {
			print("<");
			for (TypeParameterDeclaration t : typeParameters) {
				print(t);
				print(",");
			}
			if (typeParameters.length > 0) {
				removeLastChar();
			}
			print(">");
			if (whitespace) {
				print(" ");
			}
		}
		return this;
	}

	private String getTypeInitalValue(String typeName) {
		if (typeName == null) {
			return "null";
		}
		switch (typeName) {
		case "void":
			return null;
		case "boolean":
			return "false";
		case "number":
			return "0";
		default:
			return "null";
		}
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
		printDocumentation(functionDeclaration);
		TypeDeclaration owningType = getParent(TypeDeclaration.class);
		// boolean hasConstructor = owningType != null &&
		// owningType.hasConstructor;
		String name = DeclarationHelper.getActualFunctionName(functionDeclaration);
		if (name != null && !name.equals(functionDeclaration.getName())) {
			functionDeclaration.addStringAnnotation(
					JSweetDefTranslatorConfig.ANNOTATION_NAME + "(\"" + functionDeclaration.getName() + "\")");
		}
		printAnnotations(functionDeclaration).printIndent();
		boolean isNative = false;
		if (owningType != null && !owningType.getKind().equals("interface") && !functionDeclaration.isConstructor()) {
			print("native ");
			isNative = true;
		}
		if (!functionDeclaration.isConstructor()) {
			print("public ");
		}
		boolean isInterface = (owningType != null && owningType.isInterface());
		boolean isStatic = false;
		if (functionDeclaration.hasModifier("static")) {
			isStatic = true;
			print("static ");
		}
		if (functionDeclaration.isConstructor()) {
			if (JSweetDefTranslatorConfig.GLOBALS_CLASS_NAME.equals(owningType.getName())) {
				print("private ");
			} else {
				if (functionDeclaration.hasModifier("protected")) {
					print("protected ");
				} else if (functionDeclaration.hasModifier("private")) {
					print("private ");
				} else {
					print("public ");
				}
			}
			if (functionDeclaration.getTypeParameters() != null) {
				List<TypeParameterDeclaration> typeParameters = new ArrayList<>(
						asList(functionDeclaration.getTypeParameters()));
				if (owningType.getTypeParameters() != null) {
					typeParameters.removeAll(asList(owningType.getTypeParameters()));
				}
				printTypeParameters(typeParameters.toArray(new TypeParameterDeclaration[0]), true);
			}
			print(owningType.getName());
		} else {
			printTypeParameters(functionDeclaration.getTypeParameters(), true);
			switch (functionDeclaration.getName()) {
			case JSweetDefTranslatorConfig.INDEXED_GET_FUCTION_NAME:
			case JSweetDefTranslatorConfig.INDEXED_SET_FUCTION_NAME:
			case JSweetDefTranslatorConfig.INDEXED_DELETE_FUCTION_NAME:
				if (Util.wrapTypeMap.containsKey(functionDeclaration.getType().getName())) {
					print(Util.wrapTypeMap.get(functionDeclaration.getType().getName())).print(" ");
				} else {
					print(functionDeclaration.getType()).print(" ");
				}
				if (isStatic) {
					print(functionDeclaration.getName() + "Static");
				} else {
					printFunctionName(functionDeclaration.getName());
				}
				break;
			default:
				print(functionDeclaration.getType()).print(" ");
				printFunctionName(name);
			}
		}
		print("(");
		for (VariableDeclaration t : functionDeclaration.getParameters()) {
			print(t);
			print(", ");
		}
		if (functionDeclaration.getParameters().length > 0) {
			removeLastChars(2);
		}
		print(")");
		if (isNative || (isInterface && !isStatic)) {
			print(";");
		} else {
			if (functionDeclaration.isConstructor()) {
				print("{}");
			} else {
				print("{");
				String typeName = functionDeclaration.getType().getName();
				if (typeName == null) {
					typeName = "Object";
				}
				String initialValue = getTypeInitalValue(typeName);
				if (initialValue != null) {
					print("return " + initialValue + ";");
				}
				print("}");
			}
		}
		println();
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
		TypeDeclaration owningType = getParent(TypeDeclaration.class);
		printDocumentation(variableDeclaration);
		printAnnotations(variableDeclaration);
		if (variableDeclaration.isOptional() && owningType != null) {
			printIndent().print("@" + JSweetDefTranslatorConfig.ANNOTATION_OPTIONAL).println();
		}
		if (owningType != null && "enum".equals(owningType.getKind())) {
			printIndent().print(variableDeclaration.getName());
			print(",").println();
		} else {
			printIndent().print("public ");
			if (variableDeclaration.hasModifier("static")) {
				print("static ");
			}
			if (variableDeclaration.isReadonly()) {
				print("final ");
			}
			print(variableDeclaration.getType());
			print(" ");
			printIdentifier(variableDeclaration.getName());
			if (owningType != null && "interface".equals(owningType.getKind()) || variableDeclaration.isReadonly()) {
				String typeName = variableDeclaration.getType().getName();
				String initialValue = getTypeInitalValue(typeName);
				if (initialValue != null) {
					print("=" + initialValue);
				}
			}
			print(";").println();
		}
	}

	@Override
	public void visitParameterDeclaration(ParameterDeclaration parameterDeclaration) {
		printDocumentation(parameterDeclaration);
		if (parameterDeclaration.isVarargs()) {
			if (Util.hasTypeParameterReferences(context, parameterDeclaration.getType())) {
				print("@SuppressWarnings(\"unchecked\") ");
			}
			if (parameterDeclaration.getType().isArray()) {
				print(parameterDeclaration.getType().getComponentType());
			} else {
				print(parameterDeclaration.getType());
			}
			print("...");
		} else {
			print(parameterDeclaration.getType());
		}
		print(" ");
		printIdentifier(parameterDeclaration.getName());
	}

	@Override
	public void visitTypeParameterDeclaration(TypeParameterDeclaration typeParameterDeclaration) {
		String name = toJavaTypeName(typeParameterDeclaration.getName());
		print(name);
		if (typeParameterDeclaration.getUpperBound() != null) {
			print(" extends ").print(typeParameterDeclaration.getUpperBound());
		}
	}

	@Override
	public void visitTypeReference(TypeReference typeReference) {
		if (typeReference.isObjectType()) {
			logger.error(
					"object types are not allowed at this point: " + getCurrentContainerName() + ":" + getParent());
			return;
		}
		if ("this".equals(typeReference.getName())) {
			print(getParent(TypeDeclaration.class).getName());
		} else {
			String name = toJavaTypeName(typeReference.getName());
			if (Util.coreTypeMap.containsKey(typeReference.getName())
					&& !JSweetDefTranslatorConfig.isJDKReplacementMode()
					&& JSweetDefTranslatorConfig.LANG_PACKAGE.equals(currentModuleName)) {
				print("java.lang.");
				print(name);
			} else {
				print(name);
			}
		}
		if (typeReference.getTypeArguments() != null) {
			boolean functionalTypeReference = isFunctionalTypeReference(typeReference);
			boolean superTypeReference = isSuperTypeReference(typeReference);
			print("<");
			for (TypeReference t : typeReference.getTypeArguments()) {
				if (!superTypeReference && !functionalTypeReference && ("any".equals(t.getName())
						|| (t instanceof UnionTypeReference && "java.lang.Object".equals(t.getName())))) {
					print("?");
				} else {
					print(t);
				}
				print(",");
			}
			if (typeReference.getTypeArguments().length > 0) {
				removeLastChar();
			}
			print(">");
		}
	}

	@Override
	public void visitFunctionalTypeReference(FunctionalTypeReference functionalTypeReference) {
		// functional interfaces should have been generated at this point
		print("<functional_type_error>");
	}

	@Override
	public void visitArrayTypeReference(ArrayTypeReference arrayTypeReference) {
		print(arrayTypeReference.getComponentType());
		if (!arrayTypeReference.isDisableArray()) {
			print("[]");
		}
	}

	@Override
	public void visitUnionTypeReference(UnionTypeReference unionTypeReference) {
		switch (unionTypeReference.getSelected()) {
		case NONE:
		case PENDING:
			// union types should have been expanded at this point
			print("<union_type_error>");
			break;
		case LEFT:
			print(unionTypeReference.getLeftType());
			break;
		case RIGHT:
			print(unionTypeReference.getRightType());
			break;
		}
	}

	@Override
	public void onScanEnded() {
		for (QualifiedDeclaration<ModuleDeclaration> moduleDeclaration : context
				.findDeclarations(ModuleDeclaration.class, "*")) {
			String module = context.getModuleName(moduleDeclaration.getDeclaration());
			if (context.moduleDocumentations.containsKey(module)) {
				logger.info("generating package-info.java for " + module);
				File f = new File(outputDir, module.replace('.', '/') + "/package-info.java");
				f.getParentFile().mkdirs();
				try {
					FileUtils.write(f, context.moduleDocumentations.get(module) + "\npackage " + module + ";", false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
