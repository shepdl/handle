package edu.ucla.drc.sledge.documentimport;

import cc.mallet.types.*;
import edu.ucla.drc.sledge.Document;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import org.apache.poi.util.StringUtil;

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
            TokenSequence ts = (TokenSequence)instance.getData();
            for (int i = 0; i < ts.size(); i++) {
                Token token = (Token)ts.get(i);
                builder.append(ts.get(i).getText());
                if (token.hasProperty(FeatureSequenceWithBigrams.deletionMark)) {
                    builder.append(" ");
                    String text = ts.get(i).getText();
                    for (int j = 0; j < text.length(); j++) {
                        builder.append("-");
                    }
                }
                builder.append(" ");
                if (i > 0 && i % 10 == 0) {
                    builder.append("\n");
                }
            }
            this.setText(builder.toString());
        });

    }
}
