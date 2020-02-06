package edu.ucla.drc.sledge.documentimport;

import cc.mallet.types.TokenSequence;
import edu.ucla.drc.sledge.documentimport.stopwords.TokenSequenceMarkStopwords;
import org.fxmisc.richtext.model.StyleSpan;
import org.fxmisc.richtext.model.StyleSpans;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class StyleSpanGeneratorTests {

    @Test
    public void stopwordAtBeginningMarked () {
        TokenSequence inData = new TokenSequence();
        inData.add("the");
        inData.add("quick");
        inData.add("brown");
        inData.add("fox");
        inData.get(0).setProperty(TokenSequenceMarkStopwords.IsStopword, true);

        StyleSpanGenerator generator = new StyleSpanGenerator(inData);
        StyleSpans<? extends Collection<String>> result = generator.computeHighlighting();
        assertThat(result.getSpanCount(), equalTo(2));
        matchStyleSpans(result, stopwordSpan("the"), endSpan("quick brown fox"));
    }

    @Test
    public void stopwordAtMiddleMarked () {
        TokenSequence inData = new TokenSequence();
        inData.add("when");
        inData.add("in");
        inData.add("Rome");
        inData.get(1).setProperty(TokenSequenceMarkStopwords.IsStopword, true);
        StyleSpanGenerator generator = new StyleSpanGenerator(inData);
        StyleSpans<? extends Collection<String>> result = generator.computeHighlighting();
        matchStyleSpans(result, beginningSpan("when"), stopwordSpan("in"), endSpan("Rome"));
    }

    @Test
    public void wordStopwordStopwordWordMarked () {
        TokenSequence inData = new TokenSequence();
        inData.add("when");
        inData.add("in");
        inData.get(1).setProperty(TokenSequenceMarkStopwords.IsStopword, true);
        inData.add("the");
        inData.get(2).setProperty(TokenSequenceMarkStopwords.IsStopword, true);
        inData.add("springtime");
        StyleSpanGenerator generator = new StyleSpanGenerator(inData);
        StyleSpans<? extends Collection<String>> result = generator.computeHighlighting();
        matchStyleSpans(result, beginningSpan("when"), stopwordSpan("in the"), endSpan("springtime"));
    }

    @Test
    public void wordStopwordWordStopwordWordMarked () {
        TokenSequence inData = new TokenSequence();
        inData.add("when");
        inData.add("in");
        inData.get(1).setProperty(TokenSequenceMarkStopwords.IsStopword, true);
        inData.add("rome");
        inData.add("the");
        inData.get(3).setProperty(TokenSequenceMarkStopwords.IsStopword, true);
        inData.add("espresso");
        StyleSpanGenerator generator = new StyleSpanGenerator(inData);
        StyleSpans<? extends Collection<String>> result = generator.computeHighlighting();
        assertThat(result.getSpanCount(), equalTo(5));
        matchStyleSpans(result,
                beginningSpan("when"), stopwordSpan("in"), midSpan("rome"),
                stopwordSpan("the"), endSpan("espresso")
        );
    }

    private static void matchStyleSpans (StyleSpans<? extends Collection<String>> actual, StyleSpan<Collection> ... matchers) {
        List<StyleSpan<? extends Collection<String>>> trueActual = actual.stream().collect(Collectors.toList());
        assertThat(trueActual.size(), equalTo(matchers.length));
        for (int i = 0; i < trueActual.size(); i++) {
            assertThat(trueActual.get(i), equalTo(matchers[i]));
        }
    }

    @Test
    public void stopwordWordWordStopwordWordStopwordMarked () {
        TokenSequence inData = new TokenSequence();
        inData.add("the");
        inData.add("quick");
        inData.add("fox");
        inData.add("is");
        inData.add("quick");
        inData.add("and");
        inData.get(0).setProperty(TokenSequenceMarkStopwords.IsStopword, true);
        inData.get(3).setProperty(TokenSequenceMarkStopwords.IsStopword, true);
        inData.get(5).setProperty(TokenSequenceMarkStopwords.IsStopword, true);
        StyleSpanGenerator generator = new StyleSpanGenerator(inData);
        StyleSpans<? extends Collection<String>> result = generator.computeHighlighting();
        matchStyleSpans(result,
                stopwordSpan("the"), midSpan("quick fox"), stopwordSpan("is"), midSpan("quick"), stopwordSpan("and")
        );
    }

    @Test
    public void stopwordAtEndMarked () {
        TokenSequence inData = new TokenSequence();
        inData.add("Ampersands");
        inData.add("mean");
        inData.add("and");
        inData.get(2).setProperty(TokenSequenceMarkStopwords.IsStopword, true);
        StyleSpanGenerator generator = new StyleSpanGenerator(inData);
        StyleSpans<? extends Collection<String>> result = generator.computeHighlighting();
        matchStyleSpans(result,
                beginningSpan("Ampersands mean"), stopwordSpan("and")
        );
    }

    private StyleSpan<Collection> stopwordSpan(int length) {
        return new StyleSpan<>(Collections.singleton("stopword"), length);
    }

    private StyleSpan<Collection> stopwordSpan (String word) {
        return stopwordSpan(word.length());
    }

    private StyleSpan<Collection> beginningSpan(String word) {
        return normalSpan(word.length() + 1);
    }

    private StyleSpan<Collection> midSpan(String word) {
        return normalSpan(word.length() + 2); // space before and after words
    }

    private StyleSpan<Collection> endSpan (String word) {
        return normalSpan(word.length() + 1);
    }

    private StyleSpan<Collection> normalSpan (int length) {
        return new StyleSpan<>(Collections.emptyList(), length);
    }

}
