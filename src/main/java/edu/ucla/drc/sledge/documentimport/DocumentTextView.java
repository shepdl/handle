package edu.ucla.drc.sledge.documentimport;

import cc.mallet.types.*;
import edu.ucla.drc.sledge.documentimport.stopwords.TokenSequenceMarkStopwords;
import edu.ucla.drc.sledge.documents.Document;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import org.fxmisc.richtext.*;
import org.reactfx.Subscription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;

public class DocumentTextView extends AnchorPane {
//    public class DocumentTextView extends TextArea {

    @FXML private StyleClassedTextArea documentTextPane;
    @FXML private CodeArea codeTextView;

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

    private void updateView (ObservableValue<? extends Instance> observable, Instance oldValue, Instance newValue) {
        StringBuilder builder = new StringBuilder();

        Instance instance = newValue;
        TokenSequence ts = (TokenSequence)instance.getData();
        List<ReadOnlyStyledDocument> document = new ArrayList<>();
        List<String> styles = new ArrayList<>();
        styles.add(".stopword");
        List<Integer> startPositions = new ArrayList<>();
        List<Integer> endPositions = new ArrayList<>();
        int textWidth = 0;
        documentTextPane.clear();
        for (int i = 0; i < ts.size(); i++) {
            Token token = (Token)ts.get(i);
            int wordLength = ts.get(i).getText().length();
            String wordToAdd;
            if (token.hasProperty(TokenSequenceMarkStopwords.IsStopword)) {
                startPositions.add(textWidth);
                endPositions.add(textWidth + wordLength);
            }
            wordToAdd= token.getText();
            builder.append(wordToAdd);
            builder.append(" ");
            textWidth += wordLength + 1;
            if (i > 0 && i % 10 == 0) {
                documentTextPane.appendText(builder.toString());
                builder = new StringBuilder();
            }
        }
        documentTextPane.appendText(builder.toString());
        for (int i = 0; i < startPositions.size(); i++) {
            documentTextPane.setStyleClass(startPositions.get(i), endPositions.get(i), "stopword");
        }
        documentTextPane.moveTo(0); // If we don't do this, it will scroll to the end
    }

    public void setData (ObjectProperty<Instance> selectedDocument) {
//        selectedDocument.addListener(this::updateView);

        selectedDocument.addListener((ObservableValue<? extends Instance> observable, Instance oldValue, Instance newValue) -> {
            StringBuilder builder = new StringBuilder();

            Instance instance = newValue;
            TokenSequence ts = (TokenSequence)instance.getData();
            List<ReadOnlyStyledDocument> document = new ArrayList<>();
            List<String> styles = new ArrayList<>();
            styles.add(".stopword");
            List<Integer> startPositions = new ArrayList<>();
            List<Integer> endPositions = new ArrayList<>();
            int textWidth = 0;
            documentTextPane.clear();
            for (int i = 0; i < ts.size(); i++) {
                Token token = (Token)ts.get(i);
                int wordLength = ts.get(i).getText().length();
                String wordToAdd;
                if (token.hasProperty(TokenSequenceMarkStopwords.IsStopword)) {
                    startPositions.add(textWidth);
                    endPositions.add(textWidth + wordLength);
//                    wordToAdd = new String(new char[wordLength]).replace("\0", "-");
                } else {
//                    wordToAdd= token.getText();
                }
                wordToAdd= token.getText();
//                builder.append(ts.get(i).getText());
                builder.append(wordToAdd);
                builder.append(" ");
                textWidth += wordLength + 1;
                if (i > 0 && i % 10 == 0) {
//                    builder.append("\n");
//                    textWidth += 1;
                    // These are the bits that add the text incrementally
                    documentTextPane.appendText(builder.toString());
                    builder = new StringBuilder();
//                    for (int j = 0; j < startPositions.size(); j++) {
//                        documentTextPane.setStyleClass(startPositions.get(j), endPositions.get(j), "stopword");
//                    }
//                    startPositions.clear();
//                    endPositions.clear();
                }

//                textWidth += wordLength + 1;
            }
            documentTextPane.appendText(builder.toString());
            for (int i = 0; i < startPositions.size(); i++) {
                documentTextPane.setStyleClass(startPositions.get(i), endPositions.get(i), "stopword");
            }
//            for (Integer i : startPositions) {
//                documentTextPane.setStyleClass(i, i + 8, "stopword");
//            }
            documentTextPane.moveTo(0); // If we don't do this, it will scroll to the end
//            this.setText(builder.toString());
        });

    }

    /*
    public void setData (ObjectProperty<Document> selectedDocument) {
        selectedDocument.addListener((ObservableValue<? extends Document> observable, Document oldValue, Document newValue) -> {
            StringBuilder builder = new StringBuilder();

            Instance instance = newValue.getIngested();
            TokenSequence ts = (TokenSequence)instance.getData();
            List<ReadOnlyStyledDocument> document = new ArrayList<>();
            List<String> styles = new ArrayList<>();
            styles.add(".stopword");
            List<Integer> startPositions = new ArrayList<>();
            List<Integer> endPositions = new ArrayList<>();
            int textWidth = 0;
            documentTextPane.clear();
            for (int i = 0; i < ts.size(); i++) {
                Token token = (Token)ts.get(i);
                int wordLength = ts.get(i).getText().length();
                String wordToAdd;
                if (token.hasProperty(TokenSequenceMarkStopwords.IsStopword)) {
                    startPositions.add(textWidth);
                    endPositions.add(textWidth + wordLength);
                    wordToAdd = new String(new char[wordLength]).replace("\0", "-");
                } else {
                    wordToAdd= token.getText();
                }
//                builder.append(ts.get(i).getText());
                builder.append(wordToAdd);
                builder.append(" ");
                textWidth += wordLength + 1;
                if (i > 0 && i % 10 == 0) {
//                    builder.append("\n");
//                    textWidth += 1;
                    documentTextPane.appendText(builder.toString());
                    builder = new StringBuilder();
                    for (int j = 0; j < startPositions.size(); j++) {
//                        documentTextPane.setStyleClass(startPositions.get(j), endPositions.get(j), "stopword");
                    }
                    startPositions.clear();
                    endPositions.clear();
                }

//                textWidth += wordLength + 1;
            }
            documentTextPane.appendText(builder.toString());
            for (int i = 0; i < startPositions.size(); i++) {
//                documentTextPane.setStyleClass(startPositions.get(i), endPositions.get(i), "stopword");
            }
//            for (Integer i : startPositions) {
//                documentTextPane.setStyleClass(i, i + 8, "stopword");
//            }
            documentTextPane.moveTo(0); // If we don't do this, it will scroll to the end
//            this.setText(builder.toString());
        });

    }

     */
}
