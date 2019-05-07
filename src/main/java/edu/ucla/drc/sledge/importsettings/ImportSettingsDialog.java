package edu.ucla.drc.sledge.importsettings;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ImportSettingsDialog {

    private final ImportSettingsModel settings;
    private ImportSettingsController controller;
    private ImportSettingsController.SaveHandler saveHandler;
    private Stage parentStage;

    public ImportSettingsDialog(ImportSettingsModel settings, ImportSettingsController controller) {
        this.settings = settings;
        this.controller = controller;
    }

    public void initialize() {
    }

    public void show() {

        VBox root = new VBox();

        Label oneDocLabel = new Label("One document is ...");
        ToggleGroup group = new ToggleGroup();
        RadioButton oneFileButton = new RadioButton("One file");
        oneFileButton.setSelected(true);
        oneFileButton.setToggleGroup(group);
        RadioButton oneLineButton = new RadioButton("One line in one file");
        oneLineButton.setToggleGroup(group);

        Label oneWordLabel = new Label("One word is ...");


        ToggleGroup wordGroup = new ToggleGroup();

        RadioButton justLetters = new RadioButton("Just letters");
        justLetters.setToggleGroup(wordGroup);

        RadioButton allCharacters = new RadioButton("All characters between spaces");
        allCharacters.setToggleGroup(wordGroup);

        if (settings.getWordSettings() == ImportSettingsModel.WordSettings.JustLetters) {
            justLetters.setSelected(true);
        } else {
            allCharacters.setSelected(true);
        }

        CheckBox internalPunctuation = new CheckBox("Internal punctuation");
        CheckBox numbers = new CheckBox("Numbers");
        CheckBox allCharactersBetweenSpaces = new CheckBox("All characters between spaces");


        CheckBox preserveCase = new CheckBox("Preserve capital letters");
        preserveCase.setSelected(settings.getPreserveCase());

        Scene scene = new Scene(root);
        Stage dialog = new Stage();
        dialog.setScene(scene);

        Button saveButton = new Button("Save");
        saveButton.setOnMouseClicked(event -> {
            settings.setPreserveCase(preserveCase.isSelected());
            if (justLetters.isSelected()) {
                settings.setWordSettings(ImportSettingsModel.WordSettings.JustLetters);
            } else {
                settings.setWordSettings(ImportSettingsModel.WordSettings.AllCharacters);
            }
            this.controller.save();
            dialog.close();
        });

        root.getChildren().addAll(
                oneDocLabel, oneFileButton, oneLineButton,
                oneWordLabel, justLetters, allCharacters,
//                justLetters, allCharactersBetweenSpaces,
                preserveCase,
                saveButton
        );

        dialog.initOwner(parentStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
    }
}
