package edu.ucla.drc.sledge.documentlist;

import edu.ucla.drc.sledge.ImportFileSettings;
import edu.ucla.drc.sledge.ProjectModel;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class DocumentListSettingsButtonsController {

    private ImportFileSettings projectModel;

    public DocumentListSettingsButtonsController (ImportFileSettings projectModel) {
        this.projectModel = projectModel;
    }

    public void initialize (VBox root) {
        DocumentListSettingsButtonsView view = new DocumentListSettingsButtonsView(root);
        view.initialize();
    }
}
