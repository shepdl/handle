package edu.ucla.drc.sledge.topicmodeling;

import cc.mallet.topics.TopicModel;
import edu.ucla.drc.sledge.Document;
import edu.ucla.drc.sledge.LoadsFxml;
import edu.ucla.drc.sledge.ProjectModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class DocumentTopicReport extends BorderPane implements LoadsFxml {

    private ProjectModel model;
    private TopicModel topicModel;

    @FXML TreeView<Document> documentList;
    private final TreeItem rootTreeItem = new TreeItem("Documents");
    @FXML BarChart<String, Double> documentSummary;
    @FXML private PieChart compositionChart;
    @FXML private Button exportChartButton;

    public DocumentTopicReport () {
        loadFxml();
    }

    @FXML void initialize () {
        System.out.println("Initializing Document Topics Report");
        documentList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        documentList.setRoot(rootTreeItem);
        documentList.getSelectionModel().selectedItemProperty().addListener((observableValue, documentTreeItem, selectedItem) -> {
            if (selectedItem.getValue() != null) {
                updateChart(selectedItem.getValue());
            }
        });

        exportChartButton.addEventHandler(MouseEvent.MOUSE_CLICKED, this::exportChartButonHandler);
    }

    public void setModel (ProjectModel model) {
        this.model = model;
        model.getDocuments().addListener((ListChangeListener.Change<? extends Document> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    change.getAddedSubList().forEach(doc -> {
                        TreeItem<Document> treeItem = new TreeItem<>(doc);
                        rootTreeItem.getChildren().add(treeItem);
                    });
                }
            }
        });
    }

    public void selectTopicModel(TopicModel model) {
        this.topicModel = model;
        update();
    }

    private void updateChart (Document document) {
        int[] topicWeights = topicModel.getTopicWeightsForDocument(model.getDocuments().indexOf(document));
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();

        for (int i = 0; i < topicWeights.length; i++) {
            data.add(new PieChart.Data("Topic " + i, topicWeights[i]));
        }
        compositionChart.setData(data);
        exportChartButton.setVisible(true);
    }

    private void update() {
        // Set topic list
        // Set event listener
    }

    public void exportChartButonHandler (MouseEvent event) {
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("PNG image (*.png)", "*.png");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setTitle("Select output file");
        File file = fileChooser.showSaveDialog(getScene().getWindow());
        if (file != null) {
            WritableImage image = compositionChart.snapshot(new SnapshotParameters(), null);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
