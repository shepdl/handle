package edu.ucla.drc.sledge;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ucla.drc.sledge.project.ProjectModel;
import edu.ucla.drc.sledge.topicmodeling.TopicModelsTab;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


public class SledgeAppRoot extends AnchorPane {

    @FXML private DocumentImport documentImport;
    @FXML private TopicModelsTab topicModels;
    @FXML private MenuBar applicationMenu;

    private ProjectModel model;

    @FXML
    public void initialize () {

//        setupMenus();
        model = ProjectModel.blank();
//        documentImport.setData(model.getDocuments(), selectedDocument);
        documentImport.setModel(model);
        topicModels.setModel(model);
    }

    private void setupMenus () {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        menuBar.useSystemMenuBarProperty().set(true);
        MenuItem loadItem = new MenuItem("Load Project");
        MenuItem saveItem = new MenuItem("Save Project");
        MenuItem quitItem = new MenuItem("Quit");
        fileMenu.getItems().addAll(loadItem, saveItem, quitItem);

        applicationMenu.getMenus().add(fileMenu);
    }

    public void newProject(ActionEvent actionEvent) {
    }

    public void loadProject(ActionEvent actionEvent) {
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Handle Project", "*.hand");
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(filter);
        chooser.setTitle("Select file to open");
        File file = chooser.showOpenDialog(null);
        IOHelper ioHelper = new IOHelper();
        if (file != null) {
            try {
                ProjectModel model = ioHelper.loadModelFromFile(file);
                this.model = model;
                documentImport.setModel(model);
                topicModels.setModel(model);
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error importing file");
                alert.setContentText("Could not import the file");
                alert.showAndWait();
            } catch (IOHelper.InvalidFileFormatException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error parsing file");
                alert.setContentText("Could not parse file");
                alert.showAndWait();
            }
        }
    }

    public void saveProject(ActionEvent actionEvent) {
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Handle Project", "*.hand");
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(filter);
        chooser.setTitle("Select file to save");
        File file = chooser.showSaveDialog(null);
        IOHelper ioHelper = new IOHelper();
        if (file != null) {
            try {
                ioHelper.saveModelToFile(model, file);
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error saving file");
                alert.setContentText("Error saving file");
                alert.showAndWait();
            }
        }
    }

    public void quit (ActionEvent event) {
        Platform.exit();
    }
}
