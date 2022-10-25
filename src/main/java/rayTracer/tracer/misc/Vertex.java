package rayTracer.tracer.misc;

import rayTracer.util.Vec2;
import rayTracer.util.Vec3;

public class Vertex {
    public Vertex() { }

    public Vertex(Vec3 position, Vec3 normal, Vec2 texCoord) {
        setPosition(position);
        setNormal(normal);
        setTexCoord(texCoord);
    }

    public void setPosition(Vec3 position) { this.position = position; }

    public Vec3 getPosition() { return position; }

    public void setNormal(Vec3 normal) { this.normal = normal; }

    public Vec3 getNormal() { return normal; }

    public void setTexCoord(Vec2 texCoord) { this.texCoord = texCoord; }

    public Vec2 getTexCoord() { return texCoord; }

    private Vec3 position, normal;
    private Vec2 texCoord;
}