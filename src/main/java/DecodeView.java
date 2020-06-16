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

/* GUI view for decoding images */
public class DecodeView extends View {

    private final GridPane root;

    private File imageIn = null;

    /* Sets up decode view */
    public DecodeView(Stage stage) {

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

        TextArea outputTextArea = new TextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setWrapText(true);

        root.add(outputTextArea, 0, 1);

        Button startButton = new Button("START");
        root.add(startButton, 0, 2);

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

                    String password = result.get();

                    ImageReader imageReader = new ImageReader(imageIn);
                    String decodedMessage = imageReader.readString();

                    CryptoDecrypter cryptoDecrypter = new CryptoDecrypter();
                    String decryptedMessage = cryptoDecrypter.decryptString(decodedMessage, password);

                    outputTextArea.setText(decryptedMessage);

                    Alert decodeSuccessAlert = new Alert(Alert.AlertType.INFORMATION);
                    decodeSuccessAlert.setTitle("Decoding Complete");
                    decodeSuccessAlert.setHeaderText("Decoding Complete");
                    decodeSuccessAlert.setContentText("Successfully decoded message in image");
                    decodeSuccessAlert.showAndWait();
                } else {
                    throw new IOException("Password not set");
                }

            } catch (IOException ioException) {

                Alert ioExceptionAlert = new Alert(Alert.AlertType.ERROR);
                ioExceptionAlert.setTitle("Failed to Decode");
                ioExceptionAlert.setHeaderText("Decoding Failed");
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
