package edu.ucla.drc.sledge.topicmodeling;

import cc.mallet.topics.TopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import edu.ucla.drc.sledge.LoadsFxml;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import smile.math.distance.JensenShannonDistance;
import smile.mds.MDS;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class ModelSummaryTab extends AnchorPane implements LoadsFxml {
    private TopicModel model;

    @FXML private BubbleChart topicDistance;
    @FXML private StackedBarChart topWords;
    @FXML private CategoryAxis wordsAxis;
    @FXML private Button exportSingleTopicButton;

    private int selectedTopicIndex = -1;

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

    public void exportSingleTopic (MouseEvent mouseEvent) {
        if (selectedTopicIndex != -1) {
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Image file (*.png)", "*.png");
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(filter);
            fileChooser.setTitle("Select output file");
            File file = fileChooser.showSaveDialog(getScene().getWindow());
            if (file != null) {
                try {
                    WritableImage image = topWords.snapshot(new SnapshotParameters(), null);
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setData (TopicModel model) {
        this.model = model;
        update();
    }

    private void update () {
        exportSingleTopicButton.setVisible(false);
        selectedTopicIndex = -1;
        topicDistance.getData().clear();
        XYChart.Series<Double, Double> series = new XYChart.Series<>();
        // TODO: suppress series name
        // TODO: suppress labels
        generateTopicSimilarityGraph(model);
    }

    private void showTopic (int topicIndex) {
        TreeSet<IDSorter> sortedWords = model.getSortedWords().get(topicIndex);
        Alphabet alphabet = model.getAlphabet();
        int counter = 0;
        int limit = 10;
        Iterator items = sortedWords.iterator();

        topWords.getData().clear();
        topWords.setTitle(model.topicTitles[topicIndex]);
        XYChart.Series<String, Number> wordCountSeries = new StackedBarChart.Series<>();
        wordCountSeries.setName("Topic");
        XYChart.Series<String, Number> totalCountsSeries = new StackedBarChart.Series<>();
        totalCountsSeries.setName("Total");
        wordsAxis.getCategories().clear();
        List<IDSorter> categoryLabels = new ArrayList<>();
        while (counter < limit && items.hasNext()) {
            IDSorter item = (IDSorter) items.next();
            categoryLabels.add(item);
            counter += 1;
        }
        for (IDSorter item : categoryLabels) {
            wordsAxis.getCategories().add((String) alphabet.lookupObject(item.getID()));
        }
        for (IDSorter item : categoryLabels) {
            String title = (String)alphabet.lookupObject(item.getID());
            wordCountSeries.getData().add(new StackedBarChart.Data<String, Number>(title, item.getWeight()));
            int totalCount = 0;
            for (int topicCount = 0; topicCount < model.numTopics; topicCount++) {
                // TODO: Sometimes array out of bounds bugs appear here ...
                totalCount += model.typeTopicCounts[item.getID()][topicCount];
            }
            totalCountsSeries.getData().add(new StackedBarChart.Data<String, Number>(title, totalCount));
        }
        topWords.getData().addAll(wordCountSeries, totalCountsSeries);
        exportSingleTopicButton.setVisible(true);
        selectedTopicIndex = topicIndex;
    }

    private void generateTopicSimilarityGraph (TopicModel model) {
        // Source: https://slides.cpsievert.me/lda/0926/#/12
        // Calculate dissimilarity between two topics by measuring dissimilarity of distributions
        //      Use a measure like symmetric KL divergence
        // 2. Calculate similarities
        double[][] wordTotals = model.getTopicWords(true, false);
        double[][] similarities = new double[model.numTopics][model.numTopics];

        JensenShannonDistance distanceCalculator = new JensenShannonDistance();
        for (int i = 0; i < model.numTopics; i++) {
            for (int j = 0; j < model.numTopics; j++) {
                double distance = distanceCalculator.d(wordTotals[i], wordTotals[j]);
                similarities[i][j] = distance;
                similarities[j][i] = distance;
            }
        }
        // 3. Use MDS (multi-dimensional scaling) to scale points down to 2 dimensions
        MDS scale = new MDS(similarities);
        double[][] coordinates = scale.getCoordinates();
        BubbleChart.Series<Number, Number> series = new BubbleChart.Series<>();
        for (int i = 0; i < coordinates.length; i++) {
            BubbleChart.Data<Number, Number> datum = new BubbleChart.Data<>(coordinates[i][0] * 100, coordinates[i][1] * 100);
            final int outsideTopicId = i;
            datum.nodeProperty().addListener(((observableValue, oldValue, bubble) -> {
                final int topicId = outsideTopicId;
                bubble.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle (MouseEvent event) {
                        showTopic(topicId);
                    }
                });
            }));
            series.getData().add(datum);
        }
        topicDistance.getData().clear();
        ObservableList<BubbleChart.Series<Number, Number>> seriesList = FXCollections.observableArrayList();
        seriesList.add(series);
        topicDistance.setData(seriesList);
        for (int i = 0; i < series.getData().size(); i++) {
            BubbleChart.Data<Number, Number> datum = series.getData().get(i);
            Tooltip.install(datum.getNode(), new Tooltip(model.topicTitles[i]));
        }
    }

}
