package edu.ucla.drc.sledge;

import java.util.UUID;

public class TopicTrainingJob {

    private UUID id;
    private String name;

    private int topicCount = 20;

    private double alpha = 5.0;
    private double beta = 0.01;
    private int randomSeed = 1;

    private boolean useSymmetricAlpha = false;

    private int optimizeInterval = 0;

    public int getIterationCount() {
        return iterationCount;
    }

    private int iterationCount = 1000;

    public int getBurnInPeriod() {
        return burnInPeriod;
    }

    private int burnInPeriod = 200;

    public static TopicTrainingJob createBlank () {
        TopicTrainingJob job = new TopicTrainingJob();
        job.id = UUID.randomUUID();
        return job;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTopicCount() {
        return topicCount;
    }

    public void setTopicCount(int topicCount) {
        this.topicCount = topicCount;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public int getRandomSeed() {
        return randomSeed;
    }

    public void setRandomSeed(int randomSeed) {
        this.randomSeed = randomSeed;
    }

    public boolean isUseSymmetricAlpha() {
        return useSymmetricAlpha;
    }

    public void setUseSymmetricAlpha(boolean useSymmetricAlpha) {
        this.useSymmetricAlpha = useSymmetricAlpha;
    }

    public int getOptimizeInterval() {
        return optimizeInterval;
    }

    public void setOptimizeInterval(int optimizeInterval) {
        this.optimizeInterval = optimizeInterval;
    }

    public int getOptimizeBurnIn() {
        return optimizeBurnIn;
    }

    public void setOptimizeBurnIn(int optimizeBurnIn) {
        this.optimizeBurnIn = optimizeBurnIn;
    }

    private int optimizeBurnIn;

}
