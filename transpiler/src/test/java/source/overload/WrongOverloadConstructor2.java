
package source.overload;

import static jsweet.util.Lang.$export;

import def.js.Array;

public class WrongOverloadConstructor2 {

	public static Array<String> trace = new Array<>();

	public static void main(String[] args) {
		Sprite s = new Sprite(1,2,3,4);
		assert s._x == 1;
		assert s._y == 2;
		assert s._w == 3;
		assert s._h == 4;
		
		$export("trace", trace.join(":"));
	}

}

class PImage {
	String url;

	public PImage(String url) {
		super();
		this.url = url;
	}
	
}

class PVector {
	float x;
	float y;
	float z;
	
	public PVector(float x, float y, float z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}
}

class Sprite {
	  PImage _img;
	  float _w;
	  float _h;
	  float _x;
	  float _y;
	  PVector _rotVector;
	  
	  Sprite(String url, float x, float y, float w, float h) {
	    _img = new PImage(url);
	    _x = x;
	    _y = y;
	    _w = w;
	    _h = h;
	    _rotVector = new PVector(1, 0, 0);
	  }

	  Sprite(float x, float y, float w, float h) {
	    _img = null;
	    _x = x;
	    _y = y;
	    _w = w;
	    _h = h;
	    _rotVector = new PVector(1, 0, 0);
	  }
	  
	  Sprite(Sprite s) {
	    _img = s._img;
	    _x = s._x;
	    _y = s._y;
	    _w = s._w;
	    _h = s._h;
	    _rotVector = new PVector(s._rotVector.x, s._rotVector.y, 0);
	  }
}