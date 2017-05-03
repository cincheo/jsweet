package def.dom;

import jsweet.util.StringTypes;
import jsweet.util.StringTypes._2d;
import jsweet.util.StringTypes.experimental_webgl;

public class HTMLCanvasElement extends HTMLElement {
    /**
      * Gets or sets the height of a canvas element on a document.
      */
    public double height;
    /**
      * Gets or sets the width of a canvas element on a document.
      */
    public double width;
    /**
      * Returns an object that provides methods and properties for drawing and manipulating images and graphics on a canvas element in a document. A context object includes information about colors, line widths, fonts, and other graphic parameters that can be drawn on a canvas.
      * @param contextId The identifier (ID) of the type of canvas to create. Internet Explorer 9 and Internet Explorer 10 support only a 2-D context using canvas.getContext("2d"); IE11 Preview also supports 3-D or WebGL context using canvas.getContext("experimental-webgl");
      */
    native public CanvasRenderingContext2D getContext(jsweet.util.StringTypes._2d contextId);
    native public WebGLRenderingContext getContext(jsweet.util.StringTypes.experimental_webgl contextId);
    native public jsweet.util.union.Union<CanvasRenderingContext2D,WebGLRenderingContext> getContext(java.lang.String contextId, java.lang.Object... args);
    /**
      * Returns a blob object encoded as a Portable Network Graphics (PNG) format from a canvas image or drawing.
      */
    native public Blob msToBlob();
    /**
      * Returns the content of the current canvas as an image that you can use as a source for another canvas or an HTML element.
      * @param type The standard MIME type for the image format to return. If you do not specify this parameter, the default value is a PNG format image.
      */
    native public java.lang.String toDataURL(java.lang.String type, java.lang.Object... args);
    public static HTMLCanvasElement prototype;
    public HTMLCanvasElement(){}
    /**
      * Returns the content of the current canvas as an image that you can use as a source for another canvas or an HTML element.
      * @param type The standard MIME type for the image format to return. If you do not specify this parameter, the default value is a PNG format image.
      */
    native public java.lang.String toDataURL();
}

