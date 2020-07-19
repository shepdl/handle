package edu.ucla.drc.sledge.documents;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.TokenSequence;
import cc.mallet.util.SpacePreservingLexer;

import edu.ucla.drc.sledge.ImportFileSettings;
import edu.ucla.drc.sledge.documentimport.ImportPipeBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;


public class SpacePreservingLexerWithASCIIAndStopwordsTests {

    private Pipe pipe;

    @Before
    public void setUp () {
        ImportPipeBuilder builder = new ImportPipeBuilder();
        builder.addSettings(new ImportFileSettings());
        List<String> stopwords = new ArrayList<>();
        stopwords.add("a");
        stopwords.add("and");
        stopwords.add("by");
        stopwords.add("the");
        stopwords.add("of");
        builder.addStopwords(stopwords);
        pipe = builder.complete();
    }

    private TokenSequence stringToTokens (String inString) {
//        System.out.println(pipe);
        InstanceList instances = new InstanceList(pipe);
        List<Instance> docs = new ArrayList<>();
        docs.add(new Instance(inString, 0, 0, 0));
        instances.addThruPipe(docs.iterator());
        return (TokenSequence)instances.get(0).getData();
    }

    @Test
    public void stringWithOneWordReturnsOneWord() {
        String sample = "hello";
        TokenSequence ts = stringToTokens(sample);
        assertThat(ts.get(0).getText(), equalTo("hello"));
    }

    @Test
    public void stringWithOneWordOneSpaceReturnsOneWordOneSpace () {
        String sample = "hello world";
        TokenSequence ts = stringToTokens(sample);
        assertThat(ts.get(0).getText(), equalTo("hello"));
        assertThat(ts.get(1).getText(), equalTo(" "));
        assertThat(ts.get(2).getText(), equalTo("world"));
    }

    @Test
    public void stringWithThreeWordsSeparatedBySpacesReturnsAll () {
        String sample = "hello out there";
        TokenSequence ts = stringToTokens(sample);
        assertThat(ts.get(0).getText(), equalTo("hello"));
        assertThat(ts.get(1).getText(), equalTo(" "));
        assertThat(ts.get(2).getText(), equalTo("out"));
        assertThat(ts.get(3).getText(), equalTo(" "));
        assertThat(ts.get(4).getText(), equalTo("there"));
    }

    @Test
    public void stringWithThreeWordsSeparatedByTwoSpacesEachReturnsAll () {
        String sample = "hello  out  there";
        TokenSequence ts = stringToTokens(sample);
        System.out.print("The sequence: ");
        System.out.print(ts);
        assertThat(ts.size(), equalTo(5));
        assertThat(ts.get(0).getText(), equalTo("hello"));
        assertThat(ts.get(1).getText(), equalTo("  "));
        assertThat(ts.get(2).getText(), equalTo("out"));
        assertThat(ts.get(3).getText(), equalTo("  "));
        assertThat(ts.get(4).getText(), equalTo("there"));
    }

    @Test
    public void stringWithThreeWordsAndSpacesAtEndReturnsAll () {
        String sample = "hello  out there   ";
        TokenSequence ts = stringToTokens(sample);
        System.out.println(ts);
        assertThat(ts.get(0).getText(), equalTo("hello"));
        assertThat(ts.get(1).getText(), equalTo("  "));
        assertThat(ts.get(2).getText(), equalTo("out"));
        assertThat(ts.get(3).getText(), equalTo(" "));
        assertThat(ts.get(4).getText(), equalTo("there"));
//        assertThat(ts.get(5).getText(), equalTo("   "));
    }

    @Test
    public void stringWithThreeWordsWhenLastWordSeparatedByNewlineReturnsAll () {
        String sample = "hello out\nthere";
        TokenSequence ts = stringToTokens(sample);
        assertThat(ts.get(0).getText(), equalTo("hello"));
        assertThat(ts.get(1).getText(), equalTo(" "));
        assertThat(ts.get(2).getText(), equalTo("out"));
        assertThat(ts.get(3).getText(), equalTo("\n"));
        assertThat(ts.get(4).getText(), equalTo("there"));
    }

    @Test
    public void stringWithSpaceAtBeginningAndTwoWordsReturnsAll () {
        String sample = " hello out";
        TokenSequence ts = stringToTokens(sample);
        assertThat(ts.get(0).getText(), equalTo(" "));
        assertThat(ts.get(1).getText(), equalTo("hello"));
        assertThat(ts.get(2).getText(), equalTo(" "));
        assertThat(ts.get(3).getText(), equalTo("out"));
    }

    @Test
    public void stringBeginningWithNewLineAndTwoWordsReturnsAll () {
        String sample = "\n hello out";
        TokenSequence ts = stringToTokens(sample);
        assertThat(ts.get(0).getText(), equalTo("\n "));
        assertThat(ts.get(1).getText(), equalTo("hello"));
        assertThat(ts.get(2).getText(), equalTo(" "));
        assertThat(ts.get(3).getText(), equalTo("out"));
    }

    @Test
    public void stringEndingWithNewLineReturnsAll () {
        String sample = "hello out there people\n";
        TokenSequence ts = stringToTokens(sample);
        assertThat(ts.get(0).getText(), equalTo("hello"));
        assertThat(ts.get(1).getText(), equalTo(" "));
        assertThat(ts.get(2).getText(), equalTo("out"));
        assertThat(ts.get(3).getText(), equalTo(" "));
        assertThat(ts.get(4).getText(), equalTo("there"));
        assertThat(ts.get(5).getText(), equalTo(" "));
        assertThat(ts.get(6).getText(), equalTo("people"));
//        assertThat(ts.get(5).getText(), equalTo("\n"));
    }

    @Test
    public void projectGutenbergCase () {
        String sample = " the project gutenberg ebook of gods and heroes, by \nferdinand schmitt";
        TokenSequence ts = stringToTokens(sample);
        assertThat(ts.get(0).getText(), equalTo(" "));
        assertThat(ts.get(1).getText(), equalTo("the"));
        assertThat(ts.get(2).getText(), equalTo(" "));
        assertThat(ts.get(3).getText(), equalTo("project"));
        assertThat(ts.get(4).getText(), equalTo(" "));
        assertThat(ts.get(5).getText(), equalTo("gutenberg"));
        assertThat(ts.get(6).getText(), equalTo(" "));
        assertThat(ts.get(7).getText(), equalTo("ebook"));
        assertThat(ts.get(8).getText(), equalTo(" "));
        assertThat(ts.get(9).getText(), equalTo("of"));
        assertThat(ts.get(10).getText(), equalTo(" "));
        assertThat(ts.get(11).getText(), equalTo("gods"));
        assertThat(ts.get(12).getText(), equalTo(" "));
        assertThat(ts.get(13).getText(), equalTo("and"));
        assertThat(ts.get(14).getText(), equalTo(" "));
        assertThat(ts.get(15).getText(), equalTo("heroes"));
        assertThat(ts.get(16).getText(), equalTo(", "));
        assertThat(ts.get(17).getText(), equalTo("by"));
        assertThat(ts.get(18).getText(), equalTo(" \n"));
        assertThat(ts.get(19).getText(), equalTo("ferdinand"));
        assertThat(ts.get(20).getText(), equalTo(" "));
        assertThat(ts.get(21).getText(), equalTo("schmitt"));
    }
}
