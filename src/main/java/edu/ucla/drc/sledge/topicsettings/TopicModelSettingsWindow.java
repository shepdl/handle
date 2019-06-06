package edu.ucla.drc.sledge.topicsettings;

import cc.mallet.topics.TopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import edu.ucla.drc.sledge.Document;
import edu.ucla.drc.sledge.DocumentSummary;
import edu.ucla.drc.sledge.ProjectModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class TopicModelSettingsWindow {

    @FXML private TextField numTopicsField;

    @FXML private TextField alphaField;
    @FXML private TextField betaField;
    @FXML private TextField randomSeedField;
    @FXML private TextField iterationsField;
    @FXML private TextField optimizeIntervalField;
    @FXML private TextField burinInPeriodField;
    @FXML private TextField threadsField;

    @FXML private ProgressBar jobProgressBar;
    @FXML private Button runButton;

    @FXML private TitledPane topicResultsPane;
    @FXML private ScrollPane topicScrollPane;

    private boolean running;
    private TopicModel topicModel;
    private List topWords;
    private ProjectModel projectModel;

    public TopicModelSettingsWindow() {
        topicModel = new TopicModel(20, 5.0, 0.1);
    }

    @FXML
    public void initialize () {
        numTopicsField.textProperty().addListener(new IntegerValidator(numTopicsField, topicModel::setNumTopics));
        numTopicsField.setText(Integer.toString(topicModel.getNumTopics()));
        alphaField.textProperty().addListener(new DoubleValidator(alphaField, topicModel::setAlphaSum));
        alphaField.setText(Double.toString(topicModel.getAlphaSum()));
        betaField.textProperty().addListener(new DoubleValidator(betaField, topicModel::setBeta));
        betaField.setText(Double.toString(topicModel.getBeta()));
        randomSeedField.textProperty().addListener(new IntegerValidator(randomSeedField, topicModel::setRandomSeed));
        randomSeedField.setText(Integer.toString(topicModel.randomSeed));
        iterationsField.textProperty().addListener(new IntegerValidator(iterationsField, topicModel::setNumIterations));
        iterationsField.setText(Integer.toString(topicModel.numIterations));
        optimizeIntervalField.textProperty().addListener(new IntegerValidator(optimizeIntervalField, topicModel::setNumIterations));
        optimizeIntervalField.setText(Integer.toString(topicModel.optimizeInterval));
        burinInPeriodField.textProperty().addListener(new IntegerValidator(burinInPeriodField, topicModel::setBurninPeriod));
        burinInPeriodField.setText(Integer.toString(topicModel.burninPeriod));
        threadsField.textProperty().addListener(new IntegerValidator(threadsField, topicModel::setNumThreads));

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        threadsField.setText(Integer.toString(availableProcessors));
        topicModel.setNumThreads(availableProcessors);

    }

    public void setup (ProjectModel projectModel) {
        this.projectModel = projectModel;
    }

    public void runButtonClicked(MouseEvent mouseEvent) {
        if (running) {
            running = false;
            topicModel.cancel();
            runButton.setText("Run");
        } else {
            runButton.setText("Stop");
            executeJob();
        }
    }

    private void updateTopicCounts(TopicModel topicModel) {
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
                updateTopicResults(topics);
            }
        });

    }

    private void updateTopicResults (List<Topic> topics) {
        topicResultsPane.setExpanded(true);
        topicResultsPane.setVisible(true);
        GridPane pane = new GridPane();
        int widthLimit = 3;
        int widthCounter = 0;
        int heightCounter = 0;
        for (Topic topic : topics) {

            TopicSummary summary = new TopicSummary();
            summary.setData(topic, topicModel.getSummary().get(topic.getId()));

            pane.add(summary, widthCounter, heightCounter);
            widthCounter += 1;
            if (widthCounter >= widthLimit) {
                widthCounter = 0;
                heightCounter += 1;
            }
        }

        topicScrollPane.setContent(pane);
    }

    void updateProgress (int completedIterations) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                jobProgressBar.setProgress(
                        (double)completedIterations / topicModel.numIterations
                );
                if (completedIterations == topicModel.numIterations) {
                    running = false;

                }
            }
        });
    }

    void executeJob () {

        InstanceList instances = new InstanceList(projectModel.getPipe());
        List<Instance> documents = new ArrayList<>();
        for (Document doc : projectModel.getDocuments()) {
            documents.add(doc.getIngested());
        }

        topicModel.addInstances(projectModel.getInstances());
        topicModel.setProgress = this::updateProgress;
        topicModel.updateTopWords = this::updateTopicCounts;

        topicModel.start();

        jobProgressBar.setVisible(true);

        running = true;

    }

    public void showDocument(MouseEvent event) {
        List<Document> documents = projectModel.getDocuments();
        DocumentSummary summary = new DocumentSummary(
                documents.get(0),
                documents,
                topicModel
        );
    }
}
