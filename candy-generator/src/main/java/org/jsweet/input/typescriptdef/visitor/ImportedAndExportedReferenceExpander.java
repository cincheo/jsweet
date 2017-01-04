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

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.ast.AstNode;
import org.jsweet.input.typescriptdef.ast.CompilationUnit;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.Declaration;
import org.jsweet.input.typescriptdef.ast.DeclarationContainer;
import org.jsweet.input.typescriptdef.ast.DeclarationHelper;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.ModuleDeclaration;
import org.jsweet.input.typescriptdef.ast.QualifiedDeclaration;
import org.jsweet.input.typescriptdef.ast.ReferenceDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;
import org.jsweet.input.typescriptdef.util.Util;

/**
 * This scanner expands the imported / exported references to anything declared.
 * 
 * @author Louis Grignon
 * @author Renaud Pawlak
 */
public class ImportedAndExportedReferenceExpander extends Scanner {

	private int referencesCount = 0;
	private QualifiedDeclaration<? extends Declaration>[] containerScope;
	private boolean importMode = false;

	public ImportedAndExportedReferenceExpander(Context context, boolean importMode) {
		super(context);
		this.importMode = importMode;
	}

	public ImportedAndExportedReferenceExpander(Context context,
			QualifiedDeclaration<? extends Declaration>[] containerScope, boolean importMode) {
		super(context);
		this.containerScope = containerScope;
		logger.trace("searching imports in " + Arrays.asList(containerScope));
		this.importMode = importMode;
	}

	/**
	 * Overrides the default implementation because we intend to pass this
	 * scanner before the packages reorganization.
	 */
	@Override
	protected QualifiedDeclaration<ModuleDeclaration> lookupModuleDeclaration(String name) {
		for (int i = 0; i < getStack().size(); i++) {
			String containerName = getContainerNameAtIndex(i);
			String declFullName = StringUtils.isBlank(containerName) ? name : containerName + "." + name;
			// first search in the current compilation unit
			List<QualifiedDeclaration<ModuleDeclaration>> matches = context.findDeclarations(ModuleDeclaration.class,
					declFullName, (CompilationUnit) getRoot());
			for (QualifiedDeclaration<ModuleDeclaration> m : matches) {
				if (!m.getDeclaration().isQuotedName()) {
					return m;
				}
			}
			for (QualifiedDeclaration<ModuleDeclaration> m : matches) {
				return m;
			}
		}
		for (int i = 0; i < getStack().size(); i++) {
			String containerName = getContainerNameAtIndex(i);
			String declFullName = StringUtils.isBlank(containerName) ? name : containerName + "." + name;
			// search in all compilation units
			List<QualifiedDeclaration<ModuleDeclaration>> matches = context.findDeclarations(ModuleDeclaration.class,
					declFullName);
			for (QualifiedDeclaration<ModuleDeclaration> m : matches) {
				if (!m.getDeclaration().isQuotedName()) {
					return m;
				}
			}
			for (QualifiedDeclaration<ModuleDeclaration> m : matches) {
				return m;
			}
		}
		return null;
	}

	/**
	 * Overrides the default implementation because we intend to pass this
	 * scanner before the packages reorganization.
	 */
	@Override
	protected QualifiedDeclaration<TypeDeclaration> lookupTypeDeclaration(String name) {
		for (int i = 0; i < getStack().size(); i++) {
			String containerName = getContainerNameAtIndex(i);
			String declFullName = StringUtils.isBlank(containerName) ? name : containerName + "." + name;
			// first search in the current compilation unit
			QualifiedDeclaration<TypeDeclaration> match = context.findFirstDeclaration(TypeDeclaration.class,
					declFullName, (CompilationUnit) getRoot());
			if (match != null) {
				return match;
			}
			// search in all compilation units
			match = context.findFirstDeclaration(TypeDeclaration.class, declFullName);
			if (match != null) {
				return match;
			}
		}
		return null;
	}

