package edu.ucla.drc.sledge.documentlist;

import edu.ucla.drc.sledge.ImportFileSettings;
import edu.ucla.drc.sledge.importsettings.ImportSettingsController;
import edu.ucla.drc.sledge.importsettings.ImportSettingsModel;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DocumentListSettingsButtonsView {

    private ImportFileSettings settings;
    private VBox root;

    public DocumentListSettingsButtonsView(ImportFileSettings settings, VBox root) {
        this.settings = settings;
        this.root = root;
    }

    public void initialize () {
        HBox box = new HBox();

        Button settingsButton = new Button("Settings");
        settingsButton.setOnMouseClicked((event) -> {
            ImportSettingsController controller = new ImportSettingsController(ImportSettingsModel.fromImportSettings(settings));
            controller.setOnComplete((updatedSettings) -> {
                this.settings.updateFrom(updatedSettings.toSettings());
            });
            controller.initialize();
            controller.show();
        });

        Button stopwordsButton = new Button("Stopwords");
        stopwordsButton.setOnMouseClicked((event) -> {
            System.out.println("Stopwords button clicked");
        });

        box.getChildren().addAll(settingsButton, stopwordsButton);

        this.root.getChildren().add(box);
    }
}
