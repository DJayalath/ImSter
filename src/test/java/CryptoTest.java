import org.imster.cryptography.CryptoDecrypter;
import org.imster.cryptography.CryptoEncrypter;
import org.imster.cryptography.CryptoException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class CryptoTest {

    @Test
    void missingPasswordTest() {
        try {
            CryptoEncrypter cryptoEncrypter = new CryptoEncrypter();
            assertThrows(IOException.class, () -> cryptoEncrypter.encryptString("1234", ""));
            CryptoDecrypter cryptoDecrypter = new CryptoDecrypter();
            assertThrows(IOException.class, () -> cryptoDecrypter.decryptString("1234", ""));
        } catch (CryptoException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void missingInputTest() {
        try {
            CryptoEncrypter cryptoEncrypter = new CryptoEncrypter();
            assertThrows(IOException.class, () -> cryptoEncrypter.encryptString("", "1234"));
            CryptoDecrypter cryptoDecrypter = new CryptoDecrypter();
            assertThrows(IOException.class, () -> cryptoDecrypter.decryptString("", "1234"));
        } catch (CryptoException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void invalidPasswordTest() {

        String message = "test message";
        String password = "test password";

        try {
            CryptoEncrypter cryptoEncrypter = new CryptoEncrypter();
            String encryptedString = cryptoEncrypter.encryptString(message, password);
            CryptoDecrypter cryptoDecrypter = new CryptoDecrypter();
            assertThrows(IOException.class, () -> cryptoDecrypter.decryptString(encryptedString, "wrong password"));
        } catch (CryptoException | IOException e) {
            fail(e.getMessage());
        }

    }

    @Test
    void encryptDecryptTest() {

        String message = "test message";
        String password = "test password";

        try {
            CryptoEncrypter cryptoEncrypter = new CryptoEncrypter();
            String encryptedString = cryptoEncrypter.encryptString(message, password);
            CryptoDecrypter cryptoDecrypter = new CryptoDecrypter();
            String decryptedMessage = cryptoDecrypter.decryptString(encryptedString, password);
            assertEquals(message, decryptedMessage);
        } catch (CryptoException | IOException e) {
            fail(e.getMessage());
        }

    }

}
