package edu.ucla.drc.sledge.topicmodeling;

import cc.mallet.topics.TopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import edu.ucla.drc.sledge.LoadsFxml;
import edu.ucla.drc.sledge.ProjectModel;
import edu.ucla.drc.sledge.topicsettings.Topic;
import edu.ucla.drc.sledge.topicsettings.TopicSummary;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;


public class ModelVisualization extends AnchorPane implements LoadsFxml {

    @FXML private TextField topicModelName;
    @FXML private TableView topicSettingsTable;
    @FXML private ScrollPane allTopicsPane;
    @FXML private ModelSummaryTab modelSummary;
    @FXML private DocumentTopicReport documentSummaryViewer;
    @FXML private TabPane topicDetails;
    private ProjectModel model;
    private TopicModel selectedTopicModel;

    @FXML private Text topicCountText;
    @FXML private Text alphaValueText;
    @FXML private Text betaValueText;
    @FXML private Text iterationsCountText;
    @FXML private Text optimizeIntervalText;
    @FXML private Text burnInPeriodText;
    @FXML private Text randomSeedText;

    public ModelVisualization () {
        loadFxml();
        topicModelName.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                    selectedTopicModel.setTitle(topicModelName.getText());
                }
            }
        });
//        getScene().getStylesheets().add(getClass().getResource("@topic-settings.css").toExternalForm());
        getStylesheets().add(getClass().getResource("topic-settings.css").toExternalForm());
    }

    void setProjectModel (ProjectModel model) {
        this.model = model;
        documentSummaryViewer.setModel(model);
    }

    public void setTopicModel (TopicModel model) {
        selectedTopicModel = model;
        topicModelName.setText(model.getTitle());
        updateAllTopicsPane(model);
        modelSummary.setData(model);
        documentSummaryViewer.selectTopicModel(model);
        topicDetails.setVisible(true);
    }

    private void updateAllTopicsPane (TopicModel model) {
        FlowPane pane = new FlowPane();

        Alphabet alphabet = model.getAlphabet();

        topicCountText.setText(Integer.toString(model.numTopics));
        alphaValueText.setText(Double.toString(model.getAlphaSum()));
        betaValueText.setText(Double.toString(model.getBeta()));
        iterationsCountText.setText(Integer.toString(model.numIterations));
        optimizeIntervalText.setText(Integer.toString(model.optimizeInterval));
        burnInPeriodText.setText(Integer.toString(model.burninPeriod));
        randomSeedText.setText(Integer.toString(model.randomSeed));

        List<TreeSet<IDSorter>> sortedWords = model.getSortedWords();
        for (int i = 0; i < sortedWords.size(); i++) {
            Topic topic = new Topic(i);
            Iterator items = sortedWords.get(i).iterator();
            int limit = 10;
            int count = 0;
            while (count < limit && items.hasNext()) {
                IDSorter item = (IDSorter) items.next();
                topic.addTopWord((String) alphabet.lookupObject(item.getID()), item.getWeight());
            }

            TopicSummary summary = new TopicSummary();
            summary.setData(model, topic, model.getSummary().get(topic.getId()));

            pane.getChildren().add(summary);
        }
        allTopicsPane.setContent(pane);

    }

}