	private boolean isInContainerScope(QualifiedDeclaration<? extends Declaration> declaration) {
		if (containerScope == null) {
			return false;
		}
		for (QualifiedDeclaration<? extends Declaration> d : containerScope) {
			if (declaration.getDeclaration() == d.getDeclaration()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void visitReferenceDeclaration(ReferenceDeclaration referenceDeclaration) {
		if (referenceDeclaration.isExport() && importMode) {
			return;
		}
		if (referenceDeclaration.isImport() && !importMode) {
			return;
		}

		if (containerScope != null) {
			for (QualifiedDeclaration<? extends Declaration> d : containerScope) {
				if (!isInScope(d.getDeclaration())) {
					return;
				}
			}
		}
		logger.debug("analysing reference: " + getParent(CompilationUnit.class) + "->" + referenceDeclaration);

		DeclarationContainer container = (DeclarationContainer) getParent();
		DeclarationContainer parentContainer = getParent(DeclarationContainer.class, container);
		QualifiedDeclaration<? extends Declaration> foreignDeclaration = lookupModuleDeclaration(
				referenceDeclaration.getReferencedName());
		if (foreignDeclaration == null) {
			foreignDeclaration = lookupTypeDeclaration(referenceDeclaration.getReferencedName());
		}
		if (foreignDeclaration == null) {
			foreignDeclaration = lookupFunctionDeclaration(referenceDeclaration.getReferencedName());
		}
		if (foreignDeclaration == null) {
			foreignDeclaration = lookupVariableDeclaration(referenceDeclaration.getReferencedName());
		}
		if (foreignDeclaration != null) {
			if (isInContainerScope(foreignDeclaration)) {
				return;
			}
			logger.trace((containerScope == null ? "" : "(scope=" + Arrays.asList(containerScope) + ") ")
					+ "reference found: " + getCurrentContainerName() + "->" + referenceDeclaration + " references => "
					+ foreignDeclaration);

			// resolve foreign import first
			if (foreignDeclaration.getDeclaration() instanceof DeclarationContainer) {
				logger.trace("looking into context for " + foreignDeclaration.getQualifiedDeclarationName());

				ImportedAndExportedReferenceExpander subScanner = new ImportedAndExportedReferenceExpander(context,
						ArrayUtils.add(containerScope, foreignDeclaration), this.importMode);
				subScanner.scan(context.compilationUnits);
			}

			// TODO: this part contains a lot of code duplication (function/var)
			// because the development is very experimental... consolidate at
			// some point...

			if (referenceDeclaration.isExport()) {

				String foreignLib = Util.getLibPackageNameFromTsDefFile(
						new File(foreignDeclaration.getDeclaration().getToken().getFileName()));
				String currentLib = Util
						.getLibPackageNameFromTsDefFile(new File(((AstNode) container).getToken().getFileName()));
				boolean externalReference = !foreignLib.equals(currentLib);

				// FUNCTION or VARIABLE case
				if (foreignDeclaration.getDeclaration() instanceof FunctionDeclaration
						|| foreignDeclaration.getDeclaration() instanceof VariableDeclaration) {
					Declaration declaration = foreignDeclaration.getDeclaration();
					if (((Declaration) container).isQuotedName()) {
						if (!declaration.hasStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_MODULE)) {
							declaration.addStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_MODULE + "(\""
									+ ((Declaration) container).getOriginalName() + "\")");
						}
					}
					Declaration copy = declaration.copy();
					if (getParent(2) instanceof DeclarationContainer) {
						copy.setName(((Declaration) container).getName());
						((DeclarationContainer) getParent(2)).addMember(copy);
						logger.info("added member " + copy + " to " + (DeclarationContainer) getParent(2));
						rewriteTypeReferences(getCurrentContainerName(), copy);
						// make target container external if necessary
						if (((Declaration) container).isQuotedName()) {
							if (container.findDeclaration(foreignDeclaration.getDeclaration()) != null) {
								declaration.setHidden(true);
							}
						}
					} else {
						copy.setName(JSweetDefTranslatorConfig.ANONYMOUS_FUNCTION_NAME);
						container.addMember(copy);
						logger.info("added member " + copy + " to " + container);

						// if function is internal, we assume the actual
						// function to
						// export is the one that belongs to container
						if (!externalReference) {
							foreignDeclaration.getDeclaration().setHidden(true);
						}
					}
					return;
				}

				// HEURISTIC: when the exported element is not in the same
				// lib, then is it external and it should not be exported
				if (externalReference) {
					return;
				}

				// CONTAINER CASE: add members to container directly

				DeclarationContainer foreignDeclarationContainer = (DeclarationContainer) foreignDeclaration
						.getDeclaration();
				String name = ((Declaration) container).getName();
				if (((Declaration) container).isQuotedName()) {
					if (!foreignDeclaration.getDeclaration()
							.hasStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_MODULE)) {
						foreignDeclaration.getDeclaration()
								.addStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_MODULE + "(\""
										+ ((Declaration) container).getOriginalName() + "\")");
					}
				}

				boolean exportInnerNamespace = container.findDeclaration(foreignDeclaration.getDeclaration()) != null;
				if ((name.equals(foreignDeclaration.getDeclaration().getName()) || (!exportInnerNamespace)
						|| (container == foreignDeclarationContainer
								&& foreignDeclaration.getDeclaration().isQuotedName()))) {
					// useless self exporting (quoted module export non quoted
					// module of the same name)
					// referenceDeclaration.setHidden(true);
					// return;
				} else {

					logger.info("adding " + foreignDeclarationContainer + " members to " + container + " / "
							+ getCurrentContainerName());
					if (foreignDeclarationContainer instanceof ModuleDeclaration) {
						if (container.findDeclaration(foreignDeclaration.getDeclaration()) != null) {
							foreignDeclarationContainer.setHidden(true);
						}
					}
					container.addMembers(DeclarationHelper.copy(foreignDeclarationContainer.getMembers()));
					// absolute path redirector
					new TypeReferenceRedirector(foreignDeclaration, foreignDeclaration.getQualifiedDeclarationName(),
							StringUtils.isBlank(getCurrentContainerName()) ? referenceDeclaration.getName()
									: getCurrentContainerName()).scan(container);
					// relative path
					if (foreignDeclaration.getQualifiedDeclarationName().startsWith(name + ".")) {
						new TypeReferenceRedirector(foreignDeclaration,
								foreignDeclaration.getQualifiedDeclarationName().substring(name.length() + 1), "")
										.scan(container);
					}
				}

				foreignDeclarationContainer = findContainer(foreignDeclaration);

				// deal with functions and variables
				List<FunctionDeclaration> functions = foreignDeclarationContainer
						.findFunctions(foreignDeclaration.getDeclaration().getName());

				for (FunctionDeclaration function : functions) {
					if (function.isHidden()) {
						continue;
					}
					if (((Declaration) container).isQuotedName()) {
						if (!function.hasStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_MODULE)) {
							function.addStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_MODULE + "(\""
									+ ((Declaration) container).getOriginalName() + "\")");
						}
					}
					if (exportInnerNamespace) {
						logger.debug("renaming function: " + function + " -> " + name);
						function.setName(name);
					}
					if (getParent(2) instanceof DeclarationContainer) {
						FunctionDeclaration functionCopy = function.copy();
						((DeclarationContainer) getParent(2)).addMember(functionCopy);
						if (exportInnerNamespace) {
							function.setHidden(true);
						}
						rewriteTypeReferences(getCurrentContainerName(), functionCopy);
					}
				}
				// TODO: check if this case actually happens (but there is no
				// reasons why it shouldn't)
				VariableDeclaration var = foreignDeclarationContainer
						.findVariable(foreignDeclaration.getDeclaration().getName());
				if (var != null && !var.isHidden()) {
					if (((Declaration) container).isQuotedName()) {
						if (!var.hasStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_MODULE)) {
							var.addStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_MODULE + "(\""
									+ ((Declaration) container).getOriginalName() + "\")");
						}
					}
					if (exportInnerNamespace) {
						logger.debug("renaming variable: " + var + " -> " + name);
						var.setName(name);
					}
					if (getParent(2) instanceof DeclarationContainer) {
						VariableDeclaration varCopy = var.copy();
						((DeclarationContainer) getParent(2)).addMember(varCopy);
						if (exportInnerNamespace) {
							var.setHidden(true);
						}
						rewriteTypeReferences(getCurrentContainerName(), varCopy);
					}
				}

			} else {

				referenceDeclaration.setName(Util.toJavaName(referenceDeclaration.getName()));
				// we must do the job in all compilation units
				@SuppressWarnings({ "rawtypes", "unchecked" })
				List<QualifiedDeclaration<? extends Declaration>> declarations = (List) context.findDeclarations(
						foreignDeclaration.getDeclaration().getClass(),
						foreignDeclaration.getQualifiedDeclarationName());
				for (QualifiedDeclaration<? extends Declaration> declaration : declarations) {
					// HEURISTIC: if the import imports a module in the same
					// container, then it is a way to make the module public...
					// so we just rename the module.
					String modName = declaration.getQualifier();
					Declaration d = declaration.getDeclaration();
					if (parentContainer == null || getCurrentContainerName().equals(modName == null ? "" : modName)) {
						if (d != null && d instanceof ModuleDeclaration && !d.isQuotedName()) {
							logger.info("renaming module: " + d + " -> " + referenceDeclaration.getName() + " ("
									+ referenceDeclaration.getLocation() + ")");
							for (Declaration currentDecl : container.getMembers()) {
								if (currentDecl.isQuotedName() && d.getName().equals(currentDecl.getName())) {
									if (d.isQuotedName()) {
										d.setOriginalName(d.getOriginalName());
										break;
									}
								}
							}
							d.setName(referenceDeclaration.getName());
							new TypeReferenceRedirector(null, foreignDeclaration.getQualifiedDeclarationName(),
									StringUtils.isBlank(getCurrentContainerName()) ? referenceDeclaration.getName()
											: getCurrentContainerName() + "." + referenceDeclaration.getName())
													.scan(context.compilationUnits);
						}
					} else {
						if (!declaration.getDeclaration().getName().equals(referenceDeclaration.getName())) {
							logger.info("substituting " + referenceDeclaration.getName() + " -> " + declaration + " ("
									+ referenceDeclaration.getLocation() + ")");
							// redirect global members in parent modules
							// (because global functions are moved there)
							List<Declaration> declarationsToProcess = Arrays.asList(parentContainer.getMembers())
									.stream().filter(d2 -> {
										return d2 == container || !(d2 instanceof ModuleDeclaration);
									}).collect(Collectors.toList());
							// qualified
							new TypeReferenceRedirector(foreignDeclaration,
									StringUtils.isBlank(getCurrentContainerName()) ? referenceDeclaration.getName()
											: getCurrentContainerName() + "." + referenceDeclaration.getName(),
									foreignDeclaration.getQualifiedDeclarationName()).scan(declarationsToProcess);
							// not qualified
							new TypeReferenceRedirector(foreignDeclaration, referenceDeclaration.getName(),
									foreignDeclaration.getQualifiedDeclarationName()).scan(declarationsToProcess);
						}
					}
				}

			}

			referencesCount++;
		} else {
			logger.warn("referenced declaration not found in " + getCurrentContainerName() + ": "
					+ referenceDeclaration.getReferencedName());
		}

		container.removeMember(referenceDeclaration);
	}

	private DeclarationContainer findContainer(QualifiedDeclaration<? extends Declaration> foreignDeclaration) {
		if (!foreignDeclaration.getQualifiedDeclarationName().contains(".")) {
			return (DeclarationContainer) getStack().get(0);
		} else {
			String name = foreignDeclaration.getQualifier();
			List<QualifiedDeclaration<ModuleDeclaration>> modules = context.findDeclarations(ModuleDeclaration.class,
					name);
			for (QualifiedDeclaration<ModuleDeclaration> m : modules) {
				if (ArrayUtils.contains(m.getDeclaration().getMembers(), foreignDeclaration.getDeclaration())) {
					return m.getDeclaration();
				}
			}
			return null;
		}
	}

	private void rewriteTypeReferences(String initialContainerName, Declaration declaration) {
		TypeReferenceRewriter rewriter = new TypeReferenceRewriter(context, initialContainerName);
		rewriter.scan(declaration);
	}

	@Override
	public void onScanEnded() {
		// if main pass handled results
		if (containerScope == null && referencesCount > 0) {
			logger.debug(referencesCount + " imported reference(s) expanded.");
		}
	}

	public static class TypeReferenceRedirector extends Scanner {

		private static Logger logger = Logger.getLogger(TypeReferenceRedirector.class);
		private QualifiedDeclaration<? extends Declaration> initialDeclaration;
		private String sourcePrefix;
		private String targetPrefix;

		public TypeReferenceRedirector(QualifiedDeclaration<? extends Declaration> initialDeclaration,
				String sourcePrefix, String targetPrefix) {
			super((Context) null);
			logger.info(sourcePrefix + " -> " + targetPrefix);
			this.initialDeclaration = initialDeclaration;
			this.sourcePrefix = sourcePrefix;
			this.targetPrefix = targetPrefix;
		}

		@Override
		public void visitModuleDeclaration(ModuleDeclaration moduleDeclaration) {
			if (initialDeclaration == null || moduleDeclaration != initialDeclaration.getDeclaration()) {
				super.visitModuleDeclaration(moduleDeclaration);
			}
		}

		@Override
		public void visitTypeDeclaration(TypeDeclaration typeDeclaration) {
			if (initialDeclaration == null || typeDeclaration != initialDeclaration.getDeclaration()) {
				super.visitTypeDeclaration(typeDeclaration);
			}
		}

		@Override
		public void visitTypeReference(TypeReference typeReference) {
			if (typeReference.getName() != null && typeReference.getName().startsWith(sourcePrefix + ".")) {
				typeReference.setDeclaration(null);
				String newName = typeReference.getName().replaceFirst(sourcePrefix + ".",
						StringUtils.isBlank(targetPrefix) ? "" : targetPrefix + ".");
				logger.debug("redirecting type reference: " + typeReference.getName() + " -> " + newName);
				typeReference.setName(newName);
			}
			super.visitTypeReference(typeReference);
		}

		@Override
		public void visitReferenceDeclaration(ReferenceDeclaration referenceDeclaration) {
			if (referenceDeclaration.isImport() && referenceDeclaration.getReferencedName() != null
					&& referenceDeclaration.getReferencedName().startsWith(sourcePrefix + ".")) {
				String newName = referenceDeclaration.getReferencedName().replaceFirst(sourcePrefix + ".",
						StringUtils.isBlank(targetPrefix) ? "" : targetPrefix + ".");
				logger.debug("redirecting reference declaration: " + referenceDeclaration.getReferencedName() + " -> "
						+ newName);
				referenceDeclaration.setReferencedName(newName);
			}
			super.visitReferenceDeclaration(referenceDeclaration);
		}

	}

	public static class TypeReferenceRewriter extends Scanner {

		private static Logger logger = Logger.getLogger(TypeReferenceRedirector.class);
		private String initialContainerName;

		public TypeReferenceRewriter(Context context, String initialContainerName) {
			super((Context) context);
			this.initialContainerName = initialContainerName;
		}

		@Override
		public void visitTypeReference(TypeReference typeReference) {
			QualifiedDeclaration<TypeDeclaration> declaration = context.findFirstDeclaration(TypeDeclaration.class,
					initialContainerName + "." + typeReference);
			if (declaration != null) {
				logger.debug("rewriting type reference: " + typeReference.getName() + " -> "
						+ declaration.getQualifiedDeclarationName());
				typeReference.setName(declaration.getQualifiedDeclarationName());
			}
			super.visitTypeReference(typeReference);
		}

	}

}
