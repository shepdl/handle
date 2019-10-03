package edu.ucla.drc.sledge.topicsettings;

import cc.mallet.topics.TopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import edu.ucla.drc.sledge.ProjectModel;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Consumer;

public class TopicModelSettings extends VBox {

    @FXML
    private TextField titleField;
    @FXML
    private TextField numTopicsField;

    @FXML
    private TextField alphaField;
    @FXML
    private TextField betaField;
    @FXML
    private TextField randomSeedField;
    @FXML
    private TextField iterationsField;
    @FXML
    private TextField optimizeIntervalField;
    @FXML
    private TextField burinInPeriodField;
    @FXML
    private TextField threadsField;

    @FXML
    private ProgressBar jobProgressBar;
    @FXML
    private Button runButton;

    @FXML
    private TitledPane topicResultsPane;
    @FXML
    private ScrollPane topicScrollPane;

    private boolean running;
    private TopicModel topicModel = new TopicModel(20);
    private List topWords;
    private ProjectModel projectModel;
    private Consumer<TopicModel> onClose;

    @FXML
    private Button closeButton;

    public EventHandler<MouseEvent> getCloseButtonNextStep() {
        return closeButtonNextStep.get();
    }

    public ObjectProperty<EventHandler<MouseEvent>> closeButtonNextStepProperty() {
        return closeButtonNextStep;
    }

    public void setCloseButtonNextStep(EventHandler<MouseEvent> closeButtonNextStep) {
        this.closeButtonNextStep.set(closeButtonNextStep);
    }

    @FXML
    private ObjectProperty<EventHandler<MouseEvent>> closeButtonNextStep;

    public TopicModelSettings() {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TopicModelSettings.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        topicModel = new TopicModel(20);
    }

    @FXML
    public void initialize() {
        titleField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                topicModel.setTitle(newValue);
            }
        });
        numTopicsField.textProperty().addListener(new IntegerValidator(numTopicsField));
        alphaField.textProperty().addListener(new DoubleValidator(alphaField));
        betaField.textProperty().addListener(new DoubleValidator(betaField));
        randomSeedField.textProperty().addListener(new IntegerValidator(randomSeedField));
        iterationsField.textProperty().addListener(new IntegerValidator(iterationsField));
        optimizeIntervalField.textProperty().addListener(new IntegerValidator(optimizeIntervalField));
        burinInPeriodField.textProperty().addListener(new IntegerValidator(burinInPeriodField));
        threadsField.textProperty().addListener(new IntegerValidator(threadsField));
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        threadsField.setText(Integer.toString(availableProcessors));
        updateFields();
    }

    private void updateFields() {
        titleField.setText(topicModel.getTitle());
        numTopicsField.setText(Integer.toString(topicModel.getNumTopics()));
        alphaField.setText(Double.toString(topicModel.getAlphaSum()));
        betaField.setText(Double.toString(topicModel.getBeta()));
        randomSeedField.setText(Integer.toString(topicModel.randomSeed));
        iterationsField.setText(Integer.toString(topicModel.numIterations));
        optimizeIntervalField.setText(Integer.toString(topicModel.optimizeInterval));
        burinInPeriodField.setText(Integer.toString(topicModel.burninPeriod));
        numTopicsField.setText(Integer.toString(topicModel.getNumTopics()));
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        topicModel.setNumThreads(availableProcessors);

        runButton.setVisible(true);
    }

    public void setup(ProjectModel projectModel, Consumer<TopicModel> onClose) {
        this.projectModel = projectModel;
        this.onClose = onClose;
    }

    public void runButtonClicked(MouseEvent mouseEvent) {
        if (running) {
            running = false;
            topicModel.cancel();
            runButton.setText("Run");
        } else {
            if (topicModel.isComplete()) {
                onClose.accept(topicModel);
            } else {
                runButton.setText("Stop");
                jobProgressBar.setVisible(true);
                executeJob();
            }
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
                IDSorter item = (IDSorter) items.next();
                topic.addTopWord((String) alphabet.lookupObject(item.getID()), item.getWeight());
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

    private void updateTopicResults(List<Topic> topics) {
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

    void updateProgress(int completedIterations) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                jobProgressBar.setProgress(
                        (double) completedIterations / topicModel.numIterations
                );
                if (completedIterations == topicModel.numIterations) {
                    jobProgressBar.setVisible(false);
//                    runButton.setVisible(false);
                    running = false;
                    runButton.setText("Save");
                }
            }
        });
    }

    void executeJob() {

        topicModel.setNumTopics(Integer.parseInt(numTopicsField.textProperty().getValue()));
        topicModel.setAlphaSum(Double.parseDouble(alphaField.textProperty().getValue()));
        topicModel.setBeta(Double.parseDouble(betaField.textProperty().getValue()));
        topicModel.setRandomSeed(Integer.parseInt(randomSeedField.textProperty().getValue()));
        topicModel.setNumIterations(Integer.parseInt(iterationsField.textProperty().getValue()));
        topicModel.setOptimizeInterval(Integer.parseInt(optimizeIntervalField.textProperty().getValue()));
        topicModel.setBurninPeriod(Integer.parseInt(burinInPeriodField.textProperty().getValue()));
        topicModel.setNumThreads(Integer.parseInt(threadsField.textProperty().getValue()));

        topicModel.addInstances(projectModel.getInstancesForModeling());
        topicModel.setProgress = this::updateProgress;
        topicModel.updateTopWords = this::updateTopicCounts;

        topicModel.start();

        jobProgressBar.setVisible(true);

        running = true;
    }

    public void closeButtonHandler (MouseEvent event) {
        onClose.accept(topicModel);
//        this.getParent().fireEvent(new TopicModelSettingsModalWindow.CloseEvent());
    }

}
