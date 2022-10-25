package rayTracer.tracer.geometry;

import rayTracer.tracer.RTObject;
import rayTracer.tracer.misc.Vertex;
import rayTracer.util.Vec2;
import rayTracer.util.Vec3;
import rayTracer.tracer.misc.Material;

import java.util.List;
import javafx.util.Pair;

import static rayTracer.util.Vec3.infinity;

public class Cylinder extends Shape implements RTObject {
    public Cylinder() { }

    @Override
    public String getPattern(int paramCount) {
        return "cy %f,%f,%f %f,%f,%f %f" + Material.background.getPattern(paramCount - 7);
    }

    @Override
    public RTObject newInstance(List<Double> dblParams, List<String> strParams) {
        Cylinder cylinder = new Cylinder();

        cylinder.setRadius(dblParams.get(6));
        cylinder.setMaterial(Material.getByName(strParams.get(0)));
        cylinder.setCenter1(new Vec3(dblParams.get(0), dblParams.get(1), dblParams.get(2)));
        cylinder.setCenter2(new Vec3(dblParams.get(3), dblParams.get(4), dblParams.get(5)));
        return cylinder;
    }

    @Override
    public Pair<Double, Vertex> rayIntersection(Vec3 origin, Vec3 direction) {
        Vec3 pA = this.center1;
        Vec3 dP = origin.sub(pA);
        Vec3 vec1 = direction.sub(center2SubCenter1.mul(direction.dot(center2SubCenter1)));
        Vec3 vec2 = dP.sub(center2SubCenter1.mul(dP.dot(center2SubCenter1)));
        double A = vec1.dot(vec1);
        double B = 2 * vec1.dot(vec2);
        double C = vec2.dot(vec2) - this.radius * this.radius;
        double D = B * B - 4 * A * C;

        double distance = infinity;
        Vec3 normal = null, binormal = new Vec3(.9988776654, 0., .00112233445).normalized();
        Vec2 texCoord = null;

        if (D >= 0.) {
            double distance1 = (-B - Math.sqrt(D)) / (2 * A);
            if (0. <= distance1 && distance1 < distance) {
                Vec3 hitPoint1 = origin.add(direction.mul(distance1));

                if (center2SubCenter1.dot(hitPoint1.sub(center1)) > 0 &&
                        center2SubCenter1.dot(hitPoint1.sub(center2)) < 0) {
                    distance = distance1;

                    Vec3 hitPoint1SubCenter1 = hitPoint1.sub(center1);
                    double offset = hitPoint1SubCenter1.dot(center2SubCenter1);
                    Vec3 projHitPoint1OnCylinderAxis = center1.add(center2SubCenter1.mul(offset));

                    normal = hitPoint1.sub(projHitPoint1OnCylinderAxis).normalized();
                    Vec3 tangent = cap1Plane.getNormal().cross(binormal);

                    double u = Math.acos(tangent.dot(normal));
                    double v = 1. - offset / height;
                    texCoord = new Vec2(u, v);
                }
            }

            double distance2 = (-B + Math.sqrt(D)) / (2 * A);
            if (0. <= distance2 && distance2 < distance) {
                Vec3 hitPoint2 = origin.add(direction.mul(distance2));

                if (center2SubCenter1.dot(hitPoint2.sub(center1)) > 0 &&
                        center2SubCenter1.dot(hitPoint2.sub(center2)) < 0) {
                    distance = distance2;

                    Vec3 hitPoint2SubCenter1 = hitPoint2.sub(center1);
                    double offset = hitPoint2SubCenter1.dot(center2SubCenter1);
                    Vec3 projHitPoint2OnCylinderAxis = center1.add(center2SubCenter1.mul(offset));

                    normal = hitPoint2.sub(projHitPoint2OnCylinderAxis).normalized();
                    Vec3 tangent = cap1Plane.getNormal().cross(binormal);

                    double u = Math.acos(tangent.dot(normal));
                    double v = 1. - offset / height;
                    texCoord = new Vec2(u, v);
                }
            }
        }

        Pair<Double, Vertex> pair = this.cap1Plane.rayIntersection(origin, direction);
        double distance3 = pair.getKey();
        Vec3 hitPoint3 = origin.add(direction.mul(distance3));
        if (pair.getValue() != null) {
            Vec3 hitPoint3SubCenter1 = hitPoint3.sub(center1);

            if (hitPoint3SubCenter1.dot(hitPoint3SubCenter1) <= radius * radius
                    && 0. < distance3 && distance3 < distance) {
                distance = distance3;
                normal = pair.getValue().getNormal();
                texCoord = pair.getValue().getTexCoord();
            }
        }

        pair = this.cap2Plane.rayIntersection(origin, direction);
        double distance4 = pair.getKey();
        Vec3 hitPoint4 = origin.add(direction.mul(distance4));
        if (pair.getValue() != null) {
            Vec3 hitPoint4SubCenter2 = hitPoint4.sub(center2);

            if (hitPoint4SubCenter2.dot(hitPoint4SubCenter2) <= radius * radius
                    && 0. < distance4 && distance4 < distance) {
                distance = distance4;
                normal = pair.getValue().getNormal();
                texCoord = pair.getValue().getTexCoord();
            }
        }

        return new Pair<>(distance, new Vertex(null, normal, texCoord));
    }

    public void setCenter1(Vec3 center) { center1 = center; }

    public Vec3 getCenter1() { return center1; }

    public void setCenter2(Vec3 center) {
        center2 = center;

        center2SubCenter1 = center2.sub(center1);
        height = center2SubCenter1.length();
        center2SubCenter1 = center2SubCenter1.mul(1. / height);
        cap1Plane = new Plane(center1, center2SubCenter1.mul(-1.f), material);
        cap2Plane = new Plane(center2, center2SubCenter1, material);
    }

    public Vec3 getCenter2() { return center2; }

    public void setRadius(double radius) { this.radius = radius; }

    public double getRadius() { return radius; }

    private Vec3 center1, center2, center2SubCenter1;
    private double radius, height;
    private Plane cap1Plane, cap2Plane;
}
