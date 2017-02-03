package def.dom;

import def.js.Object;

public class MSTemplatePrinter extends def.js.Object {
    public java.lang.Boolean collate;
    public double copies;
    public java.lang.Boolean currentPage;
    public java.lang.Boolean currentPageAvail;
    public java.lang.Boolean duplex;
    public java.lang.String footer;
    public java.lang.Boolean frameActive;
    public java.lang.Boolean frameActiveEnabled;
    public java.lang.Boolean frameAsShown;
    public java.lang.Boolean framesetDocument;
    public java.lang.String header;
    public java.lang.String headerFooterFont;
    public double marginBottom;
    public double marginLeft;
    public double marginRight;
    public double marginTop;
    public java.lang.String orientation;
    public double pageFrom;
    public double pageHeight;
    public double pageTo;
    public double pageWidth;
    public java.lang.Boolean selectedPages;
    public java.lang.Boolean selection;
    public java.lang.Boolean selectionEnabled;
    public double unprintableBottom;
    public double unprintableLeft;
    public double unprintableRight;
    public double unprintableTop;
    public java.lang.Boolean usePrinterCopyCollate;
    native public MSHeaderFooter createHeaderFooter();
    native public java.lang.Object deviceSupports(java.lang.String property);
    native public java.lang.Boolean ensurePrintDialogDefaults();
    native public java.lang.Object getPageMarginBottom(CSSPageRule pageRule, double pageWidth, double pageHeight);
    native public java.lang.Boolean getPageMarginBottomImportant(CSSPageRule pageRule);
    native public java.lang.Object getPageMarginLeft(CSSPageRule pageRule, double pageWidth, double pageHeight);
    native public java.lang.Boolean getPageMarginLeftImportant(CSSPageRule pageRule);
    native public java.lang.Object getPageMarginRight(CSSPageRule pageRule, double pageWidth, double pageHeight);
    native public java.lang.Boolean getPageMarginRightImportant(CSSPageRule pageRule);
    native public java.lang.Object getPageMarginTop(CSSPageRule pageRule, double pageWidth, double pageHeight);
    native public java.lang.Boolean getPageMarginTopImportant(CSSPageRule pageRule);
    native public void printBlankPage();
    native public java.lang.Boolean printNonNative(java.lang.Object document);
    native public void printNonNativeFrames(java.lang.Object document, java.lang.Boolean activeFrame);
    native public void printPage(HTMLElement element);
    native public java.lang.Boolean showPageSetupDialog();
    native public java.lang.Boolean showPrintDialog();
    native public java.lang.Boolean startDoc(java.lang.String title);
    native public void stopDoc();
    native public void updatePageStatus(double status);
    public static MSTemplatePrinter prototype;
    public MSTemplatePrinter(){}
}

