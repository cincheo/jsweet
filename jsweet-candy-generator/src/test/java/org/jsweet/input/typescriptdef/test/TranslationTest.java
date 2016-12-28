package org.jsweet.input.typescriptdef.test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.List;

import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.ast.ArrayTypeReference;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.FunctionalTypeReference;
import org.jsweet.input.typescriptdef.ast.ModuleDeclaration;
import org.jsweet.input.typescriptdef.ast.NamedElement;
import org.jsweet.input.typescriptdef.ast.ParameterDeclaration;
import org.jsweet.input.typescriptdef.ast.QualifiedDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeMacroDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeParameterDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.ast.UnionTypeReference;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;
import org.jsweet.input.typescriptdef.visitor.FunctionalInterfacesCreator;
import org.junit.Assert;
import org.junit.Test;

public class TranslationTest extends AbstractTest {

	@Test
	public void defaultTest() throws FileNotFoundException {
		Context context = parseAndTranslateTestFile("types/default-test", (c) -> {
			assertNotNull(c.findFirstDeclaration(TypeDeclaration.class, "*.DateConstructor"));
		} , true);

		assertEquals(3, context.findDeclarations(ModuleDeclaration.class, "def.types.a.b.*").size());
		assertEquals(3, context.findDeclarations(ModuleDeclaration.class, "*.a.b.*").size());
		assertEquals(3, context.findDeclarations(ModuleDeclaration.class, "def.types.*.c*").size());
		assertEquals(1, context.findDeclarations(ModuleDeclaration.class, "*.c1").size());
		assertContains(context.findDeclarations(ModuleDeclaration.class, "*").stream().map(d -> d.getDeclaration())
				.toArray(size -> new NamedElement[size]), JSweetDefTranslatorConfig.LANG_PACKAGE);
		assertContains(context.findDeclarations(ModuleDeclaration.class, "*").stream().map(d -> d.getDeclaration())
				.toArray(size -> new NamedElement[size]), JSweetDefTranslatorConfig.DOM_PACKAGE);

		ModuleDeclaration module = context.findFirstDeclaration(ModuleDeclaration.class, "def.types.m")
				.getDeclaration();
		TypeDeclaration typedImpl = module.findType("TypedImpl");
		assertEquals(typedImpl,
				context.findFirstDeclaration(TypeDeclaration.class, "def.types.m.TypedImpl").getDeclaration());
		TypeReference typedRef = typedImpl.getSuperTypes()[0];
		assertEquals("Typed", typedRef.getName());
		assertEquals("String", typedRef.getTypeArguments()[0].getName());
		assertEquals("any", typedRef.getTypeArguments()[1].getName());
		assertEquals("Double", typedRef.getTypeArguments()[2].getName());

		assertNull(context.findFirstDeclaration(VariableDeclaration.class, "*.field-with-dashes"));

		VariableDeclaration var = context.findFirstDeclaration(VariableDeclaration.class, "*.field_with_dashes")
				.getDeclaration();
		assertNotNull(var);
		assertTrue(var.hasStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_NAME));
		assertTrue(var.getStringAnnotation(JSweetDefTranslatorConfig.ANNOTATION_NAME).contains("field-with-dashes"));

		assertNotNull(context.findFirstDeclaration(ModuleDeclaration.class, "*.module_with_dashes"));
		assertNotNull(context.findFirstDeclaration(ModuleDeclaration.class, "*.module_with_slashes"));

		assertNull(context.findFirstDeclaration(TypeDeclaration.class, "*.DateConstructor"));
		TypeDeclaration dateType = context.findFirstDeclaration(TypeDeclaration.class, "*.Date").getDeclaration();
		assertTrue(!dateType.getModifiers().contains("abstract"));

