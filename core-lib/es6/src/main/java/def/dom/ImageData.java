package def.dom;

import def.js.Uint8Array;
import def.js.Uint8ClampedArray;

public class ImageData extends def.js.Object {
	public int height;
	public int width;

	public static ImageData prototype;

	public Uint8Array data;

	public ImageData() {
	}

	public ImageData(Uint8ClampedArray array, int width, int height) {
	}

	public ImageData(int width, int height) {
	}
}
