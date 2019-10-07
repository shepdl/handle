package edu.ucla.drc.sledge;

import edu.ucla.drc.sledge.documentimport.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class DocumentImport extends BorderPane {

    @FXML private DocumentList documentList;

    @FXML public Button settingsButton;

    @FXML private DocumentTextView documentView;

    @FXML private WordCountTable countsTable;
    @FXML public Button stopwordsButton;

    private ProjectModel model;


    public DocumentImport () {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DocumentImport.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize () {
    }

    public void setModel (ProjectModel model) {
        this.model = model;
        SimpleObjectProperty<Document> selectedDocument = new SimpleObjectProperty<Document>();
        documentList.setData(model, selectedDocument);
        documentView.setData(selectedDocument);
        countsTable.setData(selectedDocument);
    }

    public void showSettingsPane (MouseEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("documentimport/ImportSettingsDialog.fxml"));
        try {
            Parent root = (Parent) loader.load();
            ImportSettingsDialog controller = loader.<ImportSettingsDialog>getController();
            controller.setModel(model);

            Scene scene = new Scene(root, 300, 200);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Document settings");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void showStopwordsPane (MouseEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("documentimport/StopwordsDialog.fxml"));
        try {
            Parent root = (Parent) loader.load();
            StopwordsDialog controller = loader.<StopwordsDialog>getController();
            controller.setModel(model);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Stopwords");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
