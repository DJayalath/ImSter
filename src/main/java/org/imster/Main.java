package org.imster;

import org.imster.cli.Interpreter;
import org.imster.cryptography.CryptoException;
import org.imster.views.CodingView;
import org.imster.views.TopView;

import java.io.IOException;

/* Application entry point */
public class Main {
    public static void main(String[] args) {

        for (String a : args)
            System.out.println(a);

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
                System.err.println("Error: " + e.getMessage());
                System.exit(1);
            } catch (CryptoException e) {
                CodingView.handleFatalException(e);
            }
        }
    }
}
