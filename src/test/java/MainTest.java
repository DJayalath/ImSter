import cryptography.CryptoDecrypter;
import cryptography.CryptoEncrypter;
import imageio.ImageReader;
import imageio.ImageWriter;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class MainTest {

    CryptoEncrypter cryptoEncrypter;
    CryptoDecrypter cryptoDecrypter;
    ImageReader imageReader;
    ImageWriter imageWriter;
    String resourceDirectory = "src/test/resources";

    @Test
    void writeReadImage() {

        String message = "Hello there. General KENOBI!";
        String pass = "You are a bold one!";

        try {
            cryptoEncrypter = new CryptoEncrypter();
            imageWriter = new ImageWriter(new File(resourceDirectory + "/bobby.png"), new File(resourceDirectory + "/bobbyEncrypted.png"));
            String encrypted = cryptoEncrypter.encryptString(message, pass);
            imageWriter.writeString(encrypted);
            imageReader = new ImageReader(new File(resourceDirectory + "/bobbyEncrypted.png"));
            String decoded = imageReader.readString();
            cryptoDecrypter = new CryptoDecrypter();
            String decrypted = cryptoDecrypter.decryptString(decoded, pass);
            assertEquals(message, decrypted);
        } catch (Exception e) {
            fail();
        }


    }

}