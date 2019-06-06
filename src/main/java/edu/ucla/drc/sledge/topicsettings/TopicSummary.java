package edu.ucla.drc.sledge.topicsettings;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;


public class TopicSummary extends VBox {

    @FXML private TextField titleField;
    @FXML private LineChart<String, Number> wordDistributionChart;
    @FXML private Button detailsButton;
    private Topic topic;
    private TopicDocumentContainerSummary summary;

    public TopicSummary () {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TopicSummary.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML void initialize () {
        detailsButton.setOnMouseClicked(this::showDetails);
    }

    public void setData (Topic topic, TopicDocumentContainerSummary summary) {
        this.topic = topic;
        this.summary = summary;

        XYChart.Series series = new XYChart.Series();
        StringBuilder titleBuilder = new StringBuilder();
        if (topic.getTopWords().size() > 0) {
            titleBuilder.append(topic.getTopWords().get(0));
        } else {
            titleBuilder.append("Topic " + topic.getId());
        }
        if (topic.getTopWords().size() > 1) {
            titleBuilder.append("/");
            titleBuilder.append(topic.getTopWords().get(1));
        }
        if (topic.getTopWords().size() > 2) {
            titleBuilder.append("/");
            titleBuilder.append(topic.getTopWords().get(2));
        }

        titleField.setText(titleBuilder.toString());

        for (int i = 0; i < topic.getTopWords().size() && i < 10; i++) {
            series.getData().add(
                    new XYChart.Data<String, Number>(
                            topic.getTopWords().get(i),
                            topic.getTopWordCounts().get(i)
                    )
            );
        }

        wordDistributionChart.getData().add(series);
    }

    public void showDetails (MouseEvent event) {
        TopicDetails controller = new TopicDetails(topic, summary);
        controller.setData();
    }

}
