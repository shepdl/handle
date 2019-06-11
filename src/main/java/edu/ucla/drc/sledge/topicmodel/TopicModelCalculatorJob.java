package edu.ucla.drc.sledge.topicmodel;

import java.util.function.Consumer;

public interface TopicModelCalculatorJob {
    void start ();
    void cancel ();

    void setSetProgress(Consumer<Integer> onSetProgress);
    void setUpdateTopWords(Consumer<TopicModel> onUpdateTopWords);
    void setOnCompletion(Consumer<TopicModel> onCompletion);
}
