import java.lang.Math;

import java.io.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

public class InvertImage {
    public static void main(String[] args) throws Exception {
        invertImage("img/fuji.jpeg");
    }

    public static void invertImage(String filepath) {
        // performs image inversion

        try {
            BufferedImage image = ImageIO.read(new File(filepath));
            BufferedImage invertedImage = cloneImage(image);
            
            // loops through pixels
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int pixel = image.getRGB(x, y);
                    
                    // bit operation and shifting
                    int red = Math.abs(((pixel & 0xff0000) >> 16) - 255); 
                    int green = Math.abs(((pixel & 0xff00) >> 8) - 255);
                    int blue = Math.abs((pixel & 0xff) - 255);;
                    
                    invertedImage.setRGB(x, y, (red << 16) | (green << 8) | (blue));
                }
            }

            ImageIO.write(invertedImage, "jpg", new File("test.jpg")); // image, extension, filename

        } catch(IOException e) {
            System.out.println("Could not read input file");

        }
    }

    public static BufferedImage cloneImage(BufferedImage image) {
        ColorModel colorModel = image.getColorModel();
        boolean isAlphaPremultiplied = colorModel.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        return new BufferedImage(colorModel, raster, isAlphaPremultiplied, null);
    }
}
