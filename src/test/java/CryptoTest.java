import org.imster.cryptography.CryptoDecrypter;
import org.imster.cryptography.CryptoEncrypter;
import org.imster.cryptography.CryptoException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CryptoTest {

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
