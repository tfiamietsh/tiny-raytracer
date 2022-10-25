package rayTracer.core;

import rayTracer.tracer.misc.Material;
import rayTracer.tracer.misc.Vertex;
import rayTracer.tracer.geometry.Shape;
import rayTracer.tracer.light.Light;
import rayTracer.ui.Image;
import rayTracer.util.Color;
import rayTracer.util.Log;
import rayTracer.util.Vec2;
import rayTracer.util.Vec3;

import java.awt.Dimension;
import java.util.ArrayList;
import javafx.util.Pair;

import static rayTracer.util.Vec3.infinity;
import static rayTracer.util.Vec3.epsilon;

public class TracingUnit {
    public TracingUnit(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public Image getOutputImage(Dimension resolution, Scene scene, int cores) {
        Image image = new Image();
        image.empty(resolution.width, resolution.height);

        if (resolution.height > cores) {
            int offsetY = 0;
            int deltaY = resolution.height / cores;

            for (int core = 0; core < cores; core++) {
                if (core == cores - 1) {
                    int finalOffsetY = offsetY;
                    Runnable task = () -> {
                        for (int pixelY = finalOffsetY; pixelY < resolution.height; pixelY++)
                            for (int pixelX = 0; pixelX < resolution.width; pixelX++) {


                                Vec3 direction = scene.getCamera().calculateRayDirection(pixelX, pixelY,
                                        resolution.width, resolution.height);

                                Color pixelColor = rayCast(scene.getCamera().getPosition(), direction,
                                        scene.getShapes(), scene.getLights(), 0);
                                image.setRGB(pixelX, pixelY, pixelColor.toInt());
                            }
                    };
                    Thread thread = new Thread(task);
                    thread.start();
                    try { thread.join(); }
                    catch (Exception e) { Log.println(e.getMessage()); }
                } else {
                    int finalOffsetY = offsetY;
                    Runnable task = () -> {
                        for (int pixelY = finalOffsetY; pixelY < finalOffsetY + deltaY; pixelY++)
                            for (int pixelX = 0; pixelX < resolution.width; pixelX++) {
                                Vec3 direction = scene.getCamera().calculateRayDirection(pixelX, pixelY,
                                        resolution.width, resolution.height);

                                Color pixelColor = rayCast(scene.getCamera().getPosition(), direction,
                                        scene.getShapes(), scene.getLights(), 0);
                                image.setRGB(pixelX, pixelY, pixelColor.toInt());
                            }
                    };
                    Thread thread = new Thread(task);
                    thread.start();
                    try { thread.join(); }
                    catch (Exception e) { Log.println(e.getMessage()); }
                    offsetY += deltaY;
                }
            }
        } else {
            for (int pixelY = 0; pixelY < resolution.height; pixelY++)
                for (int pixelX = 0; pixelX < resolution.width; pixelX++) {
                    Vec3 direction = scene.getCamera().calculateRayDirection(pixelX, pixelY,
                            resolution.width, resolution.height);

                    Color pixelColor = rayCast(scene.getCamera().getPosition(), direction,
                            scene.getShapes(), scene.getLights(), 0);
                    image.setRGB(pixelX, pixelY, pixelColor.toInt());
                }
        }
        return image;
    }

    public Pair<Boolean, Pair<Vertex, Material>> geometryIntersect(Vec3 origin, Vec3 direction,
            ArrayList<Shape> shapes) {
        double nearestDistance = infinity;
        Pair<Boolean, Pair<Vertex, Material>> resultPair =
            new Pair<>(false, new Pair<>(null, Material.background));

        for (Shape shape : shapes) {
            Pair<Double, Vertex> pair = shape.rayIntersection(origin, direction);
            double distance = pair.getKey();

            if (0. < distance && distance < nearestDistance) {
                nearestDistance = distance;

                resultPair = new Pair<>(true, new Pair<>(new Vertex(origin.add(direction.mul(distance)),
                    pair.getValue().getNormal(), pair.getValue().getTexCoord()), shape.getMaterial()));
            }
        }
        return resultPair;
    }

    public Color rayCast(Vec3 origin, Vec3 direction, ArrayList<Shape> shapes,
                         ArrayList<Light> lights, int depth) {
        Pair<Boolean, Pair<Vertex, Material>> pair = geometryIntersect(origin, direction, shapes);
        boolean hit = pair.getKey();
        Vertex vertex = pair.getValue().getKey();
        Material material = pair.getValue().getValue();

        if (depth >= maxDepth || !hit)
            return material.getDiffuseColor(new Vec2());

        Color finalColor = new Color(), reflectColor = new Color(), refractColor = new Color();

        double emissivity = material.getEmissivity(), reflectivity = material.getReflectivity(),
                transparency = material.getTransparency();
        if (reflectivity > epsilon) {
            Vec3 reflectDirection = Vec3.reflect(direction, vertex.getNormal()).normalized();
            reflectColor = rayCast(vertex.getPosition().add(reflectDirection.mul(epsilon)),
                    reflectDirection, shapes, lights, depth + 1);
        }
        if (transparency > epsilon) {
            Vec3 refractDirection = Vec3.refract(direction, vertex.getNormal(),
                    material.getRefractiveIndex(), 1.).normalized();
            refractColor = rayCast(vertex.getPosition().add(refractDirection.mul(epsilon)),
                    refractDirection, shapes, lights, depth + 1);
        }
        if (1. - emissivity > epsilon)
            for (Light light : lights) {    //  checking if the point lies in the shadow of the light
                Vec3 lightDir = light.getPosition().sub(vertex.getPosition()).normalized();
                Pair<Boolean, Pair<Vertex, Material>> shadowPair = geometryIntersect(
                    vertex.getPosition().add(lightDir.mul(Vec3.epsilon)), lightDir, shapes);
                boolean shadowHit = shadowPair.getKey();
                Vertex shadowVertex = shadowPair.getValue().getKey();

                if (shadowHit && shadowVertex.getPosition().sub(vertex.getPosition()).length() <
                        light.getPosition().sub(vertex.getPosition()).length())
                    continue;

                finalColor = finalColor.add(light.calculateColorAtPoint(
                    vertex, lightDir.mul(-1), material));
            }
        return finalColor
                .mul(1. - emissivity).add(material.getDiffuseColor(vertex.getTexCoord()).mul(emissivity))
                .mul(1. - reflectivity).add(reflectColor.mul(reflectivity))
                .mul(1. - transparency).add(refractColor.mul(transparency));
    }

    private final int maxDepth;
}
