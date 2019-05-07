package edu.ucla.drc.sledge.documentlist;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.Instance;
import edu.ucla.drc.sledge.Document;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableObjectValue;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class DocumentTextViewerView {
    private ObjectProperty<Document> selectedDocument;

    public DocumentTextViewerView(ObjectProperty<Document> selectedDocument) {
        this.selectedDocument = selectedDocument;
    }

    public void initialize(HBox parent) {
        TextArea textArea = new TextArea();
        parent.getChildren().add(textArea);

        selectedDocument.addListener(new ChangeListener<Document>() {
            @Override
            public void changed(ObservableValue<? extends Document> observable, Document oldValue, Document newValue) {
                StringBuilder builder = new StringBuilder();
                Instance instance = newValue.getIngested();
                FeatureSequence fs = (FeatureSequence)instance.getData();
                for (int i = 0; i < fs.getFeatures().length; i++) {
                    builder.append(fs.get(i));
                    builder.append(" ");
                    if (i % 10 == 0) {
                        builder.append("\n");
                    }
                }
                textArea.setText(builder.toString());
            }
        });
    }

}
