package def.dom;
import def.js.Uint8ClampedArray;
public class ImageData extends def.js.Object {
    public Uint8ClampedArray data;
    public double height;
    public double width;
    public static ImageData prototype;
    public ImageData(double width, double height){}
    public ImageData(Uint8ClampedArray array, double width, double height){}
    protected ImageData(){}
}

