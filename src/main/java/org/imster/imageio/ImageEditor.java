package org.imster.imageio;

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

    protected BufferedImage image;

    /* Reads supplied image file into pixel buffer */
    public ImageEditor(File imageFile) throws IOException {

        if (imageFile == null)
            throw new IOException("Missing path for input image");

        if ((image = ImageIO.read(imageFile)) == null) {
            throw new IOException("Failed to read " + imageFile.getName());
        }

        // Provide support for indexed PNGs by converting into ABGR format
        if (image.getType() == BufferedImage.TYPE_BYTE_INDEXED) {
            BufferedImage convertedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
            convertedImage.createGraphics().drawRenderedImage(image, null);
            pixelBuffer = ((DataBufferByte) convertedImage.getRaster().getDataBuffer()).getData();
            image = convertedImage;
        } else {
            pixelBuffer = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        }

    }

}
