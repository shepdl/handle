package edu.ucla.drc.sledge.documentimport.stopwords;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public class TokenSequenceMarkStopwordsRemovesStopwordsTests {
    private TokenSequenceMarkStopwords instance;
    private Set<String> initialWords = new HashSet<>();

    @Before
    public void setUp () {
        initialWords.add("apple");
        initialWords.add("banana");
        initialWords.add("cherry");
        initialWords.add("duck");
        initialWords.add("empty");
        initialWords.add("france");

        instance = new TokenSequenceMarkStopwords(false, initialWords);
    }

    @Test
    public void removingOneStopwordRemovesItFromList () {
        Set<String> toRemove = new HashSet<>();
        toRemove.add("apple");
        instance.removeStopwords(toRemove);
        Set<String> remaining = instance.getStopwords();
        assertThat(remaining, containsInAnyOrder("banana", "cherry", "duck", "empty", "france"));
    }

    @Test
    public void removingManyStopwordsRemovesThemAllFromList () {
        Set<String> toRemove = new HashSet<>();
        toRemove.add("banana");
        toRemove.add("france");
        instance.removeStopwords(toRemove);
        Set<String> remaining = instance.getStopwords();
        assertThat(remaining, containsInAnyOrder("apple", "cherry", "duck", "empty"));
    }

    @Test
    public void removingStopwordNotInListDoesNothing () {
        Set<String> toRemove = new HashSet<>();
        toRemove.add("banana");
        toRemove.add("france");
        toRemove.add("sugar");
        instance.removeStopwords(toRemove);
        Set<String> remaining = instance.getStopwords();
        assertThat(remaining, containsInAnyOrder("apple", "cherry", "duck", "empty"));
    }

}
