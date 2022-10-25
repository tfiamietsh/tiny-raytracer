package rayTracer.tracer.misc;

import rayTracer.tracer.RTObject;
import rayTracer.util.Color;
import rayTracer.util.Vec2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Material implements RTObject {
    public Material() { }

    public Material(Color diffuseColor, double specularExponent, double specularity, double reflectivity,
                    double transparency, double refractiveIndex, double emissivity) {
        setDiffuseColor(diffuseColor);
        setSpecularExponent(specularExponent);
        setSpecularity(specularity);
        setReflectivity(reflectivity);
        setTransparency(transparency);
        setRefractiveIndex(refractiveIndex);
        setEmissivity(emissivity);
    }

    public Material(Texture diffuseTexture, double specularExponent, double specularity, double reflectivity,
                    double transparency, double refractiveIndex, double emissivity) {
        setDiffuseTexture(diffuseTexture);
        setSpecularExponent(specularExponent);
        setSpecularity(specularity);
        setReflectivity(reflectivity);
        setTransparency(transparency);
        setRefractiveIndex(refractiveIndex);
        setEmissivity(emissivity);
    }

    @Override
    public String getPattern(int paramCount) {
        return switch (paramCount) {
            case 1 -> " %s";
            case 8 -> "mt %s %s %f %f %f %f %f %f";         //  pattern with diffuse texture
            case 10 -> "mt %s %f,%f,%f %f %f %f %f %f %f";  //  default pattern
            default -> null;
        };
    }

    @Override
    public RTObject newInstance(List<Double> dblParams, List<String> strParams) {
        int paramCount = dblParams.size() + strParams.size();

        if (paramCount == 8)
            materials.put(strParams.get(0), new Material(new Texture(strParams.get(1)), dblParams.get(0), dblParams.get(1),
                    dblParams.get(2), dblParams.get(3), dblParams.get(4), dblParams.get(5)));
        else if (paramCount == 10)
            materials.put(strParams.get(0), new Material(new Color(dblParams.get(0), dblParams.get(1), dblParams.get(2)),
                    dblParams.get(3), dblParams.get(4), dblParams.get(5), dblParams.get(6), dblParams.get(7), dblParams.get(8)));
        return null;
    }

    public static Material getByName(String name) { return materials.getOrDefault(name, null); }

    public Color getDiffuseColor(Vec2 texCoord) {
        if (diffuseTexture == null)
            return diffuseColor;
        return diffuseTexture.getColor(texCoord);
    }

    public void setDiffuseColor(Color diffuseColor) { this.diffuseColor = diffuseColor; }

    public void setDiffuseTexture(Texture diffuseTexture) {
        if (diffuseTexture.getImage() != null)
            this.diffuseTexture = diffuseTexture;
        diffuseColor = new Color(0.f, 0.f, 0.f);
    }

    public Texture getDiffuseTexture() { return diffuseTexture; }

    public void setSpecularity(double specularity) { this.specularity = specularity; }

    public double getSpecularity() { return specularity; }

    public void setReflectivity(double reflectivity) { this.reflectivity = reflectivity; }

    public double getReflectivity() { return reflectivity; }

    public void setTransparency(double transparency) { this.transparency = transparency; }

    public double getTransparency() { return transparency; }

    public void setSpecularExponent(double specularExponent) { this.specularExponent = specularExponent; }

    public double getSpecularExponent() { return specularExponent; }

    public void setRefractiveIndex(double refractiveIndex) { this.refractiveIndex = refractiveIndex; }

    public double getRefractiveIndex() { return refractiveIndex; }

    public void setEmissivity(double emissivity) { this.emissivity = emissivity; }

    public double getEmissivity() { return emissivity; }

    private Color diffuseColor;
    private Texture diffuseTexture;
    private double specularity, reflectivity, transparency, specularExponent, refractiveIndex, emissivity;
    public static final Material background = new Material(new Color(.02, .07, .08),
            0, 0,0, 0, 0, 0);
    private static final Map<String, Material> materials = new HashMap<>();
}
