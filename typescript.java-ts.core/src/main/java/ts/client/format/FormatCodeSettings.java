/**
 *  Copyright (c) 2015-2017 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package ts.client.format;

/**
 * 
 * @see https://github.com/Microsoft/TypeScript/blob/master/src/server/protocol.ts
 *
 */
public class FormatCodeSettings extends EditorSettings {

	private Boolean insertSpaceAfterCommaDelimiter;
	private Boolean insertSpaceAfterSemicolonInForStatements;
	private Boolean insertSpaceBeforeAndAfterBinaryOperators;
	private Boolean insertSpaceAfterConstructor;
	private Boolean insertSpaceAfterKeywordsInControlFlowStatements;
	private Boolean insertSpaceAfterFunctionKeywordForAnonymousFunctions;
	private Boolean insertSpaceAfterOpeningAndBeforeClosingNonemptyParenthesis;
	private Boolean insertSpaceAfterOpeningAndBeforeClosingNonemptyBrackets;
	private Boolean insertSpaceAfterOpeningAndBeforeClosingTemplateStringBraces;
	private Boolean insertSpaceAfterOpeningAndBeforeClosingJsxExpressionBraces;
	private Boolean insertSpaceBeforeFunctionParenthesis;
	private Boolean placeOpenBraceOnNewLineForFunctions;
	private Boolean placeOpenBraceOnNewLineForControlBlocks;

	public Boolean getInsertSpaceAfterCommaDelimiter() {
		return insertSpaceAfterCommaDelimiter;
	}

	public void setInsertSpaceAfterCommaDelimiter(Boolean insertSpaceAfterCommaDelimiter) {
		this.insertSpaceAfterCommaDelimiter = insertSpaceAfterCommaDelimiter;
	}

	public Boolean getInsertSpaceAfterSemicolonInForStatements() {
		return insertSpaceAfterSemicolonInForStatements;
	}

	public void setInsertSpaceAfterSemicolonInForStatements(Boolean insertSpaceAfterSemicolonInForStatements) {
		this.insertSpaceAfterSemicolonInForStatements = insertSpaceAfterSemicolonInForStatements;
	}

	public Boolean getInsertSpaceBeforeAndAfterBinaryOperators() {
		return insertSpaceBeforeAndAfterBinaryOperators;
	}

	public void setInsertSpaceBeforeAndAfterBinaryOperators(Boolean insertSpaceBeforeAndAfterBinaryOperators) {
		this.insertSpaceBeforeAndAfterBinaryOperators = insertSpaceBeforeAndAfterBinaryOperators;
	}

	public Boolean getInsertSpaceAfterConstructor() {
		return insertSpaceAfterConstructor;
	}

	public void setInsertSpaceAfterConstructor(Boolean insertSpaceAfterConstructor) {
		this.insertSpaceAfterConstructor = insertSpaceAfterConstructor;
	}

	public Boolean getInsertSpaceAfterKeywordsInControlFlowStatements() {
		return insertSpaceAfterKeywordsInControlFlowStatements;
	}

	public void setInsertSpaceAfterKeywordsInControlFlowStatements(
			Boolean insertSpaceAfterKeywordsInControlFlowStatements) {
		this.insertSpaceAfterKeywordsInControlFlowStatements = insertSpaceAfterKeywordsInControlFlowStatements;
	}

	public Boolean getInsertSpaceAfterFunctionKeywordForAnonymousFunctions() {
		return insertSpaceAfterFunctionKeywordForAnonymousFunctions;
	}

	public void setInsertSpaceAfterFunctionKeywordForAnonymousFunctions(
			Boolean insertSpaceAfterFunctionKeywordForAnonymousFunctions) {
		this.insertSpaceAfterFunctionKeywordForAnonymousFunctions = insertSpaceAfterFunctionKeywordForAnonymousFunctions;
	}

	public Boolean getInsertSpaceAfterOpeningAndBeforeClosingNonemptyParenthesis() {
		return insertSpaceAfterOpeningAndBeforeClosingNonemptyParenthesis;
	}

	public void setInsertSpaceAfterOpeningAndBeforeClosingNonemptyParenthesis(
			Boolean insertSpaceAfterOpeningAndBeforeClosingNonemptyParenthesis) {
		this.insertSpaceAfterOpeningAndBeforeClosingNonemptyParenthesis = insertSpaceAfterOpeningAndBeforeClosingNonemptyParenthesis;
	}

	public Boolean getInsertSpaceAfterOpeningAndBeforeClosingNonemptyBrackets() {
		return insertSpaceAfterOpeningAndBeforeClosingNonemptyBrackets;
	}

	public void setInsertSpaceAfterOpeningAndBeforeClosingNonemptyBrackets(
			Boolean insertSpaceAfterOpeningAndBeforeClosingNonemptyBrackets) {
		this.insertSpaceAfterOpeningAndBeforeClosingNonemptyBrackets = insertSpaceAfterOpeningAndBeforeClosingNonemptyBrackets;
	}

	public Boolean getInsertSpaceAfterOpeningAndBeforeClosingTemplateStringBraces() {
		return insertSpaceAfterOpeningAndBeforeClosingTemplateStringBraces;
	}

	public void setInsertSpaceAfterOpeningAndBeforeClosingTemplateStringBraces(
			Boolean insertSpaceAfterOpeningAndBeforeClosingTemplateStringBraces) {
		this.insertSpaceAfterOpeningAndBeforeClosingTemplateStringBraces = insertSpaceAfterOpeningAndBeforeClosingTemplateStringBraces;
	}

	public Boolean getInsertSpaceAfterOpeningAndBeforeClosingJsxExpressionBraces() {
		return insertSpaceAfterOpeningAndBeforeClosingJsxExpressionBraces;
	}

	public void setInsertSpaceAfterOpeningAndBeforeClosingJsxExpressionBraces(
			Boolean insertSpaceAfterOpeningAndBeforeClosingJsxExpressionBraces) {
		this.insertSpaceAfterOpeningAndBeforeClosingJsxExpressionBraces = insertSpaceAfterOpeningAndBeforeClosingJsxExpressionBraces;
	}

	public Boolean getInsertSpaceBeforeFunctionParenthesis() {
		return insertSpaceBeforeFunctionParenthesis;
	}

	public void setInsertSpaceBeforeFunctionParenthesis(Boolean insertSpaceBeforeFunctionParenthesis) {
		this.insertSpaceBeforeFunctionParenthesis = insertSpaceBeforeFunctionParenthesis;
	}

	public Boolean getPlaceOpenBraceOnNewLineForFunctions() {
		return placeOpenBraceOnNewLineForFunctions;
	}

	public void setPlaceOpenBraceOnNewLineForFunctions(Boolean placeOpenBraceOnNewLineForFunctions) {
		this.placeOpenBraceOnNewLineForFunctions = placeOpenBraceOnNewLineForFunctions;
	}

	public Boolean getPlaceOpenBraceOnNewLineForControlBlocks() {
		return placeOpenBraceOnNewLineForControlBlocks;
	}

	public void setPlaceOpenBraceOnNewLineForControlBlocks(Boolean placeOpenBraceOnNewLineForControlBlocks) {
		this.placeOpenBraceOnNewLineForControlBlocks = placeOpenBraceOnNewLineForControlBlocks;
	}

}
