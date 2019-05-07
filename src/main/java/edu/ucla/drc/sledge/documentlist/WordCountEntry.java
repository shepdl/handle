package edu.ucla.drc.sledge.documentlist;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class WordCountEntry {

    private final SimpleStringProperty word;
    private final SimpleIntegerProperty count;

    public WordCountEntry(String word, int count) {
        this.word = new SimpleStringProperty(word);
        this.count = new SimpleIntegerProperty(count);
    }

    public String getWord () {
        return word.get();
    }

    public int getCount () {
        return count.get();
    }
}
