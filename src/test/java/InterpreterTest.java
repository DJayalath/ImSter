import org.imster.cli.Interpreter;
import org.imster.cryptography.CryptoException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {

    @BeforeAll
    static void setUp() {
        Interpreter.DEBUG_MODE = true;
    }

    @AfterAll
    static void tearDown() {
        Interpreter.DEBUG_MODE = false;
    }

    @Test
    void cliMissingPasswordSwitch() {

        String[] args = new String[]{"decode", "-i", MainTest.resourceDirectory + "/rgb.png"};

        Interpreter interpreter = new Interpreter(args);

        assertThrows(IllegalArgumentException.class, interpreter::parse);
    }

    @Test
    void cliMissingModeDependency() {
        String password = "test password";

        String[] args = new String[]{"encode", "-i", MainTest.resourceDirectory + "/rgb.png", "-o",
                MainTest.resourceDirectory + "/cliOUT.png", "-p", password};

        Interpreter interpreter = new Interpreter(args);

        assertThrows(IllegalArgumentException.class, interpreter::parse);
    }

    @Test
    void cliInvalidFile() {

        String password = "test password";

        String[] args = new String[]{"decode", "-i", MainTest.resourceDirectory + "/missing.png", "-p", password};

        Interpreter interpreter = new Interpreter(args);

        assertThrows(IOException.class, interpreter::parse);

    }

    @Test
    void cliNoSwitchArg() {
        String message = "test message";

        String[] args = new String[]{"encode", "-i", MainTest.resourceDirectory + "/rgb.png", "-o",
                MainTest.resourceDirectory + "/cliOUT.png", "-m", message, "-p"};

        Interpreter interpreter = new Interpreter(args);

        assertThrows(IllegalArgumentException.class, interpreter::parse);
    }

    @Test
    void cliIllegalMode() {
        String message = "test message";
        String password = "test password";

        String[] args = new String[]{"gibberish", "-i", MainTest.resourceDirectory + "/rgb.png", "-o",
                MainTest.resourceDirectory + "/cliOUT.png", "-m", message, "-p", password};

        Interpreter interpreter = new Interpreter(args);

        assertThrows(IllegalArgumentException.class, interpreter::parse);
    }

    @Test
    void cliMissingDecodeSwitch() {
        String password = "test password";

        String[] args = new String[]{"decode", MainTest.resourceDirectory + "/cliOUT.png", "-p", password};

        Interpreter interpreter = new Interpreter(args);

        assertThrows(IllegalArgumentException.class, interpreter::parse);

    }

    @Test
    void cliMissingEncodeSwitch() {
        String message = "test message";
        String password = "test password";

        String[] args = new String[]{"encode", "-i", MainTest.resourceDirectory + "/rgb.png",
                MainTest.resourceDirectory + "/cliOUT.png", "-m", message, "-p", password};

        Interpreter interpreter = new Interpreter(args);

        assertThrows(IllegalArgumentException.class, interpreter::parse);

    }

    @Test
    void cliEncodeDecodeTest() {

        String message = "test message";
        String password = "test password";

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

    }

}
