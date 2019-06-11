package edu.ucla.drc.sledge;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class SledgeAppRoot extends AnchorPane {

    @FXML DocumentImport documentImport;

    @FXML
    public void initialize () {

        SimpleObjectProperty<Document> selectedDocument = new SimpleObjectProperty<Document>();
        documentImport.setData(FXCollections.observableArrayList(), selectedDocument);

    }
}
