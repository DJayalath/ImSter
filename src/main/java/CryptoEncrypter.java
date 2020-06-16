import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Base64;

/* Class that manages AES String-based password encryption of plaintext strings */
public class CryptoEncrypter extends CryptoResource {

    /* Initialises cryptographic resources */
    public CryptoEncrypter() throws CryptoException {
        super();
    }

    /* Encrypts plaintext string with password */
    public String encryptString(String plaintext, String password) throws IOException, CryptoException {

        if (plaintext.isEmpty())
            throw new IOException("Missing message to encrypt");


        String key = padPasswordTo16Bytes(password);

        try {

            // Initialise key and cipher
            byte[] iv = cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            // Encrypt plaintext
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // Copy initialisation vector to front
            byte[] encryptedWithIV = new byte[encrypted.length + INIT_VECTOR_SIZE];
            System.arraycopy(iv, 0, encryptedWithIV, 0, INIT_VECTOR_SIZE);
            System.arraycopy(encrypted, 0, encryptedWithIV, INIT_VECTOR_SIZE, encrypted.length);

            // Return Base64 encoded ciphertext
            return Base64.getEncoder().encodeToString(encryptedWithIV);

        } catch (InvalidKeyException e) {
            CryptoException cryptoException = new CryptoException("Invalid key supplied");
            cryptoException.initCause(e);
            throw cryptoException;
        } catch (InvalidAlgorithmParameterException e) {
            CryptoException cryptoException = new CryptoException(
                    "Invalid algorithm parameters supplied (Check initialisation vector");
            cryptoException.initCause(e);
            throw cryptoException;
        } catch (InvalidParameterSpecException e) {
            CryptoException cryptoException = new CryptoException(
                    "Invalid cipher parameters supplied (Check initialisation vector");
            cryptoException.initCause(e);
            throw cryptoException;
        } catch (IllegalBlockSizeException e) {
            CryptoException cryptoException = new CryptoException("Plaintext has illegal block size");
            cryptoException.initCause(e);
            throw cryptoException;
        } catch (BadPaddingException e) {
            CryptoException cryptoException = new CryptoException("Plaintext has pad padding");
            cryptoException.initCause(e);
            throw cryptoException;
        }

    }

}
