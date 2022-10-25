package rayTracer.util;

public class Color {
    public Color() { }

    public Color(double r, double g, double b) { setRed(r); setGreen(g); setBlue(b); }

    public Color add(Color c) {
        return new Color(red + c.red, green + c.green, blue + c.blue);
    }

    public Color mul(double d) { return new Color(d * red, d * green, d * blue); }

    public Color mul(Color c) { return new Color(red * c.red, green * c.green, blue * c.blue); }

    public int toInt() {
        int red = (int) (255 * Math.min(Math.max(this.red, 0), 1));
        int green = (int) (255 * Math.min(Math.max(this.green, 0), 1));
        int blue = (int) (255 * Math.min(Math.max(this.blue, 0), 1));

        return (red << 16) | (green << 8) | blue;
    }

    public static Color valueOf(int rgb) {
        double red = (double) ((rgb & 0x00ff0000) >>> 16) / 255.;
        double green = (double) ((rgb & 0x0000ff00) >>> 8) / 255.;
        double blue = (double) (rgb & 0x000000ff) / 255.;

        return new Color(red, green, blue);
    }

    public void setRed(double red) { this.red = red; }

    public double getRed() { return red; }

    public void setGreen(double green) { this.green = green; }

    public double getGreen() { return green; }

    public void setBlue(double blue) { this.blue = blue; }

    public double getBlue() { return blue; }

    private double red, green, blue;
}
