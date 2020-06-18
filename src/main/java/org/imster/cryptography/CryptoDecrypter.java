package org.imster.cryptography;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

/* Class that manages AES String-based password decryption of plaintext strings */
public class CryptoDecrypter extends CryptoResource {

    /* Initialises cryptographic resources */
    public CryptoDecrypter() throws CryptoException {
        super();
    }

    /* Decrypts plaintext string with password */
    public String decryptString(String ciphertext, String password) throws IOException, CryptoException {

        if (ciphertext.isEmpty())
            throw new IOException("Message is blank");

        if (password.isEmpty())
            throw new IOException("Missing password to decrypt");

        try {

            // B64 decode the ciphertext
            final byte[] decoded = Base64.getDecoder().decode(ciphertext);

            // Extract salt fragment
            final byte[] salt = new byte[SALT_SIZE];
            System.arraycopy(decoded, 0, salt, 0, SALT_SIZE);

            final char[] passwordCharArray = password.toCharArray();

            // Derive key given password and salt
            KeySpec spec = new PBEKeySpec(passwordCharArray, salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKey tmp = secretKeyFactory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), KEY_SPEC_ALGORITHM);

            final byte[] encryptedMessage;


            if (!USE_LEGACY_CBC) {

                // Extract message
                encryptedMessage = new byte[decoded.length - SALT_SIZE - GCM_INIT_VECTOR_SIZE];
                System.arraycopy(decoded, SALT_SIZE + GCM_INIT_VECTOR_SIZE, encryptedMessage, 0,
                        decoded.length - SALT_SIZE - GCM_INIT_VECTOR_SIZE);

                // Extract iv
                final byte[] iv = new byte[GCM_INIT_VECTOR_SIZE];
                System.arraycopy(decoded, SALT_SIZE, iv, 0, GCM_INIT_VECTOR_SIZE);

                GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);

                cipher.init(Cipher.DECRYPT_MODE, secret, gcmParameterSpec);


            } else {

                // Extract the message fragment
                encryptedMessage = new byte[decoded.length - SALT_SIZE - INIT_VECTOR_SIZE];
                System.arraycopy(decoded, SALT_SIZE + INIT_VECTOR_SIZE, encryptedMessage, 0,
                        decoded.length - SALT_SIZE - INIT_VECTOR_SIZE);

                // Extract the initialisation vector fragment
                final byte[] iv = new byte[INIT_VECTOR_SIZE];
                System.arraycopy(decoded, SALT_SIZE, iv, 0, INIT_VECTOR_SIZE);

                IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

                cipher.init(Cipher.DECRYPT_MODE, secret, ivParameterSpec);

            }

            // Base64 decode ciphertext and then decrypt
            byte[] decrypted = cipher.doFinal(encryptedMessage);

            // Return decrypted message
            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (InvalidKeyException e) {
            CryptoException cryptoException = new CryptoException("Invalid key supplied");
            cryptoException.initCause(e);
            throw cryptoException;
        } catch (InvalidAlgorithmParameterException e) {
            CryptoException cryptoException = new CryptoException(
                    "Invalid algorithm parameters supplied (Check initialisation vector");
            cryptoException.initCause(e);
            throw cryptoException;
        } catch (IllegalBlockSizeException e) {
            throw new IOException("Illegal block size detected. Disable legacy mode and try again.", e);
        } catch (BadPaddingException e) {
            throw new IOException("Invalid Password. If correct, perhaps the image you are trying to decode was encoded in legacy mode.", e);
        } catch (InvalidKeySpecException e) {
            CryptoException cryptoException = new CryptoException("Invalid key specification supplied");
            cryptoException.initCause(e);
            throw cryptoException;
        }

    }

}
