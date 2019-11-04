package edu.ucla.drc.sledge.documentimport.stopwords;

import java.util.ArrayList;
import java.util.List;

class ListStopwordsList implements StopwordSource {

    private String name;
    private List<String> words;

    ListStopwordsList(String name, List<String> words) {
        this.name = name;
        this.words = words;
    }

    ListStopwordsList(String name, String words) {
        this.name = name;
        String[] splitWords = words.split(" ");
        this.words = new ArrayList<>();
        for (String word : splitWords) {
            // Handle too many spaces between a word
            if (word.length() > 0) {
                this.words.add(word);
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> provideWords() {
        return words;
    }

    @Override
    public String toString () {
        return this.getName();
    }
}
