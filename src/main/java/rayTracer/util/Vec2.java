package rayTracer.util;

public class Vec2 {
    public Vec2() { }

    public Vec2(double u, double v) { setU(u); setV(v); }

    public static Vec2 reflect(Vec2 I, Vec2 N) {
        return I.sub(N.mul(2. * I.dot(N)));
    }

    public static Vec2 refract(Vec2 I, Vec2 N, double etaT, double etaI) {  //  snell's law
        double cosI = - Math.max(-1., Math.min(1., I.dot(N)));
        if (cosI < 0)
            return refract(I, N.mul(-1.), etaI, etaT); // if the ray comes from the inside the object, swap the air and the media
        double eta = etaI / etaT;
        double k = 1 - eta * eta * (1 - cosI * cosI);
        // k < 0 = total reflection, no ray to refract
        return k < 0 ? new Vec2(1., 0.) : I.mul(eta).add(N.mul(eta * cosI - Math.sqrt(k)));
    }

    public Vec2 add(Vec2 other) { return new Vec2(this.u + other.u, this.v + other.v); }

    public Vec2 mul(double a) {
        return new Vec2(this.u * a, this.v * a);
    }

    public Vec2 sub(Vec2 other) {
        return add(other.mul(-1.));
    }

    public double dot(Vec2 other) {
        return this.u * other.u + this.v * other.v;
    }

    public Vec2 normalized() {
        double len = length();
        if (len != 0.)
            return new Vec2(u / len, v / len);
        return this;
    }

    public double length() { return Math.sqrt(this.dot(this)); }

    public double getU() { return u; }

    void setU(double u) { this.u = u; }

    public double getV() { return v; }

    void setV(double v) { this.v = v; }

    private double u, v;
}
