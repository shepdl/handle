package edu.ucla.drc.sledge.documentimport.stopwords;

import edu.ucla.drc.sledge.project.ProjectModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.io.File;
import java.util.*;

public class StopWordsDialogComponent extends AnchorPane {

    @FXML TableView<String> stopwordsTable;
    @FXML TableColumn<String, String> wordColumn;
    @FXML Button saveButton;
    @FXML Button cancelButton;
    @FXML Button clearStopwordsButton;

    @FXML ComboBox<StopwordSource> defaultStopwordsComboBox;

    private ProjectModel project;

    Alert confirmAlert;

    @FXML
    public void initialize (ProjectModel project, StopwordListsSource stopwordsSource) {
        this.project = project;
        mergeNewListWithExistingStopwords(new ListStopwordsList("model", new ArrayList<>(project.getStopwords())));
        clearStopwordsButton.setOnMouseClicked(this::clearStopwordsButtonClickHandler);

        wordColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));

        Callback<ListView<StopwordSource>, ListCell<StopwordSource>> cellFactory = new Callback<ListView<StopwordSource>, ListCell<StopwordSource>>() {
            @Override
            public ListCell<StopwordSource> call(ListView<StopwordSource> param) {
                return new ListCell<StopwordSource> () {
                    @Override
                    protected void updateItem (StopwordSource item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setText(item.getName());
                        }
                    }
                };
            }
        };

        defaultStopwordsComboBox.setCellFactory(cellFactory);

        for (StopwordSource source : stopwordsSource.list()) {
            defaultStopwordsComboBox.getItems().add(source);
        }
    }

    @FXML
    private void addWordsFromDefaultFile(ActionEvent actionEvent) {
        mergeNewListWithExistingStopwords(defaultStopwordsComboBox.getValue());
    }

    @FXML
    private void setTransferMode(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        dragEvent.consume();
    }

    @FXML
    private void dropFile(DragEvent dragEvent) {
        Dragboard db = dragEvent.getDragboard();
        for (File file : db.getFiles()) {
            StopwordSource source = new StopwordFile(file);
            mergeNewListWithExistingStopwords(source);
        }
        dragEvent.setDropCompleted(true);
        dragEvent.consume();
    }

    void mergeNewListWithExistingStopwords (StopwordSource newSource) {
        Set<String> currentWords = new HashSet<>(stopwordsTable.getItems());
        currentWords.addAll(newSource.provideWords());
        List<String> completeWords = new ArrayList<>(currentWords);
        Collections.sort(completeWords);
        stopwordsTable.getItems().clear();
        stopwordsTable.getItems().addAll(completeWords);
    }

    @FXML
    private void clearStopwordsButtonClickHandler (MouseEvent mouseEvent) {
        stopwordsTable.getItems().clear();
    }

    @FXML
    private void saveStopwordsButtonClickHandler (MouseEvent mouseEvent) {
        project.setStopwords(new HashSet<>(stopwordsTable.getItems()));
    }

    @FXML
    private void cancelButtonClicked(MouseEvent mouseEvent) {
        mouseEvent.consume();
        Set<String> tableWords = new HashSet<String>(stopwordsTable.getItems());
        Set<String> projectWords = project.getStopwords();
        if (!tableWords.equals(projectWords)) {
            confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Stopwords have changed");
            confirmAlert.setHeaderText("Are you sure you want to discard your changes?");
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.CANCEL) {
                    return;
                }
            }
        }
    }
}
