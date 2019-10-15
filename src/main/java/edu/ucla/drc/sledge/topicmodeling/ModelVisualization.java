package edu.ucla.drc.sledge.topicmodeling;

import cc.mallet.topics.TopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import edu.ucla.drc.sledge.LoadsFxml;
import edu.ucla.drc.sledge.ProjectModel;
import edu.ucla.drc.sledge.topicsettings.Topic;
import edu.ucla.drc.sledge.topicsettings.TopicSummary;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;


public class ModelVisualization extends AnchorPane implements LoadsFxml {

    @FXML private TextField topicModelName;
    @FXML private ScrollPane allTopicsPane;
    @FXML private ModelSummaryTab modelSummary;
    @FXML private DocumentTopicReport documentSummaryViewer;
    @FXML private TabPane topicDetails;
    private ProjectModel model;

    public ModelVisualization () {
        loadFxml();
    }

//    public void setData (ObjectProperty<TopicModel> selectedTopicModel) {
//        selectedTopicModel.addListener((ObservableValue<? extends TopicModel> observable, TopicModel oldValue, TopicModel newValue) -> {
//            modelSummaryTab.setData(newValue);
//        });
//    }

    void setProjectModel (ProjectModel model) {
        this.model = model;
        documentSummaryViewer.setModel(model);
    }

    public void setTopicModel (TopicModel model) {
        topicModelName.setText(model.getTitle());
        topicModelName.textProperty().addListener((observable, oldValue, newValue) -> {
            model.setTitle(newValue);
        });
        updateAllTopicsPane(model);
        modelSummary.setData(model);
        documentSummaryViewer.selectTopicModel(model);
        topicDetails.setVisible(true);
    }

    private void updateAllTopicsPane (TopicModel model) {
        FlowPane pane = new FlowPane();

        Alphabet alphabet = model.getAlphabet();

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
