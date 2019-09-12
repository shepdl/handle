package edu.ucla.drc.sledge.topicmodel;

import cc.mallet.topics.TopicAssignment;
import cc.mallet.types.Alphabet;
import cc.mallet.types.LabelAlphabet;

import java.util.ArrayList;
import java.util.List;

public interface TopicModelResults {

    String getTitle();
    void setTitle();

    ArrayList<TopicAssignment> getData();
    Alphabet getAlphabet();
    LabelAlphabet getTopicAlphabet();

    int getNumTopics();
    int getNumTypes();

    double[] getAlpha();
    double getAlphaSum();
    double getBeta();
    double getBetaSum();

    int[][] getTypeTopicCounts();
    int[] getTokensPerTopic();
    int[] getDocLengthCounts();
    int[][] getTopicDocCounts();

    int getNumIterations();

    int getRandomSeed();

}
