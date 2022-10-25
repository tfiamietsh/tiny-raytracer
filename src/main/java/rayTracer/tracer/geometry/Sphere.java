package rayTracer.tracer.geometry;

import rayTracer.tracer.misc.Vertex;
import rayTracer.util.Vec2;
import rayTracer.util.Vec3;
import rayTracer.tracer.misc.Material;
import rayTracer.tracer.RTObject;

import java.util.List;
import javafx.util.Pair;

import static rayTracer.util.Vec3.infinity;

public class Sphere extends Shape implements RTObject {
    public Sphere() { }

    public Sphere(Vec3 center, double radius, Material material) {
        setCenter(center);
        setRadius(radius);
        setMaterial(material);
    }

    @Override
    public String getPattern(int paramCount) {
        return "sp %f,%f,%f %f" + Material.background.getPattern(paramCount - 4);
    }

    @Override
    public RTObject newInstance(List<Double> dblParams, List<String> strParams) {
        return new Sphere(
                new Vec3(dblParams.get(0), dblParams.get(1), dblParams.get(2)),
                dblParams.get(3), Material.getByName(strParams.get(0))
        );
    }

    @Override
    public Pair<Double, Vertex> rayIntersection(Vec3 origin, Vec3 direction) {
        Vec3 OC = origin.sub(this.center);
        double A = direction.dot(direction);
        double B = 2 * OC.dot(direction);
        double C = OC.dot(OC) - this.radius * this.radius;
        double D = B * B - 4 * A * C;

        if (D >= 0) {
            double distance1 = (-B - Math.sqrt(D)) / (2 * A);
            double distance2 = (-B + Math.sqrt(D)) / (2 * A);
            double distance = Math.min(
                    distance1 <= 0 ? infinity : distance1,
                    distance2 <= 0 ? infinity : distance2);

            Vec3 normal = origin.add(direction.mul(distance)).sub(center).normalized();

            double u = .5 + Math.atan2(normal.getX(), normal.getZ()) / (2. * Math.PI);
            double v = .5 - Math.asin(normal.getY()) / Math.PI;

            return new Pair<>(distance, new Vertex(null, normal, new Vec2(u, v)));
        }
        return new Pair<>(infinity, null);
    }

    public Vec3 getCenter() { return center; }

    public void setCenter(Vec3 center) { this.center = center; }

    public double getRadius() { return radius; }

    public void setRadius(double radius) { this.radius = radius; }

    private Vec3 center;
    private double radius;
}
