package source.transpiler;

import static jsweet.dom.Globals.console;
import static jsweet.dom.Globals.document;
import static jsweet.dom.Globals.window;
import static jsweet.util.Globals.union;

import jsweet.dom.CanvasRenderingContext2D;
import jsweet.dom.Element;
import jsweet.dom.HTMLCanvasElement;
import jsweet.lang.Array;
import jsweet.lang.Math;
import jsweet.util.StringTypes;

public class CanvasDrawing {

	public static void main(String[] args) {
		window.onload = (e) -> {
			return new CanvasDrawing();
		};
	}
	
	private HTMLCanvasElement canvas;
	private CanvasRenderingContext2D ctx;
	private double angle = 0;

	public CanvasDrawing() {
		console.info("creating canvas drawing example");
		canvas = (HTMLCanvasElement) document.getElementById("canvas");

		String[] aTestString = { "a", "b" };
		Element body = document.querySelector("body");
		double size = Math.min(body.clientHeight, body.clientWidth);
		canvas.width = size - 20;
		canvas.height = size - 20;
		canvas.style.top = (body.clientHeight / 2 - size / 2 + 10) + "px";
		canvas.style.left = (body.clientWidth / 2 - size / 2 + 10) + "px";

		ctx = canvas.getContext(StringTypes._2d);
		draw();
	}

	private void draw() {
		int color = (int) (Math.pow(2, 8 * Math.floor(angle / Math.PI * 2) - 1));

		ctx.fillStyle = union("rgb(" + (color >> 16 & 0xFF) + "," + (color >> 8 & 0xFF) + "," + (color & 0xFF) + ")");
		console.log(ctx.fillStyle, color +  "opp"  + Math.floor(angle / Math.PI * 2));

		ctx.clearRect(0, 0, canvas.width, canvas.height);
		ctx.beginPath();
		ctx.moveTo(canvas.width / 2, canvas.height / 2);
		ctx.lineTo(canvas.width, canvas.height / 2);
		ctx.arc(canvas.width / 2, canvas.height / 2, canvas.width / 2, 0, angle);
		ctx.fill();
		if (angle < Math.PI * 2) {
			angle += 0.05;
			window.requestAnimationFrame((time) -> {
				this.draw();
			});
		}
	}

	void test() {
		Array<String> a = new Array<>();
		for(@SuppressWarnings("unused") String aTestVar : a) {
			console.log(a);
		}
	}
	
	void test1(String aTestParam1) {
		console.log(aTestParam1);
	}
	
	void test1(int aTestParam2) {
		console.log(aTestParam2);
	}
	
	
}
