package edu.ucla.drc.sledge.documentimport;

import cc.mallet.types.*;
import edu.ucla.drc.sledge.Document;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.css.Styleable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import org.apache.poi.util.StringUtil;
import org.fxmisc.richtext.ReadOnlyStyledDocument;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.StyledDocument;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DocumentTextView extends AnchorPane {
//    public class DocumentTextView extends TextArea {

    @FXML private StyleClassedTextArea documentTextPane;

    public DocumentTextView () {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DocumentTextView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getStylesheets().add(getClass().getResource("document-import-viewer.css").toExternalForm());
//        documentTextPane = new StyleClassedTextArea();
//        this.getChildren().add(documentTextPane);
    }

    public void setData (ObjectProperty<Document> selectedDocument) {
        selectedDocument.addListener((ObservableValue<? extends Document> observable, Document oldValue, Document newValue) -> {
            StringBuilder builder = new StringBuilder();
            Instance instance = newValue.getIngested();
            TokenSequence ts = (TokenSequence)instance.getData();
            List<ReadOnlyStyledDocument> document = new ArrayList<>();
            List<String> styles = new ArrayList<>();
            styles.add(".stopword");
            List<Integer> positions = new ArrayList<>();
            int textWidth = 0;
            for (int i = 0; i < ts.size(); i++) {
                Token token = (Token)ts.get(i);
                builder.append(ts.get(i).getText());
                int wordLength = ts.get(i).getText().length() + 1;
                textWidth += wordLength + 1;
                if (token.hasProperty(FeatureSequenceWithBigrams.deletionMark)) {
                    builder.append(" ");
                    String text = ts.get(i).getText();
                    for (int j = 0; j < 8; j++) {
                        builder.append("-");
                    }
//                    builder.append("stopword");
//                    positions.add(textWidth);
//                    positions.add(textWidth - ts.get(i).getText().length());
                }
                builder.append(" ");
                if (i > 0 && i % 10 == 0) {
//                    builder.append("\n");
                    textWidth += 1;
                }

//                textWidth += wordLength + 1;
            }
            documentTextPane.clear();
            documentTextPane.appendText(builder.toString());
            for (Integer i : positions) {
                documentTextPane.setStyleClass(i, i + 8, "stopword");
            }
            documentTextPane.moveTo(0); // If we don't do this, it will scroll to the end
//            this.setText(builder.toString());
        });

    }
}
