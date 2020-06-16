import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Reader extends Steganographer {

    private final byte[] pixels;
    private List<Byte> message;
    private String decodedMessage;

    public Reader(File imageFile) throws IOException {

        if (imageFile == null)
            throw new IOException("No input file selected");

        BufferedImage image = ImageIO.read(imageFile);
        pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        readFromPixels();
        convertToString();
    }

    public String getDecodedMessage() {
        return decodedMessage;
    }

    private void convertToString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : message) {
            stringBuilder.append((char) b);
        }
        decodedMessage = stringBuilder.toString();
    }

    private void readFromPixels() {

        message = new ArrayList<>();

        byte[] buffer = new byte[BITS_PER_CHAR];

        int i = 0;
        int j = 0;
        boolean messageComplete = false;
        while (i < pixels.length && !messageComplete) {

            buffer[j] = (byte) ((pixels[i] % 2 + 2) % 2);

            i++;
            j++;

            if (j > BITS_PER_CHAR - 1) {

                j = 0;
                byte b = binaryToAscii(buffer);
                if (b == END_CHAR)
                    messageComplete = true;
                else {
                    message.add(b);
                }
            }
        }

    }

    private byte binaryToAscii(byte[] b) {
        byte t = 0;
        for (int i = BITS_PER_CHAR - 1; i >= 0; i--) {
            t += (1 << i) * b[BITS_PER_CHAR - 1 - i];
        }

        return t;
    }

}
