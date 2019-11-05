package edu.ucla.drc.sledge;

import edu.ucla.drc.sledge.project.ProjectExportBuilder;
import edu.ucla.drc.sledge.project.ProjectModel;
import edu.ucla.drc.sledge.topicmodeling.TopicModelsTab;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
        fileMenu.getItems().addAll(loadItem, saveItem);

        applicationMenu.getMenus().add(fileMenu);
    }

    public void newProject(ActionEvent actionEvent) {
    }

    public void loadProject(ActionEvent actionEvent) {
    }

    public void saveProject(ActionEvent actionEvent) {
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Handle Project", "*.hand");
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(filter);
        chooser.setTitle("Select file to save");
        File file = chooser.showSaveDialog(null);
        if (file != null) {
            ProjectExportBuilder builder = model.export();
//            builder.writeToFile(file);
            try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file))) {
                builder.writeObject(stream);
//                stream.writeObject(builder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
