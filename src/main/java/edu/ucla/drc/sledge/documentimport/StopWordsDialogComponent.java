package edu.ucla.drc.sledge.documentimport;

import edu.ucla.drc.sledge.project.ProjectModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

import java.io.File;
import java.util.*;

public class StopWordsDialogComponent {

    @FXML TableView<String> stopwordsTable;
    @FXML Button saveButton;
    @FXML Button cancelButton;
    @FXML Button clearStopwordsButton;

    @FXML ComboBox<StopwordSource> defaultStopwordsComboBox;

    private ProjectModel project;

    Alert confirmAlert;

    public void initialize (ProjectModel project, StopwordListsSource stopwordsSource) {
        this.project = project;
        mergeNewListWithExistingStopwords(new ListStopwordsList("model", new ArrayList<>(project.getStopwords())));
        clearStopwordsButton.setOnMouseClicked(this::clearStopwordsButtonClickHandler);

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
            if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                return;
            }
        }
    }
}
