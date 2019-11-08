package edu.ucla.drc.sledge.documentimport.wordcounttable;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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

    public final StringProperty wordProperty () {
        return word;
    }

    public final IntegerProperty countProperty () {
        return count;
    }

    public int getCount () {
        return count.get();
    }
}
