package edu.ucla.drc.sledge.topicsettings;

import edu.ucla.drc.sledge.TopicTrainingJob;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.WritableBooleanValue;
import javafx.beans.value.WritableDoubleValue;
import javafx.beans.value.WritableIntegerValue;

public class TopicSettingsWindowModel {

    public int getNumTopics() {
        return numTopics.get();
    }

    public void setNumTopics(int numTopics) {
        this.numTopics.set(numTopics);
    }

    public double getAlpha() {
        return alpha.get();
    }

    public void setAlpha(double alpha) {
        this.alpha.set(alpha);
    }

    public double getBeta() {
        return beta.get();
    }

    public void setBeta(double beta) {
        this.beta.set(beta);
    }

    public boolean getSymmetricAlpha() {
        return symmetricAlpha.get();
    }

    public void setSymmetricAlpha(boolean symmetricAlpha) {
        this.symmetricAlpha.set(symmetricAlpha);
    }

    public int getRandomSeed() {
        return randomSeed.get();
    }

    public void setRandomSeed(int randomSeed) {
        this.randomSeed.set(randomSeed);
    }

    public int getIterationCount() {
        return iterationCount.get();
    }

    public void setIterationCount(int iterationCount) {
        this.iterationCount.set(iterationCount);
    }

    public int getOptimizeInterval() {
        return optimizeInterval.get();
    }

    public void setOptimizeInterval(int optimizeInterval) {
        this.optimizeInterval.set(optimizeInterval);
    }

    public int getBurnInPeriod() {
        return burnInPeriod.get();
    }

    public void setBurnInPeriod(int burnInPeriod) {
        this.burnInPeriod.set(burnInPeriod);
    }

    public int getThreadCount() {
        return threadCount.get();
    }

    public void setThreadCount(int threadCount) {
        this.threadCount.set(threadCount);
    }

    public void setTrainingInProgress (boolean inProgress) {
        trainingInProgress.set(inProgress);
    }

    public boolean getTrainingInProgress () {
        return trainingInProgress.get();
    }

    private WritableIntegerValue numTopics = new SimpleIntegerProperty(20);
    private WritableDoubleValue alpha = new SimpleDoubleProperty(5.0);
    private WritableDoubleValue beta = new SimpleDoubleProperty(0.01);
    private WritableBooleanValue symmetricAlpha = new SimpleBooleanProperty(false);
    private WritableIntegerValue randomSeed = new SimpleIntegerProperty(-1);

    private WritableIntegerValue iterationCount = new SimpleIntegerProperty(1000);
    private WritableIntegerValue optimizeInterval = new SimpleIntegerProperty(0);
    private WritableIntegerValue burnInPeriod = new SimpleIntegerProperty(200);

    private WritableIntegerValue threadCount = new SimpleIntegerProperty();

    public WritableBooleanValue trainingInProgress = new SimpleBooleanProperty();


    public static TopicSettingsWindowModel blank () {
        TopicSettingsWindowModel model = new TopicSettingsWindowModel();
        model.threadCount.set(Runtime.getRuntime().availableProcessors());
        return model;
    }

    public static TopicSettingsWindowModel fromTopicTrainingJob (TopicTrainingJob job) {
        TopicSettingsWindowModel model = TopicSettingsWindowModel.blank();
        model.numTopics.set(job.getTopicCount());
        model.alpha.set(job.getAlpha());
        model.beta.set(job.getBeta());

        // TODO: symmetric alpha

        model.randomSeed.set(job.getRandomSeed());
        model.iterationCount.set(job.getIterationCount());
        model.optimizeInterval.set(job.getOptimizeInterval());
        model.burnInPeriod.set(job.getBurnInPeriod());

        return model;
    }

}
