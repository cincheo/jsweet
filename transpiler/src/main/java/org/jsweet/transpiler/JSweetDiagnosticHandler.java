/* 
 * JSweet transpiler - http://www.jsweet.org
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
package org.jsweet.transpiler;

import java.io.File;
import java.util.Locale;

import javax.tools.Diagnostic.Kind;

import org.apache.log4j.Logger;

import com.sun.tools.javac.util.BasicDiagnosticFormatter;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.JavacMessages;

/**
 * This class takes care of formatting and reporting errors reported by Javac.
 * 
 * @author Renaud Pawlak
 */
public class JSweetDiagnosticHandler extends BasicDiagnosticFormatter {

	/**
	 * The transpilation message/warning/error handler.
	 */
	protected TranspilationHandler transpilationHandler;
	/**
	 * The transpilation context.
	 */
	protected JSweetContext context;
	private final static Logger logger = Logger.getLogger(JSweetTranspiler.class);

	/**
	 * Creates a new diagnostic handler.
	 */
	public JSweetDiagnosticHandler(TranspilationHandler transpilationHandler, JSweetContext context) {
		super(JavacMessages.instance(context));
		this.context = context;
		this.transpilationHandler = transpilationHandler;
	}

	/**
	 * Override this method to tune how JSweet reports the errors.
	 * 
	 * @param diagnostic
	 *            a Java-reported diagnostic (errors, warning, etc.)
	 * @param locale
	 *            a locale
	 */
	protected void reportJavaError(JCDiagnostic diagnostic, Locale locale) {
		transpilationHandler.report(
				JSweetProblem.INTERNAL_JAVA_ERROR, new SourcePosition(new File(diagnostic.getSource().getName()), null,
						(int) diagnostic.getLineNumber(), (int) diagnostic.getColumnNumber()),
				diagnostic.getMessage(locale));
	}

	@Override
	public String format(JCDiagnostic diagnostic, Locale locale) {
		if (diagnostic.getKind() == Kind.ERROR) {
			if (!(context.options.isIgnoreJavaFileNameError()
					&& "compiler.err.class.public.should.be.in.file".equals(diagnostic.getCode()))) {
				reportJavaError(diagnostic, locale);
			}
		}
		switch (diagnostic.getKind()) {
		case ERROR:
			logger.error(diagnostic);
			break;
		case WARNING:
		case MANDATORY_WARNING:
			logger.debug(diagnostic);
			break;
		case NOTE:
		case OTHER:
		default:
			logger.trace(diagnostic);
			break;
		}
		if (diagnostic.getSource() != null) {
			return diagnostic.getMessage(locale) + " at " + diagnostic.getSource().getName() + "("
					+ diagnostic.getLineNumber() + ")";
		} else {
			return diagnostic.getMessage(locale);
		}
	}

}
