package imageio;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

/* Class that manages image editing */
public class ImageEditor {

    protected static final int BITS_PER_CHAR = 7;

    protected static final char START_BYTE = '\u0002';
    protected static final char END_BYTE = '\u001b';

    protected final byte[] pixelBuffer;

    protected final int imageWidth;
    protected final int imageHeight;

    /* Reads supplied image file into pixel buffer */
    public ImageEditor(File imageFile) throws IOException {

        if (imageFile == null)
            throw new IOException("Missing path for input image");

        BufferedImage image = ImageIO.read(imageFile);
        imageWidth = image.getWidth();
        imageHeight = image.getHeight();
        pixelBuffer = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

    }

}
