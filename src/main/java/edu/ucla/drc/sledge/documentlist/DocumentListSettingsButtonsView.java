package edu.ucla.drc.sledge.documentlist;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.awt.event.MouseEvent;

public class DocumentListSettingsButtonsView {

    private VBox root;

    public DocumentListSettingsButtonsView (VBox root) {
        this.root = root;
    }

    public void initialize () {
        HBox box = new HBox();

        Button settingsButton = new Button("Settings");
        settingsButton.setOnMouseClicked((event) -> {
            System.out.println("Settings button clicked");
        });

        Button stopwordsButton = new Button("Stopwords");
        stopwordsButton.setOnMouseClicked((event) -> {
            System.out.println("Stopwords button clicked");
        });

        box.getChildren().addAll(settingsButton, stopwordsButton);

        // TODO: add to root

        this.root.getChildren().add(box);
    }
}
