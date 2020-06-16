import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/* GUI view for encoding images */
public class EncodeView extends View {

    private final GridPane root;

    private File imageIn = null;
    private File imageOut = null;

    /* Sets up encode view */
    public EncodeView(Stage stage) {

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

        imageInSelectButton.setOnMouseClicked(e -> {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image File");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PNG Files", "*.png"));
            File selectedFile = fileChooser.showOpenDialog(stage);

            if (selectedFile != null) {
                imageIn = selectedFile;
                imageInText.setText(imageIn.getName());
            }

        });

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
        root.add(startButton, 0, 2);

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

                    String password = result.get();

                    CryptoEncrypter cryptoEncrypter = new CryptoEncrypter();
                    String encryptedMessage = cryptoEncrypter.encryptString(inputTextArea.getText(), password);

                    ImageWriter imageWriter = new ImageWriter(imageIn, imageOut);
                    imageWriter.writeString(encryptedMessage);

                    Alert encodeSuccessAlert = new Alert(Alert.AlertType.INFORMATION);
                    encodeSuccessAlert.setTitle("Encoding Complete");
                    encodeSuccessAlert.setHeaderText("Encoding Complete");
                    encodeSuccessAlert.setContentText("Successfully wrote message in image");
                    encodeSuccessAlert.showAndWait();

                } else {
                    throw new IOException("Password not set");
                }

            } catch (IOException ioException) {

                Alert ioExceptionAlert = new Alert(Alert.AlertType.ERROR);
                ioExceptionAlert.setTitle("Failed to Encode");
                ioExceptionAlert.setHeaderText("Encoding Failed");
                ioExceptionAlert.setContentText(ioException.getMessage());
                ioExceptionAlert.showAndWait();

            } catch (CryptoException cryptoException) {
                handleFatalException(cryptoException);
            }
        });

    }

    /* Returns root node of view */
    @Override
    public Parent getRoot() {
        return root;
    }

}
