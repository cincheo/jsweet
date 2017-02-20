package org.jsweet.transpiler.extensions;

import javax.lang.model.element.Modifier;

import org.jsweet.transpiler.AnnotationAdapter;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.typescript.Java2TypeScriptAdapter;
import org.jsweet.transpiler.util.AbstractPrinterAdapter;
import org.jsweet.transpiler.util.Util;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.VarSymbol;

public class AddPrefixToNonPublicMembersAdapter<C extends JSweetContext> extends Java2TypeScriptAdapter<C> {

	public AddPrefixToNonPublicMembersAdapter(AbstractPrinterAdapter<C> parentAdapter) {
		super(parentAdapter);
		context.addAnnotationAdapter(new AnnotationAdapter() {

			@Override
			public AnnotationState getAnnotationState(Symbol symbol, String annotationType) {
				return "jsweet.lang.Name".equals(annotationType) && isNonPublicMember(symbol) ? AnnotationState.ADDED
						: AnnotationState.UNCHANGED;
			}

			@Override
			public String getAnnotationValue(Symbol symbol, String annotationType, String propertyName,
					String defaultValue) {
				if ("jsweet.lang.Name".equals(annotationType) && isNonPublicMember(symbol)) {
					return "__" + symbol.getSimpleName();
				} else {
					return null;
				}
			}

			private boolean isNonPublicMember(Symbol symbol) {
				return (symbol instanceof VarSymbol || symbol instanceof MethodSymbol)
						&& symbol.getEnclosingElement() instanceof ClassSymbol
						&& !symbol.getModifiers().contains(Modifier.PUBLIC) && Util.isSourceElement(symbol);

			}
		});
	}

}
