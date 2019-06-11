package edu.ucla.drc.sledge;

import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableObjectValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;

import javax.print.Doc;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class DocumentImport extends BorderPane {

    @FXML public TreeView documentListTree;
    @FXML public Button settingsButton;
    @FXML public TextArea documentViewTextArea;
    @FXML public TableView countsTable;
    @FXML public Button stopwordsButton;

    private ObservableList<Document> documents;
    private WritableObjectValue<Document> selectedDocument;


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

    public void setData (ObservableList<Document> documents, WritableObjectValue<Document> selectedDocument) {
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

        this.selectedDocument
    }

    private final TreeItem rootTreeItem = new TreeItem("Files");
    @FXML
    public void initialize () {
        System.out.println("Document Importer initialized");

        documentListTree.setRoot(rootTreeItem);
        documentListTree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        documentListTree.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue observable, Object oldValue, Object newValue) -> {
                    TreeItem<Document> selectedValue = (TreeItem<Document>)newValue;
                    selectedDocument.set(selectedValue.getValue());
                }
        );

        documentListTree.setOnDragOver((DragEvent event) -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        documentListTree.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            for (File file : db.getFiles()) {
                Document doc = new Document(file, null);
                documents.add(doc);
            }

            event.setDropCompleted(true);
            event.consume();
        });


    }

    public void showSettingsPane (MouseEvent event) {

    }

    public void showStopwordsPane (MouseEvent event) {

    }

}
