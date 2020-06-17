package org.imster.cli;

import org.imster.cryptography.CryptoDecrypter;
import org.imster.cryptography.CryptoEncrypter;
import org.imster.cryptography.CryptoException;
import org.imster.imageio.ImageReader;
import org.imster.imageio.ImageWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

/* CLI Interpreter */
public class Interpreter {

    public static boolean DEBUG_MODE = false;

    private static final int ENCODE_MODE = 0;
    private static final int DECODE_MODE = 1;

    private int mode = -1;
    private String input = null;
    private String output = null;
    private String message = null;
    private String password = null;

    private File inputFile = null;
    private File outputFile = null;

    private final String[] args;

    public Interpreter(String[] args) {
        this.args = args;
    }

    /* Parses all command line arguments */
    public void parse() throws IOException {
        setMode(args[0]);
        parseSwitches(args);
        parseFiles();
        checkModeDependencies();
    }

    /* Executes instructions based on operating mode */
    public String execute() throws CryptoException, IOException {

        if (mode == ENCODE_MODE) {
            CryptoEncrypter cryptoEncrypter = new CryptoEncrypter();
            String encryptedMessage = cryptoEncrypter.encryptString(message, password);
            ImageWriter imageWriter = new ImageWriter(inputFile, outputFile);
            imageWriter.writeString(encryptedMessage);
            if (!DEBUG_MODE)
                System.out.println("Successfully encoded message");
        } else if (mode == DECODE_MODE) {
            ImageReader imageReader = new ImageReader(inputFile);
            String decodedMessage = imageReader.readString();
            CryptoDecrypter cryptoDecrypter = new CryptoDecrypter();
            String decryptedMessage = cryptoDecrypter.decryptString(decodedMessage, password);
            if (!DEBUG_MODE)
                System.out.println(decryptedMessage);
            return decryptedMessage;
        }

        return null;

    }

    /* Checks for the specified mode argument and sets it */
    private void setMode(String arg0) throws IllegalArgumentException {

        if (arg0.toLowerCase().equals("encode")) {
            this.mode = ENCODE_MODE;
        } else if (arg0.toLowerCase().equals("decode")) {
            this.mode = DECODE_MODE;
        } else {
            throw new IllegalArgumentException("Expected encode/decode but got " + arg0);
        }

    }

    /* Parses all command line passed switches and their expected arguments */
    private void parseSwitches(String[] args) throws IllegalArgumentException {

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-i":
                    if (i + 1 > args.length)
                        throw new IllegalArgumentException("Expected argument after -i but none found");
                    input = args[i + 1];
                    break;
                case "-o":
                    if (i + 1 > args.length)
                        throw new IllegalArgumentException("Expected argument after -o but none found");
                    output = args[i + 1];
                    break;
                case "-m":
                    if (i + 1 > args.length)
                        throw new IllegalArgumentException("Expected argument after -m but none found");
                    message = args[i + 1];
                    break;
                case "-p":
                    if (i + 1 > args.length)
                        throw new IllegalArgumentException("Expected argument after -p but none found");
                    password = args[i + 1];
                    break;
            }
        }

    }

    /* Ensures that files/paths exist and can be used as needed by the application */
    private void parseFiles() throws IOException, IllegalArgumentException {

        if (input != null) {
            inputFile = new File(input);
            if (!inputFile.exists()) {
                throw new IOException("Specified input path/file doesn't exist");
            } else if (!inputFile.isFile()) {
                throw new IOException("Specified input path/file isn't a file");
            } else if (!inputFile.getName().endsWith(".png")) {
                throw new IOException("Specified input path/file does not have .png extension");
            } else if (!inputFile.canRead()) {
                throw new IOException("No read permission for specified input path/file");
            }
        } else {
            throw new IllegalArgumentException("Input image path not set. Use -i to set it");
        }

        if (output != null) {
            outputFile = new File(output);
            if (!isValidPath(output)) {
                throw new IOException("Specified output path is invalid");
            }
        } else if (mode == ENCODE_MODE) {
            throw new IllegalArgumentException("Output image path not set. Use -o to set it");
        }

    }

    /* Check mode dependent switches are set */
    private void checkModeDependencies() throws IllegalArgumentException {

        if (mode == ENCODE_MODE) {
            if (message == null)
                throw new IllegalArgumentException("Message not set. Set it with -m");
        }

        if (password == null)
            throw new IllegalArgumentException("Password not set. Set it with -p");

    }

    /* Tests if a specified path is valid regardless of the existence of the file */
    private static boolean isValidPath(String path) {
        try {
            Paths.get(path);
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
        return true;
    }

    /* Help function to show user usage. Called on argument errors */
    public static void printUsage() {
        System.out.println("\n\033[0;1mUSAGE");
        System.out.println("\n  ---------- Encoding ----------\033[0;0m\n");
        System.out.println("        java -jar ImSter-xxx.jar encode -i input.png -o output.png -m \"message\" -p password");
        System.out.println("\n  \033[0;1m---------- Decoding ----------\033[0;0m\n");
        System.out.println("        java -jar ImSter-xxx.jar decode -i input.png -p password\n");
    }

}
