package rayTracer.tracer.geometry;

import rayTracer.tracer.misc.Material;
import rayTracer.tracer.RTObject;
import rayTracer.util.Vec3;

import java.util.List;

import static rayTracer.util.Vec3.infinity;

public class Skysphere extends Sphere {
    public Skysphere() { }

    public Skysphere(Material material) {
        setCenter(new Vec3(0., 0., 0.));
        setRadius(infinity / 2);
        setMaterial(material);
    }

    @Override
    public String getPattern(int paramCount) {
        return "ss " + Material.background.getPattern(paramCount);
    }

    @Override
    public RTObject newInstance(List<Double> dblParams, List<String> strParams) {
        return new Skysphere(Material.getByName(strParams.get(0)));
    }
}
