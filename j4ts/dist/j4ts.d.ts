declare namespace java.beans {
    /**
     * General-purpose beans control methods. GWT only supports a limited subset of these methods. Only
     * the documented methods are available.
     */
    class Beans {
        /**
         * @return <code>true</code> if we are running in the design time mode.
         */
        static isDesignTime(): boolean;
    }
}
declare namespace java.io {
    /**
     * An {@code AutoCloseable} whose close method may throw an {@link IOException}.
     */
    interface Closeable extends java.lang.AutoCloseable {
        /**
         * Closes the object and release any system resources it holds.
         *
         * <p>Although only the first call has any effect, it is safe to call close
         * multiple times on the same object. This is more lenient than the
         * overridden {@code AutoCloseable.close()}, which may be called at most
         * once.
         */
        close(): any;
    }
}
declare namespace java.io {
    /**
     * Defines an interface for classes that can (or need to) be flushed, typically
     * before some output processing is considered to be finished and the object
     * gets closed.
     */
    interface Flushable {
        /**
         * Flushes the object by writing out any buffered data to the underlying
         * output.
         *
         * @throws IOException
         * if there are any issues writing the data.
         */
        flush(): any;
    }
}
declare namespace java.io {
    /**
     * A readable source of bytes.
     *
     * <p>Most clients will use input streams that read data from the file system
     * ({@link FileInputStream}), the network ({@link java.net.Socket#getInputStream()}/{@link
     * java.net.HttpURLConnection#getInputStream()}), or from an in-memory byte
     * array ({@link ByteArrayInputStream}).
     *
     * <p>Use {@link InputStreamReader} to adapt a byte stream like this one into a
     * character stream.
     *
     * <p>Most clients should wrap their input stream with {@link
     * BufferedInputStream}. Callers that do only bulk reads may omit buffering.
     *
     * <p>Some implementations support marking a position in the input stream and
     * resetting back to this position later. Implementations that don't return
     * false from {@link #markSupported()} and throw an {@link IOException} when
     * {@link #reset()} is called.
     *
     * <h3>Subclassing InputStream</h3>
     * Subclasses that decorate another input stream should consider subclassing
     * {@link FilterInputStream}, which delegates all calls to the source input
     * stream.
     *
     * <p>All input stream subclasses should override <strong>both</strong> {@link
     * #read() read()} and {@link #read(byte[],int,int) read(byte[],int,int)}. The
     * three argument overload is necessary for bulk access to the data. This is
     * much more efficient than byte-by-byte access.
     *
     * @see OutputStream
     */
    abstract class InputStream implements java.io.Closeable {
        /**
         * Size of the temporary buffer used when skipping bytes with {@link skip(long)}.
         */
        static MAX_SKIP_BUFFER_SIZE: number;
        /**
         * This constructor does nothing. It is provided for signature
         * compatibility.
         */
        constructor();
        /**
         * Returns an estimated number of bytes that can be read or skipped without blocking for more
         * input.
         *
         * <p>Note that this method provides such a weak guarantee that it is not very useful in
         * practice.
         *
         * <p>Firstly, the guarantee is "without blocking for more input" rather than "without
         * blocking": a read may still block waiting for I/O to complete&nbsp;&mdash; the guarantee is
         * merely that it won't have to wait indefinitely for data to be written. The result of this
         * method should not be used as a license to do I/O on a thread that shouldn't be blocked.
         *
         * <p>Secondly, the result is a
         * conservative estimate and may be significantly smaller than the actual number of bytes
         * available. In particular, an implementation that always returns 0 would be correct.
         * In general, callers should only use this method if they'd be satisfied with
         * treating the result as a boolean yes or no answer to the question "is there definitely
         * data ready?".
         *
         * <p>Thirdly, the fact that a given number of bytes is "available" does not guarantee that a
         * read or skip will actually read or skip that many bytes: they may read or skip fewer.
         *
         * <p>It is particularly important to realize that you <i>must not</i> use this method to
         * size a container and assume that you can read the entirety of the stream without needing
         * to resize the container. Such callers should probably write everything they read to a
         * {@link ByteArrayOutputStream} and convert that to a byte array. Alternatively, if you're
         * reading from a file, {@link File#length} returns the current length of the file (though
         * assuming the file's length can't change may be incorrect, reading a file is inherently
         * racy).
         *
         * <p>The default implementation of this method in {@code InputStream} always returns 0.
         * Subclasses should override this method if they are able to indicate the number of bytes
         * available.
         *
         * @return the estimated number of bytes available
         * @throws IOException if this stream is closed or an error occurs
         */
        available(): number;
        /**
         * Closes this stream. Concrete implementations of this class should free
         * any resources during close. This implementation does nothing.
         *
         * @throws IOException
         * if an error occurs while closing this stream.
         */
        close(): void;
        /**
         * Sets a mark position in this InputStream. The parameter {@code readlimit}
         * indicates how many bytes can be read before the mark is invalidated.
         * Sending {@code reset()} will reposition the stream back to the marked
         * position provided {@code readLimit} has not been surpassed.
         * <p>
         * This default implementation does nothing and concrete subclasses must
         * provide their own implementation.
         *
         * @param readlimit
         * the number of bytes that can be read from this stream before
         * the mark is invalidated.
         * @see #markSupported()
         * @see #reset()
         */
        mark(readlimit: number): void;
        /**
         * Indicates whether this stream supports the {@code mark()} and
         * {@code reset()} methods. The default implementation returns {@code false}.
         *
         * @return always {@code false}.
         * @see #mark(int)
         * @see #reset()
         */
        markSupported(): boolean;
        /**
         * Reads a single byte from this stream and returns it as an integer in the
         * range from 0 to 255. Returns -1 if the end of the stream has been
         * reached. Blocks until one byte has been read, the end of the source
         * stream is detected or an exception is thrown.
         *
         * @throws IOException
         * if the stream is closed or another IOException occurs.
         */
        read$(): number;
        /**
         * Equivalent to {@code read(buffer, 0, buffer.length)}.
         */
        read$byte_A(buffer: number[]): number;
        /**
         * Reads up to {@code byteCount} bytes from this stream and stores them in
         * the byte array {@code buffer} starting at {@code byteOffset}.
         * Returns the number of bytes actually read or -1 if the end of the stream
         * has been reached.
         *
         * @throws IndexOutOfBoundsException
         * if {@code byteOffset < 0 || byteCount < 0 || byteOffset + byteCount > buffer.length}.
         * @throws IOException
         * if the stream is closed or another IOException occurs.
         */
        read(buffer?: any, byteOffset?: any, byteCount?: any): any;
        /**
         * Resets this stream to the last marked location. Throws an
         * {@code IOException} if the number of bytes read since the mark has been
         * set is greater than the limit provided to {@code mark}, or if no mark
         * has been set.
         * <p>
         * This implementation always throws an {@code IOException} and concrete
         * subclasses should provide the proper implementation.
         *
         * @throws IOException
         * if this stream is closed or another IOException occurs.
         */
        reset(): void;
        /**
         * Skips at most {@code byteCount} bytes in this stream. The number of actual
         * bytes skipped may be anywhere between 0 and {@code byteCount}. If
         * {@code byteCount} is negative, this method does nothing and returns 0, but
         * some subclasses may throw.
         *
         * <p>Note the "at most" in the description of this method: this method may
         * choose to skip fewer bytes than requested. Callers should <i>always</i>
         * check the return value.
         *
         * <p>This default implementation reads bytes into a temporary buffer. Concrete
         * subclasses should provide their own implementation.
         *
         * @return the number of bytes actually skipped.
         * @throws IOException if this stream is closed or another IOException
         * occurs.
         */
        skip(byteCount: number): number;
    }
}
declare namespace java.io {
    /**
     * Provides a series of utilities to be reused between IO classes.
     *
     * TODO(chehayeb): move these checks to InternalPreconditions.
     */
    class IOUtils {
        /**
         * Validates the offset and the byte count for the given array of bytes.
         *
         * @param buffer Array of bytes to be checked.
         * @param byteOffset Starting offset in the array.
         * @param byteCount Total number of bytes to be accessed.
         * @throws NullPointerException if the given reference to the buffer is null.
         * @throws IndexOutOfBoundsException if {@code byteOffset} is negative, {@code byteCount} is
         * negative or their sum exceeds the buffer length.
         */
        static checkOffsetAndCount(buffer?: any, byteOffset?: any, byteCount?: any): any;
        /**
         * Validates the offset and the byte count for the given array of characters.
         *
         * @param buffer Array of characters to be checked.
         * @param charOffset Starting offset in the array.
         * @param charCount Total number of characters to be accessed.
         * @throws NullPointerException if the given reference to the buffer is null.
         * @throws IndexOutOfBoundsException if {@code charOffset} is negative, {@code charCount} is
         * negative or their sum exceeds the buffer length.
         */
        static checkOffsetAndCount$char_A$int$int(buffer: string[], charOffset: number, charCount: number): void;
        /**
         * Validates the offset and the byte count for the given array length.
         *
         * @param length Length of the array to be checked.
         * @param offset Starting offset in the array.
         * @param count Total number of elements to be accessed.
         * @throws IndexOutOfBoundsException if {@code offset} is negative, {@code count} is negative or
         * their sum exceeds the given {@code length}.
         */
        private static checkOffsetAndCount$int$int$int(length, offset, count);
        constructor();
    }
}
declare namespace java.io {
    /**
     * A writable sink for bytes.
     *
     * <p>Most clients will use output streams that write data to the file system
     * ({@link FileOutputStream}), the network ({@link java.net.Socket#getOutputStream()}/{@link
     * java.net.HttpURLConnection#getOutputStream()}), or to an in-memory byte array
     * ({@link ByteArrayOutputStream}).
     *
     * <p>Use {@link OutputStreamWriter} to adapt a byte stream like this one into a
     * character stream.
     *
     * <p>Most clients should wrap their output stream with {@link
     * BufferedOutputStream}. Callers that do only bulk writes may omit buffering.
     *
     * <h3>Subclassing OutputStream</h3>
     * Subclasses that decorate another output stream should consider subclassing
     * {@link FilterOutputStream}, which delegates all calls to the target output
     * stream.
     *
     * <p>All output stream subclasses should override <strong>both</strong> {@link
     * #write(int)} and {@link #write(byte[],int,int) write(byte[],int,int)}. The
     * three argument overload is necessary for bulk access to the data. This is
     * much more efficient than byte-by-byte access.
     *
     * @see InputStream
     *
     * <p>The implementation provided by this class behaves as described in the Java
     * API documentation except for {@link write(int)} which throws an exception of
     * type {@link java.lang.UnsupportedOperationException} instead of being
     * abstract.
     */
    abstract class OutputStream implements java.io.Closeable, java.io.Flushable {
        /**
         * Default constructor.
         */
        constructor();
        /**
         * Closes this stream. Implementations of this method should free any
         * resources used by the stream. This implementation does nothing.
         *
         * @throws IOException
         * if an error occurs while closing this stream.
         */
        close(): void;
        /**
         * Flushes this stream. Implementations of this method should ensure that
         * any buffered data is written out. This implementation does nothing.
         *
         * @throws IOException
         * if an error occurs while flushing this stream.
         */
        flush(): void;
        /**
         * Equivalent to {@code write(buffer, 0, buffer.length)}.
         */
        write$byte_A(buffer: number[]): void;
        /**
         * Writes {@code count} bytes from the byte array {@code buffer} starting at
         * position {@code offset} to this stream.
         *
         * @param buffer
         * the buffer to be written.
         * @param offset
         * the start position in {@code buffer} from where to get bytes.
         * @param count
         * the number of bytes from {@code buffer} to write to this
         * stream.
         * @throws IOException
         * if an error occurs while writing to this stream.
         * @throws IndexOutOfBoundsException
         * if {@code offset < 0} or {@code count < 0}, or if
         * {@code offset + count} is bigger than the length of
         * {@code buffer}.
         */
        write(buffer?: any, offset?: any, count?: any): any;
        /**
         * Writes a single byte to this stream. Only the least significant byte of
         * the integer {@code oneByte} is written to the stream.
         *
         * @param oneByte
         * the byte to be written.
         * @throws IOException
         * if an error occurs while writing to this stream.
         */
        write$int(oneByte: number): void;
    }
}
declare namespace java.io {
    /**
     * JSweet implementation.
     */
    abstract class Reader implements java.io.Closeable {
        lock: any;
        constructor(lock?: any);
        read$(): number;
        read$char_A(cbuf: string[]): number;
        read(cbuf?: any, off?: any, len?: any): any;
        /**
         * Maximum skip-buffer size
         */
        static maxSkipBufferSize: number;
        /**
         * Skip buffer, null until allocated
         */
        private skipBuffer;
        skip(n: number): number;
        ready(): boolean;
        markSupported(): boolean;
        mark(readAheadLimit: number): void;
        reset(): void;
        abstract close(): any;
    }
}
declare namespace java.io {
    /**
     * Provided for interoperability; RPC treats this interface synonymously with
     * {@link com.google.gwt.user.client.rpc.IsSerializable IsSerializable}.
     * The Java serialization protocol is explicitly not supported.
     */
    interface Serializable {
    }
}
declare namespace java.io {
    /**
     * JSweet implementation.
     */
    abstract class Writer implements java.lang.Appendable, java.io.Closeable, java.io.Flushable {
        private writeBuffer;
        static WRITE_BUFFER_SIZE: number;
        lock: any;
        constructor(lock?: any);
        write$int(c: number): void;
        write$char_A(cbuf: string[]): void;
        write$char_A$int$int(cbuf: string[], off: number, len: number): void;
        write$java_lang_String(str: string): void;
        write(str?: any, off?: any, len?: any): any;
        append$java_lang_CharSequence(csq: string): Writer;
        append(csq?: any, start?: any, end?: any): any;
        append$char(c: string): Writer;
        abstract flush(): any;
        abstract close(): any;
    }
}
declare namespace java.lang {
    /**
     * A base class to share implementation between {@link StringBuffer} and {@link StringBuilder}.
     * <p>
     * Most methods will give expected performance results. Exception is {@link #setCharAt(int, char)},
     * which is O(n), and thus should not be used many times on the same <code>StringBuffer</code>.
     */
    abstract class AbstractStringBuilder {
        string: string;
        constructor(string: string);
        length(): number;
        setLength(newLength: number): void;
        capacity(): number;
        ensureCapacity(ignoredCapacity: number): void;
        trimToSize(): void;
        charAt(index: number): string;
        getChars(srcStart: number, srcEnd: number, dst: string[], dstStart: number): void;
        /**
         * Warning! This method is <b>much</b> slower than the JRE implementation. If you need to do
         * character level manipulation, you are strongly advised to use a char[] directly.
         */
        setCharAt(index: number, x: string): void;
        subSequence(start: number, end: number): string;
        substring$int(begin: number): string;
        substring(begin?: any, end?: any): any;
        indexOf$java_lang_String(x: string): number;
        indexOf(x?: any, start?: any): any;
        lastIndexOf$java_lang_String(s: string): number;
        lastIndexOf(s?: any, start?: any): any;
        toString(): string;
        append0(x: string, start: number, end: number): void;
        appendCodePoint0(x: number): void;
        replace0(start: number, end: number, toInsert: string): void;
        reverse0(): void;
        private static swap(buffer, f, s);
    }
}
declare namespace java.lang.annotation {
    /**
     * Base interface for all annotation types <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/annotation/Annotation.html">[Sun
     * docs]</a>.
     */
    interface Annotation {
        annotationType(): any;
        equals(obj: any): boolean;
        hashCode(): number;
        toString(): string;
    }
}
declare namespace java.lang.annotation {
    /**
     * Indicates the annotation parser determined the annotation was malformed when
     * reading from the class file <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/annotation/AnnotationFormatError.html">[Sun
     * docs]</a>.
     */
    class AnnotationFormatError extends Error {
        constructor();
    }
}
declare namespace java.lang.annotation {
}
declare namespace java.lang.annotation {
    /**
     * Enumerates types of declared elements in a Java program <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/annotation/ElementType.html">[Sun
     * docs]</a>.
     */
    enum ElementType {
        ANNOTATION_TYPE = 0,
        CONSTRUCTOR = 1,
        FIELD = 2,
        LOCAL_VARIABLE = 3,
        METHOD = 4,
        PACKAGE = 5,
        PARAMETER = 6,
        TYPE = 7,
    }
}
declare namespace java.lang.annotation {
}
declare namespace java.lang.annotation {
}
declare namespace java.lang.annotation {
    /**
     * Enumerates annotation retention policies <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/annotation/RetentionPolicy.html">[Sun
     * docs]</a>.
     */
    enum RetentionPolicy {
        CLASS = 0,
        RUNTIME = 1,
        SOURCE = 2,
    }
}
declare namespace java.lang.annotation {
}
declare namespace java.lang {
    /**
     * See <a
     * href="http://java.sun.com/javase/6/docs/api/java/lang/Appendable.html">the
     * official Java API doc</a> for details.
     */
    interface Appendable {
        append(x?: any, start?: any, len?: any): any;
    }
}
declare namespace java.lang {
    /**
     * Represents an error caused by an assertion failure.
     */
    class AssertionError extends Error {
        constructor(message?: any, cause?: any);
    }
}
declare namespace java.lang {
    /**
     * See <a
     * href="http://docs.oracle.com/javase/7/docs/api/java/lang/AutoCloseable.html">the
     * official Java API doc</a> for details.
     */
    interface AutoCloseable {
        /**
         * Closes this resource.
         */
        close(): any;
    }
}
declare namespace java.lang {
    /**
     * Abstracts the notion of a sequence of characters.
     */
    interface CharSequence {
        charAt(index: number): string;
        length(): number;
        subSequence(start: number, end: number): string;
        toString(): string;
    }
}
declare namespace java.lang {
    /**
     * Generally unsupported. This class is provided so that the GWT compiler can
     * choke down class literal references.
     * <p>
     * NOTE: The code in this class is very sensitive and should keep its
     * dependencies upon other classes to a minimum.
     *
     * @param <T>
     * the type of the object
     */
    class Class<T> implements java.lang.reflect.Type {
        static constructors: Array<Function>;
        static constructors_$LI$(): Array<Function>;
        static classes: Array<any>;
        static classes_$LI$(): Array<any>;
        static getConstructorForClass(clazz: any): Function;
        static getClassForConstructor(constructor: Function): any;
        static mapConstructorToClass(constructor: Function, clazz: any): void;
        static PRIMITIVE: number;
        static INTERFACE: number;
        static ARRAY: number;
        static ENUM: number;
        /**
         * Create a Class object for an array.
         * <p>
         *
         * Arrays are not registered in the prototype table and get the class
         * literal explicitly at construction.
         * <p>
         */
        private static getClassLiteralForArray<T>(leafClass, dimensions);
        private createClassLiteralForArray(dimensions);
        /**
         * Create a Class object for a class.
         *
         * @skip
         */
        static createForClass<T>(packageName: string, compoundClassName: string, typeId: string, superclass: any): any;
        /**
         * Create a Class object for an enum.
         *
         * @skip
         */
        static createForEnum<T>(packageName: string, compoundClassName: string, typeId: string, superclass: any, enumConstantsFunc: Function, enumValueOfFunc: Function): any;
        /**
         * Create a Class object for an interface.
         *
         * @skip
         */
        static createForInterface<T>(packageName: string, compoundClassName: string): any;
        /**
         * Create a Class object for a primitive.
         *
         * @skip
         */
        static createForPrimitive(className: string, primitiveTypeId: string): any;
        /**
         * Used by {@link WebModePayloadSink} to create uninitialized instances.
         */
        static getPrototypeForClass(clazz: any): any;
        /**
         * Creates the class object for a type and initiliazes its fields.
         */
        private static createClassObject<T>(packageName, compoundClassName, typeId);
        /**
         * Initiliazes {@code clazz} names from metadata.
         * <p>
         * Written in JSNI to minimize dependencies (on String.+).
         */
        private static initializeNames(clazz);
        /**
         * Sets the class object for primitives.
         * <p>
         * Written in JSNI to minimize dependencies (on (String)+).
         */
        static synthesizePrimitiveNamesFromTypeId(clazz: any, primitiveTypeId: Object): void;
        enumValueOfFunc: Function;
        modifiers: number;
        private componentType;
        private enumConstantsFunc;
        private enumSuperclass;
        private superclass;
        private simpleName;
        private typeName;
        private canonicalName;
        private packageName;
        private compoundName;
        private typeId;
        private arrayLiterals;
        private sequentialId;
        static nextSequentialId: number;
        /**
         * Not publicly instantiable.
         *
         * @skip
         */
        constructor();
        desiredAssertionStatus(): boolean;
        private ensureNamesAreInitialized();
        getCanonicalName(): string;
        getComponentType(): any;
        getEnumConstants(): T[];
        getName(): string;
        getSimpleName(): string;
        getSuperclass(): any;
        isArray(): boolean;
        isEnum(): boolean;
        isInterface(): boolean;
        isPrimitive(): boolean;
        toString(): string;
        /**
         * Used by Enum to allow getSuperclass() to be pruned.
         */
        getEnumSuperclass(): any;
    }
}
declare namespace java.lang {
    /**
     * Indicates that a class implements <code>clone()</code>.
     */
    interface Cloneable {
    }
}
declare namespace java.lang {
    /**
     * An interface used a basis for implementing custom ordering. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/Comparable.html">[Sun
     * docs]</a>
     *
     * @param <T> the type to compare to.
     */
    interface Comparable<T> {
        compareTo(other?: any): any;
    }
}
declare namespace java.lang {
}
declare namespace java.lang {
    /**
     * The first-class representation of an enumeration.
     *
     * @param <E>
     */
    abstract class Enum<E extends java.lang.Enum<E>> implements java.lang.Comparable<E>, java.io.Serializable {
        static valueOf<T extends java.lang.Enum<T>>(enumType?: any, name?: any): any;
        static createValueOfMap<T extends java.lang.Enum<T>>(enumConstants: T[]): Object;
        static valueOf$def_js_Object$java_lang_String<T extends java.lang.Enum<T>>(map: Object, name: string): T;
        private static get0<T>(map, name);
        private static invokeValueOf<T>(enumValueOfFunc, name);
        private static put0<T>(map, name, value);
        private __name;
        private __ordinal;
        constructor(name: string, ordinal: number);
        compareTo(other?: any): any;
        getDeclaringClass(): any;
        name(): string;
        ordinal(): number;
        toString(): string;
    }
}
declare namespace java.lang {
    /**
     * See <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/Exception.html">the
     * official Java API doc</a> for details.
     */
    class Exception extends Error {
        constructor(message?: any, cause?: any, enableSuppression?: any, writableStackTrace?: any);
    }
}
declare namespace java.lang {
}
declare namespace java.lang {
    /**
     * Allows an instance of a class implementing this interface to be used in the
     * foreach statement.
     * See <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/Iterable.html">
     * the official Java API doc</a> for details.
     *
     * @param <T> type of returned iterator
     */
    interface Iterable<T> {
        iterator(): java.util.Iterator<T>;
        forEach(action: (p1: any) => void): any;
    }
}
declare namespace java.lang {
}
declare namespace java.lang.ref {
    /**
     * This implements the reference API in a minimal way. In JavaScript, there is
     * no control over the reference and the GC. So this implementation's only
     * purpose is for compilation.
     */
    abstract class Reference<T> {
        private referent;
        constructor(referent: T);
        get(): T;
        clear(): void;
    }
}
declare namespace java.lang.reflect {
    /**
     * This interface makes {@link java.lang.reflect.Type} available to GWT clients.
     *
     * @see java.lang.reflect.Type
     */
    interface Type {
    }
}
declare namespace java.lang {
    /**
     * Encapsulates an action for later execution. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/Runnable.html">[Sun
     * docs]</a>
     *
     * <p>
     * This interface is provided only for JRE compatibility. GWT does not support
     * multithreading.
     * </p>
     */
    interface Runnable {
        run(): any;
    }
}
declare namespace java.lang {
}
declare namespace java.lang {
    /**
     * Included for hosted mode source compatibility. Partially implemented
     *
     * @skip
     */
    class StackTraceElement implements java.io.Serializable {
        private className;
        private fileName;
        private lineNumber;
        private methodName;
        constructor(className?: any, methodName?: any, fileName?: any, lineNumber?: any);
        getClassName(): string;
        getFileName(): string;
        getLineNumber(): number;
        getMethodName(): string;
        equals(other: any): boolean;
        hashCode(): number;
        toString(): string;
    }
}
declare namespace java.lang {
}
declare namespace java.lang {
    /**
     * Thrown to indicate that the Java Virtual Machine is broken or has
     * run out of resources necessary for it to continue operating.
     *
     *
     * @author  Frank Yellin
     * @since   JDK1.0
     */
    abstract class VirtualMachineError extends Error {
        static serialVersionUID: number;
        /**
         * Constructs a {@code VirtualMachineError} with the specified
         * detail message and cause.  <p>Note that the detail message
         * associated with {@code cause} is <i>not</i> automatically
         * incorporated in this error's detail message.
         *
         * @param  message the detail message (which is saved for later retrieval
         * by the {@link #getMessage()} method).
         * @param  cause the cause (which is saved for later retrieval by the
         * {@link #getCause()} method).  (A {@code null} value is
         * permitted, and indicates that the cause is nonexistent or
         * unknown.)
         * @since  1.8
         */
        constructor(message?: any, cause?: any);
    }
}
declare namespace java.lang {
    /**
     * For JRE compatibility.
     */
    class Void {
        /**
         * Not instantiable.
         */
        constructor();
    }
}
declare namespace java.nio.charset {
    /**
     * A minimal emulation of {@link Charset}.
     */
    abstract class Charset implements java.lang.Comparable<Charset> {
        static availableCharsets(): java.util.SortedMap<string, Charset>;
        static forName(charsetName: string): Charset;
        static createLegalCharsetNameRegex(): RegExp;
        private __name;
        constructor(name: string, aliasesIgnored: string[]);
        name(): string;
        compareTo(that?: any): any;
        hashCode(): number;
        equals(o: any): boolean;
        toString(): string;
    }
    namespace Charset {
        class AvailableCharsets {
            static CHARSETS: java.util.SortedMap<string, java.nio.charset.Charset>;
        }
    }
}
declare namespace java.security {
    /**
     * Message Digest Service Provider Interface - <a
     * href="http://java.sun.com/j2se/1.4.2/docs/api/java/security/MessageDigestSpi.html">[Sun's
     * docs]</a>.
     */
    abstract class MessageDigestSpi {
        engineDigest$(): number[];
        engineDigest(buf?: any, offset?: any, len?: any): any;
        engineGetDigestLength(): number;
        abstract engineReset(): any;
        engineUpdate$byte(input: number): void;
        engineUpdate(input?: any, offset?: any, len?: any): any;
    }
}
declare namespace java.util {
    /**
     * Skeletal implementation of the Collection interface. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/AbstractCollection.html">[Sun
     * docs]</a>
     *
     * @param <E> the element type.
     */
    abstract class AbstractCollection<E> implements java.util.Collection<E> {
        forEach(action: (p1: any) => void): void;
        constructor();
        add(index?: any, element?: any): any;
        add$java_lang_Object(o: E): boolean;
        addAll(index?: any, c?: any): any;
        addAll$java_util_Collection(c: java.util.Collection<any>): boolean;
        clear(): void;
        contains(o: any): boolean;
        containsAll(c: java.util.Collection<any>): boolean;
        isEmpty(): boolean;
        abstract iterator(): java.util.Iterator<E>;
        remove(index?: any): any;
        remove$java_lang_Object(o: any): boolean;
        removeAll(c: java.util.Collection<any>): boolean;
        retainAll(c: java.util.Collection<any>): boolean;
        abstract size(): number;
        toArray$(): any[];
        toArray<T>(a?: any): any;
        toString(): string;
        private advanceToFind(o, remove);
    }
}
declare namespace java.util {
    /**
     * Basic {@link Map.Entry} implementation that implements hashCode, equals, and
     * toString.
     */
    abstract class AbstractMapEntry<K, V> implements java.util.Map.Entry<K, V> {
        abstract getKey(): any;
        abstract getValue(): any;
        abstract setValue(value: any): any;
        equals(other: any): boolean;
        /**
         * Calculate the hash code using Sun's specified algorithm.
         */
        hashCode(): number;
        toString(): string;
        constructor();
    }
}
declare namespace java.util {
    /**
     * Incomplete and naive implementation of the BitSet utility (mainly for
     * compatibility/compilation purpose).
     *
     * @author Renaud Pawlak
     */
    class BitSet implements java.lang.Cloneable, java.io.Serializable {
        private bits;
        constructor(nbits?: any);
        static valueOf(longs: number[]): BitSet;
        flip$int(bitIndex: number): void;
        flip(fromIndex?: any, toIndex?: any): any;
        set$int(bitIndex: number): void;
        set$int$boolean(bitIndex: number, value: boolean): void;
        set$int$int(fromIndex: number, toIndex: number): void;
        set(fromIndex?: any, toIndex?: any, value?: any): any;
        clear$int(bitIndex: number): void;
        clear(fromIndex?: any, toIndex?: any): any;
        clear$(): void;
        get$int(bitIndex: number): boolean;
        get(fromIndex?: any, toIndex?: any): any;
        length(): number;
        isEmpty(): boolean;
        cardinality(): number;
        and(set: BitSet): void;
        or(set: BitSet): void;
        xor(set: BitSet): void;
        andNot(set: BitSet): void;
        size(): number;
        equals(obj: any): boolean;
        clone(): any;
    }
}
declare namespace java.util {
    /**
     * General-purpose interface for storing collections of objects. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/Collection.html">[Sun
     * docs]</a>
     *
     * @param <E> element type
     */
    interface Collection<E> extends java.lang.Iterable<E> {
        add(index?: any, element?: any): any;
        addAll(index?: any, c?: any): any;
        clear(): any;
        contains(o: any): boolean;
        containsAll(c: Collection<any>): boolean;
        equals(o: any): boolean;
        hashCode(): number;
        isEmpty(): boolean;
        iterator(): java.util.Iterator<E>;
        remove(index?: any): any;
        removeAll(c: Collection<any>): boolean;
        retainAll(c: Collection<any>): boolean;
        size(): number;
        toArray<T>(a?: any): any;
    }
}
declare namespace java.util {
    /**
     * An interface used a basis for implementing custom ordering. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/Comparator.html">[Sun
     * docs]</a>
     *
     * @param <T> the type to be compared.
     */
    interface Comparator<T> {
        compare(o1?: any, o2?: any): any;
        equals(other: any): boolean;
    }
}
declare namespace java.util {
    class Comparators {
        /**
         * Compares two Objects according to their <i>natural ordering</i>.
         *
         * @see java.lang.Comparable
         */
        static NATURAL: java.util.Comparator<any>;
        static NATURAL_$LI$(): java.util.Comparator<any>;
        /**
         * Returns the natural Comparator.
         * <p>
         * Example:
         *
         * <pre>Comparator&lt;String&gt; compareString = Comparators.natural()</pre>
         *
         * @return the natural Comparator
         */
        static natural<T>(): java.util.Comparator<T>;
    }
    namespace Comparators {
        class NaturalComparator implements java.util.Comparator<any> {
            compare(o1: any, o2: any): number;
            constructor();
        }
    }
}
declare namespace java.util {
    /**
     * Represents a date and time.
     */
    class Date implements java.lang.Cloneable, java.lang.Comparable<Date>, java.io.Serializable {
        static parse(s: string): number;
        static UTC(year: number, month: number, date: number, hrs: number, min: number, sec: number): number;
        /**
         * Ensure a number is displayed with two digits.
         *
         * @return a two-character base 10 representation of the number
         */
        static padTwo(number: number): string;
        /**
         * JavaScript Date instance.
         */
        private jsdate;
        static jsdateClass(): Object;
        constructor(year?: any, month?: any, date?: any, hrs?: any, min?: any, sec?: any);
        after(ts?: any): any;
        after$java_util_Date(when: Date): boolean;
        before(ts?: any): any;
        before$java_util_Date(when: Date): boolean;
        clone(): any;
        compareTo(other?: any): any;
        equals(ts?: any): any;
        equals$java_lang_Object(obj: any): boolean;
        getDate(): number;
        getDay(): number;
        getHours(): number;
        getMinutes(): number;
        getMonth(): number;
        getSeconds(): number;
        getTime(): number;
        getTimezoneOffset(): number;
        getYear(): number;
        hashCode(): number;
        setDate(date: number): void;
        setHours(hours: number): void;
        setMinutes(minutes: number): void;
        setMonth(month: number): void;
        setSeconds(seconds: number): void;
        setTime(time: number): void;
        setYear(year: number): void;
        toGMTString(): string;
        toLocaleString(): string;
        toString(): string;
        static ONE_HOUR_IN_MILLISECONDS: number;
        static ONE_HOUR_IN_MILLISECONDS_$LI$(): number;
        /**
         * Detects if the requested time falls into a non-existent time range due to
         * local time advancing into daylight savings time or is ambiguous due to
         * going out of daylight savings. If so, adjust accordingly.
         */
        fixDaylightSavings(requestedHours: number): void;
    }
    namespace Date {
        /**
         * Encapsulates static data to avoid Date itself having a static
         * initializer.
         */
        class StringData {
            static DAYS: string[];
            static DAYS_$LI$(): string[];
            static MONTHS: string[];
            static MONTHS_$LI$(): string[];
        }
    }
}
declare namespace java.util {
    /**
     * A collection designed for holding elements prior to processing. <a
     * href="http://docs.oracle.com/javase/6/docs/api/java/util/Deque.html">Deque</a>
     *
     * @param <E> element type.
     */
    interface Deque<E> extends java.util.Queue<E> {
        addFirst(e: E): any;
        addLast(e: E): any;
        descendingIterator(): java.util.Iterator<E>;
        getFirst(): E;
        getLast(): E;
        offerFirst(e: E): boolean;
        offerLast(e: E): boolean;
        peekFirst(): E;
        peekLast(): E;
        pollFirst(): E;
        pollLast(): E;
        pop(): E;
        push(e: E): any;
        removeFirst(): E;
        removeFirstOccurrence(o: any): boolean;
        removeLast(): E;
        removeLastOccurrence(o: any): boolean;
    }
}
declare namespace java.util {
    interface Dictionary<K, V> {
        /**
         * Returns the number of entries (distinct keys) in this dictionary.
         *
         * @return  the number of keys in this dictionary.
         */
        size(): number;
        /**
         * Tests if this dictionary maps no keys to value. The general contract
         * for the <tt>isEmpty</tt> method is that the result is true if and only
         * if this dictionary contains no entries.
         *
         * @return  <code>true</code> if this dictionary maps no keys to values;
         * <code>false</code> otherwise.
         */
        isEmpty(): boolean;
        /**
         * Returns an enumeration of the keys in this dictionary. The general
         * contract for the keys method is that an <tt>Enumeration</tt> object
         * is returned that will generate all the keys for which this dictionary
         * contains entries.
         *
         * @return  an enumeration of the keys in this dictionary.
         * @see     java.util.Dictionary#elements()
         * @see     java.util.Enumeration
         */
        keys(): java.util.Enumeration<K>;
        /**
         * Returns an enumeration of the values in this dictionary. The general
         * contract for the <tt>elements</tt> method is that an
         * <tt>Enumeration</tt> is returned that will generate all the elements
         * contained in entries in this dictionary.
         *
         * @return  an enumeration of the values in this dictionary.
         * @see     java.util.Dictionary#keys()
         * @see     java.util.Enumeration
         */
        elements(): java.util.Enumeration<V>;
        /**
         * Returns the value to which the key is mapped in this dictionary.
         * The general contract for the <tt>isEmpty</tt> method is that if this
         * dictionary contains an entry for the specified key, the associated
         * value is returned; otherwise, <tt>null</tt> is returned.
         *
         * @return  the value to which the key is mapped in this dictionary;
         * @param   key   a key in this dictionary.
         * <code>null</code> if the key is not mapped to any value in
         * this dictionary.
         * @exception NullPointerException if the <tt>key</tt> is <tt>null</tt>.
         * @see     java.util.Dictionary#put(java.lang.Object, java.lang.Object)
         */
        get(key: any): V;
        /**
         * Maps the specified <code>key</code> to the specified
         * <code>value</code> in this dictionary. Neither the key nor the
         * value can be <code>null</code>.
         * <p>
         * If this dictionary already contains an entry for the specified
         * <tt>key</tt>, the value already in this dictionary for that
         * <tt>key</tt> is returned, after modifying the entry to contain the
         * new element. <p>If this dictionary does not already have an entry
         * for the specified <tt>key</tt>, an entry is created for the
         * specified <tt>key</tt> and <tt>value</tt>, and <tt>null</tt> is
         * returned.
         * <p>
         * The <code>value</code> can be retrieved by calling the
         * <code>get</code> method with a <code>key</code> that is equal to
         * the original <code>key</code>.
         *
         * @param      key     the hashtable key.
         * @param      value   the value.
         * @return     the previous value to which the <code>key</code> was mapped
         * in this dictionary, or <code>null</code> if the key did not
         * have a previous mapping.
         * @exception  NullPointerException  if the <code>key</code> or
         * <code>value</code> is <code>null</code>.
         * @see        java.lang.Object#equals(java.lang.Object)
         * @see        java.util.Dictionary#get(java.lang.Object)
         */
        put(key: K, value: V): V;
        /**
         * Removes the <code>key</code> (and its corresponding
         * <code>value</code>) from this dictionary. This method does nothing
         * if the <code>key</code> is not in this dictionary.
         *
         * @param   key   the key that needs to be removed.
         * @return  the value to which the <code>key</code> had been mapped in this
         * dictionary, or <code>null</code> if the key did not have a
         * mapping.
         * @exception NullPointerException if <tt>key</tt> is <tt>null</tt>.
         */
        remove(key: any): V;
    }
}
declare namespace java.util {
    /**
     * An interface to generate a series of elements, one at a time. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/Enumeration.html">[Sun
     * docs]</a>
     *
     * @param <E> the type being enumerated.
     */
    interface Enumeration<E> {
        hasMoreElements(): boolean;
        nextElement(): E;
    }
}
declare namespace java.util {
    /**
     * A tag interface that other "listener" interfaces can extend to indicate their
     * adherence to the observer pattern.
     */
    interface EventListener {
    }
}
declare namespace java.util {
    abstract class EventListenerProxy<T extends java.util.EventListener> implements java.util.EventListener {
        private listener;
        constructor(listener: T);
        getListener(): T;
    }
}
declare namespace java.util {
    /**
     * Available as a superclass of event objects.
     */
    class EventObject {
        source: any;
        constructor(source: any);
        getSource(): any;
    }
}
declare namespace java.util {
    /**
     * A simple wrapper around JavaScriptObject to provide {@link java.util.Map}-like semantics for any
     * key type.
     * <p>
     * Implementation notes:
     * <p>
     * A key's hashCode is the index in backingMap which should contain that key. Since several keys may
     * have the same hash, each value in hashCodeMap is actually an array containing all entries whose
     * keys share the same hash.
     */
    class InternalHashCodeMap<K, V> implements java.lang.Iterable<java.util.Map.Entry<K, V>> {
        forEach(action: (p1: any) => void): void;
        private backingMap;
        private host;
        private __size;
        constructor(host: java.util.AbstractHashMap<K, V>);
        put(key: K, value: V): V;
        remove(key: any): V;
        getEntry(key: any): java.util.Map.Entry<K, V>;
        private findEntryInChain(key, chain);
        size(): number;
        iterator(): java.util.Iterator<java.util.Map.Entry<K, V>>;
        private getChainOrEmpty(hashCode);
        private newEntryChain();
        private unsafeCastToArray(arr);
        /**
         * Returns hash code of the key as calculated by {@link AbstractHashMap#getHashCode(Object)} but
         * also handles null keys as well.
         */
        private hash(key);
    }
    namespace InternalHashCodeMap {
        class InternalHashCodeMap$0 implements java.util.Iterator<java.util.Map.Entry<any, any>> {
            __parent: any;
            forEachRemaining(consumer: (p1: any) => void): void;
            chains: java.util.InternalJsMap.Iterator<any>;
            itemIndex: number;
            chain: java.util.Map.Entry<any, any>[];
            lastEntry: java.util.Map.Entry<any, any>;
            hasNext(): boolean;
            next(): java.util.Map.Entry<any, any>;
            remove(): void;
            constructor(__parent: any);
        }
    }
}
declare namespace java.util {
    class InternalJsMap<V> {
        get$int(key: number): V;
        get(key?: any): any;
        set$int$java_lang_Object(key: number, value: V): void;
        set(key?: any, value?: any): any;
        delete$int(key: number): void;
        delete(key?: any): any;
        entries(): InternalJsMap.Iterator<V>;
    }
    namespace InternalJsMap {
        class Iterator<V> {
            next(): InternalJsMap.IteratorEntry<V>;
        }
        class IteratorEntry<V> {
            value: any[];
            done: boolean;
            constructor();
        }
        class JsHelper {
            static delete$java_util_InternalJsMap$int(obj: java.util.InternalJsMap<any>, key: number): void;
            static delete(obj?: any, key?: any): any;
        }
    }
}
declare namespace java.util {
    /**
     * A factory to create JavaScript Map instances.
     */
    class InternalJsMapFactory {
        static jsMapCtor: any;
        static jsMapCtor_$LI$(): any;
        private static getJsMapConstructor();
        static newJsMap<V>(): java.util.InternalJsMap<V>;
        constructor();
    }
}
declare namespace java.util {
    /**
     * See <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Iterator.html">
     * the official Java API doc</a> for details.
     *
     * @param <E> element type
     */
    interface Iterator<E> {
        hasNext(): boolean;
        next(): E;
        forEachRemaining(consumer?: any): any;
        remove(): any;
    }
}
declare namespace java.util {
    /**
     * Represents a sequence of objects. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/List.html">[Sun docs]</a>
     *
     * @param <E> element type
     */
    interface List<E> extends java.util.Collection<E> {
        add(index?: any, element?: any): any;
        addAll(index?: any, c?: any): any;
        clear(): any;
        contains(o: any): boolean;
        containsAll(c: java.util.Collection<any>): boolean;
        equals(o: any): boolean;
        get(index: number): E;
        hashCode(): number;
        indexOf(o?: any, index?: any): any;
        isEmpty(): boolean;
        iterator(): java.util.Iterator<E>;
        lastIndexOf(o?: any, index?: any): any;
        listIterator(from?: any): any;
        remove(index?: any): any;
        removeAll(c: java.util.Collection<any>): boolean;
        retainAll(c: java.util.Collection<any>): boolean;
        set(index: number, element: E): E;
        size(): number;
        subList(fromIndex: number, toIndex: number): List<E>;
        toArray<T>(array?: any): any;
    }
}
declare namespace java.util {
    /**
     * Uses Java 1.5 ListIterator for documentation. The methods hasNext, next, and
     * remove are repeated to allow the specialized ListIterator documentation to be
     * associated with them. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/ListIterator.html">[Sun
     * docs]</a>
     *
     * @param <E> element type.
     */
    interface ListIterator<E> extends java.util.Iterator<E> {
        add(o: E): any;
        hasNext(): boolean;
        hasPrevious(): boolean;
        next(): E;
        nextIndex(): number;
        previous(): E;
        previousIndex(): number;
        remove(): any;
        set(o: E): any;
    }
}
declare namespace java.util {
    /**
     * A very simple emulation of Locale for shared-code patterns like
     * {@code String.toUpperCase(Locale.US)}.
     * <p>
     * Note: Any changes to this class should put into account the assumption that
     * was made in rest of the JRE emulation.
     */
    class Locale {
        static ROOT: Locale;
        static ROOT_$LI$(): Locale;
        static ENGLISH: Locale;
        static ENGLISH_$LI$(): Locale;
        static US: Locale;
        static US_$LI$(): Locale;
        static defaultLocale: Locale;
        static defaultLocale_$LI$(): Locale;
        /**
         * Returns an instance that represents the browser's default locale (not
         * necessarily the one defined by 'gwt.locale').
         */
        static getDefault(): Locale;
        constructor();
    }
    namespace Locale {
        class RootLocale extends java.util.Locale {
            toString(): string;
        }
        class EnglishLocale extends java.util.Locale {
            toString(): string;
        }
        class USLocale extends java.util.Locale {
            toString(): string;
        }
        class DefaultLocale extends java.util.Locale {
            toString(): string;
        }
    }
}
declare namespace java.util.logging {
    /**
     * An emulation of the java.util.logging.Formatter class. See
     * <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/util/logging/Formatter.html">
     * The Java API doc for details</a>
     */
    abstract class Formatter {
        abstract format(record: java.util.logging.LogRecord): string;
        formatMessage(record: java.util.logging.LogRecord): string;
    }
}
declare namespace java.util.logging {
    /**
     * An emulation of the java.util.logging.Handler class. See
     * <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/util/logging/Handler.html">
     * The Java API doc for details</a>
     */
    abstract class Handler {
        private formatter;
        private level;
        abstract close(): any;
        abstract flush(): any;
        getFormatter(): java.util.logging.Formatter;
        getLevel(): java.util.logging.Level;
        isLoggable(record: java.util.logging.LogRecord): boolean;
        abstract publish(record: java.util.logging.LogRecord): any;
        setFormatter(newFormatter: java.util.logging.Formatter): void;
        setLevel(newLevel: java.util.logging.Level): void;
        constructor();
    }
}
declare namespace java.util.logging {
    /**
     * An emulation of the java.util.logging.Level class. See
     * <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/util/logging/Level.html">
     * The Java API doc for details</a>
     */
    class Level implements java.io.Serializable {
        static ALL: Level;
        static ALL_$LI$(): Level;
        static CONFIG: Level;
        static CONFIG_$LI$(): Level;
        static FINE: Level;
        static FINE_$LI$(): Level;
        static FINER: Level;
        static FINER_$LI$(): Level;
        static FINEST: Level;
        static FINEST_$LI$(): Level;
        static INFO: Level;
        static INFO_$LI$(): Level;
        static OFF: Level;
        static OFF_$LI$(): Level;
        static SEVERE: Level;
        static SEVERE_$LI$(): Level;
        static WARNING: Level;
        static WARNING_$LI$(): Level;
        static parse(name: string): Level;
        constructor();
        getName(): string;
        intValue(): number;
        toString(): string;
    }
    namespace Level {
        class LevelAll extends java.util.logging.Level {
            getName(): string;
            intValue(): number;
            constructor();
        }
        class LevelConfig extends java.util.logging.Level {
            getName(): string;
            intValue(): number;
            constructor();
        }
        class LevelFine extends java.util.logging.Level {
            getName(): string;
            intValue(): number;
            constructor();
        }
        class LevelFiner extends java.util.logging.Level {
            getName(): string;
            intValue(): number;
            constructor();
        }
        class LevelFinest extends java.util.logging.Level {
            getName(): string;
            intValue(): number;
            constructor();
        }
        class LevelInfo extends java.util.logging.Level {
            getName(): string;
            intValue(): number;
            constructor();
        }
        class LevelOff extends java.util.logging.Level {
            getName(): string;
            intValue(): number;
            constructor();
        }
        class LevelSevere extends java.util.logging.Level {
            getName(): string;
            intValue(): number;
            constructor();
        }
        class LevelWarning extends java.util.logging.Level {
            getName(): string;
            intValue(): number;
            constructor();
        }
    }
}
declare namespace java.util.logging {
    /**
     * An emulation of the java.util.logging.LogManager class. See
     * <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/util/logging/LogManger.html">
     * The Java API doc for details</a>
     */
    class LogManager {
        static singleton: LogManager;
        static getLogManager(): LogManager;
        private loggerMap;
        constructor();
        addLogger(logger: java.util.logging.Logger): boolean;
        getLogger(name: string): java.util.logging.Logger;
        getLoggerNames(): java.util.Enumeration<string>;
        /**
         * Helper function to add a logger when we have already determined that it
         * does not exist.  When we add a logger, we recursively add all of it's
         * ancestors. Since loggers do not get removed, logger creation is cheap,
         * and there are not usually too many loggers in an ancestry chain,
         * this is a simple way to ensure that the parent/child relationships are
         * always correctly set up.
         */
        private addLoggerAndEnsureParents(logger);
        private addLoggerImpl(logger);
        /**
         * Helper function to create a logger if it does not exist since the public
         * APIs for getLogger and addLogger make it difficult to use those functions
         * for this.
         */
        ensureLogger(name: string): java.util.logging.Logger;
    }
}
declare namespace java.util.logging {
    /**
     * An emulation of the java.util.logging.LogRecord class. See
     * <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/util/logging/LogRecord.html">
     * The Java API doc for details</a>
     */
    class LogRecord implements java.io.Serializable {
        private level;
        private loggerName;
        private msg;
        private thrown;
        private millis;
        constructor(level?: any, msg?: any);
        getLevel(): java.util.logging.Level;
        getLoggerName(): string;
        getMessage(): string;
        getMillis(): number;
        getThrown(): Error;
        setLevel(newLevel: java.util.logging.Level): void;
        setLoggerName(newName: string): void;
        setMessage(newMessage: string): void;
        setMillis(newMillis: number): void;
        setThrown(newThrown: Error): void;
    }
}
declare namespace java.util {
    /**
     * Abstract interface for maps.
     *
     * @param <K> key type.
     * @param <V> value type.
     */
    interface Map<K, V> {
        clear(): any;
        containsKey(key: any): boolean;
        containsValue(value: any): boolean;
        entrySet(): java.util.Set<Map.Entry<K, V>>;
        equals(o: any): boolean;
        get(key: any): V;
        hashCode(): number;
        isEmpty(): boolean;
        keySet(): java.util.Set<K>;
        put(key?: any, value?: any): any;
        putAll(t: Map<any, any>): any;
        remove(key: any): V;
        size(): number;
        values(): java.util.Collection<V>;
    }
    namespace Map {
        /**
         * Represents an individual map entry.
         */
        interface Entry<K, V> {
            equals(o: any): boolean;
            getKey(): K;
            getValue(): V;
            hashCode(): number;
            setValue(value: V): V;
        }
    }
}
declare namespace java.util {
    /**
     * Sorted map providing additional query operations and views.
     *
     * @param <K> key type.
     * @param <V> value type.
     */
    interface NavigableMap<K, V> extends java.util.SortedMap<K, V> {
        ceilingEntry(key: K): java.util.Map.Entry<K, V>;
        ceilingKey(key: K): K;
        descendingKeySet(): java.util.NavigableSet<K>;
        descendingMap(): NavigableMap<K, V>;
        firstEntry(): java.util.Map.Entry<K, V>;
        floorEntry(key: K): java.util.Map.Entry<K, V>;
        floorKey(key: K): K;
        headMap(toKey?: any, inclusive?: any): any;
        higherEntry(key: K): java.util.Map.Entry<K, V>;
        higherKey(key: K): K;
        lastEntry(): java.util.Map.Entry<K, V>;
        lowerEntry(key: K): java.util.Map.Entry<K, V>;
        lowerKey(key: K): K;
        navigableKeySet(): java.util.NavigableSet<K>;
        pollFirstEntry(): java.util.Map.Entry<K, V>;
        pollLastEntry(): java.util.Map.Entry<K, V>;
        subMap(fromKey?: any, fromInclusive?: any, toKey?: any, toInclusive?: any): any;
        tailMap(fromKey?: any, inclusive?: any): any;
    }
}
declare namespace java.util {
    /**
     * A {@code SortedSet} with more flexible queries.
     *
     * @param <E> element type.
     */
    interface NavigableSet<E> extends java.util.SortedSet<E> {
        ceiling(e: E): E;
        descendingIterator(): java.util.Iterator<E>;
        descendingSet(): NavigableSet<E>;
        floor(e: E): E;
        headSet(toElement?: any, inclusive?: any): any;
        higher(e: E): E;
        lower(e: E): E;
        pollFirst(): E;
        pollLast(): E;
        subSet(fromElement?: any, fromInclusive?: any, toElement?: any, toInclusive?: any): any;
        tailSet(fromElement?: any, inclusive?: any): any;
    }
}
declare namespace java.util {
    /**
     * See <a
     * href="http://docs.oracle.com/javase/7/docs/api/java/util/Objects.html">the
     * official Java API doc</a> for details.
     */
    class Objects {
        constructor();
        static compare<T>(a: T, b: T, c: java.util.Comparator<any>): number;
        static deepEquals(a: any, b: any): boolean;
        static equals(a: any, b: any): boolean;
        static hashCode(o: any): number;
        static hash(...values: any[]): number;
        static isNull(obj: any): boolean;
        static nonNull(obj: any): boolean;
        static requireNonNull$java_lang_Object<T>(obj: T): T;
        static requireNonNull<T>(obj?: any, message?: any): any;
        static requireNonNull$java_lang_Object$java_util_function_Supplier<T>(obj: T, messageSupplier: () => string): T;
        static toString$java_lang_Object(o: any): string;
        static toString(o?: any, nullDefault?: any): any;
    }
}
declare namespace java.util {
    /**
     * Implementation of the observable class.
     */
    class Observable {
        private changed;
        private obs;
        /**
         * Construct an Observable with zero Observers.
         */
        constructor();
        /**
         * Adds an observer to the set of observers for this object, provided that
         * it is not the same as some observer already in the set. The order in
         * which notifications will be delivered to multiple observers is not
         * specified. See the class comment.
         *
         * @param o
         * an observer to be added.
         * @throws NullPointerException
         * if the parameter o is null.
         */
        addObserver(o: java.util.Observer): void;
        /**
         * Deletes an observer from the set of observers of this object. Passing
         * <CODE>null</CODE> to this method will have no effect.
         *
         * @param o
         * the observer to be deleted.
         */
        deleteObserver(o: java.util.Observer): void;
        /**
         * If this object has changed, as indicated by the <code>hasChanged</code>
         * method, then notify all of its observers and then call the
         * <code>clearChanged</code> method to indicate that this object has no
         * longer changed.
         * <p>
         * Each observer has its <code>update</code> method called with two
         * arguments: this observable object and the <code>arg</code> argument.
         *
         * @param arg
         * any object.
         * @see java.util.Observable#clearChanged()
         * @see java.util.Observable#hasChanged()
         * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
         */
        notifyObservers(arg?: any): void;
        /**
         * Clears the observer list so that this object no longer has any observers.
         */
        deleteObservers(): void;
        /**
         * Marks this <tt>Observable</tt> object as having been changed; the
         * <tt>hasChanged</tt> method will now return <tt>true</tt>.
         */
        setChanged(): void;
        /**
         * Indicates that this object has no longer changed, or that it has already
         * notified all of its observers of its most recent change, so that the
         * <tt>hasChanged</tt> method will now return <tt>false</tt>. This method is
         * called automatically by the <code>notifyObservers</code> methods.
         *
         * @see java.util.Observable#notifyObservers()
         * @see java.util.Observable#notifyObservers(java.lang.Object)
         */
        clearChanged(): void;
        /**
         * Tests if this object has changed.
         *
         * @return <code>true</code> if and only if the <code>setChanged</code>
         * method has been called more recently than the
         * <code>clearChanged</code> method on this object;
         * <code>false</code> otherwise.
         * @see java.util.Observable#clearChanged()
         * @see java.util.Observable#setChanged()
         */
        hasChanged(): boolean;
        /**
         * Returns the number of observers of this <tt>Observable</tt> object.
         *
         * @return the number of observers of this object.
         */
        countObservers(): number;
    }
}
declare namespace java.util {
    /**
     * Implementation of the observer interface
     */
    interface Observer {
        /**
         * This method is called whenever the observed object is changed. An
         * application calls an <tt>Observable</tt> object's
         * <code>notifyObservers</code> method to have all the object's
         * observers notified of the change.
         *
         * @param   o     the observable object.
         * @param   arg   an argument passed to the <code>notifyObservers</code>
         * method.
         */
        update(o: java.util.Observable, arg: any): any;
    }
}
declare namespace java.util {
    /**
     * See <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html">
     * the official Java API doc</a> for details.
     *
     * @param <T> type of the wrapped reference
     */
    class Optional<T> {
        static empty<T>(): Optional<T>;
        static of<T>(value: T): Optional<T>;
        static ofNullable<T>(value: T): Optional<T>;
        static EMPTY: Optional<any>;
        static EMPTY_$LI$(): Optional<any>;
        private ref;
        constructor(ref?: any);
        isPresent(): boolean;
        get(): T;
        ifPresent(consumer: (p1: any) => void): void;
        filter(predicate: (p1: any) => boolean): Optional<T>;
        map<U>(mapper: (p1: any) => any): Optional<U>;
        flatMap<U>(mapper: (p1: any) => Optional<U>): Optional<U>;
        orElse(other: T): T;
        orElseGet(other: () => any): T;
        orElseThrow<X extends Error>(exceptionSupplier: () => any): T;
        equals(obj: any): boolean;
        hashCode(): number;
        toString(): string;
    }
}
declare namespace java.util {
    /**
     * See <a href="https://docs.oracle.com/javase/8/docs/api/java/util/OptionalDouble.html">
     * the official Java API doc</a> for details.
     */
    class OptionalDouble {
        static empty(): OptionalDouble;
        static of(value: number): OptionalDouble;
        static EMPTY: OptionalDouble;
        static EMPTY_$LI$(): OptionalDouble;
        private ref;
        private present;
        constructor(value?: any);
        isPresent(): boolean;
        getAsDouble(): number;
        ifPresent(consumer: any): void;
        orElse(other: number): number;
        orElseGet(other: any): number;
        orElseThrow<X extends Error>(exceptionSupplier: () => X): number;
        equals(obj: any): boolean;
        hashCode(): number;
        toString(): string;
    }
}
declare namespace java.util {
    /**
     * See <a href="https://docs.oracle.com/javase/8/docs/api/java/util/OptionalInt.html">
     * the official Java API doc</a> for details.
     */
    class OptionalInt {
        static empty(): OptionalInt;
        static of(value: number): OptionalInt;
        static EMPTY: OptionalInt;
        static EMPTY_$LI$(): OptionalInt;
        private ref;
        private present;
        constructor(value?: any);
        isPresent(): boolean;
        getAsInt(): number;
        ifPresent(consumer: any): void;
        orElse(other: number): number;
        orElseGet(other: any): number;
        orElseThrow<X extends Error>(exceptionSupplier: () => X): number;
        equals(obj: any): boolean;
        hashCode(): number;
        toString(): string;
    }
}
declare namespace java.util {
    /**
     * See <a href="https://docs.oracle.com/javase/8/docs/api/java/util/OptionalLong.html">
     * the official Java API doc</a> for details.
     */
    class OptionalLong {
        static empty(): OptionalLong;
        static of(value: number): OptionalLong;
        static EMPTY: OptionalLong;
        static EMPTY_$LI$(): OptionalLong;
        private ref;
        private present;
        constructor(value?: any);
        isPresent(): boolean;
        getAsLong(): number;
        ifPresent(consumer: any): void;
        orElse(other: number): number;
        orElseGet(other: any): number;
        orElseThrow<X extends Error>(exceptionSupplier: () => X): number;
        equals(obj: any): boolean;
        hashCode(): number;
        toString(): string;
    }
}
declare namespace java.util {
    /**
     * See <a href="https://docs.oracle.com/javase/8/docs/api/java/util/PrimitiveIterator.html">
     * the official Java API doc</a> for details.
     *
     * @param <T> element type
     * @param <C> consumer type
     */
    interface PrimitiveIterator<T, C> extends java.util.Iterator<T> {
    }
    namespace PrimitiveIterator {
        /**
         * See <a href="https://docs.oracle.com/javase/8/docs/api/java/util/PrimitiveIterator.OfDouble.html">
         * the official Java API doc</a> for details.
         */
        interface OfDouble extends java.util.PrimitiveIterator<number, any> {
            nextDouble(): number;
            next(): number;
            forEachRemaining(consumer?: any): any;
        }
        /**
         * See <a href="https://docs.oracle.com/javase/8/docs/api/java/util/PrimitiveIterator.OfInt.html">
         * the official Java API doc</a> for details.
         */
        interface OfInt extends java.util.PrimitiveIterator<number, any> {
            nextInt(): number;
            next(): number;
            forEachRemaining(consumer?: any): any;
        }
        /**
         * See <a href="https://docs.oracle.com/javase/8/docs/api/java/util/PrimitiveIterator.OfLong.html">
         * the official Java API doc</a> for details.
         */
        interface OfLong extends java.util.PrimitiveIterator<number, any> {
            nextLong(): number;
            next(): number;
            forEachRemaining(consumer?: any): any;
        }
    }
}
declare namespace java.util {
    /**
     * A collection designed for holding elements prior to processing. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/Queue.html">[Sun
     * docs]</a>
     *
     * @param <E> element type.
     */
    interface Queue<E> extends java.util.Collection<E> {
        element(): E;
        offer(o: E): boolean;
        peek(): E;
        poll(): E;
        remove(index?: any): any;
    }
}
declare namespace java.util {
    /**
     * This class provides methods that generates pseudo-random numbers of different
     * types, such as {@code int}, {@code long}, {@code double}, and {@code float}.
     * It follows the algorithms specified in the JRE javadoc.
     *
     * This emulated version of Random is not serializable.
     */
    class Random {
        static __static_initialized: boolean;
        static __static_initialize(): void;
        static multiplierHi: number;
        static multiplierLo: number;
        static twoToThe24: number;
        static twoToThe31: number;
        static twoToThe32: number;
        static twoToTheMinus24: number;
        static twoToTheMinus26: number;
        static twoToTheMinus31: number;
        static twoToTheMinus53: number;
        static twoToTheXMinus24: number[];
        static twoToTheXMinus24_$LI$(): number[];
        static twoToTheXMinus48: number[];
        static twoToTheXMinus48_$LI$(): number[];
        /**
         * A value used to avoid two random number generators produced at the same
         * time having the same seed.
         */
        static uniqueSeed: number;
        static __static_initializer_0(): void;
        /**
         * The boolean value indicating if the second Gaussian number is available.
         */
        private haveNextNextGaussian;
        /**
         * The second Gaussian generated number.
         */
        private nextNextGaussian;
        /**
         * The high 24 bits of the 48=bit seed value.
         */
        private seedhi;
        /**
         * The low 24 bits of the 48=bit seed value.
         */
        private seedlo;
        /**
         * Construct a random generator with the given {@code seed} as the initial
         * state.
         *
         * @param seed the seed that will determine the initial state of this random
         * number generator.
         * @see #setSeed
         */
        constructor(seed?: any);
        /**
         * Returns the next pseudo-random, uniformly distributed {@code boolean} value
         * generated by this generator.
         *
         * @return a pseudo-random, uniformly distributed boolean value.
         */
        nextBoolean(): boolean;
        /**
         * Modifies the {@code byte} array by a random sequence of {@code byte}s
         * generated by this random number generator.
         *
         * @param buf non-null array to contain the new random {@code byte}s.
         * @see #next
         */
        nextBytes(buf: number[]): void;
        /**
         * Generates a normally distributed random {@code double} number between 0.0
         * inclusively and 1.0 exclusively.
         *
         * @return a random {@code double} in the range [0.0 - 1.0)
         * @see #nextFloat
         */
        nextDouble(): number;
        /**
         * Generates a normally distributed random {@code float} number between 0.0
         * inclusively and 1.0 exclusively.
         *
         * @return float a random {@code float} number between [0.0 and 1.0)
         * @see #nextDouble
         */
        nextFloat(): number;
        /**
         * Pseudo-randomly generates (approximately) a normally distributed {@code
         * double} value with mean 0.0 and a standard deviation value of {@code 1.0}
         * using the <i>polar method<i> of G. E. P. Box, M. E. Muller, and G.
         * Marsaglia, as described by Donald E. Knuth in <i>The Art of Computer
         * Programming, Volume 2: Seminumerical Algorithms</i>, section 3.4.1,
         * subsection C, algorithm P.
         *
         * @return a random {@code double}
         * @see #nextDouble
         */
        nextGaussian(): number;
        /**
         * Generates a uniformly distributed 32-bit {@code int} value from the random
         * number sequence.
         *
         * @return a uniformly distributed {@code int} value.
         * @see java.lang.Integer#MAX_VALUE
         * @see java.lang.Integer#MIN_VALUE
         * @see #next
         * @see #nextLong
         */
        nextInt$(): number;
        /**
         * Returns a new pseudo-random {@code int} value which is uniformly
         * distributed between 0 (inclusively) and the value of {@code n}
         * (exclusively).
         *
         * @param n the exclusive upper border of the range [0 - n).
         * @return a random {@code int}.
         */
        nextInt(n?: any): any;
        /**
         * Generates a uniformly distributed 64-bit integer value from the random
         * number sequence.
         *
         * @return 64-bit random integer.
         * @see java.lang.Integer#MAX_VALUE
         * @see java.lang.Integer#MIN_VALUE
         * @see #next
         * @see #nextInt()
         * @see #nextInt(int)
         */
        nextLong(): number;
        /**
         * Modifies the seed a using linear congruential formula presented in <i>The
         * Art of Computer Programming, Volume 2</i>, Section 3.2.1.
         *
         * @param seed the seed that alters the state of the random number generator.
         * @see #next
         * @see #Random()
         * @see #Random(long)
         */
        setSeed$long(seed: number): void;
        /**
         * Returns a pseudo-random uniformly distributed {@code int} value of the
         * number of bits specified by the argument {@code bits} as described by
         * Donald E. Knuth in <i>The Art of Computer Programming, Volume 2:
         * Seminumerical Algorithms</i>, section 3.2.1.
         *
         * @param bits number of bits of the returned value.
         * @return a pseudo-random generated int number.
         * @see #nextBytes
         * @see #nextDouble
         * @see #nextFloat
         * @see #nextInt()
         * @see #nextInt(int)
         * @see #nextGaussian
         * @see #nextLong
         */
        next(bits: number): number;
        private nextInternal(bits);
        setSeed(seedhi?: any, seedlo?: any): any;
    }
}
declare namespace java.util {
    /**
     * Indicates that a data structure supports constant-time random access to its
     * contained objects.
     */
    interface RandomAccess {
    }
}
declare namespace java.util {
    /**
     * Represents a set of unique objects. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/Set.html">[Sun docs]</a>
     *
     * @param <E> element type.
     */
    interface Set<E> extends java.util.Collection<E> {
        add(index?: any, element?: any): any;
        addAll(index?: any, c?: any): any;
        clear(): any;
        contains(o: any): boolean;
        containsAll(c: java.util.Collection<any>): boolean;
        equals(o: any): boolean;
        hashCode(): number;
        isEmpty(): boolean;
        iterator(): java.util.Iterator<E>;
        remove(index?: any): any;
        removeAll(c: java.util.Collection<any>): boolean;
        retainAll(c: java.util.Collection<any>): boolean;
        size(): number;
        toArray<T>(a?: any): any;
    }
}
declare namespace java.util {
    /**
     * A map with ordering. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/SortedMap.html">[Sun
     * docs]</a>
     *
     * @param <K> key type.
     * @param <V> value type.
     */
    interface SortedMap<K, V> extends java.util.Map<K, V> {
        comparator(): java.util.Comparator<any>;
        firstKey(): K;
        headMap(toKey?: any, inclusive?: any): any;
        lastKey(): K;
        subMap(fromKey?: any, fromInclusive?: any, toKey?: any, toInclusive?: any): any;
        tailMap(fromKey?: any, inclusive?: any): any;
    }
}
declare namespace java.util {
    /**
     * A set known to be in ascending order. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/SortedSet.html">[Sun
     * docs]</a>
     *
     * @param <E> element type.
     */
    interface SortedSet<E> extends java.util.Set<E> {
        comparator(): java.util.Comparator<any>;
        first(): E;
        headSet(toElement?: any, inclusive?: any): any;
        last(): E;
        subSet(fromElement?: any, fromInclusive?: any, toElement?: any, toInclusive?: any): any;
        tailSet(fromElement?: any, inclusive?: any): any;
    }
}
declare namespace java.util {
    /**
     * See <a href="https://docs.oracle.com/javase/8/docs/api/java/util/StringJoiner.html">
     * the official Java API doc</a> for details.
     */
    class StringJoiner {
        private delimiter;
        private prefix;
        private suffix;
        private builder;
        private emptyValue;
        constructor(delimiter: string, prefix?: string, suffix?: string);
        add(newElement: string): StringJoiner;
        length(): number;
        merge(other: StringJoiner): StringJoiner;
        setEmptyValue(emptyValue: string): StringJoiner;
        toString(): string;
        private initBuilderOrAddDelimiter();
    }
}
declare namespace javaemul.internal.annotations {
}
declare namespace javaemul.internal.annotations {
}
declare namespace javaemul.internal.annotations {
}
declare namespace javaemul.internal.annotations {
}
declare namespace javaemul.internal.annotations {
}
declare namespace javaemul.internal.annotations {
}
declare namespace javaemul.internal {
    /**
     * Provides utilities to perform operations on Arrays.
     */
    class ArrayHelper {
        static ARRAY_PROCESS_BATCH_SIZE: number;
        static clone<T>(array: T[], fromIndex: number, toIndex: number): T[];
        /**
         * Unlike clone, this method returns a copy of the array that is not type
         * marked. This is only safe for temp arrays as returned array will not do
         * any type checks.
         */
        static unsafeClone(array: any, fromIndex: number, toIndex: number): any[];
        static createFrom<T>(array: T[], length: number): T[];
        private static createNativeArray(length);
        static getLength(array: any): number;
        static setLength(array: any, length: number): void;
        static removeFrom(array: any, index: number, deleteCount: number): void;
        static insertTo$java_lang_Object$int$java_lang_Object(array: any, index: number, value: any): void;
        static insertTo(array?: any, index?: any, values?: any): any;
        static copy(src: any, srcOfs: number, dest: any, destOfs: number, len: number, overwrite?: boolean): void;
        private static applySplice(arrayObject, index, deleteCount, arrayToAdd);
    }
}
declare namespace javaemul.internal {
    /**
     * A utility to provide array stamping. Provided as a separate class to simplify
     * super-source.
     */
    class ArrayStamper {
        static stampJavaTypeInfo<T>(array: any, referenceType: T[]): T[];
    }
}
declare namespace javaemul.internal {
    /**
     * Wraps native <code>boolean</code> as an object.
     */
    class BooleanHelper implements java.lang.Comparable<BooleanHelper>, java.io.Serializable {
        static FALSE: boolean;
        static TRUE: boolean;
        static TYPE: any;
        static TYPE_$LI$(): any;
        static compare(x: boolean, y: boolean): number;
        static hashCode(value: boolean): number;
        static logicalAnd(a: boolean, b: boolean): boolean;
        static logicalOr(a: boolean, b: boolean): boolean;
        static logicalXor(a: boolean, b: boolean): boolean;
        static parseBoolean(s: string): boolean;
        static toString(x: boolean): string;
        static valueOf$boolean(b: boolean): boolean;
        static valueOf(s?: any): any;
        booleanValue(): boolean;
        private static unsafeCast(value);
        compareTo(b?: any): any;
        equals(o: any): boolean;
        hashCode(): number;
        toString(): string;
        constructor();
    }
}
declare namespace javaemul.internal {
    /**
     * Wraps a native <code>char</code> as an object.
     *
     * TODO(jat): many of the classification methods implemented here are not
     * correct in that they only handle ASCII characters, and many other methods are
     * not currently implemented. I think the proper approach is to introduce * a
     * deferred binding parameter which substitutes an implementation using a
     * fully-correct Unicode character database, at the expense of additional data
     * being downloaded. That way developers that need the functionality can get it
     * without those who don't need it paying for it.
     *
     * <pre>
     * The following methods are still not implemented -- most would require Unicode
     * character db to be useful:
     * - digit / is* / to*(int codePoint)
     * - isDefined(char)
     * - isIdentifierIgnorable(char)
     * - isJavaIdentifierPart(char)
     * - isJavaIdentifierStart(char)
     * - isJavaLetter(char) -- deprecated, so probably not
     * - isJavaLetterOrDigit(char) -- deprecated, so probably not
     * - isISOControl(char)
     * - isMirrored(char)
     * - isSpaceChar(char)
     * - isTitleCase(char)
     * - isUnicodeIdentifierPart(char)
     * - isUnicodeIdentifierStart(char)
     * - getDirectionality(*)
     * - getNumericValue(*)
     * - getType(*)
     * - reverseBytes(char) -- any use for this at all in the browser?
     * - toTitleCase(*)
     * - all the category constants for classification
     *
     * The following do not properly handle characters outside of ASCII:
     * - digit(char c, int radix)
     * - isDigit(char c)
     * - isLetter(char c)
     * - isLetterOrDigit(char c)
     * - isLowerCase(char c)
     * - isUpperCase(char c)
     * </pre>
     */
    class CharacterHelper implements java.lang.Comparable<CharacterHelper>, java.io.Serializable {
        static TYPE: any;
        static TYPE_$LI$(): any;
        static MIN_RADIX: number;
        static MAX_RADIX: number;
        static MIN_VALUE: string;
        static MAX_VALUE: string;
        static MIN_SURROGATE: string;
        static MAX_SURROGATE: string;
        static MIN_LOW_SURROGATE: string;
        static MAX_LOW_SURROGATE: string;
        static MIN_HIGH_SURROGATE: string;
        static MAX_HIGH_SURROGATE: string;
        static MIN_SUPPLEMENTARY_CODE_POINT: number;
        static MIN_CODE_POINT: number;
        static MAX_CODE_POINT: number;
        static SIZE: number;
        static charCount(codePoint: number): number;
        static codePointAt$char_A$int(a: string[], index: number): number;
        static codePointAt(a?: any, index?: any, limit?: any): any;
        static codePointAt$java_lang_CharSequence$int(seq: string, index: number): number;
        static codePointBefore$char_A$int(a: string[], index: number): number;
        static codePointBefore(a?: any, index?: any, start?: any): any;
        static codePointBefore$java_lang_CharSequence$int(cs: string, index: number): number;
        static codePointCount(a?: any, offset?: any, count?: any): any;
        static codePointCount$java_lang_CharSequence$int$int(seq: string, beginIndex: number, endIndex: number): number;
        static compare(x: string, y: string): number;
        static digit(c: string, radix: number): number;
        static getNumericValue(ch: string): number;
        static forDigit(digit?: any, radix?: any): any;
        /**
         * @skip
         *
         * public for shared implementation with Arrays.hashCode
         */
        static hashCode(c: string): number;
        static isDigit(c: string): boolean;
        static digitRegex(): RegExp;
        static isHighSurrogate(ch: string): boolean;
        static isLetter(c: string): boolean;
        static leterRegex(): RegExp;
        static isLetterOrDigit(c: string): boolean;
        static leterOrDigitRegex(): RegExp;
        static isLowerCase(c: string): boolean;
        static isLowSurrogate(ch: string): boolean;
        /**
         * Deprecated - see isWhitespace(char).
         */
        static isSpace(c: string): boolean;
        static isWhitespace(ch?: any): any;
        static isWhitespace$int(codePoint: number): boolean;
        static whitespaceRegex(): RegExp;
        static isSupplementaryCodePoint(codePoint: number): boolean;
        static isSurrogatePair(highSurrogate: string, lowSurrogate: string): boolean;
        static isUpperCase(c: string): boolean;
        static isValidCodePoint(codePoint: number): boolean;
        static offsetByCodePoints(a?: any, start?: any, count?: any, index?: any, codePointOffset?: any): any;
        static offsetByCodePoints$java_lang_CharSequence$int$int(seq: string, index: number, codePointOffset: number): number;
        static toChars$int(codePoint: number): string[];
        static toChars(codePoint?: any, dst?: any, dstIndex?: any): any;
        static toCodePoint(highSurrogate: string, lowSurrogate: string): number;
        static toLowerCase(c?: any): any;
        static toLowerCase$int(c: number): number;
        static toString(x: string): string;
        static toUpperCase(c?: any): any;
        static toUpperCase$int(c: number): string;
        static valueOf(c: string): CharacterHelper;
        static codePointAt$java_lang_CharSequence$int$int(cs: string, index: number, limit: number): number;
        static codePointBefore$java_lang_CharSequence$int$int(cs: string, index: number, start: number): number;
        /**
         * Shared implementation with {@link LongHelper#toString}.
         *
         * @skip
         */
        static forDigit$int(digit: number): string;
        /**
         * Computes the high surrogate character of the UTF16 representation of a
         * non-BMP code point. See {@link getLowSurrogate}.
         *
         * @param codePoint
         * requested codePoint, required to be >=
         * MIN_SUPPLEMENTARY_CODE_POINT
         * @return high surrogate character
         */
        static getHighSurrogate(codePoint: number): string;
        /**
         * Computes the low surrogate character of the UTF16 representation of a
         * non-BMP code point. See {@link getHighSurrogate}.
         *
         * @param codePoint
         * requested codePoint, required to be >=
         * MIN_SUPPLEMENTARY_CODE_POINT
         * @return low surrogate character
         */
        static getLowSurrogate(codePoint: number): string;
        private value;
        constructor(value: string);
        charValue(): string;
        compareTo(c?: any): any;
        equals(o: any): boolean;
        hashCode(): number;
        toString(): string;
    }
    namespace CharacterHelper {
        /**
         * Use nested class to avoid clinit on outer.
         */
        class BoxedValues {
            static boxedValues: javaemul.internal.CharacterHelper[];
            static boxedValues_$LI$(): javaemul.internal.CharacterHelper[];
        }
    }
}
declare namespace javaemul.internal {
    /**
     * Private implementation class for GWT. This API should not be
     * considered public or stable.
     */
    class Coercions {
        /**
         * Coerce js int to 32 bits.
         * Trick related to JS and lack of integer rollover.
         * {@see com.google.gwt.lang.Cast#narrow_int}
         */
        static ensureInt(value: number): number;
        constructor();
    }
}
declare namespace javaemul.internal {
    /**
     * Simple Helper class to return Date.now.
     */
    class DateUtil {
        /**
         * Returns the numeric value corresponding to the current time -
         * the number of milliseconds elapsed since 1 January 1970 00:00:00 UTC.
         */
        static now(): number;
    }
}
declare let Map: Object;
declare namespace javaemul.internal {
    /**
     * Contains logics for calculating hash codes in JavaScript.
     */
    class HashCodes {
        static sNextHashId: number;
        static HASH_CODE_PROPERTY: string;
        static hashCodeForString(s: string): number;
        static getIdentityHashCode(o: any): number;
        static getObjectIdentityHashCode(o: any): number;
        /**
         * Called from JSNI. Do not change this implementation without updating:
         * <ul>
         * <li>{@link com.google.gwt.user.client.rpc.impl.SerializerBase}</li>
         * </ul>
         */
        private static getNextHashId();
    }
}
declare namespace javaemul.internal {
    class JreHelper {
        static LOG10E: number;
        static LOG10E_$LI$(): number;
    }
}
declare namespace javaemul.internal {
    /**
     * Provides an interface for simple JavaScript idioms that can not be expressed in Java.
     */
    class JsUtils {
        static getInfinity(): number;
        static isUndefined(value: any): boolean;
        static unsafeCastToString(string: any): string;
        static setPropertySafe(map: any, key: string, value: any): void;
        static getIntProperty(map: any, key: string): number;
        static setIntProperty(map: any, key: string, value: number): void;
        static typeOf(o: any): string;
    }
}
declare namespace javaemul.internal {
    /**
     * A helper class for long comparison.
     */
    class LongCompareHolder {
        static getLongComparator(): any;
    }
}
declare namespace javaemul.internal {
    /**
     * Math utility methods and constants.
     */
    class MathHelper {
        static EPSILON: number;
        static EPSILON_$LI$(): number;
        static MAX_VALUE: number;
        static MAX_VALUE_$LI$(): number;
        static MIN_VALUE: number;
        static MIN_VALUE_$LI$(): number;
        static nextDown(x: number): number;
        static ulp(x: number): number;
        static nextUp(x: number): number;
        static E: number;
        static PI: number;
        static PI_OVER_180: number;
        static PI_OVER_180_$LI$(): number;
        static PI_UNDER_180: number;
        static PI_UNDER_180_$LI$(): number;
        static abs$double(x: number): number;
        static abs$float(x: number): number;
        static abs(x?: any): any;
        static abs$long(x: number): number;
        static acos(x: number): number;
        static asin(x: number): number;
        static atan(x: number): number;
        static atan2(y: number, x: number): number;
        static cbrt(x: number): number;
        static ceil(x: number): number;
        static copySign$double$double(magnitude: number, sign: number): number;
        static copySign(magnitude?: any, sign?: any): any;
        static cos(x: number): number;
        static cosh(x: number): number;
        static exp(x: number): number;
        static expm1(d: number): number;
        static floor(x: number): number;
        static hypot(x: number, y: number): number;
        static log(x: number): number;
        static log10(x: number): number;
        static log1p(x: number): number;
        static max$double$double(x: number, y: number): number;
        static max$float$float(x: number, y: number): number;
        static max(x?: any, y?: any): any;
        static max$long$long(x: number, y: number): number;
        static min$double$double(x: number, y: number): number;
        static min$float$float(x: number, y: number): number;
        static min(x?: any, y?: any): any;
        static min$long$long(x: number, y: number): number;
        static pow(x: number, exp: number): number;
        static random(): number;
        static rint(d: number): number;
        static round$double(x: number): number;
        static round(x?: any): any;
        private static unsafeCastToInt(d);
        static scalb$double$int(d: number, scaleFactor: number): number;
        static scalb(f?: any, scaleFactor?: any): any;
        static signum$double(d: number): number;
        static signum(f?: any): any;
        static sin(x: number): number;
        static sinh(x: number): number;
        static sqrt(x: number): number;
        static tan(x: number): number;
        static tanh(x: number): number;
        static toDegrees(x: number): number;
        static toRadians(x: number): number;
        static IEEEremainder(f1: number, f2: number): number;
    }
}
declare namespace javaemul.internal {
    /**
     * Abstract base class for numeric wrapper classes.
     */
    abstract class NumberHelper implements java.io.Serializable {
        /**
         * Stores a regular expression object to verify the format of float values.
         */
        static floatRegex: RegExp;
        /**
         * @skip
         *
         * This function will determine the radix that the string is expressed
         * in based on the parsing rules defined in the Javadocs for
         * Integer.decode() and invoke __parseAndValidateInt.
         */
        static __decodeAndValidateInt(s: string, lowerBound: number, upperBound: number): number;
        static __decodeNumberString(s: string): NumberHelper.__Decode;
        /**
         * @skip
         *
         * This function contains common logic for parsing a String as a
         * floating- point number and validating the range.
         */
        static __parseAndValidateDouble(s: string): number;
        /**
         * @skip
         *
         * This function contains common logic for parsing a String in a given
         * radix and validating the result.
         */
        static __parseAndValidateInt(s: string, radix: number, lowerBound: number, upperBound: number): number;
        /**
         * @skip
         *
         * This function contains common logic for parsing a String in a given
         * radix and validating the result.
         */
        static __parseAndValidateLong(s: string, radix: number): number;
        /**
         * @skip
         *
         * @param str
         * @return {@code true} if the string matches the float format,
         * {@code false} otherwise
         */
        static __isValidDouble(str: string): boolean;
        static createFloatRegex(): RegExp;
        byteValue(): number;
        abstract doubleValue(): number;
        abstract floatValue(): number;
        abstract intValue(): number;
        abstract longValue(): number;
        shortValue(): number;
        constructor();
    }
    namespace NumberHelper {
        class __Decode {
            payload: string;
            radix: number;
            constructor(radix: number, payload: string);
        }
        /**
         * Use nested class to avoid clinit on outer.
         */
        class __ParseLong {
            static __static_initialized: boolean;
            static __static_initialize(): void;
            /**
             * The number of digits (excluding minus sign and leading zeros) to
             * process at a time. The largest value expressible in maxDigits digits
             * as well as the factor radix^maxDigits must be strictly less than
             * 2^31.
             */
            static maxDigitsForRadix: number[];
            static maxDigitsForRadix_$LI$(): number[];
            /**
             * A table of values radix*maxDigitsForRadix[radix].
             */
            static maxDigitsRadixPower: number[];
            static maxDigitsRadixPower_$LI$(): number[];
            /**
             * The largest number of digits (excluding minus sign and leading zeros)
             * that can fit into a long for a given radix between 2 and 36,
             * inclusive.
             */
            static maxLengthForRadix: number[];
            static maxLengthForRadix_$LI$(): number[];
            /**
             * A table of floor(MAX_VALUE / maxDigitsRadixPower).
             */
            static maxValueForRadix: number[];
            static maxValueForRadix_$LI$(): number[];
            static __static_initializer_0(): void;
        }
    }
}
declare namespace javaemul.internal {
    class ObjectHelper {
        static clone(obj: any): any;
    }
}
declare namespace javaemul.internal {
    /**
     * Hashcode caching for strings.
     */
    class StringHashCache {
        /**
         * The "old" cache; it will be dumped when front is full.
         */
        static back: any;
        static back_$LI$(): any;
        /**
         * Tracks the number of entries in front.
         */
        static count: number;
        /**
         * The "new" cache; it will become back when it becomes full.
         */
        static front: any;
        static front_$LI$(): any;
        /**
         * Pulled this number out of thin air.
         */
        static MAX_CACHE: number;
        static getHashCode(str: string): number;
        private static compute(str);
        private static increment();
        private static getProperty(map, key);
        private static createNativeObject();
        private static unsafeCastToInt(o);
    }
}
/**
 * Declares equals and hashCode on JavaScript objects, for compilation.
 */
