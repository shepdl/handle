package edu.ucla.drc.sledge;

import cc.mallet.topics.TopicModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class DocumentSummary extends VBox {

    @FXML private Label titleLabel;
    @FXML private PieChart compositionChart;

    private final Document document;
    private List<Document> allDocs;
    private final TopicModel topicModel;

    public DocumentSummary (Document document, List<Document> allDocs, TopicModel topicModel) {
        this.document = document;
        this.allDocs = allDocs;
        this.topicModel = topicModel;

        URL path = getClass().getResource("");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DocumentSummary.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        final Stage stage = new Stage();
        try {
            stage.setScene(new Scene(fxmlLoader.load(), 450, 450));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        stage.setTitle(document.getFile().getName());

        stage.show();

    }

    @FXML
    public void initialize () {
        titleLabel.setText(document.getFile().getName());

        int[] topicWeights = topicModel.getTopicWeightsForDocument(allDocs.indexOf(document));
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();

        for (int i = 0; i < topicWeights.length; i++) {
            data.add(new PieChart.Data("Topic " + i, topicWeights[i]));
        }

        compositionChart.setData(data);

    }

}
