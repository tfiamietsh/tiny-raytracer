package rayTracer.tracer.light;

import rayTracer.tracer.misc.Material;
import rayTracer.tracer.misc.Vertex;
import rayTracer.util.Color;
import rayTracer.util.Vec3;

public abstract class Light {
    public abstract Color calculateColorAtPoint(Vertex vertex, Vec3 viewDirection, Material material);

    protected void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public double getIntensity() {
        return intensity;
    }

    protected void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    protected void setPosition(Vec3 position) {
        this.position = position;
    }

    public Vec3 getPosition() {
        return position;
    }

    protected double intensity;
    protected Color color;
    protected Vec3 position;
}
