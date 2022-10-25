package rayTracer.tracer.geometry;

import rayTracer.tracer.RTObject;
import rayTracer.tracer.misc.Vertex;
import rayTracer.util.Vec2;
import rayTracer.util.Vec3;
import rayTracer.tracer.misc.Material;

import java.util.List;
import javafx.util.Pair;

public class Square extends Shape implements RTObject {
    public Square() { }

    public Square(Vec3 center, Vec3 normal, Vec3 binormal, double side, Material material) {
        this(center, normal, binormal, side, new Vec2(0., 1.), new Vec2(0., 0.),
                new Vec2(1., 0.), new Vec2(1., 1.), material);
    }

    public Square(Vec3 center, Vec3 normal, Vec3 binormal, double side,
                  Vec2 texCoord1, Vec2 texCoord2, Vec2 texCoord3, Vec2 texCoord4, Material material) {
        setCenter(center);
        setBinormal(binormal);
        setNormal(normal);
        setMaterial(material);
        setSide(side);
        setTexCoords(texCoord1, texCoord2, texCoord3, texCoord4);
        calculateTriangles();
    }

    @Override
    public String getPattern(int paramCount) {
        return "sq %f,%f,%f %f,%f,%f %f,%f,%f %f" + Material.background.getPattern(paramCount - 10);
    }

    @Override
    public RTObject newInstance(List<Double> dblParams, List<String> strParams) {
        return new Square(
                new Vec3(dblParams.get(0), dblParams.get(1), dblParams.get(2)),
                new Vec3(dblParams.get(3), dblParams.get(4), dblParams.get(5)),
                new Vec3(dblParams.get(6), dblParams.get(7), dblParams.get(8)),
                dblParams.get(9), Material.getByName(strParams.get(0))
        );
    }

    @Override
    public Pair<Double, Vertex> rayIntersection(Vec3 origin, Vec3 direction) {
        Pair<Double, Vertex> lbtPair = leftBottomTriangle.rayIntersection(origin, direction);
        Pair<Double, Vertex> rttPair = rightTopTriangle.rayIntersection(origin, direction);

        if (0. <= lbtPair.getKey() && lbtPair.getKey() < rttPair.getKey())
            return lbtPair;
        return rttPair;
    }

    private void setTexCoords(Vec2 texCoord1, Vec2 texCoord2, Vec2 texCoord3, Vec2 texCoord4) {
        this.texCoord1 = texCoord1; this.texCoord2 = texCoord2;
        this.texCoord3 = texCoord3; this.texCoord4 = texCoord4;
    }

    private void calculateTriangles() {
        Vec3 tangent = binormal.cross(this.normal).normalized();
        Vec3 leftTopVertex = this.center.add(binormal.mul(-side / 2).add(tangent.mul(side / 2)));
        Vec3 rightTopVertex = this.center.add(binormal.mul(side / 2).add(tangent.mul(side / 2)));
        Vec3 leftBottomVertex = this.center.add(binormal.mul(-side / 2).add(tangent.mul(-side / 2)));
        Vec3 rightBottomVertex = this.center.add(binormal.mul(side / 2).add(tangent.mul(-side / 2)));

        leftBottomTriangle = new Triangle(leftTopVertex, rightBottomVertex, leftBottomVertex,
                texCoord2, texCoord4, texCoord1, material);
        rightTopTriangle = new Triangle(leftTopVertex, rightTopVertex, rightBottomVertex,
                texCoord2, texCoord3, texCoord4, material);
    }

    public void setCenter(Vec3 center) {
        this.center = center;
    }

    public Vec3 getCenter() {
        return center;
    }

    public void setBinormal(Vec3 binormal) { this.binormal = binormal; }

    public Vec3 getBinormal() { return binormal; }

    public void setNormal(Vec3 normal) {
        this.normal = normal.normalized();
    }

    public Vec3 getNormal() { return normal; }

    public void setSide(double side) { this.side = side; }

    public double getSide() { return side; }

    private Vec3 center, binormal, normal;
    private double side;
    private Vec2 texCoord1, texCoord2, texCoord3, texCoord4;
    private Triangle leftBottomTriangle, rightTopTriangle;
}
