package edu.ucla.drc.sledge.topicmodel;

import cc.mallet.topics.TopicAssignment;
import cc.mallet.types.Alphabet;
import cc.mallet.types.LabelAlphabet;

import java.util.ArrayList;
import java.util.List;

public class LDATopicModelResults implements TopicModelResults {

    private final List<TopicAssignment> data;
    private final Alphabet alphabet;
    private final LabelAlphabet topicAlphabet;
    private final int numTopics;
    private final int numTypes;
    private final double[] alpha;
    private final double alphaSum;
    private final double beta;
    private final double betaSum;
    private final int[][] typeTopicCounts;
    private final int[] tokensPerTopic;
    private final int[] docLengthCounts;
    private final int[][] topicDocCounts;
    private final int numIterations;
    private final int randomSeed;

    public LDATopicModelResults (
        List<TopicAssignment> data,
        Alphabet alphabet,
        LabelAlphabet topicAlphabet,
        int numTopics,
        int numTypes,
        double[] alpha,
        double alphaSum,
        double beta,
        double betaSum,
        int[][] typeTopicCounts,
        int[] tokensPerTopic,
        int[] docLengthCounts,
        int[][] topicDocCounts,
        int numIterations,
        int randomSeed
    ) {

        this.data = data;
        this.alphabet = alphabet;
        this.topicAlphabet = topicAlphabet;
        this.numTopics = numTopics;
        this.numTypes = numTypes;
        this.alpha = alpha;
        this.alphaSum = alphaSum;
        this.beta = beta;
        this.betaSum = betaSum;
        this.typeTopicCounts = typeTopicCounts;
        this.tokensPerTopic = tokensPerTopic;
        this.docLengthCounts = docLengthCounts;
        this.topicDocCounts = topicDocCounts;
        this.numIterations = numIterations;
        this.randomSeed = randomSeed;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void setTitle() {

    }

    @Override
    public ArrayList<TopicAssignment> getData() {
        return null;
    }

    @Override
    public Alphabet getAlphabet() {
        return null;
    }

    @Override
    public LabelAlphabet getTopicAlphabet() {
        return null;
    }

    @Override
    public int getNumTopics() {
        return 0;
    }

    @Override
    public int getNumTypes() {
        return 0;
    }

    @Override
    public double[] getAlpha() {
        return new double[0];
    }

    @Override
    public double getAlphaSum() {
        return 0;
    }

    @Override
    public double getBeta() {
        return 0;
    }

    @Override
    public double getBetaSum() {
        return 0;
    }

    @Override
    public int[][] getTypeTopicCounts() {
        return new int[0][];
    }

    @Override
    public int[] getTokensPerTopic() {
        return new int[0];
    }

    @Override
    public int[] getDocLengthCounts() {
        return new int[0];
    }

    @Override
    public int[][] getTopicDocCounts() {
        return new int[0][];
    }

    @Override
    public int getNumIterations() {
        return 0;
    }

    @Override
    public int getRandomSeed() {
        return 0;
    }
}
