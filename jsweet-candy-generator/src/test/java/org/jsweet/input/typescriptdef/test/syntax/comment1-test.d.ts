
/**********************************
 * Main comment
 **********************************/

interface NodeListOf<TNode extends Node> extends NodeList, T, U {
    /**
     * test
     */   
    length: {
    }
    item(index: number /** Buffer **/): TNode;
    /**
     * test
     */
    /**
     * test
     */
    [index: number]: TNode;
    v1: number;
    v2: number;
    /**
     * test
     *   
    v3: string;
    v4: string;
    /**
     * test
     */
}

enum StatusEnum {
    connected /** Buffer **/, connecting, // test comment
    failed, waiting, offline
}

enum Enum {
    a = 0,
    b = 1
    /**
     * test
     */
}

enum Enum2 {
    a = 0,
    b = 1,
    /**
     * test
     */
}

enum StatusEnum2 {
    /**
     * test2
    connected, 
    /**
     * test3
     */
    connecting,
    failed, waiting, offline,
    /**
     * test4
     */
}

/**
 * Comment I
 */
interface I<U> {
    
    // type inference
    
    spread<U>(onFulfill: Function, onReject?: (reason: any) => U): Promise<U>;
    /*
     // TODO or something like this?
     spread<U, W>(onFulfill: (...values: W[]) => Promise.Thenable<U>, onReject?: (reason: any) => Promise.Thenable<U>): Promise<U>;
     spread<U, W>(onFulfill: (...values: W[]) => Promise.Thenable<U>, onReject?: (reason: any) => U): Promise<U>;
     spread<U, W>(onFulfill: (...values: W[]) => U, onReject?: (reason: any) => Promise.Thenable<U>): Promise<U>;
     spread<U, W>(onFulfill: (...values: W[]) => U, onReject?: (reason: any) => U): Promise<U>;
     */
    /**
     * Same as calling `Promise.all(thisPromise)`. With the exception that if this promise is bound to a value, the returned promise is bound to that value too.
     */
    all<U>(): Promise<U[]>;

    hasBackdrop?: boolean // default: true
    clickOutsideToClose?: boolean; // default: false
    escapeToClose?: boolean; // default: true

    /**
     * Stop recording on all processes. Child processes typically are caching trace data and
     * only rarely flush and send trace data back to the main process. That is because it may
     * be an expensive operation to send the trace data over IPC, and we would like to avoid
     * much runtime overhead of tracing. So, to end tracing, we must asynchronously ask all
     * child processes to flush any pending trace data.
     * @param resultFilePath Trace data will be written into this file if it is not empty,
     * or into a temporary file.
     * @param callback Called once all child processes have acked to the stopRecording request.
     */
    export function stopRecording(resultFilePath: string, callback:
        /**
         * @param filePath A file that contains the traced data.
         */
        (filePath: string) => void
        ): void;    

}

interface /*PhoneGapNavigator extends*/ Navigator {
    /** Comment 1 */
    accelerometer: Accelerometer;
    camera: Camera;
    capture: Capture;
    compass: Compass;
    connection: Connection;
    contacts: Contacts;
    device: Device;
    globalization: Globalization;
    notification: Notification;
    splashscreen: Splashscreen;
}

// end of file comment