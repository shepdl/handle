package edu.ucla.drc.sledge.topicsettings;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class TopicDetails extends VBox {

    private final Topic topic;
    private final TopicDocumentContainerSummary summary;
    private final Stage stage;
    @FXML private TextField titleField;
    @FXML private LineChart<String, Number> wordDistributionChart;
    @FXML private BarChart<String, Number> documentDistributionChart;

    public TopicDetails (Topic topic, TopicDocumentContainerSummary summary) {
        this.topic = topic;
        this.summary = summary;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TopicDetails.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        stage = new Stage();
        try {
            stage.setScene(new Scene(
                fxmlLoader.load(), 450, 450
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Topic " + topic.getId());

    }

    @FXML void initialize () {
    }

    public void setData () {
        XYChart.Series series = new XYChart.Series();
        StringBuilder titleBuilder = new StringBuilder();
        titleBuilder.append(topic.getTopWords().get(0));
        titleBuilder.append("/");
        titleBuilder.append(topic.getTopWords().get(1));
        titleBuilder.append("/");
        titleBuilder.append(topic.getTopWords().get(2));

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

        XYChart.Series documentSeries = new XYChart.Series();

        for (TopicDocumentSummary topicDocumentSummary : summary.getItems()) {
            documentSeries.getData().add(
//                new XYChart.Data<String, Number>(
//                    topicDocumentSummary.getDocumentName(),
//                    topicDocumentSummary.getProportion()
//                )
                    new XYChart.Data<Number, String>(
                            topicDocumentSummary.getProportion(),
                            topicDocumentSummary.getDocumentName()
                    )
            );
        }

        documentDistributionChart.getData().add(documentSeries);

        stage.show();
    }

}
