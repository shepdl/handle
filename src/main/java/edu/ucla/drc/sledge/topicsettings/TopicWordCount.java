package edu.ucla.drc.sledge.topicsettings;

public class TopicWordCount {

    public String getWord() {
        return word;
    }

    private final String word;

    public int getCount() {
        return count;
    }

    private final int count;

    public TopicWordCount (String word, int count) {
        this.word = word;
        this.count = count;
    }
}
