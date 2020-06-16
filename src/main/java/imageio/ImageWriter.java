package imageio;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

/* Class that manages writing strings to image files */
public class ImageWriter extends ImageEditor {

    // Binary-encoded buffer for supplied message
    private byte[] messageBuffer;

    private final File imageOut;

    /* Reads image input into pixel buffer and sets output destination file */
    public ImageWriter(File imageIn, File imageOut) throws IOException {

        super(imageIn);

        if (imageOut == null)
            throw new IOException("Missing path for output image");

        this.imageOut = imageOut;
    }

    /* Writes supplied message into pixel buffer and saves image to destination file */
    public void writeString(String message) throws IOException {
        stringToBinaryArray(START_BYTE + message + END_BYTE);
        writeToPixelBuffer();
        writeImage();
    }

    /* Writes pixel buffer to destination file */
    private void writeImage() throws IOException {

        DataBuffer buffer = new DataBufferByte(pixelBuffer, pixelBuffer.length);

        WritableRaster raster = Raster.createInterleavedRaster(
                buffer, imageWidth, imageHeight, 3 * imageWidth,
                3, new int[] {2, 1, 0}, null);

        ColorModel cm = new ComponentColorModel(
                ColorModel.getRGBdefault().getColorSpace(), false,
                true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);

        BufferedImage image = new BufferedImage(cm, raster, true, null);
        ImageIO.write(image, "png", imageOut);

    }

    /* Modifies pixel buffer to insert the message */
    private void writeToPixelBuffer() {

        for (int i = 0; i < messageBuffer.length; i++) {

            // Pixel value is rounded down to nearest multiple of 2 in order to store binary data
            pixelBuffer[i] -= pixelBuffer[i] % 2;

            pixelBuffer[i] += messageBuffer[i];

        }
    }

    /* Converts the string representation of the message into a binary representation stored in messageBuffer */
    private void stringToBinaryArray(String message) throws IOException {

        messageBuffer = new byte[message.length() * BITS_PER_CHAR];

        if (pixelBuffer.length < messageBuffer.length)
            throw new IOException("Message is too long for this image");

        char[] messageChars = message.toCharArray();
        for (int i = 0; i < messageChars.length; i++) {
            asciiToBinary(messageChars[i], (i * BITS_PER_CHAR) + BITS_PER_CHAR - 1);
        }

    }

    /* Converts ASCII character code to binary and writes into messageBuffer */
    private void asciiToBinary(int c, int i) {
        if (c >= 2)
            asciiToBinary(c / 2, i - 1);
        messageBuffer[i] = (byte) (c % 2);
    }

}
