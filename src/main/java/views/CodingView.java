package views;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/* Class for abstract JavaFX views */
public abstract class CodingView {

    protected final Stage stage;
    protected File imageIn = null;

    public CodingView(Stage stage) {
        this.stage = stage;
    }

    /* Runs a file chooser to set input file */
    protected void runFileInputDialog(TextField target) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            imageIn = selectedFile;
            target.setText(imageIn.getName());
        }
    }

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
    protected void runIOExceptionAlert(IOException ioException, String coding) {
        Alert ioExceptionAlert = new Alert(Alert.AlertType.ERROR);
        ioExceptionAlert.setTitle("Failed to " + coding);
        ioExceptionAlert.setHeaderText(coding + " Failed");
        ioExceptionAlert.setContentText(ioException.getMessage());
        ioExceptionAlert.showAndWait();
    }

    /* Returns root node of view */
    public abstract Parent getRoot();

}
