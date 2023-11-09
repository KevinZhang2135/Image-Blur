import java.lang.Math;

import java.io.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

public class InvertImage {
    public static void main(String[] args) throws Exception {
        invertImage("img/fuji.jpeg");
    }

    /**
     * Inverts a specified image by rgb
     * 
     * @param filepath the filepath of the image to be inverted
     */
    public static void invertImage(String filepath) {
        BufferedImage image = null;
        BufferedImage invertedImage = null;

        // reads original image
        try {
            image = ImageIO.read(new File(filepath));
            invertedImage = cloneImage(image);

        } catch (IOException e) {
            System.out.println(String.format("Could not read input file from \"%s\"", filepath));

        }

        // loops through pixels for inverting
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = image.getRGB(x, y);

                // bit operation and shifting
                int red = Math.abs(((pixel & 0xff0000) >> 16) - 255);
                int green = Math.abs(((pixel & 0xff00) >> 8) - 255);
                int blue = Math.abs((pixel & 0xff) - 255);

                invertedImage.setRGB(x, y, (red << 16) | (green << 8) | (blue));
            }
        }

        // saves output file
        try {
            // image, extension, filename
            ImageIO.write(invertedImage, "jpg", new File("test.jpg"));

        } catch (IOException e) {
            System.out.println("Could not save output file");
        }

    }

    /**
     * Deep copies an image
     * 
     * @param image the image to be copied
     * @return a copied image
     */
    public static BufferedImage cloneImage(BufferedImage image) {
        ColorModel colorModel = image.getColorModel();
        boolean isAlphaPremultiplied = colorModel.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        return new BufferedImage(colorModel, raster, isAlphaPremultiplied, null);
    }
}
