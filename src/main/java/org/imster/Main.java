package org.imster;

import org.imster.cli.Interpreter;
import org.imster.cryptography.CryptoException;
import org.imster.views.CodingView;
import org.imster.views.TopView;

import java.io.IOException;

/* Application entry point */
public class Main {
    public static void main(String[] args) {

        // If no arguments provided, default to GUI
        if (args.length == 0)
            TopView.initialise(args);
        else {
            // Pass arguments to CLI interpreter
            try {
                Interpreter interpreter = new Interpreter(args);
                interpreter.parse();
                interpreter.execute();
            }  catch (IllegalArgumentException | IOException e) {
                System.err.println("ERROR: " + e.getMessage());
                Interpreter.printUsage();
                System.exit(1);
            } catch (CryptoException e) {
                CodingView.handleFatalException(e);
            }
        }
    }
}
