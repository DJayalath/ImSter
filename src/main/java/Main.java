import javafx.application.Application;
import javafx.application.Platform;
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

        View encodeView = new EncodeView(stage);
        View decodeView = new DecodeView(stage);

        rootPane.setCenter(encodeView.getRoot());

        encodeMode.setOnMouseClicked(e -> rootPane.setCenter(encodeView.getRoot()));
        decodeMode.setOnMouseClicked(e -> rootPane.setCenter(decodeView.getRoot()));

        Scene scene = new Scene(rootPane);
        stage.setScene(scene);

        stage.setTitle("ImSter");
        stage.setResizable(false);
        stage.show();
    }

}
