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
import org.fxmisc.richtext.model.ReadOnlyStyledDocument;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

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

    private StyleSpans<? extends Collection<String>> computeHighlighting(TokenSequence ts) {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        int wordStart = 0;
        int lastIndex = 0;
        int lastStopwordEnded = 0;
        // Algorithm:
        // Create a series of spans. Terminate when state changes.
        /*
        for (int i = 0; i < ts.size(); i++) {
            Token token = (Token)ts.get(i);
            String word = token.getText();
            if (token.hasProperty(TokenSequenceMarkStopwords.IsStopword)) {
                spansBuilder.add(Collections.emptyList(), wordStart - lastStopwordEnded);
                spansBuilder.add(Collections.singleton("stopword"), word.length());
                lastStopwordEnded = wordStart + word.length() + 1;
            }
            wordStart += word.length() + 1;
        }

         */
        boolean stopwordSpan = false;
        int lastSpanLength = 0;
        for (Token token : ts) {
            String word = token.getText();

            if (!stopwordSpan) {
                if (token.hasProperty(TokenSequenceMarkStopwords.IsStopword)) {
                    if (lastSpanLength > 0) {
                        spansBuilder.add(Collections.emptyList(), lastSpanLength + 1);
                        System.out.println(spansBuilder);
                    }
                    lastSpanLength = word.length() + 1;
                    stopwordSpan = true;
                } else {
                    lastSpanLength += word.length() + 1;
                }
            } else {
                if (token.hasProperty(TokenSequenceMarkStopwords.IsStopword)) {
                    lastSpanLength += word.length() + 1;
                } else {
                    spansBuilder.add(Collections.singleton("stopword"), lastSpanLength - 1);
                    System.out.println(spansBuilder);
                    lastSpanLength = word.length() + 1;
                    stopwordSpan = false;
                }
            }
        }

        return spansBuilder.create();
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
            documentTextPane.clear();
            if (instance == null) {
                return;
            }
            TokenSequence ts = (TokenSequence)instance.getData();
            List<String> styles = new ArrayList<>();
            styles.add(".stopword");
            List<Integer> startPositions = new ArrayList<>();
            List<Integer> endPositions = new ArrayList<>();
            int textWidth = 0;
            for (int i = 0; i < ts.size(); i++) {
                Token token = (Token)ts.get(i);
                int wordLength = ts.get(i).getText().length();
                String wordToAdd;
                if (token.hasProperty(TokenSequenceMarkStopwords.IsStopword)) {
                    startPositions.add(textWidth);
                    endPositions.add(textWidth + wordLength);
                    wordToAdd = new String(new char[wordLength]).replace("\0", "-");
                    wordToAdd = token.getText();
                } else {
                    wordToAdd= token.getText();
                }
//                wordToAdd = token.getText();
//                builder.append(ts.get(i).getText());
                builder.append(wordToAdd);
                builder.append(" ");
                textWidth += wordLength + 1;
                if (i > 0 && i % 10 == 0) {
                    builder.append("\n");
//                    textWidth += 1;
                    // These are the bits that add the text incrementally
//                    documentTextPane.appendText(builder.toString());
//                    builder = new StringBuilder();
//                    for (int j = 0; j < startPositions.size(); j++) {
//                        documentTextPane.setStyleClass(startPositions.get(j), endPositions.get(j), "stopword");
//                    }
//                    startPositions.clear();
//                    endPositions.clear();
                }

//                textWidth += wordLength + 1;
            }
            documentTextPane.replaceText(builder.toString());
            StyleSpanGenerator spanGenerator = new StyleSpanGenerator(ts);
//            documentTextPane.setStyleSpans(0, computeHighlighting(ts));
            documentTextPane.setStyleSpans(0, spanGenerator.computeHighlighting());
//            for (int i = 0; i < startPositions.size(); i++) {
//                documentTextPane.setStyleClass(startPositions.get(i), endPositions.get(i), "stopword");
//            }
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
