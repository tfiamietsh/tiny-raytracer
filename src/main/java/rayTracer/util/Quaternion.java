package rayTracer.util;

public class Quaternion {
    Quaternion(double w, double x, double y, double z) {
        setW(w); setX(x); setY(y); setZ(z);
    }

    public Quaternion(Vec3 rotationVector, double rotationAngle) {
        setW(Math.cos(rotationAngle / 2));
        rotationVector.normalized();
        double sinHalfRotationAngle = Math.sin(rotationAngle / 2);
        setX(rotationVector.getX() * sinHalfRotationAngle);
        setY(rotationVector.getY() * sinHalfRotationAngle);
        setZ(rotationVector.getZ() * sinHalfRotationAngle);
    }

    public Quaternion scale(double val) {
        return new Quaternion(w * val, x * val, y * val, z * val);
    }

    public double length() { return Math.sqrt(w * w + x * x + y * y + z * z); }

    public Quaternion normalized() {
        double len = length();
        if (len != 0)
            return new Quaternion(w / len, x / len, y / len, z / len);
        return this;
    }

    public Quaternion invert() {
        return new Quaternion(w, -x, -y, -z).normalized();
    }

    public Quaternion mul(Quaternion quat) {
        return new Quaternion(
                w * quat.w - x * quat.x - y * quat.y - z * quat.z,
                w * quat.x + x * quat.w + y * quat.z - z * quat.y,
                w * quat.y - x * quat.z + y * quat.w + z * quat.x,
                w * quat.z + x * quat.y - y * quat.x + z * quat.w);
    }

    public Quaternion mul(Vec3 vec) {
        return new Quaternion(
                -x * vec.getX() - y * vec.getY() - z * vec.getZ(),
                w * vec.getX() + y * vec.getZ() - z * vec.getY(),
                w * vec.getY() - x * vec.getZ() + z * vec.getX(),
                w * vec.getZ() + x * vec.getY() - y * vec.getX());
    }

    public Vec3 transform(Vec3 vec) {
        Quaternion quat = mul(vec);
        quat = quat.mul(invert());
        return new Vec3(quat.x, quat.y, quat.z).normalized();
    }

    private void setW(double w) { this.w = w; }

    public double getW() { return w; }

    private void setX(double x) { this.x = x; }

    public double getX() { return x; }

    private void setY(double y) { this.y = y; }

    public double getY() { return y; }

    private void setZ(double z) { this.z = z; }

    public double getZ() { return z; }

    private double w, x, y, z;
}
