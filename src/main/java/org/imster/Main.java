package org.imster;

import javafx.application.Platform;
import org.imster.cli.Interpreter;
import org.imster.cryptography.CryptoException;
import org.imster.gui.MenuBar;

import java.io.IOException;

/* Application entry point */
public class Main {
    public static void main(String[] args) {

        // If no arguments provided, default to GUI
        if (args.length == 0)
            MenuBar.initialise(args);
        else {
            // Pass arguments to CLI interpreter
            try {
                Interpreter interpreter = new Interpreter(args);
                interpreter.parse();
                interpreter.execute();
            }  catch (IllegalArgumentException | IOException e) {
                System.err.println("\nERROR: " + e.getMessage());
                Interpreter.printUsage();
                System.exit(1);
            } catch (CryptoException e) {
                handleFatalException(e);
            }
        }
    }

    /* Handles fatal exceptions within Application thread */
    public static void handleFatalException(Throwable e) {
        System.err.println("FATAL ERROR: " + e.getMessage());
        if (e.getCause() != null)
            System.err.println("\nINIT CAUSE: " + e.getCause().getMessage());
        System.err.println("\n----- STACK TRACE -----");
        e.printStackTrace();
        Platform.exit();
        System.exit(1);
    }
}
