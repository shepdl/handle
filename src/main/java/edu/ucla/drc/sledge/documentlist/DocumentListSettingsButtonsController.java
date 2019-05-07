package edu.ucla.drc.sledge.documentlist;

import edu.ucla.drc.sledge.ImportFileSettings;
import javafx.scene.layout.VBox;

public class DocumentListSettingsButtonsController {

    private ImportFileSettings importFileSettings;

    public DocumentListSettingsButtonsController (ImportFileSettings importFileSettings) {
        this.importFileSettings = importFileSettings;
    }

    public void initialize (VBox root) {
        DocumentListSettingsButtonsView view = new DocumentListSettingsButtonsView(importFileSettings, root);
        view.initialize();
    }
}
