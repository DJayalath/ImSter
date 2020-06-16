package views;

import cryptography.CryptoEncrypter;
import cryptography.CryptoException;
import imageio.ImageWriter;
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

/* GUI view for encoding images */
public class EncodeView extends CodingView {

    private final GridPane root;

    private File imageOut = null;

    /* Sets up encode view */
    public EncodeView(Stage stage) {

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

        inputSelectionPane.add(imageInLabel, 0, 0, 2, 1);
        inputSelectionPane.add(imageInText, 0, 1);
        inputSelectionPane.add(imageInSelectButton, 1, 1);

        TextField imageOutText = new TextField();
        imageOutText.setDisable(true);

        Label imageOutLabel = new Label("Output selection");
        imageOutLabel.setFont(new Font(12.0));

        Button imageOutSelectButton = new Button("SELECT");

        GridPane outputSelectionPane = new GridPane();
        outputSelectionPane.setAlignment(Pos.CENTER_RIGHT);
        outputSelectionPane.setVgap(2);
        outputSelectionPane.setHgap(10);

        outputSelectionPane.add(imageOutLabel, 0, 0, 2, 1);
        outputSelectionPane.add(imageOutText, 0, 1);
        outputSelectionPane.add(imageOutSelectButton, 1, 1);

        root.add(inputSelectionPane, 0, 0);
        root.add(outputSelectionPane, 1, 0);

        imageInSelectButton.setOnMouseClicked(e -> runFileInputDialog(imageInText));

        imageOutSelectButton.setOnMouseClicked(e -> {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image File");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PNG Files", "*.png"));
            fileChooser.setInitialFileName("*.png");
            File selectedFile = fileChooser.showSaveDialog(stage);

            if (selectedFile != null) {

                if(selectedFile.getName().endsWith(".png")) {
                    imageOut = selectedFile;
                    imageOutText.setText(imageOut.getName());
                } else {
                    Alert extensionAlert = new Alert(Alert.AlertType.ERROR);
                    extensionAlert.setTitle("Failed to set output");
                    extensionAlert.setHeaderText("Failed to set output path");
                    extensionAlert.setContentText("File extension was not .png");
                    extensionAlert.showAndWait();
                }
            }

        });

        TextArea inputTextArea = new TextArea();
        inputTextArea.setPromptText("Enter message here");
        inputTextArea.setWrapText(true);

        root.add(inputTextArea, 0, 1, 2, 1);

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

        startButton.setOnMouseClicked(e ->  {
            try {

                Dialog<String> passDialog = new Dialog<>();
                passDialog.setTitle("Set Password");
                passDialog.setHeaderText("Set Encryption Password");
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

                    Task<Void> encodeTask = new Task<>() {

                        @Override
                        protected Void call() throws IOException, CryptoException {

                            updateProgress(0.1, 1.0);
                            String password = result.get();
                            updateProgress(0.2, 1.0);

                            CryptoEncrypter cryptoEncrypter = new CryptoEncrypter();
                            updateProgress(0.4, 1.0);

                            String encryptedMessage = cryptoEncrypter.encryptString(inputTextArea.getText(), password);
                            updateProgress(0.6, 1.0);

                            ImageWriter imageWriter = new ImageWriter(imageIn, imageOut);
                            updateProgress(0.8, 1.0);

                            imageWriter.writeString(encryptedMessage);
                            updateProgress(1.0, 1.0);

                            return null;
                        }
                    };

                    progressBar.progressProperty().bind(encodeTask.progressProperty());

                    new Thread(encodeTask).start();

                    encodeTask.setOnRunning(t -> {
                        root.setDisable(true);
                        progressBar.setDisable(false);
                    });

                    encodeTask.setOnFailed(t -> {
                        root.setDisable(false);
                        progressBar.progressProperty().unbind();
                        progressBar.setProgress(0);
                        progressBar.setDisable(true);
                        Throwable ex = encodeTask.getException();
                        if (ex instanceof IOException) {
                            runIOExceptionAlert((IOException) ex, "Encode");
                        } else if (ex instanceof CryptoException) {
                            handleFatalException(ex);
                        }
                    });

                    encodeTask.setOnSucceeded(t -> {
                        root.setDisable(false);
                        progressBar.progressProperty().unbind();
                        progressBar.setProgress(1.0);
                        Alert encodeSuccessAlert = new Alert(Alert.AlertType.INFORMATION);
                        encodeSuccessAlert.setTitle("Encoding Complete");
                        encodeSuccessAlert.setHeaderText("Encoding Complete");
                        encodeSuccessAlert.setContentText("Successfully wrote message in image");
                        encodeSuccessAlert.showAndWait();
                        progressBar.setProgress(0);
                        progressBar.setDisable(true);
                    });

                } else {
                    throw new IOException("Password not set");
                }

            } catch (IOException ioException) {

                runIOExceptionAlert(ioException, "Encode");

            }

        });

    }

    /* Returns root node of view */
    @Override
    public Parent getRoot() {
        return root;
    }

}
