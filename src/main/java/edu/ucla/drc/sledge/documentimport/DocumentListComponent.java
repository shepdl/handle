package edu.ucla.drc.sledge.documentimport;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import edu.ucla.drc.sledge.documents.Document;
import edu.ucla.drc.sledge.documents.DocumentFactory;
import edu.ucla.drc.sledge.project.DocumentIterator;
import edu.ucla.drc.sledge.project.ProjectModel;
import javafx.beans.binding.Bindings;
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

import javax.naming.Context;
import javax.print.Doc;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DocumentListComponent extends TreeView<Document> {

    private ObjectProperty<Instance> selectedDocument;
    private ObservableList<Document> documents;

    private final TreeItem rootTreeItem = new TreeItem("Files");
    private ProjectModel projectModel;
    Alert eraseModelsConfirmationBox;
    Alert invalidFilesBox = new Alert(Alert.AlertType.ERROR);

    public void setData (ProjectModel model, ObjectProperty<Instance> selectedDocument) {
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

        setCellFactory((TreeView<Document> treeView) -> {
            final TreeCell<Document> cell = new TreeCell<Document>() {
                @Override
                protected void updateItem (Document document, boolean empty) {
                    super.updateItem(document, empty);
                    if (document == null || empty) {
                        setGraphic(null);
                    } else {
                        setText(document.getName());
                    }
                }
            };

            final ContextMenu menu = new ContextMenu();
            final MenuItem removeDocumentItem = new MenuItem("Remove");
            menu.getItems().add(removeDocumentItem);
            removeDocumentItem.setOnAction(event -> {
                treeView.getSelectionModel().getSelectedItem();
                treeView.getRoot().getChildren().remove(treeView.getSelectionModel().getSelectedItem());
                model.getDocuments().remove(cell.getItem());
            });
            cell.contextMenuProperty().bind(
                    Bindings.when(cell.emptyProperty()).then((ContextMenu)null).otherwise(menu)
            );

            return cell;
        });
    }

    @FXML
    public void initialize () {
        this.setRoot(rootTreeItem);
        this.setShowRoot(false);
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<Document>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<Document>> observable, TreeItem<Document> oldValue, TreeItem<Document> newValue) {
                List<Document> tempDocs = new ArrayList<>();
                tempDocs.add(newValue.getValue());
                InstanceList instances = new InstanceList(projectModel.getPipe());
                instances.addThruPipe(new DocumentIterator(tempDocs));
                selectedDocument.set(instances.get(0));
//                selectedDocument.set(newValue.getValue());
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
            DocumentFactory factory = new DocumentFactory();
            Document doc = factory.adaptDocument(file);
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
