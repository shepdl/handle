package edu.ucla.drc.sledge.topicmodeling;

import cc.mallet.topics.TopicModel;
import edu.ucla.drc.sledge.LoadsFxml;
import edu.ucla.drc.sledge.documents.Document;
import edu.ucla.drc.sledge.project.ProjectModel;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URI;

public class DocumentTopicReport extends BorderPane implements LoadsFxml {

    private ProjectModel model;
    private TopicModel topicModel;

    @FXML TreeView<Document> documentList;
    private final TreeItem rootTreeItem = new TreeItem("Documents");
    @FXML BarChart<String, Double> documentSummary;
    @FXML private PieChart compositionChart;
    @FXML private Button exportChartButton;
    @FXML private Button exportDocumentTopicsReportButton;

    public DocumentTopicReport () {
        loadFxml();
    }

    @FXML void initialize () {
        documentList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        documentList.setRoot(rootTreeItem);
        documentList.getSelectionModel().selectedItemProperty().addListener((observableValue, documentTreeItem, selectedItem) -> {
            if (selectedItem.getValue() != null) {
                updateChart(selectedItem.getValue());
            }
        });

        exportChartButton.addEventHandler(MouseEvent.MOUSE_CLICKED, this::exportChartButtonHandler);
        exportDocumentTopicsReportButton.addEventHandler(MouseEvent.MOUSE_CLICKED, this::exportDocumentTopicsReportButton);
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
            data.add(new PieChart.Data(topicModel.topicTitles[i], topicWeights[i]));
        }
        compositionChart.setData(data);
        exportChartButton.setVisible(true);
    }

    private void update() {
        // Set topic list
        // Set event listener
    }

    public void exportChartButtonHandler (MouseEvent event) {
        event.consume();
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

    public void exportDocumentTopicsReportButton(MouseEvent event) {
        event.consume();

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Microsoft Excel File (*.xlsx)", "*.xlsx");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setTitle("Select output file");
        File file = fileChooser.showSaveDialog(getScene().getWindow());
        if (file == null) {
            return;
        }
        Workbook workbook = new XSSFWorkbook();

        Sheet dtrWorksheet = workbook.createSheet("Document Topics Report");
        Row headerRow = dtrWorksheet.createRow(0);
        Cell indexCell = headerRow.createCell(0);
        indexCell.setCellValue("Index");
        Cell filenameCell = headerRow.createCell(1);
        filenameCell.setCellValue("Filename");
        for (int i = 0; i < topicModel.numTopics; i++) {
            int cellIndex = i + 2;
            Cell topicNameCell = headerRow.createCell(cellIndex);
            topicNameCell.setCellValue(topicModel.topicTitles[i]);
        }

        double[][] documentTopics = topicModel.getDocumentTopics(true, false);
        for (int docIndex = 0; docIndex < topicModel.data.size(); docIndex++) {
            int rowCounter = 1 + docIndex;
            Row dataRow = dtrWorksheet.createRow(rowCounter);
            Cell docIndexCell = dataRow.createCell(0);
            docIndexCell.setCellValue(docIndex);
            Cell docFilenameCell = dataRow.createCell(1);
            URI filenameUri = (URI)topicModel.data.get(docIndex).instance.getName();
            String filename = filenameUri.toString();
            docFilenameCell.setCellValue(filename);
            for (int topicIndex = 0; topicIndex < topicModel.numTopics; topicIndex++) {
                Cell dataCell = dataRow.createCell(topicIndex + 2);
                dataCell.setCellValue(documentTopics[docIndex][topicIndex]);
            }
        }

        Sheet topicWordsSheet = workbook.createSheet("Topic Keys");
        Row topicWordHeaderRow = topicWordsSheet.createRow(0);
        Cell topicIndexCell = topicWordHeaderRow.createCell(0);
        topicIndexCell.setCellValue("Topic ID");
        Cell topicTitleCell = topicWordHeaderRow.createCell(1);
        topicTitleCell.setCellValue("Title");
        Cell wordHeaderCell = topicWordHeaderRow.createCell(2);
        wordHeaderCell.setCellValue("Words");
        int wordCount = 10;
        Object[][] topWords = topicModel.getTopWords(wordCount);
        for (int topicIndex = 0; topicIndex < topicModel.numTopics; topicIndex++) {
            Row dataRow = topicWordsSheet.createRow(topicIndex + 1);
            Cell idCell = dataRow.createCell(0);
            idCell.setCellValue(topicIndex);
            Cell titleCell = dataRow.createCell(1);
            titleCell.setCellValue(topicModel.topicTitles[topicIndex]);
            for (int i = 0; i < wordCount; i++) {
                Cell wordCell = dataRow.createCell(i + 2);
                wordCell.setCellValue((String)topWords[topicIndex][i]);
            }
        }

        try {
            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
