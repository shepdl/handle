package edu.ucla.drc.sledge.documentimport.stopwords;

import edu.ucla.drc.sledge.project.ProjectModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class StopWordsDialogComponent extends AnchorPane {

    @FXML TableView<String> stopwordsTable;
    @FXML TableColumn<String, String> wordColumn;
    @FXML Button saveButton;
    @FXML Button cancelButton;
    @FXML Button clearStopwordsButton;
    @FXML Button exportButton;

    @FXML ComboBox<StopwordSource> defaultStopwordsComboBox;

    private ProjectModel project;
    private Consumer closeHandler;

    Alert confirmAlert;

    public void initialize (ProjectModel project, StopwordListsSource stopwordsSource, Consumer closeHandler) {
        this.project = project;
        this.closeHandler = closeHandler;
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
        closeHandler.accept(null);
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
                } else {
                    closeHandler.accept(null);
                }
            }
        } else {
            closeHandler.accept(null);
        }
    }

    @FXML
    private void exportButtonClicked(MouseEvent mouseEvent) {
        mouseEvent.consume();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Text file", "*.txt");
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(filter);
        chooser.setTitle("Select output file");
        File file = chooser.showSaveDialog(null);
        if (file != null) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                for (String word : stopwordsTable.getItems()) {
                    writer.append(word);
                    writer.append("\n");
                }
                writer.close();
            } catch (IOException ex) {
                Alert unhandledError = new Alert(Alert.AlertType.ERROR);
                unhandledError.setHeaderText("Error saving file");
                unhandledError.show();
            }
        }
    }
}
