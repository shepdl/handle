package edu.ucla.drc.sledge.documentimport;

import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;
import edu.ucla.drc.sledge.documentimport.stopwords.TokenSequenceMarkStopwords;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;

class StyleSpanGenerator {

    private final TokenSequence ts;

    StyleSpanGenerator(TokenSequence ts) {
        this.ts = ts;
    }

    public StyleSpans<? extends Collection<String>> computeHighlighting() {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        boolean stopwordSpan = false;
        int lastSpanLength = 0;
        for (Token token : ts) {
            String word = token.getText();

            if (stopwordSpan) {
                if (token.hasProperty(TokenSequenceMarkStopwords.IsStopword) || token.hasProperty(TokenSequenceMarkStopwords.IsWhitespace)) {
                    lastSpanLength += word.length();// + 1;
                } else {
                    spansBuilder.add(Collections.singleton("stopword"), lastSpanLength); // - 1);
                    lastSpanLength = word.length();// + 2;
                    stopwordSpan = false;
                }
            } else {
                if (token.hasProperty(TokenSequenceMarkStopwords.IsStopword) || token.hasProperty(TokenSequenceMarkStopwords.IsWhitespace)) {
                    if (lastSpanLength > 0) {
                        spansBuilder.add(Collections.emptyList(), lastSpanLength );
                    }
                    lastSpanLength = word.length();// + 1;
                    stopwordSpan = true;
                } else {
                    lastSpanLength += word.length();// + 1;
                }
            }
        }

        if (stopwordSpan) {
            spansBuilder.add(Collections.singleton("stopword"), lastSpanLength); // - 1);
        } else {
            spansBuilder.add(Collections.emptyList(), lastSpanLength); // - 1);
        }

        return spansBuilder.create();
    }

}
