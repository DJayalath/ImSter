import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import java.security.NoSuchAlgorithmException;

/* Class that manages setting up cryptographic resources */
public abstract class CryptoResource {

    protected static final int INIT_VECTOR_SIZE = 16;
    protected static final int SALT_SIZE = 8;
    protected static final int ITERATION_COUNT = 65536;
    protected static final int KEY_LENGTH = 256;

    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5PADDING";
    private static final String KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA256";
    protected static final String KEY_SPEC_ALGORITHM = "AES";
    protected static final String RANDOM_SECURE_ALGORITHM = "SHA1PRNG";

    protected final Cipher cipher;
    protected final SecretKeyFactory secretKeyFactory;

    /* Initialises cryptographic resources */
    public CryptoResource() throws CryptoException {
        try {
            cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            secretKeyFactory = SecretKeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("Algorithm not found for " + CIPHER_TRANSFORMATION);
        } catch (NoSuchPaddingException e) {
            throw new CryptoException("Padding scheme not found for " + CIPHER_TRANSFORMATION);
        }
    }

}
