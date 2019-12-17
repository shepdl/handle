package edu.ucla.drc.sledge;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ucla.drc.sledge.project.ProjectExportBuilder;
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
        if (file != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                ProjectExportBuilder builder = mapper.readValue(file, ProjectExportBuilder.class);
                model = builder.toModel();
                System.out.println(model.getStopwords());
                System.out.println(model.getDocuments());
                documentImport.setModel(model);
                topicModels.setModel(model);
            } catch (JsonParseException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error parsing file");
                alert.setContentText("Could not parse file");
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error importing file");
                alert.setContentText("Could not import the file");
                e.printStackTrace();
            }
        }
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
            ProjectModel.Exporter exporter = new ProjectExportBuilder.ProjectModelBuilderToJson();
            model.exportTo(exporter);
            try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file))) {
                ObjectMapper mapper = new ObjectMapper();
//                mapper.writerWithDefaultPrettyPrinter().writeValue(file, model.export());
//                mapper = new ObjectMapper();
                mapper.writerWithDefaultPrettyPrinter().writeValue(file, exporter);
//                builder.writeObject(stream);
//                stream.writeObject(builder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void quit (ActionEvent event) {
        Platform.exit();
    }
}
