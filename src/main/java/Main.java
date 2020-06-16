import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class Main extends Application {

    public static File image = null;
    public static File outImage = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        BorderPane rootPane = new BorderPane();
        rootPane.setPadding(new Insets(10, 20, 10, 20));

        GridPane topMenuPane = new GridPane();
        topMenuPane.setAlignment(Pos.TOP_CENTER);
        topMenuPane.setHgap(10);
        topMenuPane.setVgap(10);

        ToggleButton encodeMode = new ToggleButton("ENCODE");
        encodeMode.setSelected(true);
        ToggleButton decodeMode = new ToggleButton("DECODE");
        ToggleGroup modeGroup = new ToggleGroup();
        encodeMode.setToggleGroup(modeGroup);
        decodeMode.setToggleGroup(modeGroup);

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);
        buttonBox.getChildren().addAll(encodeMode, decodeMode);

        topMenuPane.add(buttonBox, 0, 0);

        Separator topSeparator = new Separator();
        topMenuPane.add(topSeparator, 0, 1);

        BorderPane.setAlignment(topMenuPane, Pos.TOP_CENTER);
        BorderPane.setMargin(topMenuPane, new Insets(0, 0, 10, 0));
        rootPane.setTop(topMenuPane);

        topSeparator.prefWidthProperty().bind(rootPane.widthProperty());

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.TOP_CENTER);
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        TextField openFile = new TextField();
        openFile.setDisable(true);
        Label inputLabel = new Label("Input selection");
        inputLabel.setFont(new Font(12.0));
        Button open = new Button("SELECT");

        GridPane inputSelectionPane = new GridPane();
        inputSelectionPane.setAlignment(Pos.CENTER_LEFT);
        inputSelectionPane.setVgap(2);
        inputSelectionPane.setHgap(10);
        inputSelectionPane.add(inputLabel, 0, 0, 2, 1);
        inputSelectionPane.add(openFile, 0, 1);
        inputSelectionPane.add(open, 1, 1);

        TextField openOut = new TextField();
        openOut.setDisable(true);
        Label outLabel = new Label("Output selection");
        outLabel.setFont(new Font(12.0));
        Button openOutButton = new Button("SELECT");

        GridPane outputSelectionPane = new GridPane();
        outputSelectionPane.setAlignment(Pos.CENTER_RIGHT);
        outputSelectionPane.setVgap(2);
        outputSelectionPane.setHgap(10);
        outputSelectionPane.add(outLabel, 0, 0, 2, 1);
        outputSelectionPane.add(openOut, 0, 1);
        outputSelectionPane.add(openOutButton, 1, 1);

        gridPane.add(inputSelectionPane, 0, 0);
        gridPane.add(outputSelectionPane, 1, 0);

        open.setOnMouseClicked(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image File");
            fileChooser.getExtensionFilters().add(new ExtensionFilter("PNG Files", "*.png"));
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                image = selectedFile;
                openFile.setText(image.getName());
                System.out.println("File selected: " + image.getAbsolutePath());
            } else {
                image = null;
            }
        });

        openOutButton.setOnMouseClicked(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image File");
            fileChooser.getExtensionFilters().add(new ExtensionFilter("PNG Files", "*.png"));
            File selectedFile = fileChooser.showSaveDialog(stage);
            if (selectedFile != null) {
                outImage = selectedFile;
                openOut.setText(outImage.getName());
                System.out.println("File selected: " + outImage.getAbsolutePath());
            } else {
                outImage = null;
            }
        });

        TextArea inputText = new TextArea();
        inputText.setPromptText("Enter message here");

        gridPane.add(inputText, 0, 1, 2, 1);

        Button encodeButton = new Button("START");
        gridPane.add(encodeButton, 0, 2);

        encodeButton.setOnMouseClicked(e -> {
            try {
                Dialog<String> passDialog = new Dialog<>();
                passDialog.setTitle("Set Password");
                passDialog.setHeaderText("Set Encryption Password");
                passDialog.setContentText("Password:");
                //passDialog.setGraphic();
                passDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
                PasswordField pwd = new PasswordField();
                HBox content = new HBox();
                content.setAlignment(Pos.CENTER_LEFT);
                content.setSpacing(10);
                content.getChildren().addAll(new Label("Password:"), pwd);
                passDialog.getDialogPane().setContent(content);
                passDialog.setResultConverter(dialogButton -> {
                    if (dialogButton == ButtonType.OK) {
                        return pwd.getText();
                    }
                    return null;
                });

                Optional<String> result = passDialog.showAndWait();
                String text;
                if (result.isPresent()) {
                    try {
                        CryptoEncrypter encrypter = new CryptoEncrypter();
                        text = encrypter.encryptString(inputText.getText(), result.get());
                    } catch (Exception encryptError) {
                        encryptError.printStackTrace();
                        throw new Exception(encryptError.getMessage());
                    }
                } else {
                    throw new IOException("Password not set");
                }

                ImageWriter imageWriter = new ImageWriter(image, outImage);
                imageWriter.writeString(text);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Encoding Complete");
                alert.setHeaderText("Encoding Complete");
                alert.setContentText("Successfully wrote message in image");
                alert.showAndWait();
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Failed to Encode");
                alert.setHeaderText("Encoding Failed");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });

        rootPane.setCenter(gridPane);

        GridPane decodePane = new GridPane();
        decodePane.setAlignment(Pos.TOP_CENTER);
        decodePane.setVgap(10);
        decodePane.setHgap(10);

        GridPane decodeInputPane = new GridPane();
        decodeInputPane.setAlignment(Pos.CENTER_LEFT);
        decodeInputPane.setVgap(2);
        decodeInputPane.setHgap(10);

        TextField dOpenFile = new TextField();
        dOpenFile.setDisable(true);
        Label dInputLabel = new Label("Input selection");
        dInputLabel.setFont(new Font(12.0));
        Button dOpen = new Button("SELECT");

        decodeInputPane.add(dInputLabel, 0, 0);
        decodeInputPane.add(dOpenFile, 0, 1);
        decodeInputPane.add(dOpen, 1, 1);

        decodePane.add(decodeInputPane, 0, 0);

        TextArea outputText = new TextArea();
        outputText.setEditable(false);

        decodePane.add(outputText, 0, 1);

        Button decodeButton = new Button("START");
        decodePane.add(decodeButton, 0, 2);

        dOpen.setOnMouseClicked(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image File");
            fileChooser.getExtensionFilters().add(new ExtensionFilter("PNG Files", "*.png"));
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                image = selectedFile;
                dOpenFile.setText(image.getName());
                System.out.println("File selected: " + image.getAbsolutePath());
            } else {
                image = null;
            }
        });

        decodeButton.setOnMouseClicked(e -> {
            try {
                Dialog<String> passDialog = new Dialog<>();
                passDialog.setTitle("Enter Password");
                passDialog.setHeaderText("Enter Decryption Password");
                passDialog.setContentText("Password:");
                //passDialog.setGraphic();
                passDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
                PasswordField pwd = new PasswordField();
                HBox content = new HBox();
                content.setAlignment(Pos.CENTER_LEFT);
                content.setSpacing(10);
                content.getChildren().addAll(new Label("Password:"), pwd);
                passDialog.getDialogPane().setContent(content);
                passDialog.setResultConverter(dialogButton -> {
                    if (dialogButton == ButtonType.OK) {
                        return pwd.getText();
                    }
                    return null;
                });

                Optional<String> result = passDialog.showAndWait();
                String text;
                if (result.isPresent()) {
                    text = result.get();
                } else {
                    throw new IOException("Password not set");
                }

                ImageReader imageReader = new ImageReader(image);
                String decoded = imageReader.readString();

                String decryptedText;
                {
                    CryptoDecrypter decrypter = new CryptoDecrypter();
                    decryptedText = decrypter.decryptString(decoded, text);
                }

                outputText.setText(decryptedText);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Decoding Complete");
                alert.setHeaderText("Decoding Complete");
                alert.setContentText("Successfully decoded message in image");
                alert.showAndWait();
            } catch (Exception ioException) {
                ioException.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Failed to Decode");
                alert.setHeaderText("Decoding Failed");
                alert.setContentText(ioException.getMessage());
                alert.showAndWait();
            }
        });

        inputText.setWrapText(true);
        outputText.setWrapText(true);

        encodeMode.setOnMouseClicked(e -> rootPane.setCenter(gridPane));
        decodeMode.setOnMouseClicked(e -> rootPane.setCenter(decodePane));

        Scene scene = new Scene(rootPane);
        stage.setScene(scene);

        stage.setTitle("ImSter");
        stage.setResizable(false);
        stage.show();
    }

    public static void chooseImage(Stage stage) {

    }
}
