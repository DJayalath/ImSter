import org.imster.imageio.ImageReader;
import org.imster.imageio.ImageWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class ImageIOTest {

    static String message;

    @BeforeAll
    static void setUp() {
        try (Scanner scanner = new Scanner(
                new File(MainTest.resourceDirectory + "/macbeth.txt"), "UTF-8" )) {
            ImageIOTest.message = scanner.useDelimiter("\\A").next();
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void missingInputTest() {
        assertThrows(IOException.class, () -> new ImageReader(null));
        assertThrows(IOException.class, () -> new ImageWriter(null,
                new File(MainTest.resourceDirectory + "/rgbIOTestOUT.png")));
        assertThrows(IOException.class, () -> new ImageWriter(
                new File(MainTest.resourceDirectory + "/rgb.png"), null));
    }

    @Test
    void noMessageFoundTest() {
        File in = new File(MainTest.resourceDirectory + "/grayscale.png");
        ImageReader imageReader = null;

        try {
             imageReader = new ImageReader(in);
        } catch (IOException ioException) {
            fail(ioException.getMessage());
        }

        ImageReader imageReader1 = imageReader;

        assertThrows(IOException.class, imageReader1::readString);
    }

    @Test
    void messageTooLongTest() {
        File in = new File(MainTest.resourceDirectory + "/indexed.png");
        File out = new File(MainTest.resourceDirectory + "/indexedOUT.png");
        ImageWriter imageWriter = null;
        try {
            imageWriter = new ImageWriter(in, out);
        } catch (IOException ioException) {
            fail(ioException.getMessage());
        }

        ImageWriter imageWriter1 = imageWriter;
        assertThrows(IOException.class, () -> imageWriter1.writeString(message));
    }

    @Test
    void readWriteTest() {

        File in = new File(MainTest.resourceDirectory + "/rgb.png");
        File out = new File(MainTest.resourceDirectory + "/rgbIOTestOUT.png");
        String messageWrite = "test message";

        try {
            ImageWriter imageWriter = new ImageWriter(in, out);
            imageWriter.writeString("test message");
            ImageReader imageReader = new ImageReader(out);
            String messageRead = imageReader.readString();
            assertEquals(messageWrite, messageRead);
        } catch (IOException ioException) {
            fail(ioException.getMessage());
        }

    }

    @Test
    void readWriteNonIndexedTest() {

        File in = new File(MainTest.resourceDirectory + "/rgb.png");
        File out = new File(MainTest.resourceDirectory + "/rgbOUT.png");

        try {
            ImageWriter imageWriter = new ImageWriter(in, out);
            imageWriter.writeString(message);
            ImageReader imageReader = new ImageReader(out);
            String decoded = imageReader.readString();

            byte[] imageIn = ((DataBufferByte) ImageIO.read(in).getRaster().getDataBuffer()).getData();
            byte[] imageOut = ((DataBufferByte) ImageIO.read(out).getRaster().getDataBuffer()).getData();

            if (imageIn.length != imageOut.length)
                fail("Input and output image sizes are different");
            else {

                for (int i = 0; i < imageIn.length; i++) {
                    assertEquals(imageIn[i], imageOut[i], 1);
                }

            }

            assertEquals(message, decoded);
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    @Test
    void readWriteIndexedTest() {

        String customMessage = message.substring(0, message.length() / 64);

        File in = new File(MainTest.resourceDirectory + "/indexed.png");
        File out = new File(MainTest.resourceDirectory + "/indexedOUT.png");

        try {

            ImageWriter imageWriter = new ImageWriter(in, out);
            imageWriter.writeString(customMessage);
            ImageReader imageReader = new ImageReader(out);
            String decoded = imageReader.readString();

            assertEquals(customMessage, decoded);
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    @Test
    void readWriteJPGTest() {

        File in = new File(MainTest.resourceDirectory + "/jpeg.jpg");
        File out = new File(MainTest.resourceDirectory + "/jpegOUT.png");

        try {

            ImageWriter imageWriter = new ImageWriter(in, out);
            imageWriter.writeString(message);
            ImageReader imageReader = new ImageReader(out);
            String decoded = imageReader.readString();

            assertEquals(message, decoded);
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

}
