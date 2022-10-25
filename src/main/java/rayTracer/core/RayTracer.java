package rayTracer.core;

import rayTracer.ui.Image;

import java.awt.Dimension;

public class RayTracer {
    public void configure(Dimension outputImageResolution, int maxDepth) {
        this.outputImageResolution = outputImageResolution;
        this.tracingUnit = new TracingUnit(maxDepth);
    }

    public void render(String fullPath, Scene scene, int cores) {
        Image outputImage = tracingUnit.getOutputImage(outputImageResolution, scene, cores);
        outputImage.save(fullPath);
    }

    Dimension outputImageResolution;
    TracingUnit tracingUnit;
}