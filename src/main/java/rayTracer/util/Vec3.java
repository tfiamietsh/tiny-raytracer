package rayTracer.util;

public class Vec3 {
    public Vec3() { }

    public Vec3(double x, double y, double z) { setX(x); setY(y); setZ(z); }

    public static Vec3 reflect(Vec3 I, Vec3 N) {
        return I.sub(N.mul(2. * I.dot(N)));
    }

    public static Vec3 refract(Vec3 I, Vec3 N, double etaT, double etaI) {  //  snell's law
        double cosI = -Math.max(-1., Math.min(1., I.dot(N)));
        if (cosI < 0)
            return refract(I, N.mul(-1.), etaI, etaT);
        double eta = etaI / etaT;
        double k = 1 - eta * eta * (1 - cosI * cosI);
        //  k < 0 = total reflection, no ray to refract
        return k < 0 ? new Vec3(1., 0., 0.) : I.mul(eta).add(N.mul(eta * cosI - Math.sqrt(k)));
    }

    public Vec3 add(Vec3 other) { return new Vec3(this.x + other.x, this.y + other.y, this.z + other.z); }

    public Vec3 mul(double a) {
        return new Vec3(this.x * a, this.y * a, this.z * a);
    }

    public Vec3 sub(Vec3 other) {
        return add(other.mul(-1.));
    }

    public double dot(Vec3 other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public Vec3 cross(Vec3 other) {
        double tmpX = y * other.z - z * other.y;
        double tmpY = z * other.x - x * other.z;
        double tmpZ = x * other.y - y * other.x;
        return new Vec3(tmpX, tmpY, tmpZ);
    }

    public Vec3 normalized() {
        double len = length();
        if (len != 0.)
            return new Vec3(x / len, y / len, z / len);
        return this;
    }

    public double length() { return Math.sqrt(this.dot(this)); }

    public double getX() { return x; }

    public void setX(double x) { this.x = x; }

    public double getY() { return y; }

    public void setY(double y) { this.y = y; }

    public double getZ() { return z; }

    public void setZ(double z) { this.z = z; }

    private double x, y, z;
    public static final double infinity = 10000., epsilon = 1e-4;
}