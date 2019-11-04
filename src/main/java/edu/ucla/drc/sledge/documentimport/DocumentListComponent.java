package edu.ucla.drc.sledge.documentimport;

import edu.ucla.drc.sledge.Document;
import edu.ucla.drc.sledge.project.ProjectModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DocumentListComponent extends TreeView<Document> {

    private ObjectProperty<Document> selectedDocument;
    private ObservableList<Document> documents;

    private final TreeItem rootTreeItem = new TreeItem("Files");
    private ProjectModel projectModel;
    Alert eraseModelsConfirmationBox;
    Alert invalidFilesBox = new Alert(Alert.AlertType.ERROR);

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
                selectedDocument.set(newValue.getValue());
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
            prepareToAddFiles(db.getFiles());
            event.setDropCompleted(true);
            event.consume();
        });
    }

    void prepareToAddFiles (List<File> files) {
        if (projectModel.getTopicModels().isEmpty()) {
            addFiles(files);
        } else {
            eraseModelsConfirmationBox = new Alert(
                    Alert.AlertType.CONFIRMATION
            );
            eraseModelsConfirmationBox.setTitle("Erase topic models?");
            eraseModelsConfirmationBox.setHeaderText("Adding a new document will make your existing topic models invalid.");
            eraseModelsConfirmationBox.setContentText("Are you sure you want to add the new document and erase your topic models?");
//            Optional<ButtonType> result = eraseModelsConfirmationBox.showAndWait();
//            if (result.isPresent() && result.get() == ButtonType.OK) {
//                projectModel.getTopicModels().clear();
//                addFiles(files);
//            }
            eraseModelsConfirmationBox.getDialogPane().lookupButton(ButtonType.OK).setOnMouseClicked((MouseEvent event) -> {
                event.consume();
                projectModel.getTopicModels().clear();
                addFiles(files);
            });
            eraseModelsConfirmationBox.show();
        }
    }

    private boolean hasValidExtension (File file) {
        String[] extensions = file.getName().split("\\.");
        String lastExtension = extensions[extensions.length - 1];
        switch (lastExtension) {
            case "text":
            case "txt":
            case "doc":
            case "docx":
                return true;
            default:
                return false;
        }
    }

    private void addFiles(List<File> files) {
        List<String> invalidFiles = new ArrayList<>();
        for (File file : files) {
            if (!hasValidExtension(file)) {
                invalidFiles.add(file.getName());
                continue;
            }
            Document doc = new Document(file);
            documents.add(doc);
        }

        if (!invalidFiles.isEmpty()) {
            invalidFilesBox.setTitle("Invalid Files");
            invalidFilesBox.setHeaderText("Invalid file types added");
            invalidFilesBox.setContentText(String.join("\n", invalidFiles));
            invalidFilesBox.show();
        }
    }

}
