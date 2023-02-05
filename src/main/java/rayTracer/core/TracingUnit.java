package rayTracer.core;

import rayTracer.tracer.geometry.*;
import rayTracer.tracer.misc.Material;
import rayTracer.tracer.misc.Vertex;
import rayTracer.tracer.light.Light;
import rayTracer.ui.Image;
import rayTracer.util.*;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.util.Pair;

import static rayTracer.util.Vec3.infinity;
import static rayTracer.util.Vec3.epsilon;

public class TracingUnit {
    public TracingUnit(int maxRayDepth, int shadowSamplesNum) {
        this.maxRayDepth = maxRayDepth;
        this.shadowSamplesNum = shadowSamplesNum;
        fuzzyLightPositionsMap = new HashMap<>();
    }

    public Image getOutputImage(Dimension resolution, Scene scene, int cores) {
        Color[][] colorMap = new Color[resolution.height][resolution.width];
        double shadowSamplesNumSqrt = Math.sqrt(shadowSamplesNum);

        for (Light light : scene.getLights()) {
            Vec3[][] fuzzyLightPositions = new Vec3[shadowSamplesNum][shadowSamplesNum];

            for (int i = 0; i < shadowSamplesNum; i++)
                for (int j = 0; j < shadowSamplesNum; j++)
                    fuzzyLightPositions[i][j] = light.getPosition().add(new Vec3(Math.random(),
                            Math.random(), Math.random()).mul(shadowSamplesNumSqrt));
            fuzzyLightPositionsMap.put(light, fuzzyLightPositions);
        }

        if (resolution.height > cores) {
            int offsetY = 0;
            int deltaY = resolution.height / cores;

            for (int core = 0; core < cores; core++) {
                if (core == cores - 1) {
                    int finalOffsetY = offsetY;
                    Runnable task = () -> {
                        for (int pixelY = finalOffsetY; pixelY < resolution.height; pixelY++)
                            for (int pixelX = 0; pixelX < resolution.width; pixelX++)
                                fillColorMap(colorMap, scene, pixelX, pixelY,
                                        resolution.width, resolution.height);
                    };
                    Thread thread = new Thread(task);

                    thread.start();
                    try { thread.join(); }
                    catch (Exception e) { Log.println(e.getMessage()); }
                } else {
                    int finalOffsetY = offsetY;
                    Runnable task = () -> {
                        for (int pixelY = finalOffsetY; pixelY < finalOffsetY + deltaY; pixelY++)
                            for (int pixelX = 0; pixelX < resolution.width; pixelX++)
                                fillColorMap(colorMap, scene, pixelX, pixelY,
                                        resolution.width, resolution.height);
                    };
                    Thread thread = new Thread(task);

                    thread.start();
                    try { thread.join(); }
                    catch (Exception e) { Log.println(e.getMessage()); }
                    offsetY += deltaY;
                }
            }
        } else
            for (int pixelY = 0; pixelY < resolution.height; pixelY++)
                for (int pixelX = 0; pixelX < resolution.width; pixelX++)
                    fillColorMap(colorMap, scene, pixelX, pixelY,
                            resolution.width, resolution.height);
        return convertColorMapToImage(colorMap);
    }

    private Triplet<Boolean, Vertex, Material> geometryIntersect(Vec3 origin, Vec3 direction,
            ArrayList<Shape> shapes) {
        double nearestDistance = infinity;
        Triplet<Boolean, Vertex, Material> resultPair =
            new Triplet<>(false, null, Material.background);

        for (Shape shape : shapes) {
            Pair<Double, Vertex> pair = shape.rayIntersection(origin, direction);
            double distance = pair.getKey();

            if (0. < distance && distance < nearestDistance) {
                nearestDistance = distance;

                resultPair = new Triplet<>(true, new Vertex(origin.add(direction.mul(distance)),
                    pair.getValue().getNormal(), pair.getValue().getTexCoord()), shape.getMaterial());
            }
        }
        return resultPair;
    }

    private Color rayCast(Vec3 origin, Vec3 direction, ArrayList<Shape> shapes,
              ArrayList<Light> lights, int rayDepth) {
        Triplet<Boolean, Vertex, Material> triplet = geometryIntersect(origin, direction, shapes);
        boolean hit = triplet.getValue1();
        Vertex vertex = triplet.getValue2();
        Material material = triplet.getValue3();

        if (rayDepth >= maxRayDepth || !hit)
            return material.getDiffuseColor(new Vec2());

        Color color = new Color(), reflectedColor = new Color(), refractedColor = new Color();
        double emissivity = material.getEmissivity(), reflectivity = material.getReflectivity(),
                transparency = material.getTransparency();

        if (reflectivity > epsilon) {
            Vec3 reflectDirection = Vec3.reflect(direction, vertex.getNormal()).normalized();

            reflectedColor = rayCast(vertex.getPosition().add(reflectDirection.mul(epsilon)),
                    reflectDirection, shapes, lights, rayDepth + 1);
        }
        if (transparency > epsilon) {
            Vec3 refractDirection = Vec3.refract(direction, vertex.getNormal(),
                material.getRefractiveIndex(), 1.).normalized();

            refractedColor = rayCast(vertex.getPosition().add(refractDirection.mul(epsilon)),
                    refractDirection, shapes, lights, rayDepth + 1);
        }
        if (1. - emissivity > epsilon)
            for (Light light : lights)
                color = color.add(calcShadedColorSamples(light, vertex, shapes, material));
        return color
                .mul(1. - emissivity).add(material.getDiffuseColor(vertex.getTexCoord()).mul(emissivity))
                .mul(1. - reflectivity).add(reflectedColor.mul(reflectivity))
                .mul(1. - transparency).add(refractedColor.mul(transparency));
    }

    private Color calcShadedColorSamples(Light light, Vertex vertex, ArrayList<Shape> shapes,
                Material material) {
        Vec3[][] fuzzyLightPositions = fuzzyLightPositionsMap.get(light);
        Color color = new Color();

        for (int i = 0; i < shadowSamplesNum; i++)
            for (int j = 0; j < shadowSamplesNum; j++) {
                Vec3 lightDirection = fuzzyLightPositions[i][j].sub(vertex.getPosition());
                double toLightDist = lightDirection.length();
                lightDirection = lightDirection.mul(1. / toLightDist);
                Triplet<Boolean, Vertex, Material> shadowTriplet = geometryIntersect(
                        vertex.getPosition().add(lightDirection.mul(Vec3.epsilon)),
                        lightDirection, shapes);

                if (shadowTriplet.getValue1() && shadowTriplet.getValue2().getPosition()
                        .sub(vertex.getPosition()).length() < toLightDist)
                    continue;
                color = color.add(light.calculateColorAtPoint(vertex, lightDirection.mul(-1.), material));
            }
        return color.mul(1. / shadowSamplesNum / shadowSamplesNum);
    }

    private void fillColorMap(Color[][] map, Scene scene, int x, int y, int w, int h) {
        map[y][x] = rayCast(scene.getCamera().getPosition(),
                scene.getCamera().calculateRayDirection(x, y, w, h),
                scene.getShapes(), scene.getLights(), 0);
    }

    private Image convertColorMapToImage(Color[][] map) {
        Image image = new Image();
        int w = map[0].length, h = map.length;

        image.empty(w, h);
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                image.setRGB(x, y, map[y][x].toInt());
        return image;
    }

    private final int maxRayDepth, shadowSamplesNum;
    private final Map<Light, Vec3[][]> fuzzyLightPositionsMap;
}
