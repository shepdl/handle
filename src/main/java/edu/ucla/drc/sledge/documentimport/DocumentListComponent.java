package edu.ucla.drc.sledge.documentimport;

import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import edu.ucla.drc.sledge.documents.Document;
import edu.ucla.drc.sledge.documents.DocumentFactory;
import edu.ucla.drc.sledge.documents.StubRootDocument;
import edu.ucla.drc.sledge.project.DocumentIterator;
import edu.ucla.drc.sledge.project.ProjectModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DocumentListComponent extends TreeView<Document> {

    private ObjectProperty<Instance> selectedDocument;
    private ObservableList<Document> documents;

    private final TreeItem<Document> rootTreeItem = new TreeItem<Document>(new StubRootDocument());

    private final DocumentFactory documentFactory = new DocumentFactory();

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
                        setShowRoot(false);
                        TreeItem<Document> treeItem = new TreeItem<>(doc);
                        rootTreeItem.getChildren().add(treeItem);
                    });
                } else if (c.wasRemoved()) {
                    selectedDocument.setValue(null);
                    rootTreeItem.getChildren().remove(getSelectionModel().getSelectedIndex());
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
                        setText(null);
                    } else {
                        setText(document.getName());
                    }
                }
            };

            final ContextMenu menu = new ContextMenu();
            final MenuItem removeDocumentItem = new MenuItem("Remove");
            menu.getItems().add(removeDocumentItem);
            removeDocumentItem.setOnAction(event -> {
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
            eraseModelsConfirmationBox = new Alert(Alert.AlertType.CONFIRMATION);
            eraseModelsConfirmationBox.setTitle("Erase topic models?");
            eraseModelsConfirmationBox.setHeaderText("Adding a new document will make your existing topic models invalid.");
            eraseModelsConfirmationBox.setContentText("Are you sure you want to add the new document and erase your topic models?");
            eraseModelsConfirmationBox.setOnCloseRequest((DialogEvent event) -> {
                if (eraseModelsConfirmationBox.getResult() == ButtonType.OK) {
                    projectModel.getTopicModels().clear();
                    addFiles(files);
                }
                eraseModelsConfirmationBox.hide();
            });
            eraseModelsConfirmationBox.show();
        }
    }

    private void addFiles(List<File> files) {
        List<String> invalidFiles = new ArrayList<>();
        for (File file : files) {
            if (!documentFactory.hasValidExtension(file)) {
                invalidFiles.add(file.getName());
                continue;
            }
            Document doc = documentFactory.adaptDocument(file);
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
