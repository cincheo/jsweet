package org.jsweet.transpiler.extension;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.jsweet.transpiler.util.Util;

public class AddPrefixToNonPublicMembersAdapter extends PrinterAdapter {

	public AddPrefixToNonPublicMembersAdapter(PrinterAdapter parentAdapter) {
		super(parentAdapter);
		context.addAnnotationManager(new AnnotationManager() {

			@Override
			public Action manageAnnotation(Element element, String annotationType) {
				return "jsweet.lang.Name".equals(annotationType) && isNonPublicMember(element) ? Action.ADD
						: Action.VOID;
			}

			@Override
			public <T> T getAnnotationValue(Element element, String annotationType, String propertyName,
					Class<T> propertyClass, T defaultValue) {
				if ("jsweet.lang.Name".equals(annotationType) && isNonPublicMember(element)) {
					return propertyClass.cast("__" + element.getSimpleName());
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
