package rayTracer.core;

import rayTracer.tracer.misc.Camera;
import rayTracer.tracer.light.Light;
import rayTracer.tracer.geometry.Shape;

import java.util.ArrayList;

public class Scene {
    public Scene() {
        lights = new ArrayList<>();
        shapes = new ArrayList<>();
    }

    public void setCamera(Camera camera) { this.camera = camera; }

    public Camera getCamera() { return camera; }

    public void addLight(Light light) { lights.add(light); }

    public Light getLight(int index) { return lights.get(index); }

    public ArrayList<Light> getLights() { return lights; }

    public void addShape(Shape shape) { shapes.add(shape); }

    public Shape getShape(int index) { return shapes.get(index); }

    public ArrayList<Shape> getShapes() { return shapes; }

    private Camera camera;
    private final ArrayList<Light> lights;
    private final ArrayList<Shape> shapes;
}
