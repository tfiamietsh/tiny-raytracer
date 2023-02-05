package rayTracer.tracer.light;

import rayTracer.tracer.misc.Material;
import rayTracer.tracer.misc.Vertex;
import rayTracer.util.Color;
import rayTracer.util.Vec3;
import rayTracer.tracer.RTObject;

import java.util.List;

public class AmbientLight extends Light implements RTObject {
    public AmbientLight() { }

    @Override
    public String getPattern(int paramCount) {
        return "a %f %f,%f,%f";
    }

    @Override
    public Color calculateColorAtPoint(Vertex vertex, Vec3 viewDirection, Material material) {
        return color.mul(intensity);
    }

    @Override
    public RTObject newInstance(List<Double> dblParams, List<String> strParams) {
        AmbientLight ambientLight = new AmbientLight();

        ambientLight.setIntensity(dblParams.get(0));
        ambientLight.setPosition(new Vec3(Vec3.infinity, Vec3.infinity, Vec3.infinity));
        ambientLight.setColor(new Color(dblParams.get(1), dblParams.get(2), dblParams.get(3)));

        return ambientLight;
    }
}
