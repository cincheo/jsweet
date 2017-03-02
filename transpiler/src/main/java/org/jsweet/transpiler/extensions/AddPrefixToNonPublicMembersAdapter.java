package org.jsweet.transpiler.extensions;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.jsweet.transpiler.AnnotationAdapter;
import org.jsweet.transpiler.util.PrinterAdapter;
import org.jsweet.transpiler.util.Util;

public class AddPrefixToNonPublicMembersAdapter extends PrinterAdapter {

	public AddPrefixToNonPublicMembersAdapter(PrinterAdapter parentAdapter) {
		super(parentAdapter);
		context.addAnnotationAdapter(new AnnotationAdapter() {

			@Override
			public AnnotationState getAnnotationState(Element element, String annotationType) {
				return "jsweet.lang.Name".equals(annotationType) && isNonPublicMember(element) ? AnnotationState.ADDED
						: AnnotationState.UNCHANGED;
			}

			@Override
			public String getAnnotationValue(Element element, String annotationType, String propertyName,
					String defaultValue) {
				if ("jsweet.lang.Name".equals(annotationType) && isNonPublicMember(element)) {
					return "__" + element.getSimpleName();
				} else {
					return null;
				}
			}

			private boolean isNonPublicMember(Element element) {
				return (element instanceof VariableElement || element instanceof ExecutableElement)
						&& element.getEnclosingElement() instanceof TypeElement
						&& !element.getModifiers().contains(Modifier.PUBLIC) && Util.isSourceElement(element);

			}
		});
	}

}
