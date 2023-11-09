// credit to Owen Barnes for the idea to debug

import java.lang.Math;
import java.io.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

public class BlurImage {
    public static void main(String[] args) throws Exception {
        double[][] convolution = createConvolution(2, 2);
        blurImage("img/seaside.jpeg", convolution);
    }

    /**
     * Generates a convolution from a normalized 2d guassian distribution
     * 
     * @param blurRadius the length of the convolution from an origin point
     * @throws
     * @return a 2d convolution
     */
    public static double[][] createConvolution(int blurRadius, double SD) {
        if (SD < 0) {
            throw new IllegalArgumentException();
        }

        int blurSize = 2 * blurRadius + 1;
        double[][] convolution = new double[blurSize][blurSize];
        double sum = 0;

        // maps 3d guassian to convolution
        for (int row = 0; row < blurSize; row++) {
            for (int col = 0; col < blurSize; col++) {
                double x = (row - blurRadius) / SD;
                double y = (col - blurRadius) / SD;

                convolution[row][col] = guassian(x, y, SD);
                sum += convolution[row][col];
            }
        }

        // normalizes guassian
        for (int row = 0; row < blurSize; row++) {
            for (int col = 0; col < blurSize; col++) {
                convolution[row][col] /= sum;
            }
        }

        return convolution;
    }

    /**
     * Determines z point of 3d guassian as a function of f(x, y) and standard
     * deviation
     * 
     * @param x  the x point of the 3d guassian
     * @param y  the y point of the 3d guassian
     * @param SD the standard deviation of the guassian
     * @return the z value of the 3d guassian at (x, y)
     */
    public static double guassian(double x, double y, double SD) {
        // returns the value of a 2d guassian distribution with a mean at origin

        // 1 / (o√(2π)) * e^-((x^2 + y^2) / (2o^2))
        double constant = 1 / (SD * Math.sqrt(2 * Math.PI));
        return constant * Math.pow(Math.E, (x * x + y * y) / (-2 * SD * SD));
    }

    /**
     * Performs Gaussian blur on specified image
     * 
     * @param filepath the file path of the image
     * @param blurSize the dimensions of the blur
     */
    public static void blurImage(String filepath, double[][] convolution) {
        BufferedImage image = null;
        BufferedImage blurredImage = null;

        // reads original image
        try {
            image = ImageIO.read(new File(filepath));
            blurredImage = cloneImage(image);

        } catch (IOException e) {
            System.out.println(String.format("Could not read input file from \"%s\"", filepath));
        }

        // loops through pixels for blurring
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                // retrieving colors from surrounding convolution
                int[] rgb = mapConvolution(x, y, blurredImage, convolution);
                int red = rgb[0];
                int green = rgb[1];
                int blue = rgb[2];

                // set color to blurred image
                blurredImage.setRGB(x, y, (red << 16) | (green << 8) | (blue));
            }
        }

        // saves output file
        try {
            // image, extension, filename
            ImageIO.write(blurredImage, "jpg", new File("test.jpg"));

        } catch (IOException e) {
            System.out.println("Could not save output file");
        }

    }

    /**
     * Returns an int array of rgb created by convolution about specified point in
     * the image
     * 
     * @param x           the x point of the image to convulse
     * @param y           the y point of the image to convulse
     * @param image       the image to convulse
     * @param convolution the guassian convolution map
     * @return an int array of rgb created with guassian blur
     */
    public static int[] mapConvolution(int x, int y, BufferedImage image, double[][] convolution) {
        int blurSize = convolution.length;
        int blurRadius = blurSize / 2;

        int red = 0;
        int green = 0;
        int blue = 0;

        // kernel convulsion x
        for (int row = 0; row < blurSize; row++) {
            // kernel convulsion y
            for (int col = 0; col < blurSize; col++) {
                // takes absolute value of coords unless they are out of bounds
                // reflection Guassian
                int kernelBlurx = (Math.abs(x + col - blurRadius) < image.getWidth())
                        ? Math.abs(x + col - blurRadius)
                        : 2 * (image.getWidth() - 1) - (x + col - blurRadius + 1);

                int kernelBlury = (Math.abs(y + row - blurRadius) < image.getHeight())
                        ? Math.abs(y + row - blurRadius)
                        : 2 * (image.getHeight() - 1) - (y + row - blurRadius + 1);

                int pixel = image.getRGB(kernelBlurx, kernelBlury);

                // bitwise operation and shifting
                red += ((pixel & 0xff0000) >> 16) * convolution[col][row];
                green += ((pixel & 0xff00) >> 8) * convolution[col][row];
                blue += (pixel & 0xff) * convolution[col][row];
            }
        }

        return new int[] { red, green, blue };

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
