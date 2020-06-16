import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Encrypt {

    private final String ciphertext;

    public Encrypt(String plaintext, String key) throws IOException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        StringBuilder keyBuilder = new StringBuilder(key);
        while (keyBuilder.length() < 16)
            keyBuilder.append(" ");
        key = keyBuilder.toString();

        if (plaintext == null)
            throw new IOException("No message entered");

        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

        IvParameterSpec ivParameterSpec = new IvParameterSpec("qwertyuiopasdfgh".getBytes());

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

        byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        this.ciphertext = Base64.getEncoder().encodeToString(encrypted);
    }

    public String getCiphertext() {
        return ciphertext;
    }

}
