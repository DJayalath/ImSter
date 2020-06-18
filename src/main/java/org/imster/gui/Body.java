package org.imster.gui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.imster.Main;
import org.imster.cryptography.CryptoDecrypter;
import org.imster.cryptography.CryptoEncrypter;
import org.imster.imageio.ImageReader;
import org.imster.imageio.ImageWriter;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/* Class for abstract JavaFX views */
public class Body {

    private static final int ROOT_V_GAP = 10;
    private static final int ROOT_H_GAP = 10;

    private static final float SELECTION_LABEL_SIZE = 12.f;
    private static final int SELECTION_V_GAP = 2;
    private static final int SELECTION_H_GAP = 10;

    private final Stage stage;
    private final GridPane outputSelectionPane;
    private final GridPane root;

    private final TextArea textArea;
    private final ProgressBar progressBar;

    private boolean encodeMode = false;

    private File imageIn = null;
    private File imageOut = null;

    /* Sets up basic view in encode mode */
    public Body(Stage stage) {

        this.stage = stage;

        root = new GridPane();
        root.setAlignment(Pos.TOP_CENTER);
        root.setVgap(ROOT_V_GAP);
        root.setHgap(ROOT_H_GAP);

        GridPane inputSelectionPane = buildSelectionPane(
                "Input selection", Pos.CENTER_LEFT, this::runFileInputDialog);

        outputSelectionPane = buildSelectionPane(
                "Output selection", Pos.CENTER_RIGHT, this::runFileOutputDialog);

        textArea = new TextArea();

        progressBar = new ProgressBar(0);

        HBox bottomBar = buildBottomBar();

        // Permanent features
        root.add(inputSelectionPane, 0, 0);
        root.add(textArea, 0, 1, 2, 1);
        root.add(bottomBar, 0, 2, 2, 1);

        // Start in encode mode
        encodeMode();
    }

    /* Returns root node of this view */
    public GridPane getRoot() {
        return root;
    }

    /* Transitions view to encode mode */
    public void encodeMode() {
        if (!encodeMode) {
            root.add(outputSelectionPane, 1, 0);
            textArea.clear();
            textArea.setEditable(true);
            textArea.setPromptText("Enter message here");
            encodeMode = true;
        }
    }

    /* Transitions view to decode mode */
    public void decodeMode() {
        if (encodeMode) {
            root.getChildren().remove(outputSelectionPane);
            textArea.clear();
            textArea.setEditable(false);
            textArea.setPromptText(null);
            encodeMode = false;
        }
    }

    /* Runs the operation dictated by the mode (encode/decode) */
    private void runStartTask() {

        // Run the password dialog and wait for a password
        Optional<String> passwordQuery = runPassDialog();

        if (passwordQuery.isPresent()) {

            String password = passwordQuery.get();

            Task<Void> startTask;
            if (!encodeMode) {

                // Setup task for decoding

                startTask = new Task<Void>() {

                    @Override
                    protected Void call() throws Exception {

                        updateProgress(0.2, 1.0);

                        ImageReader imageReader = new ImageReader(imageIn);
                        updateProgress(0.4, 1.0);

                        String decodedMessage = imageReader.readString();
                        updateProgress(0.6, 1.0);

                        CryptoDecrypter cryptoDecrypter = new CryptoDecrypter();
                        updateProgress(0.8, 1.0);

                        String decryptedMessage = cryptoDecrypter.decryptString(decodedMessage, password);
                        textArea.setText(decryptedMessage);
                        updateProgress(1.0, 1.0);

                        return null;
                    }
                };
            } else {

                // Setup task for encoding

                startTask = new Task<Void>() {

                    @Override
                    protected Void call() throws Exception {

                        updateProgress(0.2, 1.0);

                        CryptoEncrypter cryptoEncrypter = new CryptoEncrypter();
                        updateProgress(0.4, 1.0);

                        String encryptedMessage = cryptoEncrypter.encryptString(textArea.getText(), password);
                        updateProgress(0.6, 1.0);

                        ImageWriter imageWriter = new ImageWriter(imageIn, imageOut);
                        updateProgress(0.8, 1.0);

                        imageWriter.writeString(encryptedMessage);
                        updateProgress(1.0, 1.0);

                        return null;
                    }
                };

            }

            // Set start and end case behaviour
            startTask.setOnRunning(e -> onStart(startTask));
            startTask.setOnSucceeded(e -> onSucceed());
            startTask.setOnFailed(e -> onFail(startTask));

            // Start the task
            new Thread(startTask).start();

        } else {
            runIOExceptionAlert(new IOException("Password not entered"));
        }

    }

