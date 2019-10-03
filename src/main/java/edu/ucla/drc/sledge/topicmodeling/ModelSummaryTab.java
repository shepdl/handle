package edu.ucla.drc.sledge.topicmodeling;

import cc.mallet.topics.TopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import edu.ucla.drc.sledge.LoadsFxml;
import edu.ucla.drc.sledge.topicsettings.Topic;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;

public class ModelSummaryTab extends AnchorPane implements LoadsFxml {
    private TopicModel model;

    @FXML private BubbleChart topicDistance;
    @FXML private BarChart topWords;

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
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Image file (*.png)", "*.png");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setTitle("Select output file");
        File file = fileChooser.showSaveDialog(getScene().getWindow());
        if (file != null) {
            try {
                WritableImage image = topicDistance.snapshot(new SnapshotParameters(), null);
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setData (TopicModel model) {
        this.model = model;
        update();
    }

    private void update () {
        XYChart.Series<Double, Double> series = new XYChart.Series<>();
        // TODO: suppress series name
        // TODO: suppress labels
        // TODO: suppress grid lines
        series.setName("Topics");
        for (int i = 0; i < model.numTopics; i++) {
            // TODO: calculate topic similarity graph
            // TODO: Add event listeners to data objects that have been clicked
            // TO add event listeners, for the series of XYChart.Series events, get the Node that corresponds to
            // the data item and call setOnMouseClicked()
            XYChart.Data<Double, Double> datum = new XYChart.Data<>(i * 1.0, i * 2.0);
//            datum.setExtraValue(i);
            final int q = i;
            datum.nodeProperty().addListener(((observableValue, oldValue, newValue) -> {
                final int topicId = q;
                newValue.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle (MouseEvent event) {
                        showTopic(topicId);
                    }
                });
            }));
            series.getData().add(datum);
        }
        topicDistance.getData().add(series);
    }

    private void showTopic (int topicIndex) {
        BarChart.Series<Number, String> series = new BarChart.Series<>();
        TreeSet<IDSorter> sortedWords = model.getSortedWords().get(topicIndex);
        Alphabet alphabet = model.getAlphabet();
        int counter = 0;
        int limit = 10;
        Iterator items = sortedWords.iterator();
        List<BarChart.Data> dataList = new ArrayList<>();
        while (counter < limit && items.hasNext()) {
            IDSorter item = (IDSorter) items.next();
            dataList.add(new BarChart.Data<>(
                    item.getWeight(),
                    (String)alphabet.lookupObject(item.getID())
            ));
            counter++;
        }
        Collections.reverse(dataList);
        for (BarChart.Data item : dataList) {
            series.getData().add(item);
        }

        topWords.getData().clear();
        topWords.getData().add(series);
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
