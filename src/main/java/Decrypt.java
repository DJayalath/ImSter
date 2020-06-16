import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Decrypt {

    private final String plaintext;

    public Decrypt(String ciphertext, String key) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        StringBuilder keyBuilder = new StringBuilder(key);
        while (keyBuilder.length() < 16)
            keyBuilder.append(" ");
        key = keyBuilder.toString();

        IvParameterSpec ivParameterSpec = new IvParameterSpec("qwertyuiopasdfgh".getBytes());
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

        byte[] original = cipher.doFinal(Base64.getDecoder().decode(ciphertext));

        this.plaintext = new String(original, StandardCharsets.UTF_8);

    }

    public String getPlaintext() {
        return plaintext;
    }

}
