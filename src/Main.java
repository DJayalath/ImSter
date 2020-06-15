import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Main extends Application {

    public static File image = null;
    public static File outImage = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        BorderPane rootPane = new BorderPane();
        rootPane.setPadding(new Insets(10, 20, 20, 20));

        HBox topMenu = new HBox();
        topMenu.setAlignment(Pos.CENTER);
        topMenu.setSpacing(10);
        topMenu.setPadding(new Insets(0));
        ToggleButton encodeMode = new ToggleButton("Encode");
        encodeMode.setSelected(true);
        ToggleButton decodeMode = new ToggleButton("Decode");
        ToggleGroup modeGroup = new ToggleGroup();
        encodeMode.setToggleGroup(modeGroup);
        decodeMode.setToggleGroup(modeGroup);
        topMenu.getChildren().addAll(encodeMode, decodeMode);

        rootPane.setTop(topMenu);

        BorderPane.setMargin(topMenu, new Insets(10));

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.TOP_CENTER);
//        gridPane.setPadding(new Insets(10));
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

        Button encodeButton = new Button("ENCODE");
        gridPane.add(encodeButton, 0, 2);

        encodeButton.setOnMouseClicked(e -> {
            try {
                Writer writer = new Writer(image, outImage, inputText.getText());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Encoding Complete");
                alert.setHeaderText("Encoding Complete");
                alert.setContentText("Successfully wrote message in image");
                alert.showAndWait();
            } catch (IOException ioException) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Failed to Encode");
                alert.setHeaderText("Encoding Failed");
                alert.setContentText(ioException.getMessage());
                alert.showAndWait();
            }
        });

        rootPane.setCenter(gridPane);

        GridPane decodePane = new GridPane();

        encodeMode.setOnMouseClicked(e -> rootPane.setCenter(gridPane));
        decodeMode.setOnMouseClicked(e -> rootPane.setCenter(decodePane));

        Scene scene = new Scene(rootPane);
        stage.setScene(scene);

        stage.setTitle("ImSter");
        stage.show();
    }

    public static void chooseImage(Stage stage) {

    }
}
