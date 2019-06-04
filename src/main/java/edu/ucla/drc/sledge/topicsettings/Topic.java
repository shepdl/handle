package edu.ucla.drc.sledge.topicsettings;

import java.util.ArrayList;
import java.util.List;

public class Topic {
    public int getId() {
        return id;
    }

    private int id;

    public List<String> getTopWords() {
        return topWords;
    }

    private List<String> topWords = new ArrayList<>();

    public List<Double> getTopWordCounts() {
        return topWordCounts;
    }

    private List<Double> topWordCounts = new ArrayList<>();

    public Topic(int id) {
        this.id = id;
    }

    public void addTopWord (String word, double value) {
        topWords.add(word);
        topWordCounts.add(value);
    }

    public void clearWords () {
        this.topWords.clear();
        this.topWordCounts.clear();
    }

}
