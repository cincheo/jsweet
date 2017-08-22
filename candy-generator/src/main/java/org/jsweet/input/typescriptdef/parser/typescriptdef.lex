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
 
package org.jsweet.input.typescriptdef.parser;

import java_cup.runtime.*;
import org.jsweet.input.typescriptdef.ast.Token;
import java.util.*;

%%

%class TypescriptDefScanner
%unicode
%cup
%line
%column
%state STRING
%state CHAR
%state TYPE_MACRO
%state EOL_COMMENT

%{
	StringBuffer string=new StringBuffer();
	String fileName;
	public void setFileName(String name) {
		fileName=name;
	}
	public String getFileName() {
		return fileName;
	}
	private Symbol symbol(int type) {
		return new Symbol(type,yyline,yycolumn,
		    new Token(type,fileName,yytext(),
		                    yyline+1,yycolumn+1,
		                    yycolumn+1+yytext().length()));
	}
	private Symbol symbol(int type, String value) {
		return new Symbol(type,yyline,yycolumn,
		    new Token(type,fileName,value,
		                    yyline+1,yycolumn+1,
		                    yycolumn+1+yytext().length()));
	}
	private Stack<Symbol> openParens = new Stack<Symbol>();
%}	

LineTerminator= \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace = {LineTerminator} | [ \t\f]
WhiteSpaceChar = [ \t\f]
Comment = {TraditionalComment} | {EndOfLineComment}

