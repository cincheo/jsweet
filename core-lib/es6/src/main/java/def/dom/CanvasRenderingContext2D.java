package def.dom;

import jsweet.util.union.Union4;

public class CanvasRenderingContext2D extends def.js.Object {
	public HTMLCanvasElement canvas;
	public Union4<String, CanvasGradient, CanvasPattern, java.lang.Object> fillStyle;
	public java.lang.String font;
	public double globalAlpha;
	public java.lang.String globalCompositeOperation;
	public java.lang.String lineCap;
	public double lineDashOffset;
	public java.lang.String lineJoin;
	public double lineWidth;
	public double miterLimit;
	public java.lang.String msFillRule;
	public java.lang.Boolean msImageSmoothingEnabled;
	public double shadowBlur;
	public java.lang.String shadowColor;
	public double shadowOffsetX;
	public double shadowOffsetY;
	public Union4<String, CanvasGradient, CanvasPattern, java.lang.Object> strokeStyle;
	public java.lang.String textAlign;
	public java.lang.String textBaseline;

	native public void arc(double x, double y, double radius, double startAngle, double endAngle,
			java.lang.Boolean anticlockwise);

	native public void arcTo(double x1, double y1, double x2, double y2, double radius);

	native public void beginPath();

	native public void bezierCurveTo(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y);

	native public void clearRect(double x, double y, double w, double h);

	native public void clip(java.lang.String fillRule);

	native public void closePath();

	native public ImageData createImageData(double imageDataOrSw, double sh);

	native public ImageData createImageData(ImageData imageDataOrSw, double sh);

	native public CanvasGradient createLinearGradient(double x0, double y0, double x1, double y1);

	native public CanvasPattern createPattern(HTMLImageElement image, java.lang.String repetition);

	native public CanvasPattern createPattern(HTMLCanvasElement image, java.lang.String repetition);

	native public CanvasPattern createPattern(HTMLVideoElement image, java.lang.String repetition);

	native public CanvasGradient createRadialGradient(double x0, double y0, double r0, double x1, double y1, double r1);

	native public void drawImage(HTMLImageElement image, double offsetX, double offsetY, double width, double height,
			double canvasOffsetX, double canvasOffsetY, double canvasImageWidth, double canvasImageHeight);

	native public void drawImage(HTMLCanvasElement image, double offsetX, double offsetY, double width, double height,
			double canvasOffsetX, double canvasOffsetY, double canvasImageWidth, double canvasImageHeight);

	native public void drawImage(HTMLVideoElement image, double offsetX, double offsetY, double width, double height,
			double canvasOffsetX, double canvasOffsetY, double canvasImageWidth, double canvasImageHeight);

	native public void fill(java.lang.String fillRule);

	native public void fillRect(double x, double y, double w, double h);

	native public void fillText(java.lang.String text, double x, double y, double maxWidth);

	native public ImageData getImageData(double sx, double sy, double sw, double sh);

	native public double[] getLineDash();

	native public java.lang.Boolean isPointInPath(double x, double y, java.lang.String fillRule);

	native public void lineTo(double x, double y);

	native public TextMetrics measureText(java.lang.String text);

	native public void moveTo(double x, double y);

	native public void putImageData(ImageData imagedata, double dx, double dy, double dirtyX, double dirtyY,
			double dirtyWidth, double dirtyHeight);

	native public void quadraticCurveTo(double cpx, double cpy, double x, double y);

	native public void rect(double x, double y, double w, double h);

	native public void restore();

	native public void rotate(double angle);

	native public void save();

	native public void scale(double x, double y);

	native public void setLineDash(double[] segments);

	native public void setTransform(double m11, double m12, double m21, double m22, double dx, double dy);

	native public void stroke();

	native public void strokeRect(double x, double y, double w, double h);

	native public void strokeText(java.lang.String text, double x, double y, double maxWidth);

	native public void transform(double m11, double m12, double m21, double m22, double dx, double dy);

	native public void translate(double x, double y);

	public static CanvasRenderingContext2D prototype;

	public CanvasRenderingContext2D() {
	}

	native public void arc(double x, double y, double radius, double startAngle, double endAngle);

	native public void clip();

	native public ImageData createImageData(double imageDataOrSw);

	native public ImageData createImageData(ImageData imageDataOrSw);

	native public void drawImage(HTMLImageElement image, double offsetX, double offsetY, double width, double height,
			double canvasOffsetX, double canvasOffsetY, double canvasImageWidth);

	native public void drawImage(HTMLImageElement image, double offsetX, double offsetY, double width, double height,
			double canvasOffsetX, double canvasOffsetY);

	native public void drawImage(HTMLImageElement image, double offsetX, double offsetY, double width, double height,
			double canvasOffsetX);

	native public void drawImage(HTMLImageElement image, double offsetX, double offsetY, double width, double height);

	native public void drawImage(HTMLImageElement image, double offsetX, double offsetY, double width);

	native public void drawImage(HTMLImageElement image, double offsetX, double offsetY);

	native public void drawImage(HTMLCanvasElement image, double offsetX, double offsetY, double width, double height,
			double canvasOffsetX, double canvasOffsetY, double canvasImageWidth);

	native public void drawImage(HTMLCanvasElement image, double offsetX, double offsetY, double width, double height,
			double canvasOffsetX, double canvasOffsetY);

	native public void drawImage(HTMLCanvasElement image, double offsetX, double offsetY, double width, double height,
			double canvasOffsetX);

	native public void drawImage(HTMLCanvasElement image, double offsetX, double offsetY, double width, double height);

	native public void drawImage(HTMLCanvasElement image, double offsetX, double offsetY, double width);

	native public void drawImage(HTMLCanvasElement image, double offsetX, double offsetY);

	native public void drawImage(HTMLVideoElement image, double offsetX, double offsetY, double width, double height,
			double canvasOffsetX, double canvasOffsetY, double canvasImageWidth);

	native public void drawImage(HTMLVideoElement image, double offsetX, double offsetY, double width, double height,
			double canvasOffsetX, double canvasOffsetY);

	native public void drawImage(HTMLVideoElement image, double offsetX, double offsetY, double width, double height,
			double canvasOffsetX);

	native public void drawImage(HTMLVideoElement image, double offsetX, double offsetY, double width, double height);

	native public void drawImage(HTMLVideoElement image, double offsetX, double offsetY, double width);

	native public void drawImage(HTMLVideoElement image, double offsetX, double offsetY);

	native public void fill();

	native public void fillText(java.lang.String text, double x, double y);

	native public java.lang.Boolean isPointInPath(double x, double y);

	native public void putImageData(ImageData imagedata, double dx, double dy, double dirtyX, double dirtyY,
			double dirtyWidth);

	native public void putImageData(ImageData imagedata, double dx, double dy, double dirtyX, double dirtyY);

	native public void putImageData(ImageData imagedata, double dx, double dy, double dirtyX);

	native public void putImageData(ImageData imagedata, double dx, double dy);

	native public void strokeText(java.lang.String text, double x, double y);
}
