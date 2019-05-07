package edu.ucla.drc.sledge.importsettings;

import edu.ucla.drc.sledge.ProjectModel;
import javafx.stage.Stage;

public class ImportSettingsController {

    private ImportSettingsDialog view;
    private ImportSettingsModel settings;
    private SaveHandler saveHandler;

    public ImportSettingsController (ImportSettingsModel settings) {
        this.settings = settings;
    }

    public void initialize () {
        view = new ImportSettingsDialog(settings, this);
        view.initialize();
    }

    public void show () {
        view.show();
    }

    public interface SaveHandler {
        public void onSave (ImportSettingsModel model);
    }

    public void setOnComplete (SaveHandler handler) {
        this.saveHandler = handler;
    }

    public void save () {
        this.saveHandler.onSave(settings);
    }

}
