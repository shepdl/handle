package edu.ucla.drc.sledge.documentimport;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class TokenSequenceMarkStopwordsAddingAndRemovingTest {

    private Set<String> initialWords = new HashSet<>();

    @Before
    public void setUp () {
        initialWords.add("apple");
        initialWords.add("banana");
        initialWords.add("cherry");
    }

    @Test
    public void stopwordsAddedWhenPassedInConstructor () {
        TokenSequenceMarkStopwords instance = new TokenSequenceMarkStopwords(false, initialWords);
        assertThat(instance.getStopwords(), equalTo(initialWords));
    }

    @Test
    public void stopwordsAddedToSetWhenPassedToAddStopwords () {
        TokenSequenceMarkStopwords instance = new TokenSequenceMarkStopwords(false);
        Set<String> additional = new HashSet<>();
        additional.add("duck");
        additional.add("eclipse");
        instance.addStopwords(additional);
        assertThat(instance.getStopwords(), equalTo(additional));
    }

    @Test
    public void stopwordsMixedWithConstructorParametersWhenAdded () {
        TokenSequenceMarkStopwords instance = new TokenSequenceMarkStopwords(false, initialWords);
        Set<String> additional = new HashSet<>();
        additional.add("duck");
        additional.add("eclipse");
        instance.addStopwords(additional);
        additional.addAll(initialWords);
        assertThat(instance.getStopwords(), equalTo(additional));
    }

    @Test
    public void stopwordsMixedWhenAddedInTwoCalls () {
        TokenSequenceMarkStopwords instance = new TokenSequenceMarkStopwords(false, initialWords);
        Set<String> additional = new HashSet<>();
        additional.add("duck");
        additional.add("eclipse");
        instance.addStopwords(additional);
        Set<String> further = new HashSet<>();
        further.add("finale");
        further.add("grapes");
        instance.addStopwords(further);
        further.addAll(initialWords);
        further.addAll(additional);
        assertThat(instance.getStopwords(), equalTo(further));
    }

}