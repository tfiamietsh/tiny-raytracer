package rayTracer.tracer.geometry;

import rayTracer.tracer.RTObject;
import rayTracer.tracer.misc.Vertex;
import rayTracer.util.Vec2;
import rayTracer.util.Vec3;
import rayTracer.tracer.misc.Material;

import java.util.List;
import javafx.util.Pair;

import static rayTracer.util.Vec3.epsilon;
import static rayTracer.util.Vec3.infinity;

public class Triangle extends Shape implements RTObject {
    public Triangle() { }

    public Triangle(Vec3 vertex1, Vec3 vertex2, Vec3 vertex3, Material material) {
        this(vertex1, vertex2, vertex3, new Vec2(0., 1.), new Vec2(0., 0.),
                new Vec2(1., 1.), material);
    }

    public Triangle(Vec3 vertex1, Vec3 vertex2, Vec3 vertex3, Vec2 texCoord1,
                    Vec2 texCoord2, Vec2 texCoord3, Material material) {
        setVertices(vertex1, vertex2, vertex3);
        setTexCoords(texCoord1, texCoord2, texCoord3);
        setMaterial(material);
    }

    @Override
    public String getPattern(int paramCount) {
        return "tr %f,%f,%f %f,%f,%f %f,%f,%f" + Material.background.getPattern(paramCount - 9);
    }

    @Override
    public RTObject newInstance(List<Double> dblParams, List<String> strParams) {
        return new Triangle(
                new Vec3(dblParams.get(0), dblParams.get(1), dblParams.get(2)),
                new Vec3(dblParams.get(3), dblParams.get(4), dblParams.get(5)),
                new Vec3(dblParams.get(6), dblParams.get(7), dblParams.get(8)),
                Material.getByName(strParams.get(0))
        );
    }

    @Override
    public Pair<Double, Vertex> rayIntersection(Vec3 origin, Vec3 direction) {
        Vec3 v0v1 = vertex2.sub(vertex1);
        Vec3 v0v2 = vertex3.sub(vertex1);
        Vec3 pVec = direction.cross(v0v2);
        double det = v0v1.dot(pVec);

        if (Math.abs(det) < epsilon)
            return new Pair<>(infinity, null);

        double invDet = 1. / det;

        Vec3 tVec = origin.sub(vertex1);
        double u = tVec.dot(pVec) * invDet;
        if (u < 0. || u > 1.)
            return new Pair<>(infinity, null);

        Vec3 qVec = tVec.cross(v0v1);
        double v = direction.dot(qVec) * invDet;
        if (v < 0. || u + v > 1.)
            return new Pair<>(infinity, null);

        double distance = v0v2.dot(qVec) * invDet;
        Vec2 texCoord = texCoord1.mul(1 - u - v).add(texCoord2.mul(u)).add(texCoord3.mul(v));

        return new Pair<>(distance, new Vertex(null, normal, texCoord));
    }

    public void setTexCoords(Vec2 texCoord1, Vec2 texCoord2, Vec2 texCoord3) {
        this.texCoord1 = texCoord1; this.texCoord2 = texCoord2; this.texCoord3 = texCoord3;
    }

    public void setVertices(Vec3 vertex1, Vec3 vertex2, Vec3 vertex3) {
        setVertex1(vertex1); setVertex2(vertex2); setVertex3(vertex3);
        Vec3 vertex2SubVertex1 = vertex2.sub(vertex1);
        Vec3 vertex3SubVertex1 = vertex3.sub(vertex1);
        normal = vertex2SubVertex1.cross(vertex3SubVertex1).normalized();
    }

    public void setVertex1(Vec3 vertex1) { this.vertex1 = vertex1; }

    public Vec3 getVertex1() { return vertex1; }

    public void setVertex2(Vec3 vertex2) { this.vertex2 = vertex2; }

    public Vec3 getVertex2() { return vertex2; }

    public void setVertex3(Vec3 vertex3) { this.vertex3 = vertex3; }

    public Vec3 getVertex3() { return vertex3; }

    private Vec3 vertex1, vertex2, vertex3, normal;
    private Vec2 texCoord1, texCoord2, texCoord3;
}
