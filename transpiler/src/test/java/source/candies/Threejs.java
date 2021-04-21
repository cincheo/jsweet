//package source.candies;
//
//import static def.dom.Globals.document;
//import static def.dom.Globals.requestAnimationFrame;
//import static def.dom.Globals.window;
//import static jsweet.util.Lang.union;
//
//import def.threejs.THREE.BoxGeometry;
//import def.threejs.THREE.Color;
//import def.threejs.THREE.DirectionalLight;
//import def.threejs.THREE.Geometry;
//import def.threejs.THREE.Intersection;
//import def.threejs.THREE.Light;
//import def.threejs.THREE.Mesh;
//import def.threejs.THREE.MeshLambertMaterial;
//import def.threejs.THREE.MeshLambertMaterialParameters;
//import def.threejs.THREE.PerspectiveCamera;
//import def.threejs.THREE.Raycaster;
//import def.threejs.THREE.Raycaster.Coords;
//import def.threejs.THREE.Scene;
//import def.threejs.THREE.Vector2;
//import def.threejs.THREE.WebGLRenderer;
//import def.dom.Event;
//import def.dom.HTMLElement;
//import def.dom.MouseEvent;
//import def.js.Math;
//import jsweet.util.StringTypes;
//
//// WARNING: this example compiles with JSweet in strict mode
//// jsweet-core-strict must be place first (before the JDK) in the classpath
//
//public class Threejs {
//
//	static HTMLElement container;
//	static PerspectiveCamera camera;
//	static Scene scene;
//	static Raycaster raycaster;
//	static WebGLRenderer renderer;
//
//	static Vector2 mouse = new Vector2();
//	static Mesh INTERSECTED;
//
//	static double radius = 100;
//	static double theta = 0;
//
//	public static void main(String[] args) {
//		init();
//		animate(0);
//	}
//
//	public static void init() {
//
//		container = document.createElement("div");
//		document.body.appendChild(container);
//
//		HTMLElement info = document.createElement("div");
//		info.style.position = "absolute";
//		info.style.top = "10px";
//		info.style.width = "100%";
//		info.style.textAlign = "center";
//		info.innerHTML = "<a href=\"http://threejs.org\" target=\"_blank\">three.js</a> webgl - interactive cubes";
//		container.appendChild(info);
//
//		camera = new PerspectiveCamera(70, window.innerWidth / window.innerHeight, 1, 10000);
//
//		scene = new Scene();
//
//		Light light = new DirectionalLight(0xffffff, 1);
//		light.position.set(1, 1, 1).normalize();
//		scene.add(light);
//
//		Geometry geometry = new BoxGeometry(20, 20, 20);
//
//		for (int i = 0; i < 2000; i++) {
//
//			Mesh object = new Mesh(geometry, new MeshLambertMaterial(new MeshLambertMaterialParameters() {
//				{
//					color = union(Math.random() * 0xffffff);
//				}
//			}));
//
//			object.position.x = Math.random() * 800 - 400;
//			object.position.y = Math.random() * 800 - 400;
//			object.position.z = Math.random() * 800 - 400;
//
//			object.rotation.x = Math.random() * 2 * Math.PI;
//			object.rotation.y = Math.random() * 2 * Math.PI;
//			object.rotation.z = Math.random() * 2 * Math.PI;
//
//			object.scale.x = Math.random() + 0.5;
//			object.scale.y = Math.random() + 0.5;
//			object.scale.z = Math.random() + 0.5;
//
//			scene.add(object);
//
//		}
//
//		raycaster = new Raycaster();
//
//		renderer = new WebGLRenderer();
//		renderer.setClearColor(0xf0f0f0);
//		renderer.setPixelRatio(window.devicePixelRatio);
//		renderer.setSize(window.innerWidth, window.innerHeight);
//		renderer.sortObjects = false;
//		container.appendChild(renderer.domElement);
//
//		document.addEventListener(StringTypes.mousemove, Threejs::onDocumentMouseMove, false);
//
//		//
//
//		window.addEventListener("resize", Threejs::onWindowResize, false);
//
//	}
//
//	public static Object onWindowResize(Event event) {
//
//		camera.aspect = window.innerWidth / window.innerHeight;
//		camera.updateProjectionMatrix();
//
//		renderer.setSize(window.innerWidth, window.innerHeight);
//
//		return null;
//	}
//
//	public static Object onDocumentMouseMove(MouseEvent event) {
//
//		event.preventDefault();
//
//		mouse.x = (event.clientX / window.innerWidth) * 2 - 1;
//		mouse.y = -(event.clientY / window.innerHeight) * 2 + 1;
//
//		return null;
//	}
//
//	//
//
//	public static void animate(double time) {
//
//		requestAnimationFrame(Threejs::animate);
//
//		render();
//
//	}
//
//	public static void render() {
//
//		theta += 0.1;
//
//		camera.position.x = radius * Math.sin(def.threejs.THREE.Math.degToRad(theta));
//		camera.position.y = radius * Math.sin(def.threejs.THREE.Math.degToRad(theta));
//		camera.position.z = radius * Math.cos(def.threejs.THREE.Math.degToRad(theta));
//		camera.lookAt(scene.position);
//
//		camera.updateMatrixWorld(true);
//
//		// find intersections
//
//		raycaster.setFromCamera(new Coords() {
//			{
//				x = mouse.x;
//				y = mouse.y;
//			}
//		}, camera);
//
//		Intersection[] intersects = raycaster.intersectObjects(scene.children);
//
//		if (intersects.length > 0) {
//
//			if (INTERSECTED != intersects[0].object) {
//
//				// This part is horrible partially because there is a mistake in the definition file (emissive is a color)
//				if (INTERSECTED != null) {
//					((Color) (Object) ((MeshLambertMaterial) INTERSECTED.material).emissive)
//							.setHex((double) INTERSECTED.$get("currentHex"));
//				}
//				INTERSECTED = (Mesh) intersects[0].object;
//				INTERSECTED.$set("currentHex",
//						((Color) (Object) ((MeshLambertMaterial) INTERSECTED.material).emissive).getHex());
//				((Color) (Object) ((MeshLambertMaterial) INTERSECTED.material).emissive).setHex(0xff0000);
//
//			}
//
//		} else {
//
//			if (INTERSECTED != null) {
//				((Color) (Object) ((MeshLambertMaterial) INTERSECTED.material).emissive)
//						.setHex((double) INTERSECTED.$get("currentHex"));
//			}
//			INTERSECTED = null;
//
//		}
//
//		renderer.render(scene, camera);
//
//	}
//
//}
