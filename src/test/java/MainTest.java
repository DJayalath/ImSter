import org.imster.cryptography.CryptoDecrypter;
import org.imster.cryptography.CryptoEncrypter;
import org.imster.imageio.ImageReader;
import org.imster.imageio.ImageWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class MainTest {

    CryptoEncrypter cryptoEncrypter;
    CryptoDecrypter cryptoDecrypter;
    ImageReader imageReader;
    ImageWriter imageWriter;
    static String resourceDirectory = "src/test/resources";
    static String password = "General Kenobi! You are a bold one.";
    static String message;

    @BeforeAll
    static void setUp() {
        try (Scanner scanner = new Scanner( new File(resourceDirectory + "/macbeth.txt"), "UTF-8" )) {
            message = scanner.useDelimiter("\\A").next();
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void writeReadImageRGB() {

        try {
            cryptoEncrypter = new CryptoEncrypter();
            imageWriter = new ImageWriter(new File(resourceDirectory + "/rgb.png"), new File(resourceDirectory + "/rgbOUT.png"));
            String encrypted = cryptoEncrypter.encryptString(message, password);
            imageWriter.writeString(encrypted);
            imageReader = new ImageReader(new File(resourceDirectory + "/rgbOUT.png"));
            String decoded = imageReader.readString();
            cryptoDecrypter = new CryptoDecrypter();
            String decrypted = cryptoDecrypter.decryptString(decoded, password);
            assertEquals(message, decrypted);
        } catch (Exception e) {
            fail(e.getMessage());
        }


    }

    @Test
    @Disabled
    void writeReadImageRGBA() {

        try {
            cryptoEncrypter = new CryptoEncrypter();
            imageWriter = new ImageWriter(new File(resourceDirectory + "/rgba.png"), new File(resourceDirectory + "/rgbaOUT.png"));
            String encrypted = cryptoEncrypter.encryptString(message, password);
            imageWriter.writeString(encrypted);
            imageReader = new ImageReader(new File(resourceDirectory + "/rgbaOUT.png"));
            String decoded = imageReader.readString();
            cryptoDecrypter = new CryptoDecrypter();
            String decrypted = cryptoDecrypter.decryptString(decoded, password);
            assertEquals(message, decrypted);
        } catch (Exception e) {
            fail(e.getMessage());
        }


    }

    @Test
    void writeReadImageGrayScale() {

        try {
            String message = MainTest.message.substring(0, MainTest.message.length() / 4);

            cryptoEncrypter = new CryptoEncrypter();
            imageWriter = new ImageWriter(new File(resourceDirectory + "/grayscale.png"), new File(resourceDirectory + "/grayscaleOUT.png"));
            String encrypted = cryptoEncrypter.encryptString(message, password);
            imageWriter.writeString(encrypted);
            imageReader = new ImageReader(new File(resourceDirectory + "/grayscaleOUT.png"));
            String decoded = imageReader.readString();
            cryptoDecrypter = new CryptoDecrypter();
            String decrypted = cryptoDecrypter.decryptString(decoded, password);
            assertEquals(message, decrypted);
        } catch (Exception e) {
            fail(e.getMessage());
        }


    }

    @Test
    @Disabled
    void writeReadImageInterlaced() {

        try {
            cryptoEncrypter = new CryptoEncrypter();
            imageWriter = new ImageWriter(new File(resourceDirectory + "/interlaced.png"), new File(resourceDirectory + "/interlacedOUT.png"));
            String encrypted = cryptoEncrypter.encryptString(message, password);
            imageWriter.writeString(encrypted);
            imageReader = new ImageReader(new File(resourceDirectory + "/interlacedOUT.png"));
            String decoded = imageReader.readString();
            cryptoDecrypter = new CryptoDecrypter();
            String decrypted = cryptoDecrypter.decryptString(decoded, password);
            assertEquals(message, decrypted);
        } catch (Exception e) {
            fail(e.getMessage());
        }


    }

}