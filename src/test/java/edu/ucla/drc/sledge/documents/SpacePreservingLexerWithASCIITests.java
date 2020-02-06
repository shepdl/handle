package edu.ucla.drc.sledge.documents;

import cc.mallet.util.CharSequenceLexer;
import cc.mallet.util.SpacePreservingLexer;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class SpacePreservingLexerWithASCIITests {

    private SpacePreservingLexer instance;

    @Before
    public void setUp () {
        instance = new SpacePreservingLexer();
    }


    @Test
    public void stringWithOneWordReturnsOneWord() {
        String sample = "hello";
        instance.setCharSequence(sample);
        assertThat(instance.next(), equalTo("hello"));
    }

    @Test
    public void stringWithOneWordOneSpaceReturnsOneWordOneSpace () {
        String sample = "hello world";
        instance.setCharSequence(sample);
        assertThat(instance.next(), equalTo("hello"));
        assertThat(instance.next(), equalTo(" "));
        assertThat(instance.next(), equalTo("world"));
    }

    @Test
    public void stringWithThreeWordsSeparatedBySpacesReturnsAll () {
        String sample = "hello out there";
        instance.setCharSequence(sample);
        assertThat(instance.next(), equalTo("hello"));
        assertThat(instance.next(), equalTo(" "));
        assertThat(instance.next(), equalTo("out"));
        assertThat(instance.next(), equalTo(" "));
        assertThat(instance.next(), equalTo("there"));
    }

    @Test
    public void stringWithThreeWordsSeparatedByTwoSpacesEachReturnsAll () {
        String sample = "hello  out  there";
        instance.setCharSequence(sample);
        assertThat(instance.next(), equalTo("hello"));
        assertThat(instance.next(), equalTo("  "));
        assertThat(instance.next(), equalTo("out"));
        assertThat(instance.next(), equalTo("  "));
        assertThat(instance.next(), equalTo("there"));
    }

    @Test
    public void stringWithThreeWordsAndSpacesAtEndReturnsAll () {
        String sample = "hello  out there   ";
        instance.setCharSequence(sample);
        assertThat(instance.next(), equalTo("hello"));
        assertThat(instance.next(), equalTo("  "));
        assertThat(instance.next(), equalTo("out"));
        assertThat(instance.next(), equalTo(" "));
        assertThat(instance.next(), equalTo("there"));
        assertThat(instance.next(), equalTo("   "));
    }

    @Test
    public void stringWithThreeWordsWhenLastWordSeparatedByNewlineReturnsAll () {
        String sample = "hello out\nthere";
        instance.setCharSequence(sample);
        assertThat(instance.next(), equalTo("hello"));
        assertThat(instance.next(), equalTo(" "));
        assertThat(instance.next(), equalTo("out"));
        assertThat(instance.next(), equalTo("\n"));
        assertThat(instance.next(), equalTo("there"));
    }

    @Test
    public void stringWithSpaceAtBeginningAndTwoWordsReturnsAll () {
        String sample = " hello out";
        instance.setCharSequence(sample);
        assertThat(instance.next(), equalTo(" "));
        assertThat(instance.next(), equalTo("hello"));
        assertThat(instance.next(), equalTo(" "));
        assertThat(instance.next(), equalTo("out"));
    }

    @Test
    public void stringBeginningWithNewLineAndTwoWordsReturnsAll () {
        String sample = "\n hello out";
        instance.setCharSequence(sample);
        assertThat(instance.next(), equalTo("\n "));
        assertThat(instance.next(), equalTo("hello"));
        assertThat(instance.next(), equalTo(" "));
        assertThat(instance.next(), equalTo("out"));
    }

    @Test
    public void stringEndingWithNewLineReturnsAll () {
        String sample = "hello out there\n";
        instance.setCharSequence(sample);
        assertThat(instance.next(), equalTo("hello"));
        assertThat(instance.next(), equalTo(" "));
        assertThat(instance.next(), equalTo("out"));
        assertThat(instance.next(), equalTo(" "));
        assertThat(instance.next(), equalTo("there"));
        assertThat(instance.next(), equalTo("\n"));
    }

    @Test
    public void projectGutenbergCase () {
        String sample = " the project gutenberg ebook of gods and heroes, by \nferdinand schmitt";
        instance.setCharSequence(sample);
        assertThat(instance.next(), equalTo(" "));
        assertThat(instance.next(), equalTo("the"));
        assertThat(instance.next(), equalTo(" "));
        assertThat(instance.next(), equalTo("project"));
        assertThat(instance.next(), equalTo(" "));
        assertThat(instance.next(), equalTo("gutenberg"));
        assertThat(instance.next(), equalTo(" "));
        assertThat(instance.next(), equalTo("ebook"));
        assertThat(instance.next(), equalTo(" "));
        assertThat(instance.next(), equalTo("of"));
        assertThat(instance.next(), equalTo(" "));
        assertThat(instance.next(), equalTo("gods"));
        assertThat(instance.next(), equalTo(" "));
        assertThat(instance.next(), equalTo("and"));
        assertThat(instance.next(), equalTo(" "));
        assertThat(instance.next(), equalTo("heroes"));
        assertThat(instance.next(), equalTo(", "));
        assertThat(instance.next(), equalTo("by"));
        assertThat(instance.next(), equalTo(" \n"));
        assertThat(instance.next(), equalTo("ferdinand"));
        assertThat(instance.next(), equalTo(" "));
        assertThat(instance.next(), equalTo("schmitt"));
    }

}
