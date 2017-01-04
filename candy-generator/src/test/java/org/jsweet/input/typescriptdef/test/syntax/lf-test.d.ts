interface IUtil extends IUtilAnimation, IUtilArc, IObservable<IUtil>, IUtilDomEvent, IUtilDomMisc,
    IUtilDomRequest, IUtilDomStyle, IUtilClass, IUtilMisc {
    ease: IUtilAnimEase;
    array: IUtilArray;
    object: IUtilObject;
    string: IUtilString;
    mkdir: ((path: string, callback?: (err?: NodeJS.ErrnoException) => void) => void)
        | ((path: string, mode: number, callback?: (err?: NodeJS.ErrnoException) => void) => void)
        | ((path: string, mode: string, callback?: (err?: NodeJS.ErrnoException) => void) => void);

    field: string|number|
        any;
    
    field: string|number|
        { a:string; }
        
    getDocument(
        source: string,
        pdfDataRangeTransport?,
        passwordCallback?: (fn: (password: string) => void, reason: string) => string,
        progressCallback?: (progressData: PDFProgressData) => void)
        : PDFPromise<PDFDocumentProxy>;
    
}
