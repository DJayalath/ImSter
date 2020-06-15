import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    public static File image = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.TOP_CENTER);
        gridPane.setPadding(new Insets(10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        Label openFile = new Label();
        Button open = new Button("Open");

        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(0));
        hbox.getChildren().addAll(open, openFile);

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

        gridPane.add(hbox, 0, 0);

        TextArea inputText = new TextArea();
        inputText.setPromptText("Enter message here");

        gridPane.add(inputText, 0, 1);

        Scene scene = new Scene(gridPane, 600, 800);
        stage.setScene(scene);

        stage.setTitle("ImSter");
        stage.show();
    }

    public static void chooseImage(Stage stage) {

    }
}
