import org.imster.cli.Interpreter;
import org.imster.cryptography.CryptoException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class InterpreterTest {

    @Test
    void cliEncodeDecodeTest() {

        String message = "test message";
        String password = "test password";

        Interpreter.DEBUG_MODE = true;

        String[] args = new String[]{"encode", "-i", MainTest.resourceDirectory + "/rgb.png", "-o",
                MainTest.resourceDirectory + "/cliOUT.png", "-m", message, "-p", password};

        Interpreter interpreter = new Interpreter(args);
        try {
            interpreter.parse();
            interpreter.execute();
        } catch (IOException | CryptoException exception) {
            fail(exception.getMessage());
        }

        args = new String[]{"decode", "-i", MainTest.resourceDirectory + "/cliOUT.png", "-p", password};

        interpreter = new Interpreter(args);
        try {
            interpreter.parse();
            String decrypted = interpreter.execute();

            assertEquals(decrypted, message);

        } catch (IOException | CryptoException exception) {
            fail(exception.getMessage());
        }

        Interpreter.DEBUG_MODE = false;
    }

}