    /* Task start behaviour */
    private void onStart(Task<Void> task) {
        progressBar.progressProperty().bind(task.progressProperty());
        root.setDisable(true);
        progressBar.setDisable(false);
    }

    /* Task fail behaviour */
    private void onFail(Task<Void> task) {
        root.setDisable(false);
        progressBar.progressProperty().unbind();
        progressBar.setProgress(0);
        progressBar.setDisable(true);
        Throwable ex = task.getException();
        if (ex instanceof IOException) {
            runIOExceptionAlert((IOException) ex);
        } else {
            Main.handleFatalException(ex);
        }
    }

    /* Task success behaviour */
    private void onSucceed() {
        root.setDisable(false);
        progressBar.progressProperty().unbind();
        progressBar.setProgress(1.0);
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        if (encodeMode) {
            successAlert.setTitle("Encoding Complete");
            successAlert.setHeaderText("Encoding Complete");
            successAlert.setContentText("Successfully wrote message in image");
        } else {
            successAlert.setTitle("Decoding Complete");
            successAlert.setHeaderText("Decoding Complete");
            successAlert.setContentText("Successfully decoded message in image");
        }
        successAlert.showAndWait();
        progressBar.setProgress(0);
        progressBar.setDisable(true);
    }

    /* Queries user for a password through a dialog */
    protected Optional<String> runPassDialog() {

        Dialog<String> passDialog = new Dialog<>();
        passDialog.setTitle("Password Dialog");
        passDialog.setHeaderText("Enter Password");
        passDialog.setContentText("Password:");
        passDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        PasswordField pwd = new PasswordField();
        Platform.runLater(pwd::requestFocus);

        HBox content = new HBox();
        content.setAlignment(Pos.CENTER_LEFT);
        content.setSpacing(10);
        content.getChildren().addAll(new Label("Password:"), pwd);

        passDialog.getDialogPane().setContent(content);

        passDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK && !pwd.getText().isEmpty()) {
                return pwd.getText();
            }
            return null;
        });

        return passDialog.showAndWait();
    }

    /* Builds the bottom bar of the application */
    protected HBox buildBottomBar() {

        HBox bottomButtonBox = new HBox();
        bottomButtonBox.setAlignment(Pos.CENTER);
        bottomButtonBox.setSpacing(10);

        Button startButton = new Button("START");
        startButton.setOnMouseClicked(e -> runStartTask());

        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setDisable(true);

        HBox.setHgrow(progressBar, Priority.ALWAYS);
        bottomButtonBox.getChildren().addAll(startButton, progressBar);

        return bottomButtonBox;
    }

    /* Builds a generic file selection pane */
    protected GridPane buildSelectionPane(String label, Pos alignment, FileDialogRunner clickFunction) {
        Label selectionLabel = new Label(label);
        selectionLabel.setFont(new Font(SELECTION_LABEL_SIZE));

        TextField selectedText = new TextField();
        selectedText.setDisable(true);

        Button selectButton = new Button("SELECT");
        selectButton.setOnMouseClicked(e -> clickFunction.runFileDialog(selectedText));

        GridPane selectionPane = new GridPane();
        selectionPane.setAlignment(alignment);
        selectionPane.setVgap(SELECTION_V_GAP);
        selectionPane.setHgap(SELECTION_H_GAP);

        selectionPane.add(selectionLabel, 0, 0, 2, 1);
        selectionPane.add(selectedText, 0, 1);
        selectionPane.add(selectButton, 1, 1);

        return selectionPane;
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

    /* Runs a file chooser to set output file */
    private void runFileOutputDialog(TextField target) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        fileChooser.setInitialFileName("*.png");
        File selectedFile = fileChooser.showSaveDialog(stage);

        if (selectedFile != null) {

            if(selectedFile.getName().endsWith(".png")) {
                imageOut = selectedFile;
                target.setText(imageOut.getName());
            } else {
                Alert extensionAlert = new Alert(Alert.AlertType.ERROR);
                extensionAlert.setTitle("Failed to set output");
                extensionAlert.setHeaderText("Failed to set output path");
                extensionAlert.setContentText("File extension was not .png");
                extensionAlert.showAndWait();
            }
        }
    }

    /* Runs salvageable IO exception dialog */
    protected void runIOExceptionAlert(IOException ioException) {
        Alert ioExceptionAlert = new Alert(Alert.AlertType.ERROR);
        String coding = (encodeMode) ? "Encode" : "Decode";
        ioExceptionAlert.setTitle(coding + "Failure");
        ioExceptionAlert.setHeaderText(coding + " Failed");
        ioExceptionAlert.setContentText(ioException.getMessage());
        ioExceptionAlert.showAndWait();
    }

}
