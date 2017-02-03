package def.dom;

import def.js.Object;

public class TextRange extends def.js.Object {
    public double boundingHeight;
    public double boundingLeft;
    public double boundingTop;
    public double boundingWidth;
    public java.lang.String htmlText;
    public double offsetLeft;
    public double offsetTop;
    public java.lang.String text;
    native public void collapse(java.lang.Boolean start);
    native public double compareEndPoints(java.lang.String how, TextRange sourceRange);
    native public TextRange duplicate();
    native public java.lang.Boolean execCommand(java.lang.String cmdID, java.lang.Boolean showUI, java.lang.Object value);
    native public java.lang.Boolean execCommandShowHelp(java.lang.String cmdID);
    native public java.lang.Boolean expand(java.lang.String Unit);
    native public java.lang.Boolean findText(java.lang.String string, double count, double flags);
    native public java.lang.String getBookmark();
    native public ClientRect getBoundingClientRect();
    native public ClientRectList getClientRects();
    native public java.lang.Boolean inRange(TextRange range);
    native public java.lang.Boolean isEqual(TextRange range);
    native public double move(java.lang.String unit, double count);
    native public double moveEnd(java.lang.String unit, double count);
    native public double moveStart(java.lang.String unit, double count);
    native public java.lang.Boolean moveToBookmark(java.lang.String bookmark);
    native public void moveToElementText(Element element);
    native public void moveToPoint(double x, double y);
    native public Element parentElement();
    native public void pasteHTML(java.lang.String html);
    native public java.lang.Boolean queryCommandEnabled(java.lang.String cmdID);
    native public java.lang.Boolean queryCommandIndeterm(java.lang.String cmdID);
    native public java.lang.Boolean queryCommandState(java.lang.String cmdID);
    native public java.lang.Boolean queryCommandSupported(java.lang.String cmdID);
    native public java.lang.String queryCommandText(java.lang.String cmdID);
    native public java.lang.Object queryCommandValue(java.lang.String cmdID);
    native public void scrollIntoView(java.lang.Boolean fStart);
    native public void select();
    native public void setEndPoint(java.lang.String how, TextRange SourceRange);
    public static TextRange prototype;
    public TextRange(){}
    native public void collapse();
    native public java.lang.Boolean execCommand(java.lang.String cmdID, java.lang.Boolean showUI);
    native public java.lang.Boolean execCommand(java.lang.String cmdID);
    native public java.lang.Boolean findText(java.lang.String string, double count);
    native public java.lang.Boolean findText(java.lang.String string);
    native public double move(java.lang.String unit);
    native public double moveEnd(java.lang.String unit);
    native public double moveStart(java.lang.String unit);
    native public void scrollIntoView();
}

