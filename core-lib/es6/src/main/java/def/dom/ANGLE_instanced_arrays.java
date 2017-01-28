package def.dom;

import def.js.Object;

public class ANGLE_instanced_arrays extends def.js.Object {
    native public void drawArraysInstancedANGLE(double mode, double first, double count, double primcount);
    native public void drawElementsInstancedANGLE(double mode, double count, double type, double offset, double primcount);
    native public void vertexAttribDivisorANGLE(double index, double divisor);
    public double VERTEX_ATTRIB_ARRAY_DIVISOR_ANGLE;
    public static ANGLE_instanced_arrays prototype;
    public ANGLE_instanced_arrays(){}
}

