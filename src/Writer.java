import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

public class Writer extends Steganographer {

    public static void main(String[] args) throws IOException {

        File inFile = new File("./bobby.png");
        File outFile = new File("./edited.png");
        String message = "General Kenobi. You are a bold one.";

        Steganographer writer = new Writer(inFile, outFile, message);

        Steganographer reader = new Reader(outFile);

    }

    private final byte[] pixels;
    private byte[] message;
    private final int width;
    private final int height;

    public Writer(File imageFile, File outFile, String message) throws IOException {

        if (imageFile == null || outFile == null)
            throw new IOException("No input or output file selected");

        stringToBinaryArray(message + END_CHAR);
        BufferedImage image = ImageIO.read(imageFile);
        width = image.getWidth();
        height = image.getHeight();
        pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        writeToPixels();
        writeImage(outFile);
    }

    private void writeImage(File outFile) throws IOException {

        DataBuffer buffer = new DataBufferByte(pixels, pixels.length);
        WritableRaster raster = Raster.createInterleavedRaster(buffer, width, height, 3 * width, 3, new int[] {2, 1, 0}, null);
        ColorModel cm = new ComponentColorModel(ColorModel.getRGBdefault().getColorSpace(), false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        BufferedImage image = new BufferedImage(cm, raster, true, null);
        ImageIO.write(image, "png", outFile);

    }

    private void writeToPixels() {

        for (int i = 0; i < message.length; i++) {

            pixels[i] -= pixels[i] % 2; // Normalise
            pixels[i] += message[i];

        }
    }

    // Writes the binary representation of the string into this.message
    private void stringToBinaryArray(String message) {

        this.message = new byte[message.length() * BITS_PER_CHAR];

        char[] messageChars = message.toCharArray();
        for (int i = 0; i < messageChars.length; i++) {
            asciiToBinary(messageChars[i], this.message, (i * BITS_PER_CHAR) + BITS_PER_CHAR - 1);
        }

    }

    /**
     * Converts ASCII character code to binary bit array
     * @param c ASCII 7-bit character code
     * @param b Array to store binary bits
     * @param i Index in b to store calculated bit
     */
    private void asciiToBinary(int c, byte[] b, int i) {
        if (c >= 2)
            asciiToBinary(c / 2, b, i - 1);
        b[i] = (byte) (c % 2);
    }

}