StringText=(\\\"|[^\n\"]|\\{WhiteSpaceChar}*\\)*

TraditionalComment   = "/*" ~"*/"
// Comment can be the last line of the file, without line terminator.
//MetaComment          = "///" {WhiteSpaceChar}* "<" {InputCharacter}* {LineTerminator}?
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}
DocumentationComment = "/**" {CommentContent} [^*] "*/"
CommentContent       = ( [^*] | \*+ [^/*] )*
O = [^()]*
FunctionalTypeEnding = {O} ({O} "(" {O} ({O} "(" {O} ({O} "(" {O} ")" {O})* {O} ")" {O})* {O} ")" {O})* {O} ")" {WhiteSpaceChar}* "=>"

Identifier = [A-Za-z0-9_$]*

DecIntegerLiteral = [0-9] | [1-9][0-9]*
HexDigit = [0-9A-Fa-f]
Sign = ("+"|"-")
DecFloatLiteral = {DecIntegerLiteral}\.{DecIntegerLiteral}

KeywordEnding = {WhiteSpaceChar}+ [\'\"A-Za-z0-9_$/]
SpecialKeywordEnding = {WhiteSpaceChar}+ [\'\"\[A-Za-z0-9_$/]
//KeywordEnding = {WhiteSpaceChar}?[ ]?[^:(;]

%% 

<YYINITIAL> {
  /* keywords */
  "interface" / {KeywordEnding}		{ return symbol(sym.INTERFACE); }
  "class" / {KeywordEnding}			{ return symbol(sym.CLASS); }
  //"declare" {WhiteSpace}* "var"				{ return symbol(sym.DECLARE_VAR); }
  //"declare" {WhiteSpace}* "let"				{ return symbol(sym.DECLARE_VAR); }
  //"declare" {WhiteSpace}* "const"			{ return symbol(sym.DECLARE_VAR); }
  "declare" {WhiteSpace}* "module"			{ return symbol(sym.DECLARE_MODULE); }
  "declare" {WhiteSpace}* "namespace"		{ return symbol(sym.DECLARE_MODULE); }
  "declare" {WhiteSpace}* "function"		{ return symbol(sym.DECLARE_FUNCTION); }
  "declare" {WhiteSpace}* "class"			{ return symbol(sym.DECLARE_CLASS); }
  "declare" {WhiteSpace}* "enum"			{ return symbol(sym.DECLARE_ENUM); }
  {LineTerminator} {WhiteSpaceChar}* "type" / {KeywordEnding}			{ yypushback(yylength()); yybegin(TYPE_MACRO); }
  {LineTerminator} {WhiteSpaceChar}* "export" {WhiteSpace}* "type" / {KeywordEnding}			{ yypushback(yylength()); yybegin(TYPE_MACRO); }
  {LineTerminator} {WhiteSpaceChar}* "declare" {WhiteSpace}* "type" / {KeywordEnding}			{ yypushback(yylength()); yybegin(TYPE_MACRO); }
//  {MetaComment}			{ return symbol(sym.REFERENCE); }
  // most keyword can be used as identifiers, so we differentiate them with special ending
  "declare" / {KeywordEnding}		{ return symbol(sym.DECLARE); }
  "var" / {KeywordEnding}			{ return symbol(sym.VAR); }
  "let" / {KeywordEnding}			{ return symbol(sym.VAR); }
  //"const" / {KeywordEnding}		{ return symbol(sym.VAR); }
  "static" / {KeywordEnding}		{ return symbol(sym.STATIC); }
  "abstract" / {KeywordEnding}		{ return symbol(sym.ABSTRACT); }
  "extends" / {KeywordEnding}		{ return symbol(sym.EXTENDS); }
  "extends" / {WhiteSpaceChar}* "{"	{ return symbol(sym.EXTENDS); }
  "extends" / {WhiteSpaceChar}* "(" {FunctionalTypeEnding}		{ return symbol(sym.EXTENDS); }
  "implements" / {KeywordEnding}	{ return symbol(sym.IMPLEMENTS); }
  "import" / {KeywordEnding}		{ return symbol(sym.IMPORT); }
  "import" / {WhiteSpaceChar}* "*"  { return symbol(sym.IMPORT); }
  "export" / {KeywordEnding}		{ return symbol(sym.EXPORT); }
  "export" / {WhiteSpace}* "="		{ return symbol(sym.EXPORT); }
  "export" / {WhiteSpace}* "{"		{ return symbol(sym.EXPORT); }
  "private" / {KeywordEnding}		{ return symbol(sym.PRIVATE); }
  "protected" / {KeywordEnding}		{ return symbol(sym.PROTECTED); }
  "public"  / {KeywordEnding}		{ return symbol(sym.PUBLIC); }
  "function" / {KeywordEnding}		{ return symbol(sym.FUNCTION); }
  "typeof" / {KeywordEnding}		{ return symbol(sym.TYPEOF); }
  "enum" / {KeywordEnding}			{ return symbol(sym.ENUM); }
  "const" / {KeywordEnding}			{ return symbol(sym.CONST); }
  "readonly" / {SpecialKeywordEnding}		{ return symbol(sym.CONST); }
  "is" / {KeywordEnding}			{ return symbol(sym.IS); }
  "as" / {KeywordEnding}			{ return symbol(sym.AS); }
  "from" / {KeywordEnding}			{ return symbol(sym.FROM); }
  "new"		    					{ return symbol(sym.NEW); }

  {Sign}? {DecIntegerLiteral}   { return symbol(sym.INT); }
  [0][xX]{HexDigit}+   			{ return symbol(sym.INT); }
//  {DecFloatLiteral}		{ return symbol(sym.NUM); }
  {Identifier}          { return symbol(sym.IDENTIFIER); }
  ":"                   { return symbol(sym.COL); }
  // hack to allow lf before : (would lead to hard disambiguation problem in the parser)
  {LineTerminator} {WhiteSpaceChar}* ":" { return symbol(sym.COL); }
  ";"                   { return symbol(sym.SEMI); }
  "(" / {FunctionalTypeEnding}	{ return symbol(sym.LPAREN_FUNC); }
  ")" / {WhiteSpaceChar}* "=>"	{ return symbol(sym.RPAREN_FUNC); }
  "("                   { return symbol(sym.LPAREN); }
  ")"                   { return symbol(sym.RPAREN); }
  "{"                   { return symbol(sym.LCPAREN); }
  "}"                   { return symbol(sym.RCPAREN); }
  "[" {WhiteSpaceChar}* "]"                   { return symbol(sym.SQUARE); }
  "["                   { return symbol(sym.LSPAREN); }
  "]"                   { return symbol(sym.RSPAREN); }
  "<"                   { return symbol(sym.LT); }
  ">"                   { return symbol(sym.GT); }
  "<="                  { return symbol(sym.LTE); }
  ">="                  { return symbol(sym.GTE); }
  ","                   { return symbol(sym.COMMA); }
  "."                   { return symbol(sym.DOT); }
  "="                   { return symbol(sym.ASSIGN); }
  "=="                  { return symbol(sym.EQUALS); }
  "!="                  { return symbol(sym.NOTEQUALS); }
  "~"                   { return symbol(sym.MATCHES); }
  "||"                  { return symbol(sym.OROR); }
  "?"                   { return symbol(sym.QUESTION); }
  "&"                   { return symbol(sym.AND); }
  "&&"                  { return symbol(sym.ANDAND); }
  "=>"                  { return symbol(sym.IMPLIES); }
  "!"                   { return symbol(sym.NOT); }
  "+"                   { return symbol(sym.PLUS); }
  "++"                  { return symbol(sym.PLUSPLUS); }
  "-"                   { return symbol(sym.MINUS); }
  "--"                  { return symbol(sym.MINUSMINUS); }
  "/"                   { return symbol(sym.DIV); }
  "*"                   { return symbol(sym.MULT); }
  // hack to allow lf after | (would lead to hard disambiguation problem in the parser)
  "|" {WhiteSpaceChar}* {LineTerminator} { return symbol(sym.TUBE); }
  "|"                   { return symbol(sym.TUBE); }
  // hack to allow lf before | (would lead to hard disambiguation problem in the parser)
  {LineTerminator} {WhiteSpaceChar}* "|" {WhiteSpaceChar}* {LineTerminator}? { return symbol(sym.TUBE); }
  "..."                 { return symbol(sym.DOTDOTDOT); }
  ".."                  { return symbol(sym.DOTDOT); }
  "@"		        	{ return symbol(sym.AT); }
  "'"					{ string.setLength(0); yybegin(CHAR); }
  \"                    { string.setLength(0); yybegin(STRING); }
  {DocumentationComment}	{ return symbol(sym.DOC); }
  {TraditionalComment}  { /*System.err.println("COMMENT: "+yytext());*/ /* ignore */ }
  "//"					{ yybegin(EOL_COMMENT); }
  {LineTerminator}      { /*System.err.println("LF");*/  return symbol(sym.LF); }
  {WhiteSpaceChar}      { /* ignore */ }
  //{WhiteSpace}        { /* ignore */ }
  // any other character fallback (just ignore -- best effort)
  [^﻿] { /* ignore */ }
}

<STRING> {
	\"					{ yybegin(YYINITIAL); 
                          return symbol(sym.IDENTIFIER, 
                          "\""+string.toString()+"\""); }
    [^\n\r\"\\]+        { string.append( yytext() ); }
    \\t                 { string.append('\t'); }
    \\n                 { string.append('\n'); }

    \\r                 { string.append('\r'); }
    \\\"                { string.append('\"'); }
    \\\\                { string.append('\\'); }
    \\                  { string.append('\\'); }
}

<CHAR> {
	\'					{ yybegin(YYINITIAL); 
                          return symbol(sym.IDENTIFIER, 
                          "\""+string.toString()+"\""); }
    [^\n\r\'\\]+        { string.append( yytext() ); }
    \\t                 { string.append('\t'); }
    \\n                 { string.append('\n'); }

    \\r                 { string.append('\r'); }
    \\\'                { string.append('\''); }
    \\\\                { string.append('\\'); }
    \\                  { string.append('\\'); }
}

<TYPE_MACRO> {
  {LineTerminator} ({WhiteSpace}* | {Comment})*  { return symbol(sym.LF); }
  "type"				{ yybegin(YYINITIAL); return symbol(sym.TYPE_MACRO); }
  "export"		        { /* ignore */ }
  "declare"		        { /* ignore */ }
  {WhiteSpaceChar}*		{ /* ignore */ }
}

<EOL_COMMENT> {
  {LineTerminator}      { yybegin(YYINITIAL); yypushback(yylength()); }
  <<EOF>>				{ yybegin(YYINITIAL); }
  [^]					{ /* ignore */ }
}

.|\n { System.out.println("unmatched:"+yytext()); }
