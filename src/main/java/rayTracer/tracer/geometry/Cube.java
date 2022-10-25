package rayTracer.tracer.geometry;

import rayTracer.tracer.misc.Material;
import rayTracer.tracer.RTObject;
import rayTracer.tracer.misc.Vertex;
import rayTracer.util.Vec2;
import rayTracer.util.Vec3;

import java.util.List;
import javafx.util.Pair;

import static rayTracer.util.Vec3.infinity;

public class Cube extends Shape implements RTObject {
    public Cube() { }

    public Cube(Vec3 center, Vec3 binormal, Vec3 tangent, double side, Material material) {
        setCenter(center);
        setSide(side);
        setMaterial(material);
        setBinormal(binormal);
        setTangent(tangent);
        calculateFaces();
    }

    @Override
    public String getPattern(int paramCount) {
        return "cu %f,%f,%f %f,%f,%f %f,%f,%f %f" + Material.background.getPattern(paramCount - 10);
    }

    @Override
    public RTObject newInstance(List<Double> dblParams, List<String> strParams) {
        return new Cube(
                new Vec3(dblParams.get(0), dblParams.get(1), dblParams.get(2)),
                new Vec3(dblParams.get(3), dblParams.get(4), dblParams.get(5)),
                new Vec3(dblParams.get(6), dblParams.get(7), dblParams.get(8)),
                dblParams.get(9), Material.getByName(strParams.get(0)));
    }

    @Override
    public Pair<Double, Vertex> rayIntersection(Vec3 origin, Vec3 direction) {
        Pair<Double, Vertex> minDistancePair = new Pair<>(infinity, null);

        for (Square face : faces) {
            Pair<Double, Vertex> pair = face.rayIntersection(origin, direction);

            if (0. <= pair.getKey() && pair.getKey() < minDistancePair.getKey())
                minDistancePair = pair;
        }
        return minDistancePair;
    }

    protected void calculateFaces() {
        Vec2 texCoord1 = new Vec2(0., 1.), texCoord2 = new Vec2(0., 0.),
                texCoord3 = new Vec2(1., 0.), texCoord4 = new Vec2(1., 1.);
        faces = new Square[6];

        //  top & bottom, right & left, front & back
        faces[0] = new Square(center.add(tangent.mul(side / 2)), tangent, binormal,
                side, texCoord1, texCoord2, texCoord3, texCoord4, material);
        faces[1] = new Square(center.add(tangent.mul(-side / 2)), tangent.mul(-1.),
                binormal, side, texCoord1, texCoord2, texCoord3, texCoord4, material);

        Vec3 normal = tangent.cross(binormal);
        faces[2] = new Square(center.add(binormal.mul(side / 2)), binormal,
                normal, side, texCoord1, texCoord2, texCoord3, texCoord4, material);
        faces[3] = new Square(center.add(binormal.mul(-side / 2)), binormal.mul(-1.),
                normal.mul(-1.), side, texCoord1, texCoord2, texCoord3, texCoord4, material);

        faces[4] = new Square(center.add(normal.mul(side / 2)), normal,
                binormal.mul(-1.), side, texCoord1, texCoord2, texCoord3, texCoord4, material);
        faces[5] = new Square(center.add(normal.mul(-side / 2)), normal.mul(-1.),
                binormal, side, texCoord1, texCoord2, texCoord3, texCoord4, material);
    }

    public void setCenter(Vec3 center) { this.center = center; }

    public Vec3 getCenter() { return center; }

    public void setBinormal(Vec3 binormal) { this.binormal = binormal.normalized(); }

    public Vec3 getBinormal() { return binormal; }

    public void setTangent(Vec3 tangent) { this.tangent = tangent.normalized(); }

    public Vec3 getTangent() { return tangent; }

    public void setSide(double side) { this.side = side; }

    public double getSide() { return side; }

    protected Vec3 center, binormal, tangent;
    protected double side;
    protected Square[] faces;
}
