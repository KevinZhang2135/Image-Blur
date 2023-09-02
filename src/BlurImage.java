// credit to Owen Barnes for the idea to debug

import java.lang.Math;
import java.io.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

public class BlurImage {
    public static void main(String[] args) throws Exception {
        // System.out.println(Arrays.toString(array[0]));

        blurImage("img/fuji.jpeg");
    }

    public static void blurImage(String filepath) {
        // performs 5x5 Gaussian blur on specified image
        double[][] convolution = {
                { 1, 4, 6, 4, 1 },
                { 4, 16, 24, 16, 14 },
                { 6, 24, 36, 24, 6 },
                { 4, 16, 24, 16, 14 },
                { 1, 4, 6, 4, 1 }
        };

        for (int col = 0; col < convolution.length; col++) {
            for (int row = 0; row < convolution[col].length; row++) {
                convolution[col][row] /= 256.0;
            }
        }

        try {
            BufferedImage image = ImageIO.read(new File(filepath));
            BufferedImage blurredImage = cloneImage(image);

            // loops through pixels
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int red = 0; // bit operation and shifting
                    int green = 0;
                    int blue = 0;

                    // kernel convulsion x
                    for (int col = 0; col < 5; col++) {
                        // kernel convulsion y
                        for (int row = 0; row < 5; row++) {
                            // takes absolute value of coords unless they are out of bounds
                            // reflection Guassian
                            int kernelBlurx = (Math.abs(x + col - 2) < image.getWidth())
                                    ? Math.abs(x + col - 2)
                                    : 2 * (image.getWidth() - 1) - (x + col - 2);

                            int kernelBlury = (Math.abs(y + row - 2) < image.getHeight())
                                    ? Math.abs(y + row - 2)
                                    : 2 * (image.getHeight() - 1) - (y + row - 2);

                            int pixel = image.getRGB(kernelBlurx, kernelBlury);
                            
                            // bitwise operation and shifting
                            red += ((pixel & 0xff0000) >> 16) * convolution[col][row]; 
                            green += ((pixel & 0xff00) >> 8) * convolution[col][row];
                            blue += (pixel & 0xff) * convolution[col][row];

                        }
                    }

                    blurredImage.setRGB(x, y, (red << 16) | (green << 8) | (blue));

                }
            }

            ImageIO.write(blurredImage, "jpg", new File("test.jpg")); // image, extension, filename

        } catch (IOException e) {
            System.out.println(String.format("Could not read input file from \"%s\"", filepath));

        }
    }

    public static BufferedImage cloneImage(BufferedImage image) {
        ColorModel colorModel = image.getColorModel();
        boolean isAlphaPremultiplied = colorModel.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        return new BufferedImage(colorModel, raster, isAlphaPremultiplied, null);
    }
}
