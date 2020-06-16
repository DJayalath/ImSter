import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.security.NoSuchAlgorithmException;

/* Class that manages setting up cryptographic resources */
public abstract class CryptoResource {

    // 16-byte initialisation vector for cipher (TODO: Make this more secure)
    private static final String INIT_VECTOR = "qwertyuiopasdfgh";

    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5PADDING";
    protected final IvParameterSpec ivParameterSpec;
    protected final Cipher cipher;

    /* Initialises cryptographic resources */
    public CryptoResource() throws CryptoException {
        try {
            ivParameterSpec = new IvParameterSpec(INIT_VECTOR.getBytes());
            cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("Algorithm not found for " + CIPHER_TRANSFORMATION);
        } catch (NoSuchPaddingException e) {
            throw new CryptoException("Padding scheme not found for " + CIPHER_TRANSFORMATION);
        }
    }

    /* Pads a password to 16 bytes with spaces */
    protected String padPasswordTo16Bytes(String password) {
        StringBuilder keyBuilder = new StringBuilder(password);
        while (keyBuilder.length() < 16)
            keyBuilder.append(" ");
        return keyBuilder.toString();
    }

}
