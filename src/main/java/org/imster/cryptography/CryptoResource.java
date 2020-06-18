package org.imster.cryptography;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import java.security.NoSuchAlgorithmException;

/* Class that manages setting up cryptographic resources */
public abstract class CryptoResource {

    public static boolean USE_LEGACY_CBC = false;

    protected static final int INIT_VECTOR_SIZE = 16;
    protected static final int SALT_SIZE = 8;
    protected static final int ITERATION_COUNT = 65536;
    protected static final int KEY_LENGTH = 256;

    private static final String CIPHER_TRANSFORMATION_GCM = "AES/GCM/NoPadding";
    protected static final int GCM_INIT_VECTOR_SIZE = 12;
    protected static final int GCM_TAG_LENGTH = 16;

    private static final String CIPHER_TRANSFORMATION_CBC = "AES/CBC/PKCS5PADDING";
    private static final String KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA256";
    protected static final String KEY_SPEC_ALGORITHM = "AES";

    protected final Cipher cipher;
    protected final SecretKeyFactory secretKeyFactory;

    /* Initialises cryptographic resources */
    public CryptoResource() throws CryptoException {
        try {
            if (!USE_LEGACY_CBC) {
                cipher = Cipher.getInstance(CIPHER_TRANSFORMATION_GCM);
            } else {
                cipher = Cipher.getInstance(CIPHER_TRANSFORMATION_CBC);
            }
            secretKeyFactory = SecretKeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            CryptoException cryptoException = new CryptoException("Algorithm not found for cipher transformation");
            cryptoException.initCause(e);
            throw cryptoException;
        } catch (NoSuchPaddingException e) {
            CryptoException cryptoException = new CryptoException("Padding scheme not found for cipher transformation");
            cryptoException.initCause(e);
            throw cryptoException;
        }
    }

}
