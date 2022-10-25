package rayTracer.tracer.misc;

import rayTracer.util.Color;
import rayTracer.util.Log;
import rayTracer.util.Vec2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Texture {
    public Texture(String filename) { load(filename); }

    public void load(String filename) {
        try {
            image = ImageIO.read(new File(resourcesDirectory + '\\' + filename));
            imageWidth = image.getWidth();
            imageHeight = image.getHeight();
        }
        catch (Exception e) { Log.println(e.getMessage()); }
    }

    public Color getColor(Vec2 texCoord) {
        Vec2 trTexCoord = new Vec2(Math.abs(texCoord.getU() % 1) * imageWidth,
                Math.abs(texCoord.getV() % 1) * imageHeight);
        Vec2 texf = new Vec2(Math.floor(trTexCoord.getU()), Math.floor(trTexCoord.getV()));
        Vec2 ratio = trTexCoord.sub(texf);
        Vec2 opposite = new Vec2(1., 1.).sub(ratio);
        int x = (int) texf.getU(), y = (int) texf.getV();

        if (0 <= x && x < imageWidth - 1 && 0 <= y && y < imageHeight - 1)
            return Color.valueOf(image.getRGB(x, y)).mul(opposite.getU())
                    .add(Color.valueOf(image.getRGB(x + 1, y)).mul(ratio.getU()))
                    .mul(opposite.getV()).add(
                            Color.valueOf(image.getRGB(x, y + 1)).mul(opposite.getU())
                                    .add(Color.valueOf(image.getRGB(x + 1, y + 1)).mul(ratio.getU()))
                                    .mul(ratio.getV()));
        return Color.valueOf(image.getRGB(x, y));
    }

    public static void setDirectory(String directory) { resourcesDirectory = directory; }

    public BufferedImage getImage() { return image; }

    private BufferedImage image;
    private int imageWidth, imageHeight;

    private static String resourcesDirectory;
}
