package rayTracer.tracer.geometry;

import rayTracer.tracer.RTObject;
import rayTracer.tracer.misc.Material;
import rayTracer.tracer.misc.Vertex;
import rayTracer.util.Vec2;
import rayTracer.util.Vec3;

import java.util.List;
import javafx.util.Pair;

import static rayTracer.util.Vec3.infinity;

public class Plane extends Shape implements RTObject {
    public Plane() { }

    public Plane(Vec3 center, Vec3 normal, Material material) {
        setCenter(center);
        setNormal(normal);
        setMaterial(material);
    }

    @Override
    public String getPattern(int paramCount) {
        return "pl %f,%f,%f %f,%f,%f" + Material.background.getPattern(paramCount - 6);
    }

    @Override
    public RTObject newInstance(List<Double> dblParams, List<String> strParams) {
        return new Plane(
                new Vec3(dblParams.get(0), dblParams.get(1), dblParams.get(2)),
                new Vec3(dblParams.get(3), dblParams.get(4), dblParams.get(5)),
                Material.getByName(strParams.get(0))
        );
    }

    @Override
    public Pair<Double, Vertex> rayIntersection(Vec3 origin, Vec3 direction) {
        double distance = (center.dot(normal) - origin.dot(normal)) / direction.dot(normal);

        if (distance > 0.) {
            Vec3 binormal = new Vec3(.99887766554, 0., .00112233445).normalized();
            Vec3 tangent = binormal.cross(normal).normalized();
            binormal = tangent.cross(normal);

            Vec3 pointSubCenter = origin.add(direction.mul(distance)).sub(center);
            double projToBinormal = pointSubCenter.dot(binormal) / (10. * binormal.length());
            double projToTangent = pointSubCenter.dot(tangent) / (10. * tangent.length());

            Vec2 texCoord = new Vec2(projToBinormal % 1., projToTangent % 1.);

            return new Pair<>(distance, new Vertex(null, normal, texCoord));
        }
        return new Pair<>(infinity, null);
    }

    public Vec3 getCenter() {
        return center;
    }

    public void setCenter(Vec3 center) {
        this.center = center;
    }

    public Vec3 getNormal() {
        return normal;
    }

    public void setNormal(Vec3 normal) {
        this.normal = normal.normalized();
    }

    private Vec3 center, normal;
}