		assertNotNull(context.findFirstDeclaration(FunctionDeclaration.class, "def.types.events.Globals.myFunction"));
		assertNull(context.findFirstDeclaration(VariableDeclaration.class, "def.types.Globals.myFunction2"));
		assertNotNull(context.findFirstDeclaration(FunctionDeclaration.class, "def.types.Globals.myFunction2"));
		
	}

	@Test
	public void typeMacroTest() throws FileNotFoundException {
		String testFilePath = "types/type-macro-test";
		Context context = parseAndTranslateTestFile(testFilePath, (c) -> {
			// TYPE PARAMETERS IN FUNCTIONAL TYPES
			TypeMacroDeclaration m = c.findFirstDeclaration(TypeMacroDeclaration.class, "*.GenericType")
					.getDeclaration();
			assertNotNull(((FunctionalTypeReference) m.getType()).getTypeParameters());
			assertEquals("T1", ((FunctionalTypeReference) m.getType()).getTypeParameters()[0].getName());
			assertEquals("T2", ((FunctionalTypeReference) m.getType()).getTypeParameters()[1].getName());
		});

		String basePackage = "def.types.typemacrotests";

		TypeMacroDeclaration typeMacro = context
				.findFirstDeclaration(TypeMacroDeclaration.class, basePackage + ".HTMLElement").getDeclaration();
		TypeReference typeMacroTypeArg = typeMacro.getType().getTypeArguments()[0];

		TypeDeclaration enclosingTypeDecl = context
				.findFirstDeclaration(TypeDeclaration.class, basePackage + ".EnclosingInterface").getDeclaration();
		TypeReference fieldType = enclosingTypeDecl.findVariable("field").getType();
		assertEquals(enclosingTypeDecl.findVariable("field"),
				context.findFirstDeclaration(VariableDeclaration.class, basePackage + ".EnclosingInterface.field")
						.getDeclaration());

		logger.info("resulting type macro: " + fieldType);
		assertEquals(typeMacro.getType(), fieldType);
		assertEquals(typeMacroTypeArg, fieldType.getTypeArguments()[0]);

		TypeReference fieldGenericsType = enclosingTypeDecl.findVariable("fieldGenerics").getType();
		logger.info("resulting type macro: " + fieldGenericsType);
		assertEquals("BaseTypedElement", fieldGenericsType.getName());
		assertEquals(1, fieldGenericsType.getTypeArguments().length);
		assertEquals(typeMacro.getType(), fieldGenericsType.getTypeArguments()[0]);
		assertEquals(typeMacroTypeArg, fieldGenericsType.getTypeArguments()[0].getTypeArguments()[0]);

		// WITH PARAM AND RETURN
		boolean foundMacro = false;
		List<QualifiedDeclaration<FunctionDeclaration>> l = context.findDeclarations(FunctionDeclaration.class,
				"*.methodWithParam");
		assertEquals(3, l.size());

		l = context.findDeclarations(FunctionDeclaration.class, "*.methodWithReturn");
		assertEquals(1, l.size());
		FunctionDeclaration methodWithReturn = enclosingTypeDecl.findFirstFunction("methodWithReturn");
		assertEquals(typeMacro.getType(), methodWithReturn.getType());
		assertEquals(typeMacroTypeArg, methodWithReturn.getType().getTypeArguments()[0]);

		// WITH UNION
		TypeReference fieldWithUnionType = enclosingTypeDecl.findVariable("fieldWithUnion").getType();
		assertEquals("Union3", fieldWithUnionType.getName());
		assertContains(fieldWithUnionType.getTypeArguments(), "String");
		assertContains(fieldWithUnionType.getTypeArguments(), "Double");
		foundMacro = false;
		for (TypeReference typeArg : fieldWithUnionType.getTypeArguments()) {
			if (typeArg.getName().equals("DOMElement") && typeArg.getTypeArguments().length == 1
					&& typeArg.getTypeArguments()[0].getName().equals("HTMLAttributes")) {
				foundMacro = true;
				break;
			}
		}
		assertTrue("did not find substituted macro in " + fieldWithUnionType, foundMacro);

		TypeReference fieldWithGenericsAndUnionType = enclosingTypeDecl.findVariable("fieldWithGenericsAndUnion")
				.getType();
		assertEquals("BaseTypedElement", fieldWithGenericsAndUnionType.getName());
		assertEquals(1, fieldWithGenericsAndUnionType.getTypeArguments().length);
		TypeReference fieldWithGenericTypeArg = fieldWithGenericsAndUnionType.getTypeArguments()[0];
		assertEquals("Union3", fieldWithGenericTypeArg.getName());
		assertContains(fieldWithGenericTypeArg.getTypeArguments(), "String");
		assertContains(fieldWithGenericTypeArg.getTypeArguments(), "Double");
		foundMacro = false;
		for (TypeReference typeArg : fieldWithGenericTypeArg.getTypeArguments()) {
			if (typeArg.getName().equals("DOMElement") && typeArg.getTypeArguments().length == 1
					&& typeArg.getTypeArguments()[0].getName().equals("HTMLAttributes")) {
				foundMacro = true;
				break;
			}
		}
		assertTrue("did not find substituted macro in " + fieldWithGenericsAndUnionType, foundMacro);

		VariableDeclaration d = context.findFirstDeclaration(VariableDeclaration.class, "*.reactNode").getDeclaration();
		assertNotNull(d);
		// UNIONS ARE NOW LIMITED TO 3
		// assertEquals("Union6", d.getType().getName());
		// assertEquals("Array", d.getType().getTypeArguments()[4].getName());
		// assertEquals("Union5",
		// d.getType().getTypeArguments()[4].getTypeArguments()[0].getName());
		assertEquals("any", d.getType().getName());

		l = context.findDeclarations(FunctionDeclaration.class, "*.Test.forEach*");
		// TODO: I am not sure why it is so many functions...
		// UNIONS ARE LIMITED
		// assertEquals(30, l.size());
		for (QualifiedDeclaration<FunctionDeclaration> f : l) {
			for (QualifiedDeclaration<FunctionDeclaration> f2 : l) {
				assertFalse("duplicate method: " + f2,
						f.getDeclaration().equals(f2.getDeclaration()) && f.getDeclaration() != f2.getDeclaration());
			}
		}

		// TYPE PARAMETERS IN FUNCTIONAL TYPES
		TypeMacroDeclaration m = context.findFirstDeclaration(TypeMacroDeclaration.class, "*.GenericType")
				.getDeclaration();
		assertNotNull(m.getType().getTypeArguments());
		assertEquals("Array", m.getType().getTypeArguments()[0].getName());
		assertEquals("any", m.getType().getTypeArguments()[0].getTypeArguments()[0].getName());
		assertEquals("any", m.getType().getTypeArguments()[1].getName());

	}

	@Test
	public void importReferencesTest() throws FileNotFoundException {
		// TODO : forbid modules of the same name, with different cases

		String testFilePath = "references/import-test";
		Context context = parseAndTranslateTestFile(testFilePath);

		String basePackage = "def.references";

		TypeDeclaration referencedType = context.findFirstDeclaration(TypeDeclaration.class, basePackage + ".foo.A")
				.getDeclaration();
		assertEquals(0, context.findDeclarations(ModuleDeclaration.class, basePackage + ".importtests.foo").size());

		TypeDeclaration type = context.findFirstDeclaration(TypeDeclaration.class, basePackage + ".importtests.A")
				.getDeclaration();
		VariableDeclaration field = type.findVariable("field");
		assertEquals(referencedType, field.getType().getDeclaration());

		assertNull(context.findFirstDeclaration(ModuleDeclaration.class, basePackage + "._T1"));
		assertNotNull(context.findFirstDeclaration(ModuleDeclaration.class, basePackage + ".t1"));
		assertNotNull(context.findFirstDeclaration(TypeDeclaration.class, basePackage + ".t1.I"));
		assertNotNull(context.findFirstDeclaration(ModuleDeclaration.class, basePackage + ".publicmodule"));
		assertNull(context.findFirstDeclaration(ModuleDeclaration.class, basePackage + ".publicmodule.privatemodule"));
		assertNotNull(context.findFirstDeclaration(TypeDeclaration.class, basePackage + ".publicmodule.TestExport"));
		assertNotNull(context.findFirstDeclaration(VariableDeclaration.class, basePackage + ".publicmodule.Globals.v"));
		assertNotNull(context.findFirstDeclaration(FunctionDeclaration.class, basePackage + ".publicmodule.Globals.f"));
		QualifiedDeclaration<FunctionDeclaration> f = context.findFirstDeclaration(FunctionDeclaration.class,
				basePackage + ".Globals.publicModule");
		assertNotNull(f);
		assertEquals(basePackage + ".publicmodule.TestExport", f.getDeclaration().getType().getName());
		assertNotNull(context.findFirstDeclaration(FunctionDeclaration.class, basePackage + ".Globals.publicModule2"));
	}

	@Test
	public void unionTypeTest() throws FileNotFoundException {
		String testFilePath = "types/union-type-test";

		Context context = parseAndTranslateTestFile(testFilePath, (c) -> {
			assertEquals(1, c.findDeclarations(ModuleDeclaration.class, "UnionTypeTests").size());
			VariableDeclaration f1 = c.findFirstDeclaration(VariableDeclaration.class, "UnionTypeTests.I1.f")
					.getDeclaration();
			assertTrue("type should be a union", f1.getType() instanceof UnionTypeReference);
			VariableDeclaration f2 = c.findFirstDeclaration(VariableDeclaration.class, "UnionTypeTests.I2.f")
					.getDeclaration();
			assertTrue("type should be a union", f2.getType() instanceof UnionTypeReference);
		});

		final String basePackage = "def.types.uniontypetests";
		assertEquals(1, context.findDeclarations(ModuleDeclaration.class, basePackage).size());
		TypeDeclaration type = context.findFirstDeclaration(TypeDeclaration.class, basePackage + ".I1")
				.getDeclaration();
		assertEquals(1, type.getMembers().length);
		VariableDeclaration f = type.findVariable("f");
		assertEquals(JSweetDefTranslatorConfig.UNION_CLASS_NAME, f.getType().getName());
		type = context.findFirstDeclaration(TypeDeclaration.class, basePackage + ".I2").getDeclaration();
		assertEquals(1, type.getMembers().length);
		f = type.findVariable("f");
		assertEquals(JSweetDefTranslatorConfig.UNION_CLASS_NAME, f.getType().getName());
		assertContains(f.getType().getTypeArguments(), "String");
		assertContains(f.getType().getTypeArguments(), FunctionalInterfacesCreator.SUPPLIER);
		type = context.findFirstDeclaration(TypeDeclaration.class, basePackage + ".I3").getDeclaration();
		assertEquals(2, type.getMembers().length);
		f = type.findVariable("f");
		assertEquals(JSweetDefTranslatorConfig.UNION_CLASS_NAME, f.getType().getName());
		assertContains(f.getType().getTypeArguments(), "String");
		// generated object type
		assertContains(f.getType().getTypeArguments(), "F");

		// I. assert that B and A has union
		TypeDeclaration typeA = context.findFirstDeclaration(TypeDeclaration.class, basePackage + ".A")
				.getDeclaration();
		VariableDeclaration fieldA = typeA.findVariable("field");
		assertNotNull(fieldA.getType());
		assertEquals(JSweetDefTranslatorConfig.UNION_PACKAGE + ".Union3",
				getTypeQualifiedName(context, fieldA.getType()));
		assertContains(fieldA.getType().getTypeArguments(), "String");
		assertContains(fieldA.getType().getTypeArguments(), "Double");
		assertContains(fieldA.getType().getTypeArguments(), "Boolean");

		TypeDeclaration typeB = context.findFirstDeclaration(TypeDeclaration.class, basePackage + ".B")
				.getDeclaration();
		VariableDeclaration fieldB = typeB.findVariable("field");
		assertNotNull(fieldB.getType());
		assertEquals(JSweetDefTranslatorConfig.UNION_PACKAGE + ".Union3",
				getTypeQualifiedName(context, fieldB.getType()));
		assertContains(fieldB.getType().getTypeArguments(), "String");
		assertContains(fieldB.getType().getTypeArguments(), "Double");
		assertContains(fieldB.getType().getTypeArguments(), "Boolean");

		// II. type param
		TypeDeclaration typeC = context.findFirstDeclaration(TypeDeclaration.class, basePackage + ".C")
				.getDeclaration();
		TypeParameterDeclaration[] typeParamsC = typeC.getTypeParameters();
		assertEquals(1, typeParamsC.length);

		// UNIONS ARE NOW LIMITED TO 3
		// assertEquals(JSweetDefTranslatorConfig.UNION_PACKAGE + ".Union4",
		// getTypeQualifiedName(context, typeParamsC[0].getUpperBound()));
		// assertContains(typeParamsC[0].getUpperBound().getTypeArguments(),
		// "String");
		// assertContains(typeParamsC[0].getUpperBound().getTypeArguments(),
		// "Double");
		// assertContains(typeParamsC[0].getUpperBound().getTypeArguments(),
		// "Boolean");
		// assertContains(typeParamsC[0].getUpperBound().getTypeArguments(),
		// "A");
		assertEquals("any", getTypeQualifiedName(context, typeParamsC[0].getUpperBound()));

		TypeDeclaration typeD = context.findFirstDeclaration(TypeDeclaration.class, basePackage + ".D")
				.getDeclaration();
		TypeReference typeDSuper = typeD.getSuperTypes()[0];
		assertEquals("C", typeDSuper.getName());
		assertEquals(1, typeDSuper.getTypeArguments().length);
		assertEquals(JSweetDefTranslatorConfig.UNION_CLASS_NAME,
				getTypeQualifiedName(context, typeDSuper.getTypeArguments()[0]));
		assertContains(typeDSuper.getTypeArguments()[0].getTypeArguments(), "A");
		assertContains(typeDSuper.getTypeArguments()[0].getTypeArguments(), "Boolean");

		// III. variables
		VariableDeclaration variable = context
				.findFirstDeclaration(VariableDeclaration.class, basePackage + ".Globals.variable").getDeclaration();
		assertEquals(JSweetDefTranslatorConfig.UNION_CLASS_NAME, getTypeQualifiedName(context, variable.getType()));
		assertContains(variable.getType().getTypeArguments(), "String");
		assertContains(variable.getType().getTypeArguments(), "Double");

		VariableDeclaration variableGenerics = context
				.findFirstDeclaration(VariableDeclaration.class, basePackage + ".Globals.variableGenerics")
				.getDeclaration();
		assertEquals("C", variableGenerics.getType().getName());
		assertEquals(1, variableGenerics.getType().getTypeArguments().length);
		assertEquals(JSweetDefTranslatorConfig.UNION_CLASS_NAME,
				getTypeQualifiedName(context, variableGenerics.getType().getTypeArguments()[0]));
		assertContains(variableGenerics.getType().getTypeArguments()[0].getTypeArguments(), "String");
		assertContains(variableGenerics.getType().getTypeArguments()[0].getTypeArguments(), "Double");

		// IV. functions
		List<QualifiedDeclaration<FunctionDeclaration>> functionsWithParams = context
				.findDeclarations(FunctionDeclaration.class, basePackage + ".Globals.foo");
		assertEquals(2, functionsWithParams.size());

		boolean objectSignatureFound = false;
		boolean numberSignatureFound = false;
		for (QualifiedDeclaration<FunctionDeclaration> func : functionsWithParams) {
			if (func.getDeclaration().getParameters().length == 1
					&& func.getDeclaration().getParameters()[0].getType().getName().equals("A")) {
				objectSignatureFound = true;
			}

			if (func.getDeclaration().getParameters().length == 1
					&& func.getDeclaration().getParameters()[0].getType().getName().equals("number")) {
				numberSignatureFound = true;
			}

		}
		assertTrue("expanded object signature not found in " + functionsWithParams, objectSignatureFound);
		assertTrue("expanded number signature not found in " + functionsWithParams, numberSignatureFound);

		List<QualifiedDeclaration<FunctionDeclaration>> functionsWithReturnType = context
				.findDeclarations(FunctionDeclaration.class, basePackage + ".Globals.bar");
		assertEquals(1, functionsWithReturnType.size());

		assertEquals(JSweetDefTranslatorConfig.UNION_PACKAGE + ".Union",
				getTypeQualifiedName(context, functionsWithReturnType.get(0).getDeclaration().getType()));
		assertEquals(2, functionsWithReturnType.get(0).getDeclaration().getType().getTypeArguments().length);
		assertContains(functionsWithReturnType.get(0).getDeclaration().getType().getTypeArguments(), "A");
		assertContains(functionsWithReturnType.get(0).getDeclaration().getType().getTypeArguments(), "Boolean");

	}

	@Test
	public void functionalInterfaceTest() throws FileNotFoundException {
		String testFilePath = "types/functional-interface-test";
		Context context = parseAndTranslateTestFile(testFilePath);
		final String basePackage = "def.types.functionalinterfacetests";
		TypeDeclaration list = context.findFirstDeclaration(TypeDeclaration.class, basePackage + ".List")
				.getDeclaration();
		assertContains(list.getSuperTypes(), "Iterable");
		TypeDeclaration listIterator = context
				.findFirstDeclaration(TypeDeclaration.class, basePackage + ".ListIterator").getDeclaration();
		assertEquals("only one method is allowed", 1, listIterator.getMembers().length);
		assertContains(listIterator.getMembers(), JSweetDefTranslatorConfig.ANONYMOUS_FUNCTION_NAME);
	}

	// TODO : externalize, We shouldn't need to do this at all
	private String getTypeQualifiedName(Context context, TypeReference type) {
		if (type.getDeclaration() != null) {
			return context.getTypeName((TypeDeclaration) type.getDeclaration());
		} else {
			return type.getName();
		}
	}

	@Test
	public void objectTypeTest() throws FileNotFoundException {
		String testFilePath = "types/object-type-test";
		Context context = parseAndTranslateTestFile(testFilePath);

		final String basePackage = "def.types.objecttypetests";

		// I. assert that B inherits ObjectType from A
		TypeDeclaration typeA = context.findFirstDeclaration(TypeDeclaration.class, basePackage + ".A")
				.getDeclaration();
		VariableDeclaration fieldA = typeA.findVariable("field");
		assertNotNull(fieldA.getType());
		assertNotNull(fieldA.getType().getDeclaration());
		assertTrue(fieldA.getType().getDeclaration() instanceof TypeDeclaration);
		assertEquals(basePackage + ".A.Field",
				context.getTypeName((TypeDeclaration) fieldA.getType().getDeclaration()));
		assertContains(typeA.getMembers(), "Field");
		assertContains(typeA.findType("Field").getMembers(), "a");
		assertContains(typeA.findType("Field").getMembers(), "b");

		TypeDeclaration typeB = context.findFirstDeclaration(TypeDeclaration.class, basePackage + ".B")
				.getDeclaration();
		VariableDeclaration fieldB = typeB.findVariable("field");
		assertNotNull(fieldB.getType());
		assertNotNull(fieldB.getType().getDeclaration());
		assertTrue(fieldB.getType().getDeclaration() instanceof TypeDeclaration);
		assertEquals(basePackage + ".A.Field",
				context.getTypeName((TypeDeclaration) fieldB.getType().getDeclaration()));
		assertFalse(contains(typeB.getMembers(), "Field"));

		// II. type param
		TypeDeclaration typeC = context.findFirstDeclaration(TypeDeclaration.class, basePackage + ".C")
				.getDeclaration();
		TypeParameterDeclaration[] typeParamsC = typeC.getTypeParameters();
		assertEquals(1, typeParamsC.length);
		assertContains(typeC.getMembers(), "T");
		assertEquals("T", typeParamsC[0].getUpperBound().getName());
		assertContains(typeC.findType("T").getMembers(), "a");
		assertContains(typeC.findType("T").getMembers(), "b");

		// III. variables
		String globalsClassName = basePackage + ".Globals";
		ModuleDeclaration module = context.findFirstDeclaration(ModuleDeclaration.class, basePackage).getDeclaration();
		VariableDeclaration variable = context
				.findFirstDeclaration(VariableDeclaration.class, globalsClassName + ".variable").getDeclaration();
		assertEquals("Variable", variable.getType().getName());
		assertContains(module.getMembers(), "Variable");
		assertContains(module.findType("Variable").getMembers(), "a");
		assertContains(module.findType("Variable").getMembers(), "b");

		VariableDeclaration variableGenerics = context
				.findFirstDeclaration(VariableDeclaration.class, globalsClassName + ".variableGenerics")
				.getDeclaration();
		assertEquals("C", variableGenerics.getType().getName());
		assertEquals("VariableGenerics", variableGenerics.getType().getTypeArguments()[0].getName());
		assertContains(module.getMembers(), "VariableGenerics");
		assertContains(module.findType("VariableGenerics").getMembers(), "a");
		assertContains(module.findType("VariableGenerics").getMembers(), "b");
		// TODO: VariableGenerics is used as a type argument and should then
		// extends the upper bounds of the type parameters
		// assertContains(module.findType("VariableGenerics").getSuperTypes(),
		// "C.T");

		// / IV. functions
		FunctionDeclaration function = context
				.findFirstDeclaration(FunctionDeclaration.class, globalsClassName + ".foo").getDeclaration();
		assertEquals(1, function.getParameters().length);
		ParameterDeclaration functionParam = function.getParameters()[0];
		assertEquals("P", functionParam.getType().getName());
		assertContains(module.getMembers(), "P");
		assertContains(module.findType("P").getMembers(), "a");
		assertContains(module.findType("P").getMembers(), "b");

		// TODO : test deduplicate object type parameters with same name and
		// members inside a single class

		FunctionDeclaration function2 = context
				.findFirstDeclaration(FunctionDeclaration.class, globalsClassName + ".bar").getDeclaration();
		assertEquals("Bar", function2.getType().getName());
		assertContains(module.getMembers(), "Bar");
		assertContains(module.findType("Bar").getMembers(), "a");
		assertContains(module.findType("Bar").getMembers(), "b");

	}

	private boolean contains(NamedElement[] elements, String name) {
		for (NamedElement decl : elements) {
			if (decl.getName().equals(name)) {
				return true;
			}
		}

		return false;
	}

	private void assertContains(NamedElement[] elements, String name) {
		if (!contains(elements, name)) {
			fail(name + " not found in " + asList(elements));
		}
	}

	@Test
	public void typeTest() throws FileNotFoundException {

		String testFilePath = "types/type-test";
		Context context = parseAndTranslateTestFile(testFilePath);

		TypeDeclaration d = context.findFirstDeclaration(TypeDeclaration.class, "def.types.TestStringTypes")
				.getDeclaration();
		FunctionDeclaration f = d.findFirstFunction("m1");

		Assert.assertEquals("parameter of function m1 should be a string type 'abc'", "def.types.StringTypes.abc",
				f.getParameters()[0].getType().getName());
		f = d.findFirstFunction("m2");
		Assert.assertEquals("parameter of function m2 should be a string type, 'abc'", "def.types.StringTypes.abc",
				f.getParameters()[0].getType().getName());

		f = context.findFirstDeclaration(FunctionDeclaration.class, "def.types.I.items").getDeclaration();
		assertEquals(JSweetDefTranslatorConfig.TUPLE_CLASSES_PACKAGE + "."
				+ JSweetDefTranslatorConfig.TUPLE_CLASSES_PREFIX + "2",
				((ArrayTypeReference) f.getType()).getComponentType().getName());

	}

}
