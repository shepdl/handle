package edu.ucla.drc.sledge.topicmodeling;

import cc.mallet.topics.TopicModel;
import edu.ucla.drc.sledge.LoadsFxml;
import edu.ucla.drc.sledge.topicsettings.Topic;
import javafx.fxml.FXML;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ModelSummaryTab extends AnchorPane implements LoadsFxml {
    private TopicModel model;

    @FXML private BubbleChart topicDistance;
    @FXML private LineChart topWords;

    public ModelSummaryTab () {
        loadFxml();
    }

    public void exportModel (MouseEvent mouseEvent) {
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("MALLET topic model (*.mallet)", "*.mallet");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setTitle("Select output file");
        File file = fileChooser.showSaveDialog(getScene().getWindow());
        if (file != null) {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
                oos.writeObject(model);
                oos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void exportTopics (MouseEvent mouseEvent) {
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Image file (*.jpeg)", "*.jpeg");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setTitle("Select output file");
        File file = fileChooser.showSaveDialog(getScene().getWindow());
        if (file != null) {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
                oos.writeObject(model);
                oos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setData (TopicModel model) {
        System.out.println("Data set");
        this.model = model;
        update();
    }

    private void update () {
        XYChart.Series<Double, Double> series = new XYChart.Series<>();
        for (int i = 0; i < model.numTopics; i++) {
            // TODO: calculate topic similarity graph
            // TODO: Add event listeners to data objects that have been clicked
            // TO add event listeners, for the series of XYChart.Series events, get the Node that corresponds to
            // the data item and call setOnMouseClicked()
        }
    }

    private void showSelectedTopicGraph (MouseEvent event) {
        // Get topic ID from item clicked
        // Update topic word graph
    }

    private void generateTopicSimilarityGraph (TopicModel model) {
        // Source: https://slides.cpsievert.me/lda/0926/#/12
        // Calculate dissimilarity between two topics by measuring dissimilarity of distributions
        //      Use a measure like symmetric KL divergence
        // 1. Count up the number of instances of every word
        int[] wordTotals = new int[model.alphabet.size()];
        for (int word = 0; word < model.alphabet.size(); word++) {
            for (int topic = 0; topic < model.numTopics; topic++) {
                wordTotals[word] += model.typeTopicCounts[word][topic];
            }
        }
        // 2. Calculate similarities
        double[][] similarities = new double[model.numTopics][model.numTopics];
        for (int i = 0; i < model.numTopics; i++) {
            for (int j = i; j < model.numTopics; j++) {
                for (int word = 0; word < model.alphabet.size(); word++) {
                    double probabilityOfPGivenX = ((double)model.typeTopicCounts[word][i] / (double)wordTotals[word]);
                    double probabilityOfQGivenX = ((double)model.typeTopicCounts[word][j] / (double)wordTotals[word]);
                    similarities[i][j] += probabilityOfPGivenX * Math.log(probabilityOfQGivenX / probabilityOfPGivenX );
                }
            }
        }
        // 3. Use MDS (multi-dimensional scaling) to scale points down to 2 dimensions

        // To return: (topicId, X, Y)
    }

}
