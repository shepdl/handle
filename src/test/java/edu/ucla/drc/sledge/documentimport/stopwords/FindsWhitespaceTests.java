package edu.ucla.drc.sledge.documentimport.stopwords;

import org.junit.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class FindsWhitespaceTests {

    private TokenSequenceMarkStopwords instance = new TokenSequenceMarkStopwords(true);

    @Test
    public void recognizesOneSpace () {
        assertTrue(TokenSequenceMarkStopwords.isWhitespace(" "));
    }

    @Test
    public void recognizesFiveSpaces () {
        assertTrue(TokenSequenceMarkStopwords.isWhitespace("     "));
    }

    @Test
    public void doesNotRecognizeWord () {
        assertFalse(TokenSequenceMarkStopwords.isWhitespace("apple"));
    }

    @Test
    public void doesNotRecognizeSpacesFollowedByWord () {
        assertFalse(TokenSequenceMarkStopwords.isWhitespace("  apple"));
    }

    @Test
    public void doesNotRecognizeWordFollowedBySpaces () {
        assertFalse(TokenSequenceMarkStopwords.isWhitespace("apple  "));
    }

    @Test
    public void doesNotRecognizeWordSurroundedBySpaces () {
        assertFalse(TokenSequenceMarkStopwords.isWhitespace("apple   "));
    }

    @Test
    public void recognizesNewLine() {
        assertTrue(TokenSequenceMarkStopwords.isWhitespace("\n"));
    }

    @Test
    public void recognizesMultipleNewLines () {
        assertTrue(TokenSequenceMarkStopwords.isWhitespace("\n\n\n"));
    }

    @Test
    public void recognizesNewLineSurroundedBySpaces () {
        assertTrue(TokenSequenceMarkStopwords.isWhitespace("  \n   "));
    }

    @Test
    public void recognizesNewLineFollowedBySpaces () {
        assertTrue(TokenSequenceMarkStopwords.isWhitespace("\n    "));
    }

    @Test
    public void recognizesNewLinePrecededBySpaces () {
        assertTrue(TokenSequenceMarkStopwords.isWhitespace("   \n"));
    }

    @Test
    public void doesNotRecognizeWordFollowingNewline () {
        assertFalse(TokenSequenceMarkStopwords.isWhitespace("\napple"));
    }

    @Test
    public void doesNotRecognizeWordFollowedByNewLine () {
        assertFalse(TokenSequenceMarkStopwords.isWhitespace("apple\n"));
    }

    @Test
    public void doesNotRecognizeWordFollowedBySpacesThenNewLine () {
        assertFalse(TokenSequenceMarkStopwords.isWhitespace("apple   \n"));
    }

    @Test
    public void doesNotRecognizeNewLineSurroundedByWords () {
        assertFalse(TokenSequenceMarkStopwords.isWhitespace("apple\nbanana"));
    }

    @Test
    public void recognizesTab () {
        assertTrue(TokenSequenceMarkStopwords.isWhitespace("\t"));
    }

    @Test
    public void recognizesTabPrecededBySpaces () {
        assertTrue(TokenSequenceMarkStopwords.isWhitespace("   \t"));
    }

    @Test
    public void recognizesTabSurroundedBySpaces () {
        assertTrue(TokenSequenceMarkStopwords.isWhitespace("\t   "));
    }

    @Test
    public void doesNotRecognizeTabFollowedByWord () {
        assertFalse(TokenSequenceMarkStopwords.isWhitespace("\tapple"));
    }

    @Test
    public void doesNotRecognizeWordFollowedByTab () {
        assertFalse(TokenSequenceMarkStopwords.isWhitespace("apple\t"));
    }

    @Test
    public void doesNotRecognizeWordsSeparatedByTabs () {
        assertFalse(TokenSequenceMarkStopwords.isWhitespace("apple\tapple"));
    }

    @Test
    public void doesNotRecognizeTabsSeparatedByWords () {
        assertFalse(TokenSequenceMarkStopwords.isWhitespace("\tapple\t"));
    }
}
