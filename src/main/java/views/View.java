package views;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Alert;

import java.io.IOException;

/* Class for abstract JavaFX views */
public abstract class View {

    /* Handles fatal exceptions within Application thread */
    protected void handleFatalException(Throwable e) {
        System.out.println("FATAL ERROR: " + e.getMessage());
        System.out.println("\nINIT CAUSE: " + e.getCause().getMessage());
        System.out.println("\n----- STACK TRACE -----");
        e.printStackTrace();
        Platform.exit();
        System.exit(1);
    }

    /* Runs salvageable IO exception dialog */
    protected void runIOExceptionAlert(IOException ioException) {
        Alert ioExceptionAlert = new Alert(Alert.AlertType.ERROR);
        ioExceptionAlert.setTitle("Failed to Encode");
        ioExceptionAlert.setHeaderText("Encoding Failed");
        ioExceptionAlert.setContentText(ioException.getMessage());
        ioExceptionAlert.showAndWait();
    }

    /* Returns root node of view */
    public abstract Parent getRoot();

}
