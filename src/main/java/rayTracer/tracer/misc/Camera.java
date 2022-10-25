package rayTracer.tracer.misc;

import rayTracer.tracer.RTObject;
import rayTracer.util.Quaternion;
import rayTracer.util.Vec3;

import java.util.List;

public class Camera implements RTObject {
    public Camera() { }

    @Override
    public String getPattern(int paramCount) {
        return "c %f,%f,%f %f,%f,%f %f";
    }

    @Override
    public RTObject newInstance(List<Double> dblParams, List<String> strParams) {
        Camera camera = new Camera();

        camera.setPosition(new Vec3(dblParams.get(0), dblParams.get(1), dblParams.get(2)));
        camera.setLookAt(new Vec3(dblParams.get(3), dblParams.get(4), dblParams.get(5)));
        camera.setFOVy(dblParams.get(6).intValue());

        Vec3 cameraDirection = camera.lookAt.sub(camera.position).normalized();
        Vec3 axisOfRotation = initDirection.cross(cameraDirection).normalized();
        double angle = Math.acos(initDirection.dot(cameraDirection));
        camera.setQuaternion(new Quaternion(axisOfRotation, angle));

        return camera;
    }

    public Vec3 calculateRayDirection(int pixelX, int pixelY, int width, int height) {
        double x = (2 * (pixelX + .5) / width - 1) * tanHalfFOVy * width / height;
        double y = (2 * ((height - pixelY) + .5) / height - 1) * tanHalfFOVy;

        return quaternion.transform(new Vec3(x, y, initDirection.getZ()));
    }

    public void setFOVy(int fovyInDegrees) {
        tanHalfFOVy = Math.tan(fovyInDegrees * Math.PI / 360);
    }

    public Vec3 getPosition() {
        return position;
    }

    public void setPosition(Vec3 position) {
        this.position = position;
    }

    public void setLookAt(Vec3 lookAt) { this.lookAt = lookAt; }

    public void setQuaternion(Quaternion quaternion) { this.quaternion = quaternion; }

    private Vec3 position, lookAt;
    private double tanHalfFOVy;
    private static final Vec3 initDirection = new Vec3(0., 0., 1.);
    private Quaternion quaternion;
}
