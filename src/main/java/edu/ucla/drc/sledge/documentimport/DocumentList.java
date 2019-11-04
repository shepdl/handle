package edu.ucla.drc.sledge.documentimport;

import edu.ucla.drc.sledge.Document;
import edu.ucla.drc.sledge.project.ProjectModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DocumentList extends TreeView<Document> {

    private ObjectProperty<Document> selectedDocument;
    private ObservableList<Document> documents;

    private final TreeItem rootTreeItem = new TreeItem("Files");
    private ProjectModel projectModel;

    public DocumentList () {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DocumentList.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setData (ProjectModel model, ObjectProperty<Document> selectedDocument) {
        this.projectModel = model;
        this.documents = model.getDocuments();
        this.selectedDocument = selectedDocument;

        this.documents.addListener((ListChangeListener.Change<? extends Document> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(doc -> {
                        TreeItem<Document> treeItem = new TreeItem<>(doc);
                        rootTreeItem.getChildren().add(treeItem);
                    });
                }
            }
        });

    }

    @FXML
    public void initialize () {
        this.setRoot(rootTreeItem);
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<Document>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<Document>> observable, TreeItem<Document> oldValue, TreeItem<Document> newValue) {

                TreeItem<Document> selectedValue = (TreeItem<Document>)newValue;
                selectedDocument.set(selectedValue.getValue());
            }
        });

        this.setOnDragOver((DragEvent event) -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        this.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ImportWarningDialog.fxml"));
            if (!projectModel.getTopicModels().isEmpty()) {
                Parent root;
                try {
                    root = (Parent)loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                ImportWarningDialog controller = loader.getController();
                Scene scene = new Scene(root, 300, 200);
                Stage stage = new Stage();
                controller.setCallbacks((nullEvent) -> {
                    // Clear topic model
                    this.projectModel.getTopicModels().clear();
                    // Add files
                    addFiles(db.getFiles(), event);
                    stage.hide();
                }, (nullEvent) -> stage.hide());
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Confirm erasing models");
                stage.setScene(scene);
                stage.show();
            } else {
                addFiles(db.getFiles(), event);
            }
        });

    }

    private void addFiles (List<File> files, DragEvent event) {
        for (File file : files) {
            Document doc = new Document(file);
            documents.add(doc);
        }

        event.setDropCompleted(true);
        event.consume();
    }
}
