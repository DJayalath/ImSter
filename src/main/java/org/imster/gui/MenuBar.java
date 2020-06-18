package org.imster.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.imster.cryptography.CryptoResource;

/* GUI view for top menu and JavaFX entry point */
public class MenuBar extends Application {

    private static final String MAIN_WINDOW_TITLE = "ImSter v1.1.0";

    public static void initialise(String[] args) {
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

        /* Custom toggle button class ensures one member of the toggle group is always toggled on.
         A button can't be un-toggled by pressing it while toggled */
        class PersistentToggleButton extends ToggleButton {
            public PersistentToggleButton(String label) {
                super(label);
            }

            @Override
            public void fire() {
                // Don't un-toggle if selected
                if (getToggleGroup() == null || !isSelected()) {
                    super.fire();
                }
            }
        }

        ToggleButton encodeMode = new PersistentToggleButton("ENCODE");
        encodeMode.setSelected(true);
        ToggleButton decodeMode = new PersistentToggleButton("DECODE");
        ToggleGroup modeGroup = new ToggleGroup();
        modeGroup.selectToggle(encodeMode);
        encodeMode.setToggleGroup(modeGroup);
        decodeMode.setToggleGroup(modeGroup);

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setSpacing(10);
        buttonBox.getChildren().addAll(encodeMode, decodeMode);

        topMenuPane.add(buttonBox, 0, 0);

        CheckBox legacyBox = new CheckBox("Legacy Mode");
        legacyBox.selectedProperty().addListener((obs, oldVal, newVal) -> CryptoResource.USE_LEGACY_CBC = newVal);

        HBox legacyHBox = new HBox();
        legacyHBox.setAlignment(Pos.CENTER_RIGHT);
        legacyHBox.getChildren().add(legacyBox);

        topMenuPane.add(legacyHBox, 1, 0);

        Separator topSeparator = new Separator();
        topMenuPane.add(topSeparator, 0, 1, 2, 1);

        BorderPane.setAlignment(topMenuPane, Pos.TOP_CENTER);
        BorderPane.setMargin(topMenuPane, new Insets(0, 0, 10, 0));
        rootPane.setTop(topMenuPane);

        topSeparator.prefWidthProperty().bind(rootPane.widthProperty());

        Body body = new Body(stage);

        rootPane.setCenter(body.getRoot());

        encodeMode.setOnMouseClicked(e -> body.encodeMode());
        decodeMode.setOnMouseClicked(e -> body.decodeMode());

        Scene scene = new Scene(rootPane);
        stage.setScene(scene);

        stage.setTitle(MAIN_WINDOW_TITLE);
        stage.setResizable(false);

        stage.show();
    }

}
