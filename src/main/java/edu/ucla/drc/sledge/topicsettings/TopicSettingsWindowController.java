package edu.ucla.drc.sledge.topicsettings;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import edu.ucla.drc.sledge.Document;
import edu.ucla.drc.sledge.ProjectModel;
import edu.ucla.drc.sledge.TopicTrainingJob;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;

public class TopicSettingsWindowController {

    private final TopicSettingsWindowModel model;
    private ProjectModel projectModel;
    private TopicTrainingJob job;
    private TopicSettingsWindowView view;

    private ObservableList<TopTenWords> topWords = FXCollections.observableArrayList();

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
        view = new TopicSettingsWindowView(model, this);
        view.show();
    }

    int updateProgress (int completedIterations) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                model.progress.set(new Double(completedIterations) / new Double(model.getIterationCount()));
            }
        });
        return 0;
    }

    static class Topic {
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

        public Topic (int id) {
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

    String updateTopicCounts (TopicModel topicModel) {
        Alphabet alphabet = topicModel.getAlphabet();

        List<TreeSet<IDSorter>> sortedWords = topicModel.getSortedWords();
        List<Topic> topics = new ArrayList<>();
        for (int i = 0; i < sortedWords.size(); i++) {
            Topic topic = new Topic(i);
            Iterator items = sortedWords.get(i).iterator();
            int limit = 10;
            int count = 0;
            while (count < limit && items.hasNext()) {
                IDSorter item = (IDSorter)items.next();
                topic.addTopWord((String)alphabet.lookupObject(item.getID()), item.getWeight());
            }
            topics.add(topic);
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                view.updateTopicResults(topics);
            }
        });

        return "";

    }

    String updateTopicWords (TopicModel topicModel) {
        Object[][] topWords = topicModel.getTopWords(10);
        List<ArrayList<String>> topWordsAsString = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < topWords.length; i++) {
            ArrayList<String> thisTopicWords = new ArrayList<>();
            String[] fixedTopWords = new String[topWords[i].length];
            for (int j = 0; j < 10; j++) {
                fixedTopWords[j] = topWords[i][j].toString();
            }
            if (i >= this.topWords.size()) {
                this.topWords.add(new TopTenWords(fixedTopWords));
            } else {
                this.topWords.set(i, new TopTenWords(fixedTopWords));
            }
            continue;
//            for (int j = 0; j < topWords[i].length; j++) {
//                thisTopicWords.add((String)topWords[i][j]);
//                String[] fixedTopWords = new String[topWords[i].length];
//                for (int k = 0; k < topWords[i].length; k++) {
//                    fixedTopWords[k] = (String)topWords[i][k];
//                }
//                this.topWords.set(i, new TopTenWords(topWords[i]));
//                this.topWords.get(i).set(j, (String)topWords[i][j]);
//            }
//            topWordsAsString.add(thisTopicWords);
//            this.topWords.set(i, FXCollections.observableArrayList())
        }

        return "";
    }

    public class TopTenWords {

        private final String[] words;

        public TopTenWords (String[] words) {
            this.words = words;
        }

        public String getWord0 () {
            return words[0];
        }

        public String getWord1 () {
            return words[1];
        }

        public String getWord2 () {
            return words[2];
        }

        public String getWord3 () {
            return words[3];
        }
        public String getWord4 () {
            return words[4];
        }
        public String getWord5 () {
            return words[5];
        }
        public String getWord6 () {
            return words[6];
        }
        public String getWord7 () {
            return words[7];
        }
        public String getWord8 () {
            return words[8];
        }
        public String getWord9 () {
            return words[9];
        }
    }

    ObservableList<TopTenWords> getTopWords () {
        return this.topWords;
    }

    void stopJob () {
        if (topicModel != null) {
            topicModel.cancel();
        }
    }

    TopicModel topicModel;

    void executeJob () {

        topicModel = new TopicModel(
                model.getNumTopics(), model.getAlpha(), model.getBeta()
        );

//        ParallelTopicModel.logger.setLevel(Level.OFF);

        topicModel.setRandomSeed(model.getRandomSeed());
        topicModel.setTopicDisplay(0, 0);
        topicModel.setBurninPeriod(model.getBurnInPeriod());
        topicModel.setNumThreads(model.getThreadCount());

        InstanceList instances = new InstanceList(projectModel.getPipe());
        List<Instance> documents = new ArrayList<>();
        for (Document doc : projectModel.getDocuments()) {
            documents.add(doc.getIngested());
        }
//        instances.addThruPipe(documents.iterator());
//        topicModel.addInstances(instances);
        topicModel.addInstances(projectModel.getInstances());
        topicModel.setProgress = this::updateProgress;
        topicModel.updateTopWords = this::updateTopicWords;
        topicModel.updateTopWords = this::updateTopicCounts;

        topicModel.start();

//        try {
//            topicModel.estimate();
//        } catch (IOException ex) {
//            System.out.println("IOException occurred during training");
//        }
    }

}
