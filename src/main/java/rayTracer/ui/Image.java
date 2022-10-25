package rayTracer.ui;

import rayTracer.util.Log;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.io.File;

@SuppressWarnings("serial")
public class Image extends JLabel {
    public Image() {
        super();
    }

    public void empty(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public void setRGB(int x, int y, int color) {
        image.setRGB(x, y, color);
    }

    public void save(String fullPath) {
        String fileExtension = fullPath.substring(fullPath.lastIndexOf('.') + 1);
        try { ImageIO.write(image, fileExtension, new File(fullPath)); }
        catch (Exception e) { Log.println(e.getMessage()); }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        java.awt.Image scaledImage = image.getScaledInstance(this.getWidth(), this.getHeight(), java.awt.Image.SCALE_SMOOTH);
        g.drawImage(scaledImage, 0, 0, null);
    }

    private BufferedImage image;
}