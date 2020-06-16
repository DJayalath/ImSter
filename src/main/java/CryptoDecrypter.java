import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.Base64;

/* Class that manages AES String-based password decryption of plaintext strings */
public class CryptoDecrypter extends CryptoResource {

    /* Initialises cryptographic resources */
    public CryptoDecrypter() throws CryptoException {
        super();
    }

    /* Decrypts plaintext string with password */
    public String decryptString(String ciphertext, String password) throws IOException, CryptoException {

        if (ciphertext == null)
            throw new IOException("Missing message to decrypt");

        String key = padPasswordTo16Bytes(password);

        // Initialise key and cipher
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        } catch (InvalidKeyException e) {
            CryptoException cryptoException = new CryptoException("Invalid key supplied");
            cryptoException.initCause(e);
            throw cryptoException;
        } catch (InvalidAlgorithmParameterException e) {
            CryptoException cryptoException = new CryptoException(
                    "Invalid algorithm parameters supplied (Check initialisation vector");
            cryptoException.initCause(e);
            throw cryptoException;
        }

        // Decrypt ciphertext
        try {

            // Base64 decode ciphertext and then decrypt
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(ciphertext));

            // Return decrypted message
            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (IllegalBlockSizeException e) {
            CryptoException cryptoException = new CryptoException("Plaintext has illegal block size");
            cryptoException.initCause(e);
            throw cryptoException;
        } catch (BadPaddingException e) {
            throw new IOException("Invalid Password", e);
        }

    }

}
