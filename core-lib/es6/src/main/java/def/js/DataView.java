package def.js;
public class DataView extends def.js.Object {
    public ArrayBuffer buffer;
    public double byteLength;
    public double byteOffset;
    /**
      * Gets the Float32 value at the specified byte offset from the start of the view. There is 
      * no alignment constraint; multi-byte values may be fetched from any offset. 
      * @param byteOffset The place in the buffer at which the value should be retrieved.
      */
    native public double getFloat32(double byteOffset, java.lang.Boolean littleEndian);
    /**
      * Gets the Float64 value at the specified byte offset from the start of the view. There is
      * no alignment constraint; multi-byte values may be fetched from any offset. 
      * @param byteOffset The place in the buffer at which the value should be retrieved.
      */
    native public double getFloat64(double byteOffset, java.lang.Boolean littleEndian);
    /**
      * Gets the Int8 value at the specified byte offset from the start of the view. There is 
      * no alignment constraint; multi-byte values may be fetched from any offset. 
      * @param byteOffset The place in the buffer at which the value should be retrieved.
      */
    native public double getInt8(double byteOffset);
    /**
      * Gets the Int16 value at the specified byte offset from the start of the view. There is 
      * no alignment constraint; multi-byte values may be fetched from any offset. 
      * @param byteOffset The place in the buffer at which the value should be retrieved.
      */
    native public double getInt16(double byteOffset, java.lang.Boolean littleEndian);
    /**
      * Gets the Int32 value at the specified byte offset from the start of the view. There is 
      * no alignment constraint; multi-byte values may be fetched from any offset. 
      * @param byteOffset The place in the buffer at which the value should be retrieved.
      */
    native public double getInt32(double byteOffset, java.lang.Boolean littleEndian);
    /**
      * Gets the Uint8 value at the specified byte offset from the start of the view. There is 
      * no alignment constraint; multi-byte values may be fetched from any offset. 
      * @param byteOffset The place in the buffer at which the value should be retrieved.
      */
    native public double getUint8(double byteOffset);
    /**
      * Gets the Uint16 value at the specified byte offset from the start of the view. There is 
      * no alignment constraint; multi-byte values may be fetched from any offset. 
      * @param byteOffset The place in the buffer at which the value should be retrieved.
      */
    native public double getUint16(double byteOffset, java.lang.Boolean littleEndian);
    /**
      * Gets the Uint32 value at the specified byte offset from the start of the view. There is 
      * no alignment constraint; multi-byte values may be fetched from any offset. 
      * @param byteOffset The place in the buffer at which the value should be retrieved.
      */
    native public double getUint32(double byteOffset, java.lang.Boolean littleEndian);
    /**
      * Stores an Float32 value at the specified byte offset from the start of the view. 
      * @param byteOffset The place in the buffer at which the value should be set.
      * @param value The value to set.
      * @param littleEndian If false or undefined, a big-endian value should be written, 
      * otherwise a little-endian value should be written.
      */
    native public void setFloat32(double byteOffset, double value, java.lang.Boolean littleEndian);
    /**
      * Stores an Float64 value at the specified byte offset from the start of the view. 
      * @param byteOffset The place in the buffer at which the value should be set.
      * @param value The value to set.
      * @param littleEndian If false or undefined, a big-endian value should be written, 
      * otherwise a little-endian value should be written.
      */
    native public void setFloat64(double byteOffset, double value, java.lang.Boolean littleEndian);
    /**
      * Stores an Int8 value at the specified byte offset from the start of the view. 
      * @param byteOffset The place in the buffer at which the value should be set.
      * @param value The value to set.
      */
    native public void setInt8(double byteOffset, double value);
    /**
      * Stores an Int16 value at the specified byte offset from the start of the view. 
      * @param byteOffset The place in the buffer at which the value should be set.
      * @param value The value to set.
      * @param littleEndian If false or undefined, a big-endian value should be written, 
      * otherwise a little-endian value should be written.
      */
    native public void setInt16(double byteOffset, double value, java.lang.Boolean littleEndian);
    /**
      * Stores an Int32 value at the specified byte offset from the start of the view. 
      * @param byteOffset The place in the buffer at which the value should be set.
      * @param value The value to set.
      * @param littleEndian If false or undefined, a big-endian value should be written, 
      * otherwise a little-endian value should be written.
      */
    native public void setInt32(double byteOffset, double value, java.lang.Boolean littleEndian);
    /**
      * Stores an Uint8 value at the specified byte offset from the start of the view. 
      * @param byteOffset The place in the buffer at which the value should be set.
      * @param value The value to set.
      */
    native public void setUint8(double byteOffset, double value);
    /**
      * Stores an Uint16 value at the specified byte offset from the start of the view. 
      * @param byteOffset The place in the buffer at which the value should be set.
      * @param value The value to set.
      * @param littleEndian If false or undefined, a big-endian value should be written, 
      * otherwise a little-endian value should be written.
      */
    native public void setUint16(double byteOffset, double value, java.lang.Boolean littleEndian);
    /**
      * Stores an Uint32 value at the specified byte offset from the start of the view. 
      * @param byteOffset The place in the buffer at which the value should be set.
      * @param value The value to set.
      * @param littleEndian If false or undefined, a big-endian value should be written, 
      * otherwise a little-endian value should be written.
      */
    native public void setUint32(double byteOffset, double value, java.lang.Boolean littleEndian);
    native public java.lang.String $get(Symbol toStringTag);
    public DataView(ArrayBuffer buffer, double byteOffset, double byteLength){}
    public DataView(ArrayBuffer buffer, double byteOffset){}
    public DataView(ArrayBuffer buffer){}
    protected DataView(){}
}