interface Object {
    equals(object: Object): boolean;
    hashCode(): number;
}
declare namespace test {
    class Test {
        static assertEquals(o1: any, o2: any): void;
        static assertTrue(b: boolean): void;
        static assertFalse(b: boolean): void;
        static test(): void;
        static testArrays(): void;
        static testList(): void;
        static testSet(): void;
        static testMap(): void;
        static testString(): void;
        static testIO(): void;
    }
}
declare namespace java.io {
    /**
     * A specialized {@link InputStream } for reading the contents of a byte array.
     *
     * @see ByteArrayOutputStream
     */
    class ByteArrayInputStream extends java.io.InputStream {
        /**
         * The {@code byte} array containing the bytes to stream over.
         */
        buf: number[];
        /**
         * The current position within the byte array.
         */
        pos: number;
        /**
         * The current mark position. Initially set to 0 or the <code>offset</code>
         * parameter within the constructor.
         */
        _mark: number;
        /**
         * The total number of bytes initially available in the byte array
         * {@code buf}.
         */
        count: number;
        /**
         * Constructs a new {@code ByteArrayInputStream} on the byte array
         * {@code buf} with the initial position set to {@code offset} and the
         * number of bytes available set to {@code offset} + {@code length}.
         *
         * @param buf
         * the byte array to stream over.
         * @param offset
         * the initial position in {@code buf} to start streaming from.
         * @param length
         * the number of bytes available for streaming.
         */
        constructor(buf: number[], offset?: number, length?: number);
        /**
         * Returns the number of remaining bytes.
         *
         * @return {@code count - pos}
         */
        available(): number;
        /**
         * Closes this stream and frees resources associated with this stream.
         *
         * @throws IOException
         * if an I/O error occurs while closing this stream.
         */
        close(): void;
        /**
         * Sets a mark position in this ByteArrayInputStream. The parameter
         * {@code readlimit} is ignored. Sending {@code reset()} will reposition the
         * stream back to the marked position.
         *
         * @param readlimit
         * ignored.
         * @see #markSupported()
         * @see #reset()
         */
        mark(readlimit: number): void;
        /**
         * Indicates whether this stream supports the {@code mark()} and
         * {@code reset()} methods. Returns {@code true} since this class supports
         * these methods.
         *
         * @return always {@code true}.
         * @see #mark(int)
         * @see #reset()
         */
        markSupported(): boolean;
        /**
         * Reads a single byte from the source byte array and returns it as an
         * integer in the range from 0 to 255. Returns -1 if the end of the source
         * array has been reached.
         *
         * @return the byte read or -1 if the end of this stream has been reached.
         */
        read$(): number;
        read(buffer?: any, byteOffset?: any, byteCount?: any): any;
        /**
         * Resets this stream to the last marked location. This implementation
         * resets the position to either the marked position, the start position
         * supplied in the constructor or 0 if neither has been provided.
         *
         * @see #mark(int)
         */
        reset(): void;
        /**
         * Skips {@code byteCount} bytes in this InputStream. Subsequent calls to
         * {@code read} will not return these bytes unless {@code reset} is used.
         * This implementation skips {@code byteCount} number of bytes in the target
         * stream. It does nothing and returns 0 if {@code byteCount} is negative.
         *
         * @return the number of bytes actually skipped.
         */
        skip(byteCount: number): number;
    }
}
declare namespace java.io {
    /**
     * Wraps an existing {@link InputStream} and performs some transformation on
     * the input data while it is being read. Transformations can be anything from a
     * simple byte-wise filtering input data to an on-the-fly compression or
     * decompression of the underlying stream. Input streams that wrap another input
     * stream and provide some additional functionality on top of it usually inherit
     * from this class.
     *
     * @see FilterOutputStream
     */
    class FilterInputStream extends java.io.InputStream {
        /**
         * The source input stream that is filtered.
         */
        in: java.io.InputStream;
        /**
         * Constructs a new {@code FilterInputStream} with the specified input
         * stream as source.
         *
         * <p><strong>Warning:</strong> passing a null source creates an invalid
         * {@code FilterInputStream}, that fails on every method that is not
         * overridden. Subclasses should check for null in their constructors.
         *
         * @param in the input stream to filter reads on.
         */
        constructor(__in: java.io.InputStream);
        available(): number;
        /**
         * Closes this stream. This implementation closes the filtered stream.
         *
         * @throws IOException
         * if an error occurs while closing this stream.
         */
        close(): void;
        /**
         * Sets a mark position in this stream. The parameter {@code readlimit}
         * indicates how many bytes can be read before the mark is invalidated.
         * Sending {@code reset()} will reposition this stream back to the marked
         * position, provided that {@code readlimit} has not been surpassed.
         * <p>
         * This implementation sets a mark in the filtered stream.
         *
         * @param readlimit
         * the number of bytes that can be read from this stream before
         * the mark is invalidated.
         * @see #markSupported()
         * @see #reset()
         */
        mark(readlimit: number): void;
        /**
         * Indicates whether this stream supports {@code mark()} and {@code reset()}.
         * This implementation returns whether or not the filtered stream supports
         * marking.
         *
         * @return {@code true} if {@code mark()} and {@code reset()} are supported,
         * {@code false} otherwise.
         * @see #mark(int)
         * @see #reset()
         * @see #skip(long)
         */
        markSupported(): boolean;
        /**
         * Reads a single byte from the filtered stream and returns it as an integer
         * in the range from 0 to 255. Returns -1 if the end of this stream has been
         * reached.
         *
         * @return the byte read or -1 if the end of the filtered stream has been
         * reached.
         * @throws IOException
         * if the stream is closed or another IOException occurs.
         */
        read$(): number;
        read(buffer?: any, byteOffset?: any, byteCount?: any): any;
        /**
         * Resets this stream to the last marked location. This implementation
         * resets the target stream.
         *
         * @throws IOException
         * if this stream is already closed, no mark has been set or the
         * mark is no longer valid because more than {@code readlimit}
         * bytes have been read since setting the mark.
         * @see #mark(int)
         * @see #markSupported()
         */
        reset(): void;
        /**
         * Skips {@code byteCount} bytes in this stream. Subsequent
         * calls to {@code read} will not return these bytes unless {@code reset} is
         * used. This implementation skips {@code byteCount} bytes in the
         * filtered stream.
         *
         * @return the number of bytes actually skipped.
         * @throws IOException
         * if this stream is closed or another IOException occurs.
         * @see #mark(int)
         * @see #reset()
         */
        skip(byteCount: number): number;
    }
}
declare namespace java.io {
    /**
     * A specialized {@link OutputStream} for class for writing content to an
     * (internal) byte array. As bytes are written to this stream, the byte array
     * may be expanded to hold more bytes. When the writing is considered to be
     * finished, a copy of the byte array can be requested from the class.
     *
     * @see ByteArrayInputStream
     */
    class ByteArrayOutputStream extends java.io.OutputStream {
        /**
         * The byte array containing the bytes written.
         */
        buf: number[];
        /**
         * The number of bytes written.
         */
        count: number;
        /**
         * Constructs a new {@code ByteArrayOutputStream} with a default size of
         * {@code size} bytes. If more than {@code size} bytes are written to this
         * instance, the underlying byte array will expand.
         *
         * @param size
         * initial size for the underlying byte array, must be
         * non-negative.
         * @throws IllegalArgumentException
         * if {@code size} < 0.
         */
        constructor(size?: any);
        /**
         * Closes this stream. This releases system resources used for this stream.
         *
         * @throws IOException
         * if an error occurs while attempting to close this stream.
         */
        close(): void;
        private expand(i);
        /**
         * Resets this stream to the beginning of the underlying byte array. All
         * subsequent writes will overwrite any bytes previously stored in this
         * stream.
         */
        reset(): void;
        /**
         * Returns the total number of bytes written to this stream so far.
         *
         * @return the number of bytes written to this stream.
         */
        size(): number;
        /**
         * Returns the contents of this ByteArrayOutputStream as a byte array. Any
         * changes made to the receiver after returning will not be reflected in the
         * byte array returned to the caller.
         *
         * @return this stream's current contents as a byte array.
         */
        toByteArray(): number[];
        /**
         * Returns the contents of this ByteArrayOutputStream as a string. Any
         * changes made to the receiver after returning will not be reflected in the
         * string returned to the caller.
         *
         * @return this stream's current contents as a string.
         */
        toString$(): string;
        /**
         * Returns the contents of this ByteArrayOutputStream as a string. Each byte
         * {@code b} in this stream is converted to a character {@code c} using the
         * following function:
         * {@code c == (char)(((hibyte & 0xff) << 8) | (b & 0xff))}. This method is
         * deprecated and either {@link #toString()} or {@link #toString(String)}
         * should be used.
         *
         * @param hibyte
         * the high byte of each resulting Unicode character.
         * @return this stream's current contents as a string with the high byte set
         * to {@code hibyte}.
         * @deprecated Use {@link #toString()} instead.
         */
        toString$int(hibyte: number): string;
        /**
         * Returns the contents of this ByteArrayOutputStream as a string converted
         * according to the encoding declared in {@code charsetName}.
         *
         * @param charsetName
         * a string representing the encoding to use when translating
         * this stream to a string.
         * @return this stream's current contents as an encoded string.
         * @throws UnsupportedEncodingException
         * if the provided encoding is not supported.
         */
        toString(charsetName?: any): any;
        /**
         * Writes {@code count} bytes from the byte array {@code buffer} starting at
         * offset {@code index} to this stream.
         *
         * @param buffer
         * the buffer to be written.
         * @param offset
         * the initial position in {@code buffer} to retrieve bytes.
         * @param len
         * the number of bytes of {@code buffer} to write.
         * @throws NullPointerException
         * if {@code buffer} is {@code null}.
         * @throws IndexOutOfBoundsException
         * if {@code offset < 0} or {@code len < 0}, or if
         * {@code offset + len} is greater than the length of
         * {@code buffer}.
         */
        write(buffer?: any, offset?: any, len?: any): any;
        /**
         * Writes the specified byte {@code oneByte} to the OutputStream. Only the
         * low order byte of {@code oneByte} is written.
         *
         * @param oneByte
         * the byte to be written.
         */
        write$int(oneByte: number): void;
        /**
         * Takes the contents of this stream and writes it to the output stream
         * {@code out}.
         *
         * @param out
         * an OutputStream on which to write the contents of this stream.
         * @throws IOException
         * if an error occurs while writing to {@code out}.
         */
        writeTo(out: java.io.OutputStream): void;
    }
}
declare namespace java.io {
    /**
     * Wraps an existing {@link OutputStream} and performs some transformation on
     * the output data while it is being written. Transformations can be anything
     * from a simple byte-wise filtering output data to an on-the-fly compression or
     * decompression of the underlying stream. Output streams that wrap another
     * output stream and provide some additional functionality on top of it usually
     * inherit from this class.
     *
     * @see FilterOutputStream
     */
    class FilterOutputStream extends java.io.OutputStream {
        /**
         * The target output stream for this filter stream.
         */
        out: java.io.OutputStream;
        /**
         * Constructs a new {@code FilterOutputStream} with {@code out} as its
         * target stream.
         *
         * @param out
         * the target stream that this stream writes to.
         */
        constructor(out: java.io.OutputStream);
        /**
         * Closes this stream. This implementation closes the target stream.
         *
         * @throws IOException
         * if an error occurs attempting to close this stream.
         */
        close(): void;
        /**
         * Ensures that all pending data is sent out to the target stream. This
         * implementation flushes the target stream.
         *
         * @throws IOException
         * if an error occurs attempting to flush this stream.
         */
        flush(): void;
        /**
         * Writes {@code count} bytes from the byte array {@code buffer} starting at
         * {@code offset} to the target stream.
         *
         * @param buffer
         * the buffer to write.
         * @param offset
         * the index of the first byte in {@code buffer} to write.
         * @param length
         * the number of bytes in {@code buffer} to write.
         * @throws IndexOutOfBoundsException
         * if {@code offset < 0} or {@code count < 0}, or if
         * {@code offset + count} is bigger than the length of
         * {@code buffer}.
         * @throws IOException
         * if an I/O error occurs while writing to this stream.
         */
        write(buffer?: any, offset?: any, length?: any): any;
        /**
         * Writes one byte to the target stream. Only the low order byte of the
         * integer {@code oneByte} is written.
         *
         * @param oneByte
         * the byte to be written.
         * @throws IOException
         * if an I/O error occurs while writing to this stream.
         */
        write$int(oneByte: number): void;
    }
}
declare namespace java.io {
    /**
     * JSweet implementation.
     */
    class BufferedReader extends java.io.Reader {
        private in;
        private cb;
        private nChars;
        private nextChar;
        static INVALIDATED: number;
        static UNMARKED: number;
        private markedChar;
        private readAheadLimit;
        private skipLF;
        private markedSkipLF;
        static defaultCharBufferSize: number;
        static defaultExpectedLineLength: number;
        constructor(__in?: any, sz?: any);
        private ensureOpen();
        private fill();
        read$(): number;
        private read1(cbuf, off, len);
        read(cbuf?: any, off?: any, len?: any): any;
        readLine(ignoreLF?: boolean): string;
        skip(n: number): number;
        ready(): boolean;
        markSupported(): boolean;
        mark(readAheadLimit: number): void;
        reset(): void;
        close(): void;
    }
}
declare namespace java.io {
    /**
     * JSweet implementation.
     */
    class InputStreamReader extends java.io.Reader {
        in: java.io.InputStream;
        constructor(__in?: any, charsetName?: any);
        read(cbuf?: any, offset?: any, length?: any): any;
        ready(): boolean;
        close(): void;
    }
}
declare namespace java.io {
    /**
     * JSweet implementation (partial).
     *
     * TODO: actual support of charsets.
     */
    class OutputStreamWriter extends java.io.Writer {
        private out;
        constructor(out?: any, charsetName?: any);
        flushBuffer(): void;
        write$int(c: number): void;
        write(cbuf?: any, off?: any, len?: any): any;
        write$java_lang_String$int$int(str: string, off: number, len: number): void;
        flush(): void;
        close(): void;
    }
}
declare namespace java.lang {
    /**
     * A fast way to create strings using multiple appends.
     *
     * This class is an exact clone of {@link StringBuilder} except for the name.
     * Any change made to one should be mirrored in the other.
     */
    class StringBuffer extends java.lang.AbstractStringBuilder implements java.lang.CharSequence, java.lang.Appendable {
        constructor(s?: any);
        append$boolean(x: boolean): java.lang.StringBuffer;
        append$char(x: string): java.lang.StringBuffer;
        append$char_A(x: string[]): java.lang.StringBuffer;
        append(x?: any, start?: any, len?: any): any;
        append$java_lang_CharSequence(x: string): java.lang.StringBuffer;
        append$java_lang_CharSequence$int$int(x: string, start: number, end: number): java.lang.StringBuffer;
        append$double(x: number): java.lang.StringBuffer;
        append$float(x: number): java.lang.StringBuffer;
        append$int(x: number): java.lang.StringBuffer;
        append$long(x: number): java.lang.StringBuffer;
        append$java_lang_Object(x: any): java.lang.StringBuffer;
        append$java_lang_String(x: string): java.lang.StringBuffer;
        append$java_lang_StringBuffer(x: java.lang.StringBuffer): java.lang.StringBuffer;
        appendCodePoint(x: number): java.lang.StringBuffer;
        delete(start: number, end: number): java.lang.StringBuffer;
        deleteCharAt(start: number): java.lang.StringBuffer;
        insert$int$boolean(index: number, x: boolean): java.lang.StringBuffer;
        insert$int$char(index: number, x: string): java.lang.StringBuffer;
        insert$int$char_A(index: number, x: string[]): java.lang.StringBuffer;
        insert(index?: any, x?: any, offset?: any, len?: any): any;
        insert$int$java_lang_CharSequence(index: number, chars: string): java.lang.StringBuffer;
        insert$int$java_lang_CharSequence$int$int(index: number, chars: string, start: number, end: number): java.lang.StringBuffer;
        insert$int$double(index: number, x: number): java.lang.StringBuffer;
        insert$int$float(index: number, x: number): java.lang.StringBuffer;
        insert$int$int(index: number, x: number): java.lang.StringBuffer;
        insert$int$long(index: number, x: number): java.lang.StringBuffer;
        insert$int$java_lang_Object(index: number, x: any): java.lang.StringBuffer;
        insert$int$java_lang_String(index: number, x: string): java.lang.StringBuffer;
        replace(start: number, end: number, toInsert: string): java.lang.StringBuffer;
        reverse(): java.lang.StringBuffer;
    }
}
declare namespace java.lang {
    /**
     * A fast way to create strings using multiple appends.
     *
     * This class is an exact clone of {@link StringBuffer} except for the name. Any
     * change made to one should be mirrored in the other.
     */
    class StringBuilder extends java.lang.AbstractStringBuilder implements java.lang.CharSequence, java.lang.Appendable {
        constructor(s?: any);
        append$boolean(x: boolean): java.lang.StringBuilder;
        append$char(x: string): java.lang.StringBuilder;
        append$char_A(x: string[]): java.lang.StringBuilder;
        append(x?: any, start?: any, len?: any): any;
        append$java_lang_CharSequence(x: string): java.lang.StringBuilder;
        append$java_lang_CharSequence$int$int(x: string, start: number, end: number): java.lang.StringBuilder;
        append$double(x: number): java.lang.StringBuilder;
        append$float(x: number): java.lang.StringBuilder;
        append$int(x: number): java.lang.StringBuilder;
        append$long(x: number): java.lang.StringBuilder;
        append$java_lang_Object(x: any): java.lang.StringBuilder;
        append$java_lang_String(x: string): java.lang.StringBuilder;
        append$java_lang_StringBuffer(x: java.lang.StringBuffer): java.lang.StringBuilder;
        appendCodePoint(x: number): java.lang.StringBuilder;
        delete(start: number, end: number): java.lang.StringBuilder;
        deleteCharAt(start: number): java.lang.StringBuilder;
        insert$int$boolean(index: number, x: boolean): java.lang.StringBuilder;
        insert$int$char(index: number, x: string): java.lang.StringBuilder;
        insert$int$char_A(index: number, x: string[]): java.lang.StringBuilder;
        insert(index?: any, x?: any, offset?: any, len?: any): any;
        insert$int$java_lang_CharSequence(index: number, chars: string): java.lang.StringBuilder;
        insert$int$java_lang_CharSequence$int$int(index: number, chars: string, start: number, end: number): java.lang.StringBuilder;
        insert$int$double(index: number, x: number): java.lang.StringBuilder;
        insert$int$float(index: number, x: number): java.lang.StringBuilder;
        insert$int$int(index: number, x: number): java.lang.StringBuilder;
        insert$int$long(index: number, x: number): java.lang.StringBuilder;
        insert$int$java_lang_Object(index: number, x: any): java.lang.StringBuilder;
        insert$int$java_lang_String(index: number, x: string): java.lang.StringBuilder;
        replace(start: number, end: number, toInsert: string): java.lang.StringBuilder;
        reverse(): java.lang.StringBuilder;
    }
}
declare namespace java.io {
    /**
     * See <a
     * href="http://java.sun.com/javase/6/docs/api/java/io/IOException.html">the
     * official Java API doc</a> for details.
     */
    class IOException extends Error {
        constructor(message?: any, throwable?: any);
    }
}
declare namespace java.lang {
    /**
     * See <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/CloneNotSupportedException.html">
     * the official Java API doc</a> for details.
     */
    class CloneNotSupportedException extends Error {
        constructor(msg?: any);
    }
}
declare namespace java.lang {
    /**
     * See <a href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/NoSuchMethodException.html">the
     * official Java API doc</a> for details.
     *
     * This exception is never thrown by GWT or GWT's libraries, as GWT does not support reflection. It
     * is provided in GWT only for compatibility with user code that explicitly throws or catches it for
     * non-reflection purposes.
     */
    class NoSuchMethodException extends Error {
        constructor(message?: any);
    }
}
declare namespace java.lang {
    /**
     * See <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/RuntimeException.html">the
     * official Java API doc</a> for details.
     */
    class RuntimeException extends Error {
        constructor(message?: any, cause?: any, enableSuppression?: any, writableStackTrace?: any);
    }
}
declare namespace java.security {
    /**
     * A generic security exception type - <a
     * href="http://java.sun.com/j2se/1.4.2/docs/api/java/security/GeneralSecurityException.html">[Sun's
     * docs]</a>.
     */
    class GeneralSecurityException extends Error {
        constructor(msg?: any);
    }
}
declare namespace java.text {
    /**
     * Emulation of {@code java.text.ParseException}.
     */
    class ParseException extends Error {
        private errorOffset;
        constructor(s: string, errorOffset: number);
        getErrorOffset(): number;
    }
}
declare namespace java.util {
    /**
     * Thrown when the subject of an observer cannot support additional observers.
     *
     */
    class TooManyListenersException extends Error {
        constructor(message?: any);
    }
}
declare namespace java.lang.ref {
    /**
     * This implements the reference API in a minimal way. In JavaScript, there is
     * no control over the reference and the GC. So this implementation's only
     * purpose is for compilation.
     */
    class WeakReference<T> extends java.lang.ref.Reference<T> {
        constructor(referent: T);
    }
}
declare namespace java.lang {
    /**
     * Thrown to indicate some unexpected internal error has occurred in
     * the Java Virtual Machine.
     *
     * @author  unascribed
     * @since   JDK1.0
     */
    class InternalError extends java.lang.VirtualMachineError {
        static serialVersionUID: number;
        /**
         * Constructs an {@code InternalError} with the specified detail
         * message and cause.  <p>Note that the detail message associated
         * with {@code cause} is <i>not</i> automatically incorporated in
         * this error's detail message.
         *
         * @param  message the detail message (which is saved for later retrieval
         * by the {@link #getMessage()} method).
         * @param  cause the cause (which is saved for later retrieval by the
         * {@link #getCause()} method).  (A {@code null} value is
         * permitted, and indicates that the cause is nonexistent or
         * unknown.)
         * @since  1.8
         */
        constructor(message?: any, cause?: any);
    }
}
declare namespace javaemul.internal {
    /**
     * Provides Charset implementations.
     */
    abstract class EmulatedCharset extends java.nio.charset.Charset {
        static UTF_8: EmulatedCharset;
        static UTF_8_$LI$(): EmulatedCharset;
        static ISO_LATIN_1: EmulatedCharset;
        static ISO_LATIN_1_$LI$(): EmulatedCharset;
        static ISO_8859_1: EmulatedCharset;
        static ISO_8859_1_$LI$(): EmulatedCharset;
        constructor(name: string);
        abstract getBytes(string: string): number[];
        abstract decodeString(bytes: number[], ofs: number, len: number): string[];
    }
    namespace EmulatedCharset {
        class LatinCharset extends javaemul.internal.EmulatedCharset {
            constructor(name: string);
            getBytes(str: string): number[];
            decodeString(bytes: number[], ofs: number, len: number): string[];
        }
        class UtfCharset extends javaemul.internal.EmulatedCharset {
            constructor(name: string);
            decodeString(bytes: number[], ofs: number, len: number): string[];
            getBytes(str: string): number[];
            /**
             * Encode a single character in UTF8.
             *
             * @param bytes byte array to store character in
             * @param ofs offset into byte array to store first byte
             * @param codePoint character to encode
             * @return number of bytes consumed by encoding the character
             * @throws IllegalArgumentException if codepoint >= 2^26
             */
            encodeUtf8(bytes: number[], ofs: number, codePoint: number): number;
        }
    }
}
declare namespace java.security {
    /**
     * Message Digest algorithm - <a href=
     * "http://java.sun.com/j2se/1.4.2/docs/api/java/security/MessageDigest.html"
     * >[Sun's docs]</a>.
     */
    abstract class MessageDigest extends java.security.MessageDigestSpi {
        static getInstance(algorithm: string): MessageDigest;
        static isEqual(digestA: number[], digestB: number[]): boolean;
        private algorithm;
        constructor(algorithm: string);
        digest$(): number[];
        digest$byte_A(input: number[]): number[];
        digest(buf?: any, offset?: any, len?: any): any;
        getAlgorithm(): string;
        getDigestLength(): number;
        reset(): void;
        update$byte(input: number): void;
        update$byte_A(input: number[]): void;
        update(input?: any, offset?: any, len?: any): any;
    }
    namespace MessageDigest {
        class Md5Digest extends java.security.MessageDigest {
            static padding: number[];
            static padding_$LI$(): number[];
            /**
             * Converts a long to a 8-byte array using low order first.
             *
             * @param n A long.
             * @return A byte[].
             */
            static toBytes(n: number): number[];
            /**
             * Converts a 64-byte array into a 16-int array.
             *
             * @param in A byte[].
             * @param out An int[].
             */
            static byte2int(__in: number[], out: number[]): void;
            static f(x: number, y: number, z: number): number;
            static ff(a: number, b: number, c: number, d: number, x: number, s: number, ac: number): number;
            static g(x: number, y: number, z: number): number;
            static gg(a: number, b: number, c: number, d: number, x: number, s: number, ac: number): number;
            static h(x: number, y: number, z: number): number;
            static hh(a: number, b: number, c: number, d: number, x: number, s: number, ac: number): number;
            static i(x: number, y: number, z: number): number;
            static ii(a: number, b: number, c: number, d: number, x: number, s: number, ac: number): number;
            /**
             * Converts a 4-int array into a 16-byte array.
             *
             * @param in An int[].
             * @param out A byte[].
             */
            static int2byte(__in: number[], out: number[]): void;
            buffer: number[];
            counter: number;
            oneByte: number[];
            remainder: number;
            state: number[];
            x: number[];
            constructor();
            engineDigest$(): number[];
            engineGetDigestLength(): number;
            engineReset(): void;
            engineUpdate$byte(input: number): void;
            engineUpdate(input?: any, offset?: any, len?: any): any;
            transform(buffer: number[]): void;
        }
    }
}
declare namespace java.util {
    /**
     * Skeletal implementation of the List interface. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/AbstractList.html">[Sun
     * docs]</a>
     *
     * @param <E> the element type.
     */
    abstract class AbstractList<E> extends java.util.AbstractCollection<E> implements java.util.List<E> {
        forEach(action: (p1: any) => void): void;
        abstract size(): any;
        modCount: number;
        constructor();
        add$java_lang_Object(obj: E): boolean;
        add(index?: any, element?: any): any;
        addAll(index?: any, c?: any): any;
        clear(): void;
        equals(o: any): boolean;
        abstract get(index: number): E;
        hashCode(): number;
        indexOf(o?: any, index?: any): any;
        indexOf$java_lang_Object(toFind: any): number;
        iterator(): java.util.Iterator<E>;
        lastIndexOf(o?: any, index?: any): any;
        lastIndexOf$java_lang_Object(toFind: any): number;
        listIterator$(): java.util.ListIterator<E>;
        listIterator(from?: any): any;
        remove(index?: any): any;
        set(index: number, o: E): E;
        subList(fromIndex: number, toIndex: number): java.util.List<E>;
        removeRange(fromIndex: number, endIndex: number): void;
    }
    namespace AbstractList {
        class IteratorImpl implements java.util.Iterator<any> {
            __parent: any;
            forEachRemaining(consumer: (p1: any) => void): void;
            i: number;
            last: number;
            constructor(__parent: any);
            hasNext(): boolean;
            next(): any;
            remove(): void;
        }
        /**
         * Implementation of <code>ListIterator</code> for abstract lists.
         */
        class ListIteratorImpl extends AbstractList.IteratorImpl implements java.util.ListIterator<any> {
            __parent: any;
            forEachRemaining(consumer: (p1: any) => void): void;
            constructor(__parent: any, start?: any);
            add(o: any): void;
            hasPrevious(): boolean;
            nextIndex(): number;
            previous(): any;
            previousIndex(): number;
            set(o: any): void;
        }
        class SubList<E> extends java.util.AbstractList<E> {
            wrapped: java.util.List<E>;
            fromIndex: number;
            __size: number;
            constructor(wrapped: java.util.List<E>, fromIndex: number, toIndex: number);
            add(index?: any, element?: any): any;
            get(index: number): E;
            remove(index?: any): any;
            set(index: number, element: E): E;
            size(): number;
        }
    }
}
declare namespace java.util {
    /**
     * Skeletal implementation of the Queue interface. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/AbstractQueue.html">[Sun
     * docs]</a>
     *
     * @param <E> element type.
     */
    abstract class AbstractQueue<E> extends java.util.AbstractCollection<E> implements java.util.Queue<E> {
        forEach(action: (p1: any) => void): void;
        abstract iterator(): any;
        abstract size(): any;
        constructor();
        add(index?: any, element?: any): any;
        add$java_lang_Object(o: E): boolean;
        addAll(index?: any, c?: any): any;
        addAll$java_util_Collection(c: java.util.Collection<any>): boolean;
        clear(): void;
        element(): E;
        abstract offer(o: E): boolean;
        abstract peek(): E;
        abstract poll(): E;
        remove(index?: any): any;
        remove$(): E;
    }
}
declare namespace java.util {
    /**
     * Skeletal implementation of the Set interface. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/AbstractSet.html">[Sun
     * docs]</a>
     *
     * @param <E> the element type.
     */
    abstract class AbstractSet<E> extends java.util.AbstractCollection<E> implements java.util.Set<E> {
        forEach(action: (p1: any) => void): void;
        abstract iterator(): any;
        abstract size(): any;
        equals(o: any): boolean;
        hashCode(): number;
        removeAll(c: java.util.Collection<any>): boolean;
        constructor();
    }
}
declare namespace java.util {
    /**
     * A simple wrapper around JavaScript Map for key type is string.
     */
    class InternalStringMap<K, V> implements java.lang.Iterable<java.util.Map.Entry<K, V>> {
        forEach(action: (p1: any) => void): void;
        private backingMap;
        private host;
        private size;
        /**
         * A mod count to track 'value' replacements in map to ensure that the
         * 'value' that we have in the iterator entry is guaranteed to be still
         * correct. This is to optimize for the common scenario where the values are
         * not modified during iterations where the entries are never stale.
         */
        private valueMod;
        constructor(host: java.util.AbstractHashMap<K, V>);
        contains(key: string): boolean;
        get(key: string): V;
        put(key: string, value: V): V;
        remove(key: string): V;
        getSize(): number;
        iterator(): java.util.Iterator<java.util.Map.Entry<K, V>>;
        private newMapEntry(entry, lastValueMod);
        private static toNullIfUndefined<T>(value);
    }
    namespace InternalStringMap {
        class InternalStringMap$0 implements java.util.Iterator<java.util.Map.Entry<any, any>> {
            __parent: any;
            forEachRemaining(consumer: (p1: any) => void): void;
            entries: java.util.InternalJsMap.Iterator<any>;
            current: java.util.InternalJsMap.IteratorEntry<any>;
            last: java.util.InternalJsMap.IteratorEntry<any>;
            hasNext(): boolean;
            next(): java.util.Map.Entry<any, any>;
            remove(): void;
            constructor(__parent: any);
        }
        class InternalStringMap$1 extends java.util.AbstractMapEntry<any, any> {
            private entry;
            private lastValueMod;
            __parent: any;
            getKey(): any;
            getValue(): any;
            setValue(object: any): any;
            constructor(__parent: any, entry: any, lastValueMod: any);
        }
    }
}
declare namespace javaemul.internal {
    /**
     * Intrinsic string class.
     */
    class StringHelper {
        static CASE_INSENSITIVE_ORDER: java.util.Comparator<string>;
        static CASE_INSENSITIVE_ORDER_$LI$(): java.util.Comparator<string>;
        static copyValueOf$char_A(v: string[]): string;
        static copyValueOf(v?: any, offset?: any, count?: any): any;
        static valueOf$boolean(x: boolean): string;
        static valueOf$char(x: string): string;
        static valueOf(x?: any, offset?: any, count?: any): any;
        private static fromCharCode(array);
        static valueOf$char_A(x: string[]): string;
        static valueOf$double(x: number): string;
        static valueOf$float(x: number): string;
        static valueOf$int(x: number): string;
        static valueOf$long(x: number): string;
        static valueOf$java_lang_Object(x: any): string;
        /**
         * This method converts Java-escaped dollar signs "\$" into
         * JavaScript-escaped dollar signs "$$", and removes all other lone
         * backslashes, which serve as escapes in Java but are passed through
         * literally in JavaScript.
         *
         * @skip
         */
        private static translateReplaceString(replaceStr);
        private static compareTo(thisStr, otherStr);
        private static getCharset(charsetName);
        static fromCodePoint(codePoint: number): string;
        static format(formatString: string, ...args: any[]): string;
        constructor();
    }
    namespace StringHelper {
        class StringHelper$0 implements java.util.Comparator<string> {
            compare(a: string, b: string): number;
            constructor();
        }
    }
}
declare namespace java.sql {
    /**
     * An implementation of java.sql.Date. Derived from
     * http://java.sun.com/j2se/1.5.0/docs/api/java/sql/Date.html
     */
    class Date extends java.util.Date {
        static valueOf(s: string): Date;
        constructor(year?: any, month?: any, day?: any);
        getHours(): number;
        getMinutes(): number;
        getSeconds(): number;
        setHours(i: number): void;
        setMinutes(i: number): void;
        setSeconds(i: number): void;
    }
}
declare namespace java.sql {
    /**
     * An implementation of java.sql.Time. Derived from
     * http://java.sun.com/j2se/1.5.0/docs/api/java/sql/Time.html
     */
    class Time extends java.util.Date {
        static valueOf(s: string): Time;
        constructor(hour?: any, minute?: any, second?: any);
        getDate(): number;
        getDay(): number;
        getMonth(): number;
        getYear(): number;
        setDate(i: number): void;
        setMonth(i: number): void;
        setYear(i: number): void;
    }
}
declare namespace java.sql {
    /**
     * An implementation of java.sql.Timestame. Derived from
     * http://java.sun.com/j2se/1.5.0/docs/api/java/sql/Timestamp.html. This is
     * basically just regular Date decorated with a nanoseconds field.
     */
    class Timestamp extends java.util.Date {
        static valueOf(s: string): Timestamp;
        private static padNine(value);
        /**
         * Stores the nanosecond resolution of the timestamp; must be kept in sync
         * with the sub-second part of Date.millis.
         */
        private nanos;
        constructor(year?: any, month?: any, date?: any, hour?: any, minute?: any, second?: any, nano?: any);
        after(ts?: any): any;
        before(ts?: any): any;
        compareTo$java_util_Date(o: java.util.Date): number;
        compareTo(o?: any): any;
        equals$java_lang_Object(ts: any): boolean;
        equals(ts?: any): any;
        getNanos(): number;
        getTime(): number;
        hashCode(): number;
        setNanos(n: number): void;
        setTime(time: number): void;
    }
}
declare namespace java.util.logging {
    /**
     * A simple console logger used in super dev mode.
     */
    class SimpleConsoleLogHandler extends java.util.logging.Handler {
        publish(record: java.util.logging.LogRecord): void;
        private toConsoleLogLevel(level);
        close(): void;
        flush(): void;
    }
}
declare namespace javaemul.internal {
    /**
     * Wraps native <code>byte</code> as an object.
     */
    class ByteHelper extends javaemul.internal.NumberHelper implements java.lang.Comparable<ByteHelper> {
        static MIN_VALUE: number;
        static MIN_VALUE_$LI$(): number;
        static MAX_VALUE: number;
        static MAX_VALUE_$LI$(): number;
        static SIZE: number;
        static TYPE: any;
        static TYPE_$LI$(): any;
        static compare(x: number, y: number): number;
        static decode(s: string): ByteHelper;
        /**
         * @skip
         *
         * Here for shared implementation with Arrays.hashCode
         */
        static hashCode(b: number): number;
        static parseByte(s: string, radix?: number): number;
        static toString(b: number): string;
        static valueOf$byte(b: number): ByteHelper;
        static valueOf$java_lang_String(s: string): ByteHelper;
        static valueOf(s?: any, radix?: any): any;
        private value;
        constructor(s?: any);
        byteValue(): number;
        compareTo(b?: any): any;
        doubleValue(): number;
        equals(o: any): boolean;
        floatValue(): number;
        hashCode(): number;
        intValue(): number;
        longValue(): number;
        shortValue(): number;
        toString(): string;
    }
    namespace ByteHelper {
        /**
         * Use nested class to avoid clinit on outer.
         */
        class BoxedValues {
            static boxedValues: javaemul.internal.ByteHelper[];
            static boxedValues_$LI$(): javaemul.internal.ByteHelper[];
        }
    }
}
declare namespace javaemul.internal {
    /**
     * Wraps a primitive <code>double</code> as an object.
     */
    class DoubleHelper extends javaemul.internal.NumberHelper implements java.lang.Comparable<DoubleHelper> {
        static MAX_VALUE: number;
        static MIN_VALUE: number;
        static MIN_NORMAL: number;
        static MAX_EXPONENT: number;
        static MIN_EXPONENT: number;
        static NaN: number;
        static NaN_$LI$(): number;
        static NEGATIVE_INFINITY: number;
        static NEGATIVE_INFINITY_$LI$(): number;
        static POSITIVE_INFINITY: number;
        static POSITIVE_INFINITY_$LI$(): number;
        static SIZE: number;
        static POWER_512: number;
        static POWER_MINUS_512: number;
        static POWER_256: number;
        static POWER_MINUS_256: number;
        static POWER_128: number;
        static POWER_MINUS_128: number;
        static POWER_64: number;
        static POWER_MINUS_64: number;
        static POWER_52: number;
        static POWER_MINUS_52: number;
        static POWER_32: number;
        static POWER_MINUS_32: number;
        static POWER_31: number;
        static POWER_20: number;
        static POWER_MINUS_20: number;
        static POWER_16: number;
        static POWER_MINUS_16: number;
        static POWER_8: number;
        static POWER_MINUS_8: number;
        static POWER_4: number;
        static POWER_MINUS_4: number;
        static POWER_2: number;
        static POWER_MINUS_2: number;
        static POWER_1: number;
        static POWER_MINUS_1: number;
        static POWER_MINUS_1022: number;
        static compare(x: number, y: number): number;
        static doubleToLongBits(value: number): number;
        /**
         * @skip Here for shared implementation with Arrays.hashCode
         */
        static hashCode(d: number): number;
        static isInfinite(x: number): boolean;
        static isNaN(x: number): boolean;
        static longBitsToDouble(bits: number): number;
        static parseDouble(s: string): number;
        static toString(b: number): string;
        static valueOf$double(d: number): DoubleHelper;
        static valueOf(s?: any): any;
        constructor(s?: any);
        byteValue(): number;
        compareTo(b?: any): any;
        doubleValue(): number;
        static unsafeCast(instance: any): number;
        equals(o: any): boolean;
        floatValue(): number;
        /**
         * Performance caution: using Double objects as map keys is not recommended.
         * Using double values as keys is generally a bad idea due to difficulty
         * determining exact equality. In addition, there is no efficient JavaScript
         * equivalent of <code>doubleToIntBits</code>. As a result, this method
         * computes a hash code by truncating the whole number portion of the
         * double, which may lead to poor performance for certain value sets if
         * Doubles are used as keys in a {@link java.util.HashMap}.
         */
        hashCode(): number;
        intValue(): number;
        isInfinite(): boolean;
        isNaN(): boolean;
        longValue(): number;
        shortValue(): number;
        toString(): string;
    }
    namespace DoubleHelper {
        class PowersTable {
            static powers: number[];
            static powers_$LI$(): number[];
            static invPowers: number[];
            static invPowers_$LI$(): number[];
        }
    }
}
declare namespace javaemul.internal {
    /**
     * Wraps a primitive <code>float</code> as an object.
     */
    class FloatHelper extends javaemul.internal.NumberHelper implements java.lang.Comparable<FloatHelper> {
        static MAX_VALUE: number;
        static MIN_VALUE: number;
        static MAX_EXPONENT: number;
        static MIN_EXPONENT: number;
        static MIN_NORMAL: number;
        static NaN: number;
        static NaN_$LI$(): number;
        static NEGATIVE_INFINITY: number;
        static NEGATIVE_INFINITY_$LI$(): number;
        static POSITIVE_INFINITY: number;
        static POSITIVE_INFINITY_$LI$(): number;
        static SIZE: number;
        static POWER_31_INT: number;
        static compare(x: number, y: number): number;
        static floatToIntBits(value: number): number;
        /**
         * @skip Here for shared implementation with Arrays.hashCode.
         * @param f
         * @return hash value of float (currently just truncated to int)
         */
        static hashCode(f: number): number;
        static intBitsToFloat(bits: number): number;
        static isInfinite(x: number): boolean;
        static isNaN(x: number): boolean;
        static parseFloat(s: string): number;
        static toString(b: number): string;
        static valueOf$float(f: number): FloatHelper;
        static valueOf(s?: any): any;
        private value;
        constructor(s?: any);
        byteValue(): number;
        compareTo(b?: any): any;
        doubleValue(): number;
        equals(o: any): boolean;
        floatValue(): number;
        /**
         * Performance caution: using Float objects as map keys is not recommended.
         * Using floating point values as keys is generally a bad idea due to
         * difficulty determining exact equality. In addition, there is no efficient
         * JavaScript equivalent of <code>floatToIntBits</code>. As a result, this
         * method computes a hash code by truncating the whole number portion of the
         * float, which may lead to poor performance for certain value sets if
         * Floats are used as keys in a {@link java.util.HashMap}.
         */
        hashCode(): number;
        intValue(): number;
        isInfinite(): boolean;
        isNaN(): boolean;
        longValue(): number;
        shortValue(): number;
        toString(): string;
    }
}
declare namespace javaemul.internal {
    /**
     * Wraps a primitive <code>int</code> as an object.
     */
    class IntegerHelper extends javaemul.internal.NumberHelper implements java.lang.Comparable<IntegerHelper> {
        static MAX_VALUE: number;
        static MIN_VALUE: number;
        static SIZE: number;
        static bitCount(x: number): number;
        static compare(x: number, y: number): number;
        static decode(s: string): number;
        /**
         * @skip
         *
         * Here for shared implementation with Arrays.hashCode
         */
        static hashCode(i: number): number;
        static highestOneBit(i: number): number;
        static lowestOneBit(i: number): number;
        static numberOfLeadingZeros(i: number): number;
        static numberOfTrailingZeros(i: number): number;
        static parseInt(s: string, radix?: number): number;
        static reverse(i: number): number;
        static reverseBytes(i: number): number;
        static rotateLeft(i: number, distance: number): number;
        static rotateRight(i: number, distance: number): number;
        static signum(i: number): number;
        static toBinaryString(value: number): string;
        static toHexString(value: number): string;
        static toOctalString(value: number): string;
        static toString$int(value: number): string;
        static toString(value?: any, radix?: any): any;
        static valueOf$int(i: number): number;
        static valueOf$java_lang_String(s: string): number;
        static valueOf(s?: any, radix?: any): any;
        static toRadixString(value: number, radix: number): string;
        static toUnsignedRadixString(value: number, radix: number): string;
        private value;
        constructor(s?: any);
        byteValue(): number;
        compareTo(b?: any): any;
        doubleValue(): number;
        equals(o: any): boolean;
        floatValue(): number;
        hashCode(): number;
        intValue(): number;
        longValue(): number;
        shortValue(): number;
        toString(): string;
        static getInteger(nm: string): number;
    }
    namespace IntegerHelper {
        /**
         * Use nested class to avoid clinit on outer.
         */
        class BoxedValues {
            static boxedValues: number[];
            static boxedValues_$LI$(): number[];
        }
        /**
         * Use nested class to avoid clinit on outer.
         */
        class ReverseNibbles {
            /**
             * A fast-lookup of the reversed bits of all the nibbles 0-15. Used to
             * implement {@link #reverse(int)}.
             */
            static reverseNibbles: number[];
            static reverseNibbles_$LI$(): number[];
        }
    }
}
declare namespace javaemul.internal {
    /**
     * Wraps a primitive <code>long</code> as an object.
     */
    class LongHelper extends javaemul.internal.NumberHelper implements java.lang.Comparable<LongHelper> {
        static MAX_VALUE: number;
        static MIN_VALUE: number;
        static SIZE: number;
        static bitCount(i: number): number;
        static compare(x: number, y: number): number;
        static decode(s: string): LongHelper;
        /**
         * @skip Here for shared implementation with Arrays.hashCode
         */
        static hashCode(l: number): number;
        static highestOneBit(i: number): number;
        static lowestOneBit(i: number): number;
        static numberOfLeadingZeros(i: number): number;
        static numberOfTrailingZeros(i: number): number;
        static parseLong(s: string, radix?: number): number;
        static reverse(i: number): number;
        static reverseBytes(i: number): number;
        static rotateLeft(i: number, distance: number): number;
        static rotateRight(i: number, distance: number): number;
        static signum(i: number): number;
        static toBinaryString(value: number): string;
        static toHexString(value: number): string;
        static toOctalString(value: number): string;
        static toString$long(value: number): string;
        static toString(value?: any, intRadix?: any): any;
        static valueOf$long(i: number): LongHelper;
        static valueOf$java_lang_String(s: string): LongHelper;
        static valueOf(s?: any, radix?: any): any;
        static toPowerOfTwoUnsignedString(value: number, shift: number): string;
        private value;
        constructor(s?: any);
        byteValue(): number;
        compareTo(b?: any): any;
        doubleValue(): number;
        equals(o: any): boolean;
        floatValue(): number;
        hashCode(): number;
        intValue(): number;
        longValue(): number;
        shortValue(): number;
        toString(): string;
    }
    namespace LongHelper {
        /**
         * Use nested class to avoid clinit on outer.
         */
        class BoxedValues {
            static boxedValues: javaemul.internal.LongHelper[];
            static boxedValues_$LI$(): javaemul.internal.LongHelper[];
        }
    }
}
declare namespace javaemul.internal {
    /**
     * Wraps a primitive <code>short</code> as an object.
     */
    class ShortHelper extends javaemul.internal.NumberHelper implements java.lang.Comparable<ShortHelper> {
        static MIN_VALUE: number;
        static MIN_VALUE_$LI$(): number;
        static MAX_VALUE: number;
        static MAX_VALUE_$LI$(): number;
        static SIZE: number;
        static TYPE: any;
        static TYPE_$LI$(): any;
        static compare(x: number, y: number): number;
        static decode(s: string): ShortHelper;
        /**
         * @skip Here for shared implementation with Arrays.hashCode
         */
        static hashCode(s: number): number;
        static parseShort(s: string, radix?: number): number;
        static reverseBytes(s: number): number;
        static toString(b: number): string;
        static valueOf$short(s: number): ShortHelper;
        static valueOf$java_lang_String(s: string): ShortHelper;
        static valueOf(s?: any, radix?: any): any;
        private value;
        constructor(s?: any);
        byteValue(): number;
        compareTo(b?: any): any;
        doubleValue(): number;
        equals(o: any): boolean;
        floatValue(): number;
        hashCode(): number;
        intValue(): number;
        longValue(): number;
        shortValue(): number;
        toString(): string;
    }
    namespace ShortHelper {
        /**
         * Use nested class to avoid clinit on outer.
         */
        class BoxedValues {
            static boxedValues: javaemul.internal.ShortHelper[];
            static boxedValues_$LI$(): javaemul.internal.ShortHelper[];
        }
    }
}
declare namespace java.io {
    /**
     * @skip
     */
    class PrintStream extends java.io.FilterOutputStream {
        constructor(out: java.io.OutputStream);
        print$boolean(x: boolean): void;
        print$char(x: string): void;
        print(x?: any): any;
        print$double(x: number): void;
        print$float(x: number): void;
        print$int(x: number): void;
        print$long(x: number): void;
        print$java_lang_Object(x: any): void;
        print$java_lang_String(s: string): void;
        println$(): void;
        println$boolean(x: boolean): void;
        println$char(x: string): void;
        println(x?: any): any;
        println$double(x: number): void;
        println$float(x: number): void;
        println$int(x: number): void;
        println$long(x: number): void;
        println$java_lang_Object(x: any): void;
        println$java_lang_String(s: string): void;
    }
}
declare namespace java.io {
    /**
     * A character encoding is not supported - <a
     * href="http://java.sun.com/javase/6/docs/api/java/io/UnsupportedEncodingException.html">[Sun's
     * docs]</a>.
     */
    class UnsupportedEncodingException extends java.io.IOException {
        constructor(msg?: any);
    }
}
declare namespace java.io {
    /**
     * See <a
     * href="https://docs.oracle.com/javase/8/docs/api/java/io/UncheckedIOException.html">the
     * official Java API doc</a> for details.
     */
    class UncheckedIOException extends Error {
        constructor(message?: any, cause?: any);
        getCause(): java.io.IOException;
    }
}
declare namespace java.lang.annotation {
    /**
     * Indicates an attempt to access an element of an annotation that has changed
     * since it was compiled or serialized <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/annotation/AnnotationTypeMismatchException.html">[Sun
     * docs]</a>.
     */
    class AnnotationTypeMismatchException extends Error {
        constructor();
    }
}
declare namespace java.lang.annotation {
    /**
     * Indicates an attempt to access an element of an annotation that was added
     * since it was compiled or serialized <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/annotation/IncompleteAnnotationException.html">[Sun
     * docs]</a>.
     */
    class IncompleteAnnotationException extends Error {
        __annotationType: any;
        __elementName: string;
        constructor(annotationType: any, elementName: string);
        annotationType(): any;
        elementName(): string;
    }
}
declare namespace java.lang {
    /**
     * NOTE: in GWT this is only thrown for division by zero on longs and
     * BigInteger/BigDecimal.
     * <p>
     * See <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/ArithmeticException.html">the
     * official Java API doc</a> for details.
     */
    class ArithmeticException extends Error {
        constructor(explanation?: any);
    }
}
declare namespace java.lang {
    /**
     * See <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/ArrayStoreException.html">the
     * official Java API doc</a> for details.
     */
    class ArrayStoreException extends Error {
        constructor(message?: any);
    }
}
declare namespace java.lang {
    /**
     * Indicates failure to cast one type into another.
     */
    class ClassCastException extends Error {
        constructor(message?: any);
    }
}
declare namespace java.lang {
    /**
     * See <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/IllegalArgumentException.html">the
     * official Java API doc</a> for details.
     */
    class IllegalArgumentException extends Error {
        constructor(message?: any, cause?: any);
    }
}
declare namespace java.lang {
    /**
     * Indicates that an objet was in an invalid state during an attempted
     * operation.
     */
    class IllegalStateException extends Error {
        constructor(message?: any, cause?: any);
    }
}
declare namespace java.lang {
    /**
     * See <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/IndexOutOfBoundsException.html">the
     * official Java API doc</a> for details.
     */
    class IndexOutOfBoundsException extends Error {
        constructor(message?: any);
    }
}
declare namespace java.lang {
    /**
     * See <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/NegativeArraySizeException.html">the
     * official Java API doc</a> for details.
     */
    class NegativeArraySizeException extends Error {
        constructor(message?: any);
    }
}
declare namespace java.lang {
    /**
     * See <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/NullPointerException.html">the
     * official Java API doc</a> for details.
     */
    class NullPointerException extends Error {
        constructor(message?: any);
        createError(msg: string): any;
    }
}
declare namespace java.lang {
    /**
     * See <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/UnsupportedOperationException.html">the
     * official Java API doc</a> for details.
     */
    class UnsupportedOperationException extends Error {
        constructor(message?: any, cause?: any);
    }
}
declare namespace java.util {
    /**
     * See <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/ConcurrentModificationException.html">the
     * official Java API doc</a> for details.
     */
    class ConcurrentModificationException extends Error {
        constructor(message?: any);
    }
}
declare namespace java.util {
    /**
     * See <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/EmptyStackException.html">the
     * official Java API doc</a> for details.
     */
    class EmptyStackException extends Error {
        constructor();
    }
}
declare namespace java.util {
    /**
     * See <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/MissingResourceException.html">the
     * official Java API doc</a> for details.
     */
    class MissingResourceException extends Error {
        private className;
        private key;
        constructor(s: string, className: string, key: string);
        getClassName(): string;
        getKey(): string;
    }
}
declare namespace java.util {
    /**
     * See <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/NoSuchElementException.html">the
     * official Java API doc</a> for details.
     */
    class NoSuchElementException extends Error {
        constructor(s?: any);
    }
}
declare namespace java.security {
    /**
     * A generic security exception type - <a
     * href="http://java.sun.com/j2se/1.4.2/docs/api/java/security/DigestException.html">[Sun's
     * docs]</a>.
     */
    class DigestException extends java.security.GeneralSecurityException {
        constructor(msg?: any);
    }
}
declare namespace java.security {
    /**
     * A generic security exception type - <a
     * href="http://java.sun.com/j2se/1.4.2/docs/api/java/security/NoSuchAlgorithmException.html">[Sun's
     * docs]</a>.
     */
    class NoSuchAlgorithmException extends java.security.GeneralSecurityException {
        constructor(msg?: any);
    }
}
declare namespace java.nio.charset {
    /**
     * Constant definitions for the standard Charsets.
     */
    class StandardCharsets {
        static ISO_8859_1: java.nio.charset.Charset;
        static ISO_8859_1_$LI$(): java.nio.charset.Charset;
        static UTF_8: java.nio.charset.Charset;
        static UTF_8_$LI$(): java.nio.charset.Charset;
        constructor();
    }
}
declare namespace java.util {
    /**
     * Skeletal implementation of the List interface. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/AbstractSequentialList.html">[Sun
     * docs]</a>
     *
     * @param <E> element type.
     */
    abstract class AbstractSequentialList<E> extends java.util.AbstractList<E> {
        constructor();
        add(index?: any, element?: any): any;
        addAll(index?: any, c?: any): any;
        get(index: number): E;
        iterator(): java.util.Iterator<E>;
        listIterator(index?: any): any;
        remove(index?: any): any;
        set(index: number, element: E): E;
        abstract size(): number;
    }
}
declare namespace java.util {
    /**
     * Resizeable array implementation of the List interface. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/ArrayList.html">[Sun
     * docs]</a>
     *
     * <p>
     * This implementation differs from JDK 1.5 <code>ArrayList</code> in terms of
     * capacity management. There is no speed advantage to pre-allocating array
     * sizes in JavaScript, so this implementation does not include any of the
     * capacity and "growth increment" concepts in the standard ArrayList class.
     * Although <code>ArrayList(int)</code> accepts a value for the initial
     * capacity of the array, this constructor simply delegates to
     * <code>ArrayList()</code>. It is only present for compatibility with JDK
     * 1.5's API.
     * </p>
     *
     * @param <E> the element type.
     */
    class ArrayList<E> extends java.util.AbstractList<E> implements java.util.List<E>, java.lang.Cloneable, java.util.RandomAccess, java.io.Serializable {
        forEach(action: (p1: any) => void): void;
        /**
         * This field holds a JavaScript array.
         */
        private array;
        /**
         * Ensures that RPC will consider type parameter E to be exposed. It will be
         * pruned by dead code elimination.
         */
        private exposeElement;
        constructor(c?: any);
        add$java_lang_Object(o: E): boolean;
        add(index?: any, o?: any): any;
        addAll$java_util_Collection(c: java.util.Collection<any>): boolean;
        addAll(index?: any, c?: any): any;
        clear(): void;
        clone(): any;
        contains(o: any): boolean;
        ensureCapacity(ignored: number): void;
        get(index: number): E;
        indexOf$java_lang_Object(o: any): number;
        iterator(): java.util.Iterator<E>;
        isEmpty(): boolean;
        lastIndexOf$java_lang_Object(o: any): number;
        remove(index?: any): any;
        remove$java_lang_Object(o: any): boolean;
        set(index: number, o: E): E;
        size(): number;
        toArray$(): any[];
        toArray<T>(out?: any): any;
        trimToSize(): void;
        removeRange(fromIndex: number, endIndex: number): void;
        /**
         * Used by Vector.
         */
        indexOf(o?: any, index?: any): any;
        /**
         * Used by Vector.
         */
        lastIndexOf(o?: any, index?: any): any;
        setSize(newSize: number): void;
    }
    namespace ArrayList {
        class ArrayList$0 implements java.util.Iterator<any> {
            __parent: any;
            forEachRemaining(consumer: (p1: any) => void): void;
            i: number;
            last: number;
            hasNext(): boolean;
            next(): any;
            remove(): void;
            constructor(__parent: any);
        }
    }
}
declare namespace java.util {
    /**
     * Utility methods related to native arrays. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/Arrays.html">[Sun
     * docs]</a>
     */
    class Arrays {
        static asList<T>(...array: T[]): java.util.List<T>;
        /**
         * Perform a binary search on a sorted byte array.
         *
         * @param sortedArray byte array to search
         * @param key value to search for
         * @return the index of an element with a matching value, or a negative number
         * which is the index of the next larger value (or just past the end
         * of the array if the searched value is larger than all elements in
         * the array) minus 1 (to ensure error returns are negative)
         */
        static binarySearch$byte_A$byte(sortedArray: number[], key: number): number;
        /**
         * Perform a binary search on a sorted char array.
         *
         * @param a char array to search
         * @param key value to search for
         * @return the index of an element with a matching value, or a negative number
         * which is the index of the next larger value (or just past the end
         * of the array if the searched value is larger than all elements in
         * the array) minus 1 (to ensure error returns are negative)
         */
        static binarySearch$char_A$char(a: string[], key: string): number;
        /**
         * Perform a binary search on a sorted double array.
         *
         * @param sortedArray double array to search
         * @param key value to search for
         * @return the index of an element with a matching value, or a negative number
         * which is the index of the next larger value (or just past the end
         * of the array if the searched value is larger than all elements in
         * the array) minus 1 (to ensure error returns are negative)
         */
        static binarySearch$double_A$double(sortedArray: number[], key: number): number;
        /**
         * Perform a binary search on a sorted float array.
         *
         * Note that some underlying JavaScript interpreters do not actually implement
         * floats (using double instead), so you may get slightly different behavior
         * regarding values that are very close (or equal) since conversion errors
         * to/from double may change the values slightly.
         *
         * @param sortedArray float array to search
         * @param key value to search for
         * @return the index of an element with a matching value, or a negative number
         * which is the index of the next larger value (or just past the end
         * of the array if the searched value is larger than all elements in
         * the array) minus 1 (to ensure error returns are negative)
         */
        static binarySearch$float_A$float(sortedArray: number[], key: number): number;
        /**
         * Perform a binary search on a sorted int array.
         *
         * @param sortedArray int array to search
         * @param key value to search for
         * @return the index of an element with a matching value, or a negative number
         * which is the index of the next larger value (or just past the end
         * of the array if the searched value is larger than all elements in
         * the array) minus 1 (to ensure error returns are negative)
         */
        static binarySearch$int_A$int(sortedArray: number[], key: number): number;
        /**
         * Perform a binary search on a sorted long array.
         *
         * Note that most underlying JavaScript interpreters do not actually implement
         * longs, so the values must be stored in doubles instead. This means that
         * certain legal values cannot be represented, and comparison of two unequal
         * long values may result in unexpected results if they are not also
         * representable as doubles.
         *
         * @param sortedArray long array to search
         * @param key value to search for
         * @return the index of an element with a matching value, or a negative number
         * which is the index of the next larger value (or just past the end
         * of the array if the searched value is larger than all elements in
         * the array) minus 1 (to ensure error returns are negative)
         */
        static binarySearch$long_A$long(sortedArray: number[], key: number): number;
        /**
         * Perform a binary search on a sorted object array, using natural ordering.
         *
         * @param sortedArray object array to search
         * @param key value to search for
         * @return the index of an element with a matching value, or a negative number
         * which is the index of the next larger value (or just past the end
         * of the array if the searched value is larger than all elements in
         * the array) minus 1 (to ensure error returns are negative)
         * @throws ClassCastException if <code>key</code> is not comparable to
         * <code>sortedArray</code>'s elements.
         */
        static binarySearch$java_lang_Object_A$java_lang_Object(sortedArray: any[], key: any): number;
        /**
         * Perform a binary search on a sorted short array.
         *
         * @param sortedArray short array to search
         * @param key value to search for
         * @return the index of an element with a matching value, or a negative number
         * which is the index of the next larger value (or just past the end
         * of the array if the searched value is larger than all elements in
         * the array) minus 1 (to ensure error returns are negative)
         */
        static binarySearch$short_A$short(sortedArray: number[], key: number): number;
        /**
         * Perform a binary search on a sorted object array, using a user-specified
         * comparison function.
         *
         * @param sortedArray object array to search
         * @param key value to search for
         * @param comparator comparision function, <code>null</code> indicates
         * <i>natural ordering</i> should be used.
         * @return the index of an element with a matching value, or a negative number
         * which is the index of the next larger value (or just past the end
         * of the array if the searched value is larger than all elements in
         * the array) minus 1 (to ensure error returns are negative)
         * @throws ClassCastException if <code>key</code> and
         * <code>sortedArray</code>'s elements cannot be compared by
         * <code>comparator</code>.
         */
        static binarySearch<T>(sortedArray?: any, key?: any, comparator?: any): any;
        static copyOf(original?: any, newLength?: any): any;
        static copyOf$byte_A$int(original: number[], newLength: number): number[];
        static copyOf$char_A$int(original: string[], newLength: number): string[];
        static copyOf$double_A$int(original: number[], newLength: number): number[];
        static copyOf$float_A$int(original: number[], newLength: number): number[];
        static copyOf$int_A$int(original: number[], newLength: number): number[];
        static copyOf$long_A$int(original: number[], newLength: number): number[];
        static copyOf$short_A$int(original: number[], newLength: number): number[];
        static copyOf$java_lang_Object_A$int<T>(original: T[], newLength: number): T[];
        static copyOfRange(original?: any, from?: any, to?: any): any;
        static copyOfRange$byte_A$int$int(original: number[], from: number, to: number): number[];
        static copyOfRange$char_A$int$int(original: string[], from: number, to: number): string[];
        static copyOfRange$double_A$int$int(original: number[], from: number, to: number): number[];
        static copyOfRange$float_A$int$int(original: number[], from: number, to: number): number[];
        static copyOfRange$int_A$int$int(original: number[], from: number, to: number): number[];
        static copyOfRange$long_A$int$int(original: number[], from: number, to: number): number[];
        static copyOfRange$short_A$int$int(original: number[], from: number, to: number): number[];
        static copyOfRange$java_lang_Object_A$int$int<T>(original: T[], from: number, to: number): T[];
        static deepEquals(a1: any[], a2: any[]): boolean;
        static deepHashCode(a: any[]): number;
        static deepToString$java_lang_Object_A(a: any[]): string;
        static equals(array1?: any, array2?: any): any;
        static equals$byte_A$byte_A(array1: number[], array2: number[]): boolean;
        static equals$char_A$char_A(array1: string[], array2: string[]): boolean;
        static equals$double_A$double_A(array1: number[], array2: number[]): boolean;
        static equals$float_A$float_A(array1: number[], array2: number[]): boolean;
        static equals$int_A$int_A(array1: number[], array2: number[]): boolean;
        static equals$long_A$long_A(array1: number[], array2: number[]): boolean;
        static equals$java_lang_Object_A$java_lang_Object_A(array1: any[], array2: any[]): boolean;
        static equals$short_A$short_A(array1: number[], array2: number[]): boolean;
        static fill$boolean_A$boolean(a: boolean[], val: boolean): void;
        static fill(a?: any, fromIndex?: any, toIndex?: any, val?: any): any;
        static fill$byte_A$byte(a: number[], val: number): void;
        static fill$byte_A$int$int$byte(a: number[], fromIndex: number, toIndex: number, val: number): void;
        static fill$char_A$char(a: string[], val: string): void;
        static fill$char_A$int$int$char(a: string[], fromIndex: number, toIndex: number, val: string): void;
        static fill$double_A$double(a: number[], val: number): void;
        static fill$double_A$int$int$double(a: number[], fromIndex: number, toIndex: number, val: number): void;
        static fill$float_A$float(a: number[], val: number): void;
        static fill$float_A$int$int$float(a: number[], fromIndex: number, toIndex: number, val: number): void;
        static fill$int_A$int(a: number[], val: number): void;
        static fill$int_A$int$int$int(a: number[], fromIndex: number, toIndex: number, val: number): void;
        static fill$long_A$int$int$long(a: number[], fromIndex: number, toIndex: number, val: number): void;
        static fill$long_A$long(a: number[], val: number): void;
        static fill$java_lang_Object_A$int$int$java_lang_Object(a: any[], fromIndex: number, toIndex: number, val: any): void;
        static fill$java_lang_Object_A$java_lang_Object(a: any[], val: any): void;
        static fill$short_A$int$int$short(a: number[], fromIndex: number, toIndex: number, val: number): void;
        static fill$short_A$short(a: number[], val: number): void;
        static hashCode(a?: any): any;
        static hashCode$byte_A(a: number[]): number;
        static hashCode$char_A(a: string[]): number;
        static hashCode$double_A(a: number[]): number;
        static hashCode$float_A(a: number[]): number;
        static hashCode$int_A(a: number[]): number;
        static hashCode$long_A(a: number[]): number;
        static hashCode$java_lang_Object_A(a: any[]): number;
        static hashCode$short_A(a: number[]): number;
        static sort$byte_A(array: number[]): void;
        static sort$byte_A$int$int(array: number[], fromIndex: number, toIndex: number): void;
        static sort$char_A(array: string[]): void;
        static sort$char_A$int$int(array: string[], fromIndex: number, toIndex: number): void;
        static sort$double_A(array: number[]): void;
        static sort$double_A$int$int(array: number[], fromIndex: number, toIndex: number): void;
        static sort$float_A(array: number[]): void;
        static sort$float_A$int$int(array: number[], fromIndex: number, toIndex: number): void;
        static sort$int_A(array: number[]): void;
        static sort$int_A$int$int(array: number[], fromIndex: number, toIndex: number): void;
        static sort$long_A(array: number[]): void;
        static sort$long_A$int$int(array: number[], fromIndex: number, toIndex: number): void;
        static sort$java_lang_Object_A(array: any[]): void;
        static sort$java_lang_Object_A$int$int(x: any[], fromIndex: number, toIndex: number): void;
        static sort$short_A(array: number[]): void;
        static sort$short_A$int$int(array: number[], fromIndex: number, toIndex: number): void;
        static sort$java_lang_Object_A$java_util_Comparator<T>(x: T[], c: java.util.Comparator<any>): void;
        static sort<T>(x?: any, fromIndex?: any, toIndex?: any, c?: any): any;
        static toString(a?: any): any;
        static toString$byte_A(a: number[]): string;
        static toString$char_A(a: string[]): string;
        static toString$double_A(a: number[]): string;
        static toString$float_A(a: number[]): string;
        static toString$int_A(a: number[]): string;
        static toString$long_A(a: number[]): string;
        static toString$java_lang_Object_A(x: any[]): string;
        static toString$short_A(a: number[]): string;
        /**
         * Recursive helper function for {@link Arrays#deepToString(Object[])}.
         */
        static deepToString(a?: any, arraysIveSeen?: any): any;
        static getCopyLength(array: any, from: number, to: number): number;
        /**
         * Sort a small subsection of an array by insertion sort.
         *
         * @param array array to sort
         * @param low lower bound of range to sort
         * @param high upper bound of range to sort
         * @param comp comparator to use
         */
        static insertionSort(array: any[], low: number, high: number, comp: java.util.Comparator<any>): void;
        /**
         * Merge the two sorted subarrays (srcLow,srcMid] and (srcMid,srcHigh] into
         * dest.
         *
         * @param src source array for merge
         * @param srcLow lower bound of bottom sorted half
         * @param srcMid upper bound of bottom sorted half & lower bound of top sorted
         * half
         * @param srcHigh upper bound of top sorted half
         * @param dest destination array for merge
         * @param destLow lower bound of destination
         * @param destHigh upper bound of destination
         * @param comp comparator to use
         */
        static merge(src: any[], srcLow: number, srcMid: number, srcHigh: number, dest: any[], destLow: number, destHigh: number, comp: java.util.Comparator<any>): void;
        /**
         * Performs a merge sort on the specified portion of an object array.
         *
         * Uses O(n) temporary space to perform the merge, but is stable.
         */
        static mergeSort$java_lang_Object_A$int$int$java_util_Comparator(x: any[], fromIndex: number, toIndex: number, comp: java.util.Comparator<any>): void;
        /**
         * Recursive helper function for
         * {@link Arrays#mergeSort(Object[], int, int, Comparator)}.
         *
         * @param temp temporary space, as large as the range of elements being
         * sorted. On entry, temp should contain a copy of the sort range
         * from array.
         * @param array array to sort
         * @param low lower bound of range to sort
         * @param high upper bound of range to sort
         * @param ofs offset to convert an array index into a temp index
         * @param comp comparison function
         */
        static mergeSort(temp?: any, array?: any, low?: any, high?: any, ofs?: any, comp?: any): any;
        /**
         * Sort an entire array of number primitives.
         */
        static nativeLongSort$java_lang_Object$java_lang_Object(array: any, compareFunction: any): void;
        /**
         * Sort a subset of an array of number primitives.
         */
        static nativeLongSort(array?: any, fromIndex?: any, toIndex?: any): any;
        /**
         * Sort an entire array of number primitives.
         */
        static nativeNumberSort$java_lang_Object(array: any): void;
        /**
         * Sort a subset of an array of number primitives.
         */
        static nativeNumberSort(array?: any, fromIndex?: any, toIndex?: any): any;
    }
    namespace Arrays {
        class ArrayList<E> extends java.util.AbstractList<E> implements java.util.RandomAccess, java.io.Serializable {
            /**
             * The only reason this is non-final is so that E[] (and E) will be exposed
             * for serialization.
             */
            array: E[];
            constructor(array: E[]);
            contains(o: any): boolean;
            get(index: number): E;
            set(index: number, value: E): E;
            size(): number;
            toArray$(): any[];
            toArray<T>(out?: any): any;
        }
    }
}
declare namespace java.util {
    /**
     * To keep performance characteristics in line with Java community expectations,
     * <code>Vector</code> is a wrapper around <code>ArrayList</code>. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/Vector.html">[Sun
     * docs]</a>
     *
     * @param <E> element type.
     */
    class Vector<E> extends java.util.AbstractList<E> implements java.util.List<E>, java.util.RandomAccess, java.lang.Cloneable, java.io.Serializable {
        forEach(action: (p1: any) => void): void;
        private arrayList;
        /**
         * Ensures that RPC will consider type parameter E to be exposed. It will be
         * pruned by dead code elimination.
         */
        private exposeElement;
        /**
         * Capacity increment is ignored.
         */
        constructor(initialCapacity?: any, ignoredCapacityIncrement?: any);
        add$java_lang_Object(o: E): boolean;
        add(index?: any, o?: any): any;
        addAll$java_util_Collection(c: java.util.Collection<any>): boolean;
        addAll(index?: any, c?: any): any;
        addElement(o: E): void;
        capacity(): number;
        clear(): void;
        clone(): any;
        contains(elem: any): boolean;
        containsAll(c: java.util.Collection<any>): boolean;
        copyInto(objs: any[]): void;
        elementAt(index: number): E;
        elements(): java.util.Enumeration<E>;
        ensureCapacity(capacity: number): void;
        firstElement(): E;
        get(index: number): E;
        indexOf$java_lang_Object(elem: any): number;
        indexOf(elem?: any, index?: any): any;
        insertElementAt(o: E, index: number): void;
        isEmpty(): boolean;
        iterator(): java.util.Iterator<E>;
        lastElement(): E;
        lastIndexOf$java_lang_Object(o: any): number;
        lastIndexOf(o?: any, index?: any): any;
        remove(index?: any): any;
        removeAll(c: java.util.Collection<any>): boolean;
        removeAllElements(): void;
        removeElement(o: any): boolean;
        removeElementAt(index: number): void;
        set(index: number, elem: E): E;
        setElementAt(o: E, index: number): void;
        setSize(size: number): void;
        size(): number;
        subList(fromIndex: number, toIndex: number): java.util.List<E>;
        toArray$(): any[];
        toArray<T>(a?: any): any;
        toString(): string;
        trimToSize(): void;
        removeRange(fromIndex: number, endIndex: number): void;
        private static checkArrayElementIndex(index, size);
        private static checkArrayIndexOutOfBounds(expression, index);
    }
}
declare namespace java.util {
    /**
     * An unbounded priority queue based on a priority heap. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/PriorityQueue.html">[Sun
     * docs]</a>
     *
     * @param <E> element type.
     */
    class PriorityQueue<E> extends java.util.AbstractQueue<E> {
        private static getLeftChild(node);
        private static getParent(node);
        private static getRightChild(node);
        private static isLeaf(node, size);
        private cmp;
        /**
         * A heap held in an array. heap[0] is the root of the heap (the smallest
         * element), the subtrees of node i are 2*i+1 (left) and 2*i+2 (right). Node i
         * is a leaf node if 2*i>=n. Node i's parent, if i>0, is floor((i-1)/2).
         */
        private heap;
        constructor(initialCapacity?: any, cmp?: any);
        addAll(index?: any, c?: any): any;
        addAll$java_util_Collection(c: java.util.Collection<any>): boolean;
        clear(): void;
        comparator(): java.util.Comparator<any>;
        contains(o: any): boolean;
        containsAll(c: java.util.Collection<any>): boolean;
        isEmpty(): boolean;
        iterator(): java.util.Iterator<E>;
        offer(e: E): boolean;
        peek(): E;
        poll(): E;
        remove(index?: any): any;
        remove$java_lang_Object(o: any): boolean;
        removeAll(c: java.util.Collection<any>): boolean;
        retainAll(c: java.util.Collection<any>): boolean;
        size(): number;
        toArray$(): any[];
        toArray<T>(a?: any): any;
        toString(): string;
        /**
         * Make the subtree rooted at <code>node</code> a valid heap. O(n) time
         *
         * @param node
         */
        makeHeap(node: number): void;
        /**
         * Merge two subheaps into a single heap. O(log n) time
         *
         * PRECONDITION: both children of <code>node</code> are heaps
         *
         * @param node the parent of the two subtrees to merge
         */
        mergeHeaps(node: number): void;
        private getSmallestChild(node, heapSize);
        private isLeaf(node);
        private removeAtIndex(index);
    }
}
declare namespace java.util {
    /**
     * Skeletal implementation of the Map interface.
     * <a href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/AbstractMap.html">
     * [Sun docs]</a>
     *
     * @param <K>
     * the key type.
     * @param <V>
     * the value type.
     */
    abstract class AbstractMap<K, V> implements java.util.Map<K, V> {
        constructor();
        clear(): void;
        containsKey(key: any): boolean;
        containsValue(value: any): boolean;
        containsEntry(entry: Map.Entry<any, any>): boolean;
        abstract entrySet(): java.util.Set<Map.Entry<K, V>>;
        equals(obj: any): boolean;
        get(key: any): V;
        hashCode(): number;
        isEmpty(): boolean;
        keySet(): java.util.Set<K>;
        put(key?: any, value?: any): any;
        put$java_lang_Object$java_lang_Object(key: K, value: V): V;
        putAll(map: java.util.Map<any, any>): void;
        remove(key: any): V;
        size(): number;
        toString$(): string;
        toString(entry?: any): any;
        toString$java_lang_Object(o: any): string;
        values(): java.util.Collection<V>;
        static getEntryKeyOrNull<K, V>(entry: Map.Entry<K, V>): K;
        static getEntryValueOrNull<K, V>(entry: Map.Entry<K, V>): V;
        implFindEntry(key: any, remove: boolean): Map.Entry<K, V>;
    }
    namespace AbstractMap {
        /**
         * Basic {@link Map.Entry} implementation used by {@link SimpleEntry} and
         * {@link SimpleImmutableEntry}.
         */
        abstract class AbstractEntry<K, V> implements Map.Entry<K, V> {
            key: K;
            value: V;
            constructor(key: K, value: V);
            getKey(): K;
            getValue(): V;
            setValue(value: V): V;
            equals(other: any): boolean;
            /**
             * Calculate the hash code using Sun's specified algorithm.
             */
            hashCode(): number;
            toString(): string;
        }
        /**
         * A mutable {@link Map.Entry} shared by several {@link Map}
         * implementations.
         */
        class SimpleEntry<K, V> extends AbstractMap.AbstractEntry<K, V> {
            constructor(key?: any, value?: any);
        }
        /**
         * An immutable {@link Map.Entry} shared by several {@link Map}
         * implementations.
         */
        class SimpleImmutableEntry<K, V> extends AbstractMap.AbstractEntry<K, V> {
            constructor(key?: any, value?: any);
            setValue(value: V): V;
        }
        class AbstractMap$0 extends java.util.AbstractSet<any> {
            __parent: any;
            clear(): void;
            contains(key: any): boolean;
            iterator(): java.util.Iterator<any>;
            remove(key: any): boolean;
            size(): number;
            constructor(__parent: any);
        }
        namespace AbstractMap$0 {
            class AbstractMap$0$0 implements java.util.Iterator<any> {
                private outerIter;
                __parent: any;
                forEachRemaining(consumer: (p1: any) => void): void;
                hasNext(): boolean;
                next(): any;
                remove(): void;
                constructor(__parent: any, outerIter: any);
            }
        }
        class AbstractMap$1 extends java.util.AbstractCollection<any> {
            __parent: any;
            clear(): void;
            contains(value: any): boolean;
            iterator(): java.util.Iterator<any>;
            size(): number;
            constructor(__parent: any);
        }
        namespace AbstractMap$1 {
            class AbstractMap$1$0 implements java.util.Iterator<any> {
                private outerIter;
                __parent: any;
                forEachRemaining(consumer: (p1: any) => void): void;
                hasNext(): boolean;
                next(): any;
                remove(): void;
                constructor(__parent: any, outerIter: any);
            }
        }
    }
}
declare namespace java.util {
    /**
     * A {@link java.util.Set} of {@link Enum}s. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/EnumSet.html">[Sun
     * docs]</a>
     *
     * @param <E> enumeration type
     */
    abstract class EnumSet<E extends java.lang.Enum<E>> extends java.util.AbstractSet<E> {
        static allOf<E extends java.lang.Enum<E>>(elementType: any): EnumSet<E>;
        static complementOf<E extends java.lang.Enum<E>>(other: EnumSet<E>): EnumSet<E>;
        static copyOf$java_util_Collection<E extends java.lang.Enum<E>>(c: java.util.Collection<E>): EnumSet<E>;
        static copyOf<E extends java.lang.Enum<E>>(s?: any): any;
        static noneOf<E extends java.lang.Enum<E>>(elementType: any): EnumSet<E>;
        static of$java_lang_Enum<E extends java.lang.Enum<E>>(first: E): EnumSet<E>;
        static of<E extends java.lang.Enum<E>>(first?: any, ...rest: any[]): any;
        static range<E extends java.lang.Enum<E>>(from: E, to: E): EnumSet<E>;
        /**
         * Single implementation only.
         */
        constructor();
        abstract clone(): EnumSet<E>;
        abstract capacity(): number;
    }
    namespace EnumSet {
        /**
         * Implemented via sparse array since the set size is finite. Iteration takes
         * linear time with respect to the set of the enum rather than the number of
         * items in the set.
         *
         * Note: Implemented as a subclass instead of a concrete final EnumSet class.
         * This is because declaring an EnumSet.add(E) causes hosted mode to bind to
         * the tighter method rather than the bridge method; but the tighter method
         * isn't available in the real JRE.
         */
        class EnumSetImpl<E extends java.lang.Enum<E>> extends java.util.EnumSet<E> {
            /**
             * All enums; reference to the class's copy; must not be modified.
             */
            all: E[];
            /**
             * Live enums in the set.
             */
            set: E[];
            /**
             * Count of enums in the set.
             */
            __size: number;
            /**
             * Constructs a set taking ownership of the specified set. The size must
             * accurately reflect the number of non-null items in set.
             */
            constructor(all: E[], set: E[], size: number);
            add(index?: any, element?: any): any;
            add$java_lang_Enum(e: E): boolean;
            clone(): java.util.EnumSet<E>;
            contains(o: any): boolean;
            containsEnum(e: java.lang.Enum<any>): boolean;
            iterator(): java.util.Iterator<E>;
            remove(index?: any): any;
            remove$java_lang_Object(o: any): boolean;
            removeEnum(e: java.lang.Enum<any>): boolean;
            size(): number;
            capacity(): number;
        }
        namespace EnumSetImpl {
            class IteratorImpl implements java.util.Iterator<any> {
                __parent: any;
                forEachRemaining(consumer: (p1: any) => void): void;
                i: number;
                last: number;
                constructor(__parent: any);
                hasNext(): boolean;
                next(): any;
                remove(): void;
                findNext(): void;
            }
        }
    }
}
declare namespace java.util {
    /**
     * Implements a set in terms of a hash table. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/HashSet.html">[Sun
     * docs]</a>
     *
     * @param <E> element type.
     */
    class HashSet<E> extends java.util.AbstractSet<E> implements java.util.Set<E>, java.lang.Cloneable, java.io.Serializable {
        forEach(action: (p1: any) => void): void;
        private map;
        /**
         * Ensures that RPC will consider type parameter E to be exposed. It will be
         * pruned by dead code elimination.
         */
        private exposeElement;
        constructor(initialCapacity?: any, loadFactor?: any);
        add(index?: any, element?: any): any;
        add$java_lang_Object(o: E): boolean;
        clear(): void;
        clone(): any;
        contains(o: any): boolean;
        isEmpty(): boolean;
        iterator(): java.util.Iterator<E>;
        remove(index?: any): any;
        remove$java_lang_Object(o: any): boolean;
        size(): number;
        toString(): string;
    }
}
declare namespace java.util {
    /**
     * Implements a set using a TreeMap. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/TreeSet.html">[Sun
     * docs]</a>
     *
     * @param <E> element type.
     */
    class TreeSet<E> extends java.util.AbstractSet<E> implements java.util.NavigableSet<E>, java.io.Serializable {
        forEach(action: (p1: any) => void): void;
        /**
         * TreeSet is stored as a TreeMap of the requested type to a constant Boolean.
         */
        private map;
        constructor(c?: any);
        add(index?: any, element?: any): any;
        add$java_lang_Object(o: E): boolean;
        ceiling(e: E): E;
        clear(): void;
        comparator(): java.util.Comparator<any>;
        contains(o: any): boolean;
        descendingIterator(): java.util.Iterator<E>;
        descendingSet(): java.util.NavigableSet<E>;
        first(): E;
        floor(e: E): E;
        headSet$java_lang_Object(toElement: E): java.util.SortedSet<E>;
        headSet(toElement?: any, inclusive?: any): any;
        higher(e: E): E;
        iterator(): java.util.Iterator<E>;
        last(): E;
        lower(e: E): E;
        pollFirst(): E;
        pollLast(): E;
        remove(index?: any): any;
        remove$java_lang_Object(o: any): boolean;
        size(): number;
        subSet(fromElement?: any, fromInclusive?: any, toElement?: any, toInclusive?: any): any;
        subSet$java_lang_Object$java_lang_Object(fromElement: E, toElement: E): java.util.SortedSet<E>;
        tailSet$java_lang_Object(fromElement: E): java.util.SortedSet<E>;
        tailSet(fromElement?: any, inclusive?: any): any;
    }
}
declare namespace java.lang {
    /**
     * General-purpose low-level utility methods. GWT only supports a limited subset
     * of these methods due to browser limitations. Only the documented methods are
     * available.
     */
    class System {
        /**
         * Does nothing in web mode. To get output in web mode, subclass PrintStream
         * and call {@link #setErr(PrintStream)}.
         */
        static err: java.io.PrintStream;
        static err_$LI$(): java.io.PrintStream;
        /**
         * Does nothing in web mode. To get output in web mode, subclass
         * {@link PrintStream} and call {@link #setOut(PrintStream)}.
         */
        static out: java.io.PrintStream;
        static out_$LI$(): java.io.PrintStream;
        static arraycopy(src: any, srcOfs: number, dest: any, destOfs: number, len: number): void;
        static currentTimeMillis(): number;
        /**
         * Has no effect; just here for source compatibility.
         *
         * @skip
         */
        static gc(): void;
        /**
         * The compiler replaces getProperty by the actual value of the property.
         */
        static getProperty$java_lang_String(key: string): string;
        /**
         * The compiler replaces getProperty by the actual value of the property.
         */
        static getProperty(key?: any, def?: any): any;
        static identityHashCode(o: any): number;
        static setErr(err: java.io.PrintStream): void;
        static setOut(out: java.io.PrintStream): void;
    }
}
declare namespace java.lang {
    /**
     * See <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/NumberFormatException.html">the
     * official Java API doc</a> for details.
     */
    class NumberFormatException extends java.lang.IllegalArgumentException {
        static forInputString(s: string): java.lang.NumberFormatException;
        static forNullInputString(): java.lang.NumberFormatException;
        static forRadix(radix: number): java.lang.NumberFormatException;
        constructor(message?: any);
    }
}
declare namespace java.nio.charset {
    /**
     * GWT emulation of {@link IllegalCharsetNameException}.
     */
    class IllegalCharsetNameException extends java.lang.IllegalArgumentException {
        private charsetName;
        constructor(charsetName: string);
        getCharsetName(): string;
    }
}
declare namespace java.nio.charset {
    /**
     * GWT emulation of {@link UnsupportedCharsetException}.
     */
    class UnsupportedCharsetException extends java.lang.IllegalArgumentException {
        private charsetName;
        constructor(charsetName: string);
        getCharsetName(): string;
    }
}
declare namespace java.lang {
    /**
     * NOTE: in GWT this will never be thrown for normal array accesses, only for
     * explicit throws.
     *
     * See <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/ArrayIndexOutOfBoundsException.html">the
     * official Java API doc</a> for details.
     */
    class ArrayIndexOutOfBoundsException extends java.lang.IndexOutOfBoundsException {
        constructor(msg?: any);
    }
}
declare namespace java.lang {
    /**
     * See <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/StringIndexOfBoundsException.html">the
     * official Java API doc</a> for details.
     */
    class StringIndexOutOfBoundsException extends java.lang.IndexOutOfBoundsException {
        constructor(message?: any);
    }
}
declare namespace java.util {
    /**
     * Linked list implementation.
     * <a href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/LinkedList.html">
     * [Sun docs]</a>
     *
     * @param <E>
     * element type.
     */
    class LinkedList<E> extends java.util.AbstractSequentialList<E> implements java.lang.Cloneable, java.util.List<E>, java.util.Deque<E>, java.io.Serializable {
        forEach(action: (p1: any) => void): void;
        /**
         * Ensures that RPC will consider type parameter E to be exposed. It will be
         * pruned by dead code elimination.
         */
        private exposeElement;
        /**
         * Header node - header.next is the first element of the list.
         */
        private header;
        /**
         * Tail node - tail.prev is the last element of the list.
         */
        private tail;
        /**
         * Number of nodes currently present in the list.
         */
        private __size;
        constructor(c?: any);
        add$java_lang_Object(o: E): boolean;
        addFirst(o: E): void;
        addLast(o: E): void;
        clear(): void;
        reset(): void;
        clone(): any;
        descendingIterator(): java.util.Iterator<E>;
        element(): E;
        getFirst(): E;
        getLast(): E;
        listIterator(index?: any): any;
        offer(o: E): boolean;
        offerFirst(e: E): boolean;
        offerLast(e: E): boolean;
        peek(): E;
        peekFirst(): E;
        peekLast(): E;
        poll(): E;
        pollFirst(): E;
        pollLast(): E;
        pop(): E;
        push(e: E): void;
        remove$(): E;
        removeFirst(): E;
        removeFirstOccurrence(o: any): boolean;
        removeLast(): E;
        removeLastOccurrence(o: any): boolean;
        size(): number;
        addNode(o: E, prev: LinkedList.Node<E>, next: LinkedList.Node<E>): void;
        removeNode(node: LinkedList.Node<E>): E;
    }
    namespace LinkedList {
        class DescendingIteratorImpl implements java.util.Iterator<any> {
            __parent: any;
            forEachRemaining(consumer: (p1: any) => void): void;
            itr: java.util.ListIterator<any>;
            hasNext(): boolean;
            next(): any;
            remove(): void;
            constructor(__parent: any);
        }
        /**
         * Implementation of ListIterator for linked lists.
         */
        class ListIteratorImpl2 implements java.util.ListIterator<any> {
            __parent: any;
            forEachRemaining(consumer: (p1: any) => void): void;
            /**
             * The index to the current position.
             */
            currentIndex: number;
            /**
             * Current node, to be returned from next.
             */
            currentNode: LinkedList.Node<any>;
            /**
             * The last node returned from next/previous, or null if deleted or
             * never called.
             */
            lastNode: LinkedList.Node<any>;
            /**
             * @param index
             * from the beginning of the list (0 = first node)
             * @param startNode
             * the initial current node
             */
            constructor(__parent: any, index: number, startNode: LinkedList.Node<any>);
            add(o: any): void;
            hasNext(): boolean;
            hasPrevious(): boolean;
            next(): any;
            nextIndex(): number;
            previous(): any;
            previousIndex(): number;
            remove(): void;
            set(o: any): void;
        }
        /**
         * Internal class representing a doubly-linked list node.
         *
         * @param <E>
         * element type
         */
        class Node<E> {
            next: LinkedList.Node<E>;
            prev: LinkedList.Node<E>;
            value: E;
            constructor();
        }
    }
}
declare namespace java.util {
    /**
     * Maintains a last-in, first-out collection of objects. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/Stack.html">[Sun
     * docs]</a>
     *
     * @param <E> element type.
     */
    class Stack<E> extends java.util.Vector<E> {
        clone(): any;
        empty(): boolean;
        peek(): E;
        pop(): E;
        push(o: E): E;
        search(o: any): number;
        constructor();
    }
}
declare namespace java.util {
    /**
     * Implementation of Map interface based on a hash table. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/HashMap.html">[Sun
     * docs]</a>
     *
     * @param <K> key type
     * @param <V> value type
     */
    abstract class AbstractHashMap<K, V> extends java.util.AbstractMap<K, V> {
        /**
         * A map of integral hashCodes onto entries.
         */
        private hashCodeMap;
        /**
         * A map of Strings onto values.
         */
        private stringMap;
        constructor(ignored?: any, alsoIgnored?: any);
        clear(): void;
        reset(): void;
        containsKey(key: any): boolean;
        containsValue(value: any): boolean;
        _containsValue(value: any, entries: java.lang.Iterable<Map.Entry<K, V>>): boolean;
        entrySet(): java.util.Set<java.util.Map.Entry<K, V>>;
        get(key: any): V;
        put(key: K, value: V): V;
        remove(key: any): V;
        size(): number;
        /**
         * Subclasses must override to return a whether or not two keys or values are
         * equal.
         */
        abstract _equals(value1: any, value2: any): boolean;
        /**
         * Subclasses must override to return a hash code for a given key. The key is
         * guaranteed to be non-null and not a String.
         */
        abstract getHashCode(key: any): number;
        /**
         * Returns the Map.Entry whose key is Object equal to <code>key</code>,
         * provided that <code>key</code>'s hash code is <code>hashCode</code>;
         * or <code>null</code> if no such Map.Entry exists at the specified
         * hashCode.
         */
        getHashValue(key: any): V;
        /**
         * Returns the value for the given key in the stringMap. Returns
         * <code>null</code> if the specified key does not exist.
         */
        getStringValue(key: string): V;
        /**
         * Returns true if the a key exists in the hashCodeMap that is Object equal to
         * <code>key</code>, provided that <code>key</code>'s hash code is
         * <code>hashCode</code>.
         */
        hasHashValue(key: any): boolean;
        /**
         * Returns true if the given key exists in the stringMap.
         */
        hasStringValue(key: string): boolean;
        /**
         * Sets the specified key to the specified value in the hashCodeMap. Returns
         * the value previously at that key. Returns <code>null</code> if the
         * specified key did not exist.
         */
        putHashValue(key: K, value: V): V;
        /**
         * Sets the specified key to the specified value in the stringMap. Returns the
         * value previously at that key. Returns <code>null</code> if the specified
         * key did not exist.
         */
        putStringValue(key: string, value: V): V;
        /**
         * Removes the pair whose key is Object equal to <code>key</code> from
         * <code>hashCodeMap</code>, provided that <code>key</code>'s hash code
         * is <code>hashCode</code>. Returns the value that was associated with the
         * removed key, or null if no such key existed.
         */
        removeHashValue(key: any): V;
        /**
         * Removes the specified key from the stringMap and returns the value that was
         * previously there. Returns <code>null</code> if the specified key does not
         * exist.
         */
        removeStringValue(key: string): V;
    }
    namespace AbstractHashMap {
        class EntrySet extends java.util.AbstractSet<Map.Entry<any, any>> {
            __parent: any;
            clear(): void;
            contains(o: any): boolean;
            iterator(): java.util.Iterator<Map.Entry<any, any>>;
            remove(index?: any): any;
            remove$java_lang_Object(entry: any): boolean;
            size(): number;
            constructor(__parent: any);
        }
        /**
         * Iterator for <code>EntrySet</code>.
         */
        class EntrySetIterator implements java.util.Iterator<Map.Entry<any, any>> {
            __parent: any;
            forEachRemaining(consumer: (p1: any) => void): void;
            stringMapEntries: java.util.Iterator<Map.Entry<any, any>>;
            current: java.util.Iterator<Map.Entry<any, any>>;
            last: java.util.Iterator<Map.Entry<any, any>>;
            __hasNext: boolean;
            constructor(__parent: any);
            hasNext(): boolean;
            computeHasNext(): boolean;
            next(): Map.Entry<any, any>;
            remove(): void;
        }
    }
}
declare namespace java.util {
    /**
     * Skeletal implementation of a NavigableMap.
     */
    abstract class AbstractNavigableMap<K, V> extends java.util.AbstractMap<K, V> implements java.util.NavigableMap<K, V> {
        abstract comparator(): any;
        static copyOf<K, V>(entry: Map.Entry<K, V>): Map.Entry<K, V>;
        static getKeyOrNSE<K, V>(entry: Map.Entry<K, V>): K;
        ceilingEntry(key: K): Map.Entry<K, V>;
        ceilingKey(key: K): K;
        containsKey(k: any): boolean;
        descendingKeySet(): java.util.NavigableSet<K>;
        descendingMap(): java.util.NavigableMap<K, V>;
        entrySet(): java.util.Set<Map.Entry<K, V>>;
        firstEntry(): Map.Entry<K, V>;
        firstKey(): K;
        floorEntry(key: K): Map.Entry<K, V>;
        floorKey(key: K): K;
        get(k: any): V;
        headMap(toKey?: any, inclusive?: any): any;
        headMap$java_lang_Object(toKey: K): java.util.SortedMap<K, V>;
        higherEntry(key: K): Map.Entry<K, V>;
        higherKey(key: K): K;
        keySet(): java.util.Set<K>;
        lastEntry(): Map.Entry<K, V>;
        lastKey(): K;
        lowerEntry(key: K): Map.Entry<K, V>;
        lowerKey(key: K): K;
        navigableKeySet(): java.util.NavigableSet<K>;
        pollFirstEntry(): Map.Entry<K, V>;
        pollLastEntry(): Map.Entry<K, V>;
        subMap(fromKey?: any, fromInclusive?: any, toKey?: any, toInclusive?: any): any;
        subMap$java_lang_Object$java_lang_Object(fromKey: K, toKey: K): java.util.SortedMap<K, V>;
        tailMap(fromKey?: any, inclusive?: any): any;
        tailMap$java_lang_Object(fromKey: K): java.util.SortedMap<K, V>;
        containsEntry(entry: Map.Entry<any, any>): boolean;
        /**
         * Returns an iterator over the entries in this map in descending order.
         */
        abstract descendingEntryIterator(): java.util.Iterator<Map.Entry<K, V>>;
        /**
         * Returns an iterator over the entries in this map in ascending order.
         */
        abstract entryIterator(): java.util.Iterator<Map.Entry<K, V>>;
        /**
         * Returns the entry corresponding to the specified key. If no such entry exists returns
         * {@code null}.
         */
        abstract getEntry(key: K): Map.Entry<K, V>;
        /**
         * Returns the first entry or {@code null} if map is empty.
         */
        abstract getFirstEntry(): Map.Entry<K, V>;
        /**
         * Returns the last entry or {@code null} if map is empty.
         */
        abstract getLastEntry(): Map.Entry<K, V>;
        /**
         * Gets the entry corresponding to the specified key or the entry for the least key greater than
         * the specified key. If no such entry exists returns {@code null}.
         */
        abstract getCeilingEntry(key: K): Map.Entry<K, V>;
        /**
         * Gets the entry corresponding to the specified key or the entry for the greatest key less than
         * the specified key. If no such entry exists returns {@code null}.
         */
        abstract getFloorEntry(key: K): Map.Entry<K, V>;
        /**
         * Gets the entry for the least key greater than the specified key. If no such entry exists
         * returns {@code null}.
         */
        abstract getHigherEntry(key: K): Map.Entry<K, V>;
        /**
         * Returns the entry for the greatest key less than the specified key. If no such entry exists
         * returns {@code null}.
         */
        abstract getLowerEntry(key: K): Map.Entry<K, V>;
        /**
         * Remove an entry from the tree, returning whether it was found.
         */
        abstract removeEntry(entry: Map.Entry<K, V>): boolean;
        pollEntry(entry: Map.Entry<K, V>): Map.Entry<K, V>;
        constructor();
    }
    namespace AbstractNavigableMap {
        class DescendingMap extends java.util.AbstractNavigableMap<any, any> {
            __parent: any;
            clear(): void;
            comparator(): java.util.Comparator<any>;
            descendingMap(): java.util.NavigableMap<any, any>;
            headMap(toKey?: any, inclusive?: any): any;
            put(key: any, value: any): any;
            remove(key: any): any;
            size(): number;
            subMap(fromKey?: any, fromInclusive?: any, toKey?: any, toInclusive?: any): any;
            tailMap(fromKey?: any, inclusive?: any): any;
            ascendingMap(): java.util.AbstractNavigableMap<any, any>;
            descendingEntryIterator(): java.util.Iterator<Map.Entry<any, any>>;
            entryIterator(): java.util.Iterator<Map.Entry<any, any>>;
            getEntry(key: any): Map.Entry<any, any>;
            getFirstEntry(): Map.Entry<any, any>;
            getLastEntry(): Map.Entry<any, any>;
            getCeilingEntry(key: any): Map.Entry<any, any>;
            getFloorEntry(key: any): Map.Entry<any, any>;
            getHigherEntry(key: any): Map.Entry<any, any>;
            getLowerEntry(key: any): Map.Entry<any, any>;
            removeEntry(entry: Map.Entry<any, any>): boolean;
            constructor(__parent: any);
        }
        class EntrySet extends java.util.AbstractSet<Map.Entry<any, any>> {
            __parent: any;
            contains(o: any): boolean;
            iterator(): java.util.Iterator<Map.Entry<any, any>>;
            remove(index?: any): any;
            remove$java_lang_Object(o: any): boolean;
            size(): number;
            constructor(__parent: any);
        }
        class NavigableKeySet<K, V> extends java.util.AbstractSet<K> implements java.util.NavigableSet<K> {
            forEach(action: (p1: any) => void): void;
            map: java.util.NavigableMap<K, V>;
            constructor(map: java.util.NavigableMap<K, V>);
            ceiling(k: K): K;
            clear(): void;
            comparator(): java.util.Comparator<any>;
            contains(o: any): boolean;
            descendingIterator(): java.util.Iterator<K>;
            descendingSet(): java.util.NavigableSet<K>;
            first(): K;
            floor(k: K): K;
            headSet$java_lang_Object(toElement: K): java.util.SortedSet<K>;
            headSet(toElement?: any, inclusive?: any): any;
            higher(k: K): K;
            iterator(): java.util.Iterator<K>;
            last(): K;
            lower(k: K): K;
            pollFirst(): K;
            pollLast(): K;
            remove(index?: any): any;
            remove$java_lang_Object(o: any): boolean;
            size(): number;
            subSet(fromElement?: any, fromInclusive?: any, toElement?: any, toInclusive?: any): any;
            subSet$java_lang_Object$java_lang_Object(fromElement: K, toElement: K): java.util.SortedSet<K>;
            tailSet$java_lang_Object(fromElement: K): java.util.SortedSet<K>;
            tailSet(fromElement?: any, inclusive?: any): any;
        }
        namespace NavigableKeySet {
            class NavigableKeySet$0 implements java.util.Iterator<any> {
                private entryIterator;
                __parent: any;
                forEachRemaining(consumer: (p1: any) => void): void;
                hasNext(): boolean;
                next(): any;
                remove(): void;
                constructor(__parent: any, entryIterator: any);
            }
        }
    }
}
declare namespace java.util {
    /**
     * Utility methods that operate on collections. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/Collections.html">[Sun
     * docs]</a>
     */
    class Collections {
        static EMPTY_LIST: java.util.List<any>;
        static EMPTY_LIST_$LI$(): java.util.List<any>;
        static EMPTY_MAP: java.util.Map<any, any>;
        static EMPTY_MAP_$LI$(): java.util.Map<any, any>;
        static EMPTY_SET: java.util.Set<any>;
        static EMPTY_SET_$LI$(): java.util.Set<any>;
        static addAll<T>(c: java.util.Collection<any>, ...a: T[]): boolean;
        static asLifoQueue<T>(deque: java.util.Deque<T>): java.util.Queue<T>;
        /**
         * Perform a binary search on a sorted List, using a user-specified comparison
         * function.
         *
         * <p>
         * Note: The GWT implementation differs from the JDK implementation in that it
         * does not do an iterator-based binary search for Lists that do not implement
         * RandomAccess.
         * </p>
         *
         * @param sortedList List to search
         * @param key value to search for
         * @param comparator comparision function, <code>null</code> indicates
         * <i>natural ordering</i> should be used.
         * @return the index of an element with a matching value, or a negative number
         * which is the index of the next larger value (or just past the end
         * of the array if the searched value is larger than all elements in
         * the array) minus 1 (to ensure error returns are negative)
         * @throws ClassCastException if <code>key</code> and
         * <code>sortedList</code>'s elements cannot be compared by
         * <code>comparator</code>.
         */
        static binarySearch<T>(sortedList: java.util.List<any>, key: T, comparator?: java.util.Comparator<any>): number;
        static copy<T>(dest: java.util.List<any>, src: java.util.List<any>): void;
        static disjoint(c1: java.util.Collection<any>, c2: java.util.Collection<any>): boolean;
        static emptyIterator<T>(): java.util.Iterator<T>;
        static emptyList<T>(): java.util.List<T>;
        static emptyListIterator<T>(): java.util.ListIterator<T>;
        static emptyMap<K, V>(): java.util.Map<K, V>;
        static emptySet<T>(): java.util.Set<T>;
        static enumeration<T>(c: java.util.Collection<T>): java.util.Enumeration<T>;
        static fill<T>(list: java.util.List<any>, obj: T): void;
        static frequency(c: java.util.Collection<any>, o: any): number;
        static list<T>(e: java.util.Enumeration<T>): java.util.ArrayList<T>;
        static max<T>(coll: java.util.Collection<any>, comp?: java.util.Comparator<any>): T;
        static min<T>(coll: java.util.Collection<any>, comp?: java.util.Comparator<any>): T;
        static newSetFromMap<E>(map: java.util.Map<E, boolean>): java.util.Set<E>;
        static nCopies<T>(n: number, o: T): java.util.List<T>;
        static replaceAll<T>(list: java.util.List<T>, oldVal: T, newVal: T): boolean;
        static reverse<T>(l: java.util.List<T>): void;
        static reverseOrder$<T>(): java.util.Comparator<T>;
        static reverseOrder<T>(cmp?: any): any;
        /**
         * Rotates the elements in {@code list} by the distance {@code dist}
         * <p>
         * e.g. for a given list with elements [1, 2, 3, 4, 5, 6, 7, 8, 9, 0], calling rotate(list, 3) or
         * rotate(list, -7) would modify the list to look like this: [8, 9, 0, 1, 2, 3, 4, 5, 6, 7]
         *
         * @param lst the list whose elements are to be rotated.
         * @param dist is the distance the list is rotated. This can be any valid integer. Negative values
         * rotate the list backwards.
         */
        static rotate(lst: java.util.List<any>, dist: number): void;
        static shuffle<T>(list: java.util.List<T>, rnd?: java.util.Random): void;
        static singleton<T>(o: T): java.util.Set<T>;
        static singletonList<T>(o: T): java.util.List<T>;
        static singletonMap<K, V>(key: K, value: V): java.util.Map<K, V>;
        static sort<T>(target: java.util.List<T>, c?: java.util.Comparator<any>): void;
        static swap(list: java.util.List<any>, i: number, j: number): void;
        static unmodifiableCollection<T>(coll: java.util.Collection<any>): java.util.Collection<T>;
        static unmodifiableList<T>(list: java.util.List<any>): java.util.List<T>;
        static unmodifiableMap<K, V>(map: java.util.Map<any, any>): java.util.Map<K, V>;
        static unmodifiableSet<T>(set: java.util.Set<any>): java.util.Set<T>;
        static unmodifiableSortedMap<K, V>(map: java.util.SortedMap<K, any>): java.util.SortedMap<K, V>;
        static unmodifiableSortedSet<T>(set: java.util.SortedSet<any>): java.util.SortedSet<T>;
        /**
         * Computes hash code without preserving elements order (e.g. HashSet).
         */
        static hashCode$java_lang_Iterable<T>(collection: java.lang.Iterable<T>): number;
        /**
         * Computes hash code preserving collection order (e.g. ArrayList).
         */
        static hashCode<T>(list?: any): any;
        /**
         * Replace contents of a list from an array.
         *
         * @param <T> element type
         * @param target list to replace contents from an array
         * @param x an Object array which can contain only T instances
         */
        static replaceContents<T>(target: java.util.List<T>, x: any[]): void;
        static swapImpl<T>(list?: any, i?: any, j?: any): any;
        static swapImpl$java_lang_Object_A$int$int(a: any[], i: number, j: number): void;
    }
    namespace Collections {
        class LifoQueue<E> extends java.util.AbstractQueue<E> implements java.io.Serializable {
            deque: java.util.Deque<E>;
            constructor(deque: java.util.Deque<E>);
            iterator(): java.util.Iterator<E>;
            offer(e: E): boolean;
            peek(): E;
            poll(): E;
            size(): number;
        }
        class EmptyList extends java.util.AbstractList<any> implements java.util.RandomAccess, java.io.Serializable {
            contains(object: any): boolean;
            get(location: number): any;
            iterator(): java.util.Iterator<any>;
            listIterator$(): java.util.ListIterator<any>;
            size(): number;
            constructor();
        }
        class EmptyListIterator implements java.util.ListIterator<any> {
            forEachRemaining(consumer: (p1: any) => void): void;
            static INSTANCE: Collections.EmptyListIterator;
            static INSTANCE_$LI$(): Collections.EmptyListIterator;
            add(o: any): void;
            hasNext(): boolean;
            hasPrevious(): boolean;
            next(): any;
            nextIndex(): number;
            previous(): any;
            previousIndex(): number;
            remove(): void;
            set(o: any): void;
            constructor();
        }
        class EmptySet extends java.util.AbstractSet<any> implements java.io.Serializable {
            contains(object: any): boolean;
            iterator(): java.util.Iterator<any>;
            size(): number;
            constructor();
        }
        class EmptyMap extends java.util.AbstractMap<any, any> implements java.io.Serializable {
            containsKey(key: any): boolean;
            containsValue(value: any): boolean;
            entrySet(): java.util.Set<any>;
            get(key: any): any;
            keySet(): java.util.Set<any>;
            size(): number;
            values(): java.util.Collection<any>;
            constructor();
        }
        class ReverseComparator implements java.util.Comparator<java.lang.Comparable<any>> {
            static INSTANCE: Collections.ReverseComparator;
            static INSTANCE_$LI$(): Collections.ReverseComparator;
            compare(o1?: any, o2?: any): any;
            constructor();
        }
        class SetFromMap<E> extends java.util.AbstractSet<E> implements java.io.Serializable {
            backingMap: java.util.Map<E, boolean>;
            __keySet: java.util.Set<E>;
            constructor(map: java.util.Map<E, boolean>);
            add(index?: any, element?: any): any;
            add$java_lang_Object(e: E): boolean;
            clear(): void;
            contains(o: any): boolean;
            equals(o: any): boolean;
            hashCode(): number;
            iterator(): java.util.Iterator<E>;
            remove(index?: any): any;
            remove$java_lang_Object(o: any): boolean;
            size(): number;
            toString(): string;
            /**
             * Lazy initialize keySet to avoid NPE after deserialization.
             */
            keySet(): java.util.Set<E>;
        }
        class SingletonList<E> extends java.util.AbstractList<E> implements java.io.Serializable {
            element: E;
            constructor(element: E);
            contains(item: any): boolean;
            get(index: number): E;
            size(): number;
        }
        class UnmodifiableCollection<T> implements java.util.Collection<T> {
            forEach(action: (p1: any) => void): void;
            coll: java.util.Collection<any>;
            constructor(coll: java.util.Collection<any>);
            add(index?: any, element?: any): any;
            add$java_lang_Object(o: T): boolean;
            addAll(index?: any, c?: any): any;
            addAll$java_util_Collection(c: java.util.Collection<any>): boolean;
            clear(): void;
            contains(o: any): boolean;
            containsAll(c: java.util.Collection<any>): boolean;
            isEmpty(): boolean;
            iterator(): java.util.Iterator<T>;
            remove(index?: any): any;
            remove$java_lang_Object(o: any): boolean;
            removeAll(c: java.util.Collection<any>): boolean;
            retainAll(c: java.util.Collection<any>): boolean;
            size(): number;
            toArray$(): any[];
            toArray<E>(a?: any): any;
            toString(): string;
        }
        class UnmodifiableList<T> extends Collections.UnmodifiableCollection<T> implements java.util.List<T> {
            forEach(action: (p1: any) => void): void;
            list: java.util.List<any>;
            constructor(list: java.util.List<any>);
            add(index?: any, element?: any): any;
            addAll(index?: any, c?: any): any;
            equals(o: any): boolean;
            get(index: number): T;
            hashCode(): number;
            indexOf(o?: any, index?: any): any;
            indexOf$java_lang_Object(o: any): number;
            isEmpty(): boolean;
            lastIndexOf(o?: any, index?: any): any;
            lastIndexOf$java_lang_Object(o: any): number;
            listIterator$(): java.util.ListIterator<T>;
            listIterator(from?: any): any;
            remove(index?: any): any;
            set(index: number, element: T): T;
            subList(fromIndex: number, toIndex: number): java.util.List<T>;
        }
        class UnmodifiableRandomAccessList<T> extends Collections.UnmodifiableList<T> implements java.util.RandomAccess {
            constructor(list: java.util.List<any>);
        }
        class UnmodifiableSet<T> extends Collections.UnmodifiableCollection<T> implements java.util.Set<T> {
            forEach(action: (p1: any) => void): void;
            constructor(set: java.util.Set<any>);
            equals(o: any): boolean;
            hashCode(): number;
        }
        class UnmodifiableMap<K, V> implements java.util.Map<K, V> {
            __entrySet: Collections.UnmodifiableSet<java.util.Map.Entry<K, V>>;
            __keySet: Collections.UnmodifiableSet<K>;
            map: java.util.Map<any, any>;
            __values: Collections.UnmodifiableCollection<V>;
            constructor(map: java.util.Map<any, any>);
            clear(): void;
            containsKey(key: any): boolean;
            containsValue(val: any): boolean;
            entrySet(): java.util.Set<java.util.Map.Entry<K, V>>;
            equals(o: any): boolean;
            get(key: any): V;
            hashCode(): number;
            isEmpty(): boolean;
            keySet(): java.util.Set<K>;
            put(key: K, value: V): V;
            putAll(t: java.util.Map<any, any>): void;
            remove(key: any): V;
            size(): number;
            toString(): string;
            values(): java.util.Collection<V>;
        }
        namespace UnmodifiableMap {
            class UnmodifiableEntrySet<K, V> extends Collections.UnmodifiableSet<java.util.Map.Entry<K, V>> {
                constructor(s: java.util.Set<any>);
                contains(o: any): boolean;
                containsAll(o: java.util.Collection<any>): boolean;
                iterator(): java.util.Iterator<java.util.Map.Entry<K, V>>;
                toArray$(): any[];
                toArray<T>(a?: any): any;
                /**
                 * Wrap an array of Map.Entries as UnmodifiableEntries.
                 *
                 * @param array array to wrap
                 * @param size number of entries to wrap
                 */
                wrap(array: any[], size: number): void;
            }
            namespace UnmodifiableEntrySet {
                class UnmodifiableEntry<K, V> implements java.util.Map.Entry<K, V> {
                    entry: java.util.Map.Entry<any, any>;
                    constructor(entry: java.util.Map.Entry<any, any>);
                    equals(o: any): boolean;
                    getKey(): K;
                    getValue(): V;
                    hashCode(): number;
                    setValue(value: V): V;
                    toString(): string;
                }
                class UnmodifiableEntrySet$0 implements java.util.Iterator<java.util.Map.Entry<any, any>> {
                    private it;
                    __parent: any;
                    forEachRemaining(consumer: (p1: any) => void): void;
                    hasNext(): boolean;
                    next(): java.util.Map.Entry<any, any>;
                    remove(): void;
                    constructor(__parent: any, it: any);
                }
            }
        }
        class UnmodifiableSortedMap<K, V> extends Collections.UnmodifiableMap<K, V> implements java.util.SortedMap<K, V> {
            sortedMap: java.util.SortedMap<K, any>;
            constructor(sortedMap: java.util.SortedMap<K, any>);
            comparator(): java.util.Comparator<any>;
            equals(o: any): boolean;
            firstKey(): K;
            hashCode(): number;
            headMap(toKey?: any, inclusive?: any): any;
            headMap$java_lang_Object(toKey: K): java.util.SortedMap<K, V>;
            lastKey(): K;
            subMap(fromKey?: any, fromInclusive?: any, toKey?: any, toInclusive?: any): any;
            subMap$java_lang_Object$java_lang_Object(fromKey: K, toKey: K): java.util.SortedMap<K, V>;
            tailMap(fromKey?: any, inclusive?: any): any;
            tailMap$java_lang_Object(fromKey: K): java.util.SortedMap<K, V>;
        }
        class UnmodifiableSortedSet<E> extends Collections.UnmodifiableSet<E> implements java.util.SortedSet<E> {
            forEach(action: (p1: any) => void): void;
            sortedSet: java.util.SortedSet<E>;
            constructor(sortedSet: java.util.SortedSet<any>);
            comparator(): java.util.Comparator<any>;
            equals(o: any): boolean;
            first(): E;
            hashCode(): number;
            headSet(toElement?: any, inclusive?: any): any;
            headSet$java_lang_Object(toElement: E): java.util.SortedSet<E>;
            last(): E;
            subSet(fromElement?: any, fromInclusive?: any, toElement?: any, toInclusive?: any): any;
            subSet$java_lang_Object$java_lang_Object(fromElement: E, toElement: E): java.util.SortedSet<E>;
            tailSet(fromElement?: any, inclusive?: any): any;
            tailSet$java_lang_Object(fromElement: E): java.util.SortedSet<E>;
        }
        class UnmodifiableCollectionIterator<T> implements java.util.Iterator<T> {
            forEachRemaining(consumer: (p1: any) => void): void;
            it: java.util.Iterator<any>;
            constructor(it: java.util.Iterator<any>);
            hasNext(): boolean;
            next(): T;
            remove(): void;
        }
        class UnmodifiableListIterator<T> extends Collections.UnmodifiableCollectionIterator<T> implements java.util.ListIterator<T> {
            forEachRemaining(consumer: (p1: any) => void): void;
            lit: java.util.ListIterator<any>;
            constructor(lit: java.util.ListIterator<any>);
            add(o: T): void;
            hasPrevious(): boolean;
            nextIndex(): number;
            previous(): T;
            previousIndex(): number;
            set(o: T): void;
        }
        class RandomHolder {
            static rnd: java.util.Random;
            static rnd_$LI$(): java.util.Random;
        }
        class Collections$0<T> implements java.util.Enumeration<T> {
            private it;
            hasMoreElements(): boolean;
            nextElement(): T;
            constructor(it: any);
        }
        class Collections$1<T> implements java.util.Comparator<T> {
            private cmp;
            compare(t1: T, t2: T): number;
            constructor(cmp: any);
        }
    }
}
declare namespace java.util {
    /**
     * A {@link java.util.Map} of {@link Enum}s. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/EnumMap.html">[Sun
     * docs]</a>
     *
     * @param <K> key type
     * @param <V> value type
     */
    class EnumMap<K extends java.lang.Enum<K>, V> extends java.util.AbstractMap<K, V> {
        private __keySet;
        private __values;
        constructor(type?: any);
        clear(): void;
        clone(): EnumMap<K, V>;
        containsKey(key: any): boolean;
        containsValue(value: any): boolean;
        entrySet(): java.util.Set<java.util.Map.Entry<K, V>>;
        get(k: any): V;
        put(key?: any, value?: any): any;
        remove(key: any): V;
        size(): number;
        /**
         * Returns <code>key</code> as <code>K</code>. Only runtime checks that
         * key is an Enum, not that it's the particular Enum K. Should only be called
         * when you are sure <code>key</code> is of type <code>K</code>.
         */
        asKey(key: any): K;
        asOrdinal(key: any): number;
        init(type?: any): any;
        init$java_util_EnumMap(m: EnumMap<K, any>): void;
        set(ordinal: number, value: V): V;
    }
    namespace EnumMap {
        class EntrySet extends java.util.AbstractSet<Map.Entry<any, any>> {
            __parent: any;
            clear(): void;
            contains(o: any): boolean;
            iterator(): java.util.Iterator<Map.Entry<any, any>>;
            remove(index?: any): any;
            remove$java_lang_Object(entry: any): boolean;
            size(): number;
            constructor(__parent: any);
        }
        class EntrySetIterator implements java.util.Iterator<Map.Entry<any, any>> {
            __parent: any;
            forEachRemaining(consumer: (p1: any) => void): void;
            it: java.util.Iterator<any>;
            key: any;
            hasNext(): boolean;
            next(): Map.Entry<any, any>;
            remove(): void;
            constructor(__parent: any);
        }
        class MapEntry extends java.util.AbstractMapEntry<any, any> {
            __parent: any;
            key: any;
            constructor(__parent: any, key: any);
            getKey(): any;
            getValue(): any;
            setValue(value: any): any;
        }
    }
}
declare namespace java.util {
    /**
     * Hash table and linked-list implementation of the Set interface with
     * predictable iteration order. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/LinkedHashSet.html">[Sun
     * docs]</a>
     *
     * @param <E> element type.
     */
    class LinkedHashSet<E> extends java.util.HashSet<E> implements java.util.Set<E>, java.lang.Cloneable {
        forEach(action: (p1: any) => void): void;
        constructor(ignored?: any, alsoIgnored?: any);
        clone(): any;
    }
}
declare namespace java.util {
    /**
     * A helper to detect concurrent modifications to collections. This is implemented as a helper
     * utility so that we could remove the checks easily by a flag.
     */
    class ConcurrentModificationDetector {
        static API_CHECK: boolean;
        static API_CHECK_$LI$(): boolean;
        static MOD_COUNT_PROPERTY: string;
        static structureChanged(map: any): void;
        static recordLastKnownStructure(host: any, iterator: java.util.Iterator<any>): void;
        static checkStructuralChange(host: any, iterator: java.util.Iterator<any>): void;
    }
}
declare namespace java.util.logging {
    /**
     * An emulation of the java.util.logging.Logger class. See
     * <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/util/logging/Logger.html">
     * The Java API doc for details</a>
     */
    class Logger {
        static __static_initialized: boolean;
        static __static_initialize(): void;
        static GLOBAL_LOGGER_NAME: string;
        static LOGGING_ENABLED: string;
        static LOGGING_ENABLED_$LI$(): string;
        static LOGGING_WARNING: boolean;
        static LOGGING_WARNING_$LI$(): boolean;
        static LOGGING_SEVERE: boolean;
        static LOGGING_SEVERE_$LI$(): boolean;
        static LOGGING_FALSE: boolean;
        static LOGGING_FALSE_$LI$(): boolean;
        static __static_initializer_0(): void;
        static getGlobal(): Logger;
        static getLogger(name: string): Logger;
        static assertLoggingValues(): void;
        private handlers;
        private level;
        private name;
        private parent;
        private useParentHandlers;
        constructor(name: string, resourceName: string);
        addHandler(handler: java.util.logging.Handler): void;
        config(msg: string): void;
        fine(msg: string): void;
        finer(msg: string): void;
        finest(msg: string): void;
        info(msg: string): void;
        warning(msg: string): void;
        severe(msg: string): void;
        getHandlers(): java.util.logging.Handler[];
        getLevel(): java.util.logging.Level;
        getName(): string;
        getParent(): Logger;
        getUseParentHandlers(): boolean;
        isLoggable(messageLevel: java.util.logging.Level): boolean;
        log$java_util_logging_Level$java_lang_String(level: java.util.logging.Level, msg: string): void;
        log(level?: any, msg?: any, thrown?: any): any;
        log$java_util_logging_LogRecord(record: java.util.logging.LogRecord): void;
        removeHandler(handler: java.util.logging.Handler): void;
        setLevel(newLevel: java.util.logging.Level): void;
        setParent(newParent: Logger): void;
        setUseParentHandlers(newUseParentHandlers: boolean): void;
        private getEffectiveLevel();
        actuallyLog(level?: any, msg?: any, thrown?: any): any;
        private actuallyLog$java_util_logging_LogRecord(record);
    }
}
declare namespace javaemul.internal {
    /**
     * A utility class that provides utility functions to do precondition checks inside GWT-SDK.
     */
    class InternalPreconditions {
        static CHECKED_MODE: boolean;
        static CHECKED_MODE_$LI$(): boolean;
        static TYPE_CHECK: boolean;
        static TYPE_CHECK_$LI$(): boolean;
        static API_CHECK: boolean;
        static API_CHECK_$LI$(): boolean;
        static BOUND_CHECK: boolean;
        static BOUND_CHECK_$LI$(): boolean;
        static checkType(expression: boolean): void;
        static checkCriticalType(expression: boolean): void;
        /**
         * Ensures the truth of an expression that verifies array type.
         */
        static checkArrayType$boolean(expression: boolean): void;
        static checkCriticalArrayType$boolean(expression: boolean): void;
        /**
         * Ensures the truth of an expression that verifies array type.
         */
        static checkArrayType(expression?: any, errorMessage?: any): any;
        static checkCriticalArrayType(expression?: any, errorMessage?: any): any;
        /**
         * Ensures the truth of an expression involving existence of an element.
         */
        static checkElement$boolean(expression: boolean): void;
        /**
         * Ensures the truth of an expression involving existence of an element.
         * <p>
         * For cases where failing fast is pretty important and not failing early could cause bugs that
         * are much harder to debug.
         */
        static checkCriticalElement$boolean(expression: boolean): void;
        /**
         * Ensures the truth of an expression involving existence of an element.
         */
        static checkElement(expression?: any, errorMessage?: any): any;
        /**
         * Ensures the truth of an expression involving existence of an element.
         * <p>
         * For cases where failing fast is pretty important and not failing early could cause bugs that
         * are much harder to debug.
         */
        static checkCriticalElement(expression?: any, errorMessage?: any): any;
        /**
         * Ensures the truth of an expression involving one or more parameters to the calling method.
         */
        static checkArgument$boolean(expression: boolean): void;
        /**
         * Ensures the truth of an expression involving one or more parameters to the calling method.
         * <p>
         * For cases where failing fast is pretty important and not failing early could cause bugs that
         * are much harder to debug.
         */
        static checkCriticalArgument$boolean(expression: boolean): void;
        /**
         * Ensures the truth of an expression involving one or more parameters to the calling method.
         */
        static checkArgument$boolean$java_lang_Object(expression: boolean, errorMessage: any): void;
        /**
         * Ensures the truth of an expression involving one or more parameters to the calling method.
         * <p>
         * For cases where failing fast is pretty important and not failing early could cause bugs that
         * are much harder to debug.
         */
        static checkCriticalArgument$boolean$java_lang_Object(expression: boolean, errorMessage: any): void;
        /**
         * Ensures the truth of an expression involving one or more parameters to the calling method.
         */
        static checkArgument(expression?: any, errorMessageTemplate?: any, ...errorMessageArgs: any[]): any;
        /**
         * Ensures the truth of an expression involving one or more parameters to the calling method.
         * <p>
         * For cases where failing fast is pretty important and not failing early could cause bugs that
         * are much harder to debug.
         */
        static checkCriticalArgument(expression?: any, errorMessageTemplate?: any, ...errorMessageArgs: any[]): any;
        /**
         * Ensures the truth of an expression involving the state of the calling instance, but not
         * involving any parameters to the calling method.
         *
         * @param expression a boolean expression
         * @throws IllegalStateException if {@code expression} is false
         */
        static checkState$boolean(expression: boolean): void;
        /**
         * Ensures the truth of an expression involving the state of the calling instance, but not
         * involving any parameters to the calling method.
         * <p>
         * For cases where failing fast is pretty important and not failing early could cause bugs that
         * are much harder to debug.
         */
        static checkCritcalState(expression: boolean): void;
        /**
         * Ensures the truth of an expression involving the state of the calling instance, but not
         * involving any parameters to the calling method.
         */
        static checkState(expression?: any, errorMessage?: any): any;
        /**
         * Ensures the truth of an expression involving the state of the calling instance, but not
         * involving any parameters to the calling method.
         */
        static checkCriticalState(expression: boolean, errorMessage: any): void;
        /**
         * Ensures that an object reference passed as a parameter to the calling method is not null.
         */
        static checkNotNull$java_lang_Object<T>(reference: T): T;
        static checkCriticalNotNull$java_lang_Object<T>(reference: T): T;
        /**
         * Ensures that an object reference passed as a parameter to the calling method is not null.
         */
        static checkNotNull(reference?: any, errorMessage?: any): any;
        static checkCriticalNotNull(reference?: any, errorMessage?: any): any;
        /**
         * Ensures that {@code size} specifies a valid array size (i.e. non-negative).
         */
        static checkArraySize(size: number): void;
        static checkCriticalArraySize(size: number): void;
        /**
         * Ensures that {@code index} specifies a valid <i>element</i> in an array, list or string of size
         * {@code size}. An element index may range from zero, inclusive, to {@code size}, exclusive.
         */
        static checkElementIndex(index: number, size: number): void;
        static checkCriticalElementIndex(index: number, size: number): void;
        /**
         * Ensures that {@code index} specifies a valid <i>position</i> in an array, list or string of
         * size {@code size}. A position index may range from zero to {@code size}, inclusive.
         */
        static checkPositionIndex(index: number, size: number): void;
        static checkCriticalPositionIndex(index: number, size: number): void;
        /**
         * Ensures that {@code start} and {@code end} specify a valid <i>positions</i> in an array, list
         * or string of size {@code size}, and are in order. A position index may range from zero to
         * {@code size}, inclusive.
         */
        static checkPositionIndexes(start: number, end: number, size: number): void;
        /**
         * Ensures that {@code start} and {@code end} specify a valid <i>positions</i> in an array, list
         * or string of size {@code size}, and are in order. A position index may range from zero to
         * {@code size}, inclusive.
         */
        static checkCriticalPositionIndexes(start: number, end: number, size: number): void;
        /**
         * Checks that bounds are correct.
         *
         * @throw StringIndexOutOfBoundsException if the range is not legal
         */
        static checkStringBounds(start: number, end: number, size: number): void;
        /**
         * Substitutes each {@code %s} in {@code template} with an argument. These are matched by
         * position: the first {@code %s} gets {@code args[0]}, etc.  If there are more arguments than
         * placeholders, the unmatched arguments will be appended to the end of the formatted message in
         * square braces.
         */
        private static format(template, ...args);
        constructor();
    }
}
declare namespace java.util {
    /**
     * Implementation of Map interface based on a hash table. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/HashMap.html">[Sun
     * docs]</a>
     *
     * @param <K> key type
     * @param <V> value type
     */
    class HashMap<K, V> extends java.util.AbstractHashMap<K, V> implements java.lang.Cloneable, java.io.Serializable {
        /**
         * Ensures that RPC will consider type parameter K to be exposed. It will be
         * pruned by dead code elimination.
         */
        private exposeKey;
        /**
         * Ensures that RPC will consider type parameter V to be exposed. It will be
         * pruned by dead code elimination.
         */
        private exposeValue;
        constructor(ignored?: any, alsoIgnored?: any);
        clone(): any;
        _equals(value1: any, value2: any): boolean;
        getHashCode(key: any): number;
    }
}
declare namespace java.util {
    /**
     * Map using reference equality on keys. <a
     * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/IdentityHashMap.html">[Sun
     * docs]</a>
     *
     * @param <K> key type
     * @param <V> value type
     */
    class IdentityHashMap<K, V> extends java.util.AbstractHashMap<K, V> implements java.util.Map<K, V>, java.lang.Cloneable, java.io.Serializable {
        /**
         * Ensures that RPC will consider type parameter K to be exposed. It will be
         * pruned by dead code elimination.
         */
        private exposeKey;
        /**
         * Ensures that RPC will consider type parameter V to be exposed. It will be
         * pruned by dead code elimination.
         */
        private exposeValue;
        constructor(toBeCopied?: any);
        clone(): any;
        equals(obj: any): boolean;
        hashCode(): number;
        _equals(value1: any, value2: any): boolean;
        getHashCode(key: any): number;
    }
}
declare namespace java.util {
    /**
     * Implements a TreeMap using a red-black tree. This guarantees O(log n)
     * performance on lookups, inserts, and deletes while maintaining linear
     * in-order traversal time. Null keys and values are fully supported if the
     * comparator supports them (the default comparator does not).
     *
     * @param <K> key type
     * @param <V> value type
     */
    class TreeMap<K, V> extends java.util.AbstractNavigableMap<K, V> implements java.io.Serializable {
        static SubMapType_All: TreeMap.SubMapType;
        static SubMapType_All_$LI$(): TreeMap.SubMapType;
        static SubMapType_Head: TreeMap.SubMapType;
        static SubMapType_Head_$LI$(): TreeMap.SubMapType;
        static SubMapType_Range: TreeMap.SubMapType;
        static SubMapType_Range_$LI$(): TreeMap.SubMapType;
        static SubMapType_Tail: TreeMap.SubMapType;
        static SubMapType_Tail_$LI$(): TreeMap.SubMapType;
        static LEFT: number;
        static RIGHT: number;
        static otherChild(child: number): number;
        private cmp;
        private exposeKeyType;
        private exposeValueType;
        private root;
        private __size;
        constructor(c?: any);
        clear(): void;
        comparator(): java.util.Comparator<any>;
        entrySet(): java.util.Set<Map.Entry<K, V>>;
        headMap(toKey?: any, inclusive?: any): any;
        put(key?: any, value?: any): any;
        put$java_lang_Object$java_lang_Object(key: K, value: V): V;
        remove(k: any): V;
        size(): number;
        subMap(fromKey?: any, fromInclusive?: any, toKey?: any, toInclusive?: any): any;
        tailMap(fromKey?: any, inclusive?: any): any;
        /**
         * Returns the first node which compares greater than the given key.
         *
         * @param key the key to search for
         * @return the next node, or null if there is none
         */
        getNodeAfter(key: K, inclusive: boolean): TreeMap.Node<K, V>;
        /**
         * Returns the last node which is strictly less than the given key.
         *
         * @param key the key to search for
         * @return the previous node, or null if there is none
         */
        getNodeBefore(key: K, inclusive: boolean): TreeMap.Node<K, V>;
        /**
         * Used for testing. Validate that the tree meets all red-black correctness
         * requirements. These include:
         *
         * <pre>
         * - root is black
         * - no children of a red node may be red
         * - the black height of every path through the three to a leaf is exactly the same
         * </pre>
         *
         * @throws RuntimeException if any correctness errors are detected.
         */
        assertCorrectness$(): void;
        descendingEntryIterator(): java.util.Iterator<Map.Entry<K, V>>;
        entryIterator(): java.util.Iterator<Map.Entry<K, V>>;
        /**
         * Internal helper function for public {@link #assertCorrectness()}.
         *
         * @param tree the subtree to validate.
         * @param isRed true if the parent of this node is red.
         * @return the black height of this subtree.
         * @throws RuntimeException if this RB-tree is not valid.
         */
        assertCorrectness(tree?: any, isRed?: any): any;
        /**
         * Finds an entry given a key and returns the node.
         *
         * @param key the search key
         * @return the node matching the key or null
         */
        getEntry(key: K): Map.Entry<K, V>;
        /**
         * Returns the left-most node of the tree, or null if empty.
         */
        getFirstEntry(): Map.Entry<K, V>;
        /**
         * Returns the right-most node of the tree, or null if empty.
         */
        getLastEntry(): Map.Entry<K, V>;
        getCeilingEntry(key: K): Map.Entry<K, V>;
        getFloorEntry(key: K): Map.Entry<K, V>;
        getHigherEntry(key: K): Map.Entry<K, V>;
        getLowerEntry(key: K): Map.Entry<K, V>;
        removeEntry(entry: Map.Entry<K, V>): boolean;
        inOrderAdd(list: java.util.List<Map.Entry<K, V>>, type: TreeMap.SubMapType, current: TreeMap.Node<K, V>, fromKey: K, fromInclusive: boolean, toKey: K, toInclusive: boolean): void;
        inRange(type: TreeMap.SubMapType, key: K, fromKey: K, fromInclusive: boolean, toKey: K, toInclusive: boolean): boolean;
        /**
         * Insert a node into a subtree, collecting state about the insertion.
         *
         * If the same key already exists, the value of the node is overwritten with
         * the value from the new node instead.
         *
         * @param tree subtree to insert into
         * @param newNode new node to insert
         * @param state result of the insertion: state.found true if the key already
         * existed in the tree state.value the old value if the key existed
         * @return the new subtree root
         */
        insert(tree: TreeMap.Node<K, V>, newNode: TreeMap.Node<K, V>, state: TreeMap.State<V>): TreeMap.Node<K, V>;
        /**
         * Returns true if <code>node</code> is red. Note that null pointers are
         * considered black.
         */
        isRed(node: TreeMap.Node<K, V>): boolean;
        /**
         * Returns true if <code>a</code> is greater than or equal to <code>b</code>.
         */
        larger(a: K, b: K, orEqual: boolean): boolean;
        /**
         * Returns true if <code>a</code> is less than or equal to <code>b</code>.
         */
        smaller(a: K, b: K, orEqual: boolean): boolean;
        /**
         * Remove a key from the tree, returning whether it was found and its value.
         *
         * @param key key to remove
         * @param state return state, not null
         * @return true if the value was found
         */
        removeWithState(key: K, state: TreeMap.State<V>): boolean;
        /**
         * replace 'node' with 'newNode' in the tree rooted at 'head'. Could have
         * avoided this traversal if each node maintained a parent pointer.
         */
        replaceNode(head: TreeMap.Node<K, V>, node: TreeMap.Node<K, V>, newNode: TreeMap.Node<K, V>): void;
        /**
         * Perform a double rotation, first rotating the child which will become the
         * root in the opposite direction, then rotating the root in the specified
         * direction.
         *
         * <pre>
         * A                                               F
         * B   C    becomes (with rotateDirection=0)       A   C
         * D E F G                                         B E   G
         * D
         * </pre>
         *
         * @param tree root of the subtree to rotate
         * @param rotateDirection the direction to rotate: 0=left, 1=right
         * @return the new root of the rotated subtree
         */
        rotateDouble(tree: TreeMap.Node<K, V>, rotateDirection: number): TreeMap.Node<K, V>;
        /**
         * Perform a single rotation, pushing the root of the subtree to the specified
         * direction.
         *
         * <pre>
         * A                                              B
         * B   C     becomes (with rotateDirection=1)     D   A
         * D E                                              E   C
         * </pre>
         *
         * @param tree the root of the subtree to rotate
         * @param rotateDirection the direction to rotate: 0=left rotation, 1=right
         * @return the new root of the rotated subtree
         */
        rotateSingle(tree: TreeMap.Node<K, V>, rotateDirection: number): TreeMap.Node<K, V>;
    }
    namespace TreeMap {
        /**
         * Iterator for <code>descendingMap().entrySet()</code>.
         */
        class DescendingEntryIterator implements java.util.Iterator<Map.Entry<any, any>> {
            __parent: any;
            forEachRemaining(consumer: (p1: any) => void): void;
            iter: java.util.ListIterator<Map.Entry<any, any>>;
            last: Map.Entry<any, any>;
            /**
             * Create an iterator which may return only a restricted range.
             *
             * @param fromKey the first key to return in the iterator.
             * @param toKey the upper bound of keys to return.
             */
            constructor(__parent: any, type?: TreeMap.SubMapType, fromKey?: any, fromInclusive?: boolean, toKey?: any, toInclusive?: boolean);
            hasNext(): boolean;
            next(): Map.Entry<any, any>;
            remove(): void;
        }
        /**
         * Iterator for <code>EntrySet</code>.
         */
        class EntryIterator implements java.util.Iterator<Map.Entry<any, any>> {
            __parent: any;
            forEachRemaining(consumer: (p1: any) => void): void;
            iter: java.util.ListIterator<Map.Entry<any, any>>;
            last: Map.Entry<any, any>;
            /**
             * Create an iterator which may return only a restricted range.
             *
             * @param fromKey the first key to return in the iterator.
             * @param toKey the upper bound of keys to return.
             */
            constructor(__parent: any, type?: TreeMap.SubMapType, fromKey?: any, fromInclusive?: boolean, toKey?: any, toInclusive?: boolean);
            hasNext(): boolean;
            next(): Map.Entry<any, any>;
            remove(): void;
        }
        class EntrySet extends java.util.AbstractNavigableMap.EntrySet {
            __parent: any;
            clear(): void;
            constructor(__parent: any);
        }
        /**
         * Tree node.
         *
         * @param <K> key type
         * @param <V> value type
         */
        class Node<K, V> extends AbstractMap.SimpleEntry<K, V> {
            child: TreeMap.Node<K, V>[];
            isRed: boolean;
            /**
             * Create a node of the specified color.
             *
             * @param key
             * @param value
             * @param isRed true if this should be a red node, false for black
             */
            constructor(key: K, value: V, isRed?: boolean);
        }
        /**
         * A state object which is passed down the tree for both insert and remove.
         * All uses make use of the done flag to indicate when no further rebalancing
         * of the tree is required. Remove methods use the found flag to indicate when
         * the desired key has been found. value is used both to return the value of a
         * removed node as well as to pass in a value which must match (used for
         * entrySet().remove(entry)), and the matchValue flag is used to request this
         * behavior.
         *
         * @param <V> value type
         */
        class State<V> {
            done: boolean;
            found: boolean;
            matchValue: boolean;
            value: V;
            toString(): string;
            constructor();
        }
        class SubMap extends java.util.AbstractNavigableMap<any, any> {
            __parent: any;
            fromInclusive: boolean;
            fromKey: any;
            toInclusive: boolean;
            toKey: any;
            type: TreeMap.SubMapType;
            constructor(__parent: any, type: TreeMap.SubMapType, fromKey: any, fromInclusive: boolean, toKey: any, toInclusive: boolean);
            comparator(): java.util.Comparator<any>;
            entrySet(): java.util.Set<Map.Entry<any, any>>;
            headMap(toKey?: any, toInclusive?: any): any;
            isEmpty(): boolean;
            put(key?: any, value?: any): any;
            put$java_lang_Object$java_lang_Object(key: any, value: any): any;
            remove(k: any): any;
            size(): number;
            subMap(newFromKey?: any, newFromInclusive?: any, newToKey?: any, newToInclusive?: any): any;
            tailMap(fromKey?: any, fromInclusive?: any): any;
            descendingEntryIterator(): java.util.Iterator<Map.Entry<any, any>>;
            entryIterator(): java.util.Iterator<Map.Entry<any, any>>;
            getEntry(key: any): Map.Entry<any, any>;
            getFirstEntry(): Map.Entry<any, any>;
            getLastEntry(): Map.Entry<any, any>;
            getCeilingEntry(key: any): Map.Entry<any, any>;
            getFloorEntry(key: any): Map.Entry<any, any>;
            getHigherEntry(key: any): Map.Entry<any, any>;
            getLowerEntry(key: any): Map.Entry<any, any>;
            removeEntry(entry: Map.Entry<any, any>): boolean;
            guardInRange(entry: Map.Entry<any, any>): Map.Entry<any, any>;
            inRange(key: any): boolean;
        }
        namespace SubMap {
            class SubMap$0 extends TreeMap.SubMap.EntrySet {
                __parent: any;
                isEmpty(): boolean;
                constructor(__parent: any);
            }
        }
        class SubMapType {
            /**
             * Returns true if this submap type uses a from-key.
             */
            fromKeyValid(): boolean;
            /**
             * Returns true if this submap type uses a to-key.
             */
            toKeyValid(): boolean;
        }
        class SubMapTypeHead extends TreeMap.SubMapType {
            toKeyValid(): boolean;
        }
        class SubMapTypeRange extends TreeMap.SubMapType {
            fromKeyValid(): boolean;
            toKeyValid(): boolean;
        }
        class SubMapTypeTail extends TreeMap.SubMapType {
            fromKeyValid(): boolean;
        }
    }
}
declare namespace java.util {
    class Hashtable<K, V> extends java.util.HashMap<K, V> implements java.util.Dictionary<K, V> {
        static serialVersionUID: number;
        constructor(ignored?: any, alsoIgnored?: any);
        keys(): java.util.Enumeration<K>;
        elements(): java.util.Enumeration<V>;
    }
    namespace Hashtable {
        class Hashtable$0 implements java.util.Enumeration<any> {
            private it;
            __parent: any;
            hasMoreElements(): boolean;
            nextElement(): any;
            constructor(__parent: any, it: any);
        }
        class Hashtable$1 implements java.util.Enumeration<any> {
            private it;
            __parent: any;
            hasMoreElements(): boolean;
            nextElement(): any;
            constructor(__parent: any, it: any);
        }
    }
}
declare namespace java.util {
    /**
     * Hash table implementation of the Map interface with predictable iteration
     * order. <a href=
     * "http://java.sun.com/j2se/1.5.0/docs/api/java/util/LinkedHashMap.html">[Sun
     * docs]</a>
     *
     * @param <K>
     * key type.
     * @param <V>
     * value type.
     */
    class LinkedHashMap<K, V> extends java.util.HashMap<K, V> implements java.util.Map<K, V> {
        private accessOrder;
        private head;
        private map;
        constructor(ignored?: any, alsoIgnored?: any, accessOrder?: any);
        clear(): void;
        resetChainEntries(): void;
        clone(): any;
        containsKey(key: any): boolean;
        containsValue(value: any): boolean;
        entrySet(): java.util.Set<java.util.Map.Entry<K, V>>;
        get(key: any): V;
        put(key?: any, value?: any): any;
        put$java_lang_Object$java_lang_Object(key: K, value: V): V;
        remove(key: any): V;
        size(): number;
        removeEldestEntry(eldest: java.util.Map.Entry<K, V>): boolean;
        recordAccess(entry: LinkedHashMap.ChainEntry): void;
    }
    namespace LinkedHashMap {
        /**
         * The entry we use includes next/prev pointers for a doubly-linked circular
         * list with a head node. This reduces the special cases we have to deal
         * with in the list operations.
         *
         * Note that we duplicate the key from the underlying hash map so we can
         * find the eldest entry. The alternative would have been to modify HashMap
         * so more of the code was directly usable here, but this would have added
         * some overhead to HashMap, or to reimplement most of the HashMap code here
         * with small modifications. Paying a small storage cost only if you use
         * LinkedHashMap and minimizing code size seemed like a better tradeoff
         */
        class ChainEntry extends AbstractMap.SimpleEntry<any, any> {
            __parent: any;
            next: LinkedHashMap.ChainEntry;
            prev: LinkedHashMap.ChainEntry;
            constructor(__parent: any, key?: any, value?: any);
            /**
             * Add this node to the end of the chain.
             */
            addToEnd(): void;
            /**
             * Remove this node from any list it may be a part of.
             */
            remove(): void;
        }
        class EntrySet extends java.util.AbstractSet<java.util.Map.Entry<any, any>> {
            __parent: any;
            clear(): void;
            contains(o: any): boolean;
            iterator(): java.util.Iterator<java.util.Map.Entry<any, any>>;
            remove(index?: any): any;
            remove$java_lang_Object(entry: any): boolean;
            size(): number;
            constructor(__parent: any);
        }
        namespace EntrySet {
            class EntryIterator implements java.util.Iterator<java.util.Map.Entry<any, any>> {
                __parent: any;
                forEachRemaining(consumer: (p1: any) => void): void;
                last: LinkedHashMap.ChainEntry;
                __next: LinkedHashMap.ChainEntry;
                constructor(__parent: any);
                hasNext(): boolean;
                next(): java.util.Map.Entry<any, any>;
                remove(): void;
            }
        }
    }
}
