package edu.ucla.drc.sledge.topicmodel;

import cc.mallet.topics.TopicAssignment;
import cc.mallet.types.Alphabet;
import cc.mallet.types.LabelAlphabet;

import java.util.ArrayList;
import java.util.List;

public interface TopicModel {

    ArrayList<TopicAssignment> getData();
    Alphabet getAlphabet();
    LabelAlphabet getTopicAlphabet();

    int[] getTokensPerTopic();

    int getNumTopics();
    int getNumTypes();

    double getAlphaSum();

    int[][] getTypeTopicCounts();

    double[] getAlpha();

    double getBeta();

    int getDocLengthCounts();

    int getNumIterations();
}
