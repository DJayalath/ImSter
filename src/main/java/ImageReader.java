import java.io.File;
import java.io.IOException;
import java.util.*;

/* Class that manages reading strings from image files */
public class ImageReader extends ImageEditor {

    // ASCII encoded buffer for message read
    private List<Byte> messageBuffer;

    /* Reads image input into pixel buffer */
    public ImageReader(File imageFile) throws IOException {
        super(imageFile);
    }

    /* Reads string data from image file */
    public String readString() throws IOException {
        readToMessageBuffer();
        return messageBufferToString();
    }

    /* Builds a string from character codes in messageBuffer */
    private String messageBufferToString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : messageBuffer) {
            stringBuilder.append((char) b);
        }
        return stringBuilder.toString();
    }

    /* Reads data from pixel buffer into message buffer */
    private void readToMessageBuffer() throws IOException {

        messageBuffer = new ArrayList<>();

        // Stores last n bits to convert into character
        byte[] buffer = new byte[BITS_PER_CHAR];

        int i = 0; // Pixel buffer index
        int j = 0; // Buffer index

        // Indicates whether message end character is found
        boolean messageComplete = false;

        while (i < pixelBuffer.length && !messageComplete) {

            // Read binary value from pixel
            buffer[j] = (byte) ((pixelBuffer[i] % 2 + 2) % 2);

            i++;
            j++;

            // Once buffer is full, convert to character code and add to messageBuffer
            if (j > BITS_PER_CHAR - 1) {
                j = 0;
                byte b = binaryToAscii(buffer);
                if (b == END_CHAR)
                    messageComplete = true;
                else {
                    messageBuffer.add(b);
                }
            }

        }

        if (!messageComplete)
            throw new IOException("Message not found in image");

    }

    /* Converts binary array b of size BITS_PER_CHAR into character code */
    private byte binaryToAscii(byte[] b) {
        byte t = 0;
        for (int i = BITS_PER_CHAR - 1; i >= 0; i--) {
            t += (1 << i) * b[BITS_PER_CHAR - 1 - i];
        }
        return t;
    }

}
