package rayTracer.tracer.light;

import rayTracer.tracer.misc.Material;
import rayTracer.tracer.misc.Vertex;
import rayTracer.util.Color;
import rayTracer.util.Vec2;
import rayTracer.util.Vec3;
import rayTracer.tracer.RTObject;

import java.util.List;

public class PointLight extends Light implements RTObject {
    public PointLight() { }

    @Override
    public String getPattern(int paramCount) {
        return "p %f,%f,%f %f %f,%f,%f";
    }

    @Override
    public RTObject newInstance(List<Double> dblParams, List<String> strParams) {
        PointLight pointLight = new PointLight();

        pointLight.setPosition(new Vec3(dblParams.get(0), dblParams.get(1), dblParams.get(2)));
        pointLight.setIntensity(dblParams.get(3));
        pointLight.setColor(new Color(dblParams.get(4), dblParams.get(5), dblParams.get(6)));

        return pointLight;
    }

    @Override
    public Color calculateColorAtPoint(Vertex vertex, Vec3 viewDirection, Material material) {
        Vec3 lightDir = this.position.sub(vertex.getPosition());

        double distance = lightDir.dot(lightDir);
        double A = 1., B = .001, C = .00001;
        double attenuation = 1. / (A + B * distance + C * distance * distance);

        lightDir = lightDir.normalized();
        double diff = Math.max(vertex.getNormal().dot(lightDir), 0.f);

        Vec3 reflectDir = Vec3.reflect(lightDir.mul(-1.f), vertex.getNormal()).mul(-1.f);
        double spec = (float) (Math.pow(Math.max(viewDirection.dot(reflectDir), 0.f),
                Math.max(material.getSpecularExponent(), 0.1f)));

        Color diffuse = material.getDiffuseColor(vertex.getTexCoord());
        Color diffuseComponent = this.color.mul(this.intensity * diff).mul(diffuse);
        Color specularComponent = this.color.mul(this.intensity * spec * material.getSpecularity())
                .mul(diffuse);
        diffuseComponent = diffuseComponent.mul(attenuation);

        return diffuseComponent.add(specularComponent);
    }
}
