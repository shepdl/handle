package edu.ucla.drc.sledge;

import cc.mallet.types.Instance;
import edu.ucla.drc.sledge.documentimport.*;
import edu.ucla.drc.sledge.documentimport.stopwords.*;
import edu.ucla.drc.sledge.documentimport.wordcounttable.WordCountTable;
import edu.ucla.drc.sledge.project.ProjectModel;
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

import java.io.File;
import java.io.IOException;

public class DocumentImport extends BorderPane {

    @FXML private DocumentListComponent documentList;

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
        documentList.initialize();
    }

    public void setModel (ProjectModel model) {
        this.model = model;
//        SimpleObjectProperty<Document> selectedDocument = new SimpleObjectProperty<>();
        SimpleObjectProperty<Instance> selectedDocument = new SimpleObjectProperty<>();
        documentList.setData(model, selectedDocument);
        documentView.setData(selectedDocument);
        countsTable.setData(model, selectedDocument);
    }

    @FXML
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

    @FXML
    public void showStopwordsPane (MouseEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("documentimport/stopwords/StopWordsDialogComponent.fxml"));
        try {
            Parent root = (Parent) loader.load();
            StopWordsDialogComponent controller = loader.<StopWordsDialogComponent>getController();
            StopwordListsSource source = new StopwordsDirectory(
                    new File(getClass().getResource("documentimport/stopwords/lists/").getPath())
            );
            controller.initialize(model, source);
//            controller.setModel(model);

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
