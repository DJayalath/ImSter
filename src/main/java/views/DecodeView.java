package views;

import cryptography.CryptoDecrypter;
import cryptography.CryptoException;
import imageio.ImageReader;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/* GUI view for decoding images */
public class DecodeView extends View {

    private final GridPane root;

    /* Sets up decode view */
    public DecodeView(Stage stage) {

        super(stage);

        root = new GridPane();

        root.setAlignment(Pos.TOP_CENTER);
        root.setVgap(10);
        root.setHgap(10);

        TextField imageInText = new TextField();
        imageInText.setDisable(true);

        Label imageInLabel = new Label("Input selection");
        imageInLabel.setFont(new Font(12.0));

        Button imageInSelectButton = new Button("SELECT");

        GridPane inputSelectionPane = new GridPane();
        inputSelectionPane.setAlignment(Pos.CENTER_LEFT);
        inputSelectionPane.setVgap(2);
        inputSelectionPane.setHgap(10);

        inputSelectionPane.add(imageInLabel, 0, 0);
        inputSelectionPane.add(imageInText, 0, 1);
        inputSelectionPane.add(imageInSelectButton, 1, 1);

        root.add(inputSelectionPane, 0, 0);

        imageInSelectButton.setOnMouseClicked(e -> runFileInputDialog(imageInText));

        TextArea outputTextArea = new TextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setWrapText(true);

        root.add(outputTextArea, 0, 1, 2, 1);

        Button startButton = new Button("START");

        HBox progressBox = new HBox();
        progressBox.setAlignment(Pos.CENTER);
        progressBox.setSpacing(10);

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setDisable(true);

        HBox.setHgrow(progressBar, Priority.ALWAYS);
        progressBox.getChildren().addAll(startButton, progressBar);

        root.add(progressBox, 0, 2, 2, 1);

        startButton.setOnMouseClicked(e -> {
            try {

                Dialog<String> passDialog = new Dialog<>();
                passDialog.setTitle("Enter Password");
                passDialog.setHeaderText("Enter Decryption Password");
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

                Optional<String> result = passDialog.showAndWait();
                if (result.isPresent()) {

                    Task<Void> decodeTask = new Task<>() {

                        @Override
                        protected Void call() throws IOException, CryptoException {

                            updateProgress(0.1, 1.0);
                            String password = result.get();
                            updateProgress(0.2, 1.0);

                            ImageReader imageReader = new ImageReader(imageIn);
                            updateProgress(0.4, 1.0);

                            String decodedMessage = imageReader.readString();
                            updateProgress(0.6, 1.0);

                            CryptoDecrypter cryptoDecrypter = new CryptoDecrypter();
                            updateProgress(0.8, 1.0);

                            String decryptedMessage = cryptoDecrypter.decryptString(decodedMessage, password);
                            outputTextArea.setText(decryptedMessage);
                            updateProgress(1.0, 1.0);

                            return null;
                        }
                    };

                    progressBar.progressProperty().bind(decodeTask.progressProperty());

                    new Thread(decodeTask).start();

                    decodeTask.setOnRunning(t -> {
                        root.setDisable(true);
                        progressBar.setDisable(false);
                    });

                    decodeTask.setOnFailed(t -> {
                        root.setDisable(false);
                        progressBar.progressProperty().unbind();
                        progressBar.setProgress(0);
                        progressBar.setDisable(true);
                        Throwable ex = decodeTask.getException();
                        if (ex instanceof IOException) {
                            runIOExceptionAlert((IOException) ex, "Decode");
                        } else if (ex instanceof CryptoException) {
                            handleFatalException(ex);
                        }
                    });

                    decodeTask.setOnSucceeded(t -> {
                        root.setDisable(false);
                        progressBar.progressProperty().unbind();
                        progressBar.setProgress(1.0);
                        Alert decodeSuccessAlert = new Alert(Alert.AlertType.INFORMATION);
                        decodeSuccessAlert.setTitle("Decoding Complete");
                        decodeSuccessAlert.setHeaderText("Decoding Complete");
                        decodeSuccessAlert.setContentText("Successfully decoded message in image");
                        decodeSuccessAlert.showAndWait();
                        progressBar.setProgress(0);
                        progressBar.setDisable(true);
                    });

                } else {
                    throw new IOException("Password not set");
                }

            } catch (IOException ioException) {

                Alert ioExceptionAlert = new Alert(Alert.AlertType.ERROR);
                ioExceptionAlert.setTitle("Failed to Decode");
                ioExceptionAlert.setHeaderText("Decoding Failed");
                ioExceptionAlert.setContentText(ioException.getMessage());
                ioExceptionAlert.showAndWait();

            }
        });

    }

    /* Returns root node of view */
    @Override
    public Parent getRoot() {
        return root;
    }
}
