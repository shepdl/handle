package edu.ucla.drc.sledge.documentimport;

import cc.mallet.types.Instance;
import edu.ucla.drc.sledge.Document;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.io.File;
import java.io.IOException;

public class DocumentList extends TreeView<Document> {

    private ObjectProperty<Document> selectedDocument;
    private ObservableList<Document> documents;

    private final TreeItem rootTreeItem = new TreeItem("Files");

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

    public void setData (ObservableList<Document> documents, ObjectProperty<Document> selectedDocument) {
        this.documents = documents;
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
            for (File file : db.getFiles()) {
                Document doc = new Document(file, null);
                documents.add(doc);
            }

            event.setDropCompleted(true);
            event.consume();
        });


    }
}
