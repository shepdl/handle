package edu.ucla.drc.sledge.documentimport;

import cc.mallet.types.FeatureSequence;
import cc.mallet.types.Instance;
import edu.ucla.drc.sledge.Document;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;

import java.io.IOException;

public class DocumentTextView extends TextArea {

    public DocumentTextView () {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DocumentTextView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setData (ObjectProperty<Document> selectedDocument) {
        selectedDocument.addListener((ObservableValue<? extends Document> observable, Document oldValue, Document newValue) -> {
            StringBuilder builder = new StringBuilder();
            Instance instance = newValue.getIngested();
            FeatureSequence fs = (FeatureSequence)instance.getData();
            for (int i = 0; i < fs.getFeatures().length; i++) {
                builder.append(fs.get(i));
                builder.append(" ");
                if (i > 0 && i % 10 == 0) {
                    builder.append("\n");
                }
            }
            this.setText(builder.toString());
        });

    }
}
