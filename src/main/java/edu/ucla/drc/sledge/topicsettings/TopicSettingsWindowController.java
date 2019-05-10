package edu.ucla.drc.sledge.topicsettings;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import edu.ucla.drc.sledge.Document;
import edu.ucla.drc.sledge.ProjectModel;
import edu.ucla.drc.sledge.TopicTrainingJob;

import javax.imageio.IIOException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TopicSettingsWindowController {

    private final TopicSettingsWindowModel model;
    private ProjectModel projectModel;
    private TopicTrainingJob job;

    TopicSettingsWindowController (TopicTrainingJob job) {
        this.job = job;
        this.model = TopicSettingsWindowModel.fromTopicTrainingJob(job);
    }

    public TopicSettingsWindowController (ProjectModel projectModel, TopicTrainingJob job) {
        this.projectModel = projectModel;
        this.job = job;
        this.model = TopicSettingsWindowModel.fromTopicTrainingJob(job);
    }

    public void initialize () {
        TopicSettingsWindowView view = new TopicSettingsWindowView(model, this);
        view.show();
    }

    void executeJob () {
        ParallelTopicModel topicModel = new ParallelTopicModel(
            model.getNumTopics(), model.getAlpha(), model.getBeta()
        );

        ParallelTopicModel.logger.setLevel(Level.OFF);

        topicModel.setRandomSeed(model.getRandomSeed());
        topicModel.setTopicDisplay(0, 0);
        topicModel.setBurninPeriod(model.getBurnInPeriod());
        topicModel.setNumThreads(model.getThreadCount());

        InstanceList instances = new InstanceList(projectModel.getPipe());
        List<Instance> documents = new ArrayList<>();
        for (Document doc : projectModel.getDocuments()) {
            documents.add(doc.getIngested());
        }
        instances.addThruPipe(documents.iterator());
        topicModel.addInstances(instances);

        try {
            topicModel.estimate();
        } catch (IOException ex) {
            System.out.println("IOException occurred during training");
        }
    }

}
