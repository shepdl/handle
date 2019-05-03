package edu.ucla.drc.sledge.documentlist;

import edu.ucla.drc.sledge.Document;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableObjectValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;

import java.io.File;

public class DocumentListView {

    private ObservableList<Document> documents;
    private WritableObjectValue<Document> selectedDocument;

    public DocumentListView(ObservableList<Document> documents, WritableObjectValue<Document> selectedDocument) {
        this.documents = documents;
        this.selectedDocument = selectedDocument;
    }

    public void initialize (VBox parent) {
        final TreeView documentListTree = new TreeView();

        final TreeItem rootTreeItem = new TreeItem("Files");
        documentListTree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        documentListTree.setShowRoot(false);

        documentListTree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
//                selectedDocument.set(newValue);
            }
        });

        documents.addListener(new ListChangeListener<Document>() {
            @Override
            public void onChanged(Change<? extends Document> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        c.getAddedSubList().forEach(doc -> {
                            TreeItem<Document> treeItem = new TreeItem<Document>(doc);
                            rootTreeItem.getChildren().add(treeItem);
                        });
                    }
                }
            }
        });

        documentListTree.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                if (event.getDragboard().hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            }
        });

        documentListTree.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();

                for (File file : db.getFiles()) {
                    Document doc = new Document(file, null);
                    documents.add(doc);
                }

                event.setDropCompleted(true);
                event.consume();
            }
        });

        documentListTree.setRoot(rootTreeItem);
        parent.getChildren().add(documentListTree);
    }
}
