package edu.ucla.drc.sledge.documentimport;

import edu.ucla.drc.sledge.ImportFileSettings;
import edu.ucla.drc.sledge.project.ProjectModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import org.apache.xmlbeans.impl.xb.xsdschema.ImportDocument;

public class ImportSettingsDialog extends AnchorPane {

    private ProjectModel model;

    @FXML private ToggleGroup documentGroup;
    @FXML private RadioButton oneFileRadio;
    @FXML private RadioButton oneLineRadio;

    @FXML private ToggleGroup tokenGroup;
    @FXML private RadioButton justLettersRadio;
    @FXML private RadioButton allCharactersRadio;
    @FXML private CheckBox preserveCaseCheckBox;

    public void setModel (ProjectModel model) {
        this.model = model;
        ImportFileSettings settings = model.getImportFileSettings();
        Toggle selectedTokenToggle;
        if (settings.getTokenRegexPattern() == ImportFileSettings.defaultRegex) {
            selectedTokenToggle = justLettersRadio;
        } else {
            selectedTokenToggle = allCharactersRadio;
        }
        tokenGroup.selectToggle(selectedTokenToggle);

        preserveCaseCheckBox.setSelected(settings.preserveCase());
        documentGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                RadioButton selectedRadioButton = (RadioButton) newValue;
                if (selectedRadioButton.isSelected()) {
                    if (selectedRadioButton == oneLineRadio) {
                        settings.setIterationSchema(ImportFileSettings.DocumentIterationSchema.ONE_DOC_PER_LINE);
                    } else {
                        settings.setIterationSchema(ImportFileSettings.DocumentIterationSchema.ONE_DOC_PER_FILE);
                    }
                }
            }
        });

        tokenGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                RadioButton selectedButton = (RadioButton) newValue;
                if (selectedButton == justLettersRadio) {
                    settings.setTokenRegexPattern(ImportFileSettings.defaultRegex);
                } else {
                    settings.setTokenRegexPattern(ImportFileSettings.nonEnglishRegex);
                }
            }
        });
    }


    @FXML
    public void initialize () {

    }
}
