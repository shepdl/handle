package edu.ucla.drc.sledge.topicsettings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class TopicSettingsWindowView {

    private final Stage stage;
    private final boolean initialized;
    private TopicSettingsWindowModel model;
    private TopicSettingsWindowController controller;
    private TitledPane topicResults;
    private ScrollPane topicScrollPane;

    public TopicSettingsWindowView(TopicSettingsWindowModel model, TopicSettingsWindowController topicSettingsWindowController) {
        this.model = model;
        controller = topicSettingsWindowController;
        this.stage = new Stage();
        this.initialized = false;
    }

    public void show() {
        if (!this.initialized) {
            this.initialize();
        }
    }

    private void initialize() {
        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);

        HBox topicCountRoot = new HBox();
        TextField topicCountField = new TextField();
        topicCountField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    int topicCount = Integer.parseInt(newValue);
                    topicCountField.setText(newValue);
                    model.setNumTopics(topicCount);
                } catch (NumberFormatException ex) {
                    topicCountField.setText(oldValue);
                }
            }
        });
        topicCountField.setText(Integer.toString(model.getNumTopics()));
        topicCountRoot.getChildren().addAll(
                new Label("Topic Count"),
                topicCountField
        );


        TitledPane advancedPane = buildAdvancedOptions();

        Button runButton = new Button("Run");
        runButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            private boolean running = false;
            @Override
            public void handle(MouseEvent event) {
                // Get project model
                if (!running) {
                    model.setTrainingInProgress(true);
                    controller.executeJob();
                    running = true;
                    runButton.setText("Stop");
                } else {
                    running = false;
                    model.setTrainingInProgress(false);
                    controller.stopJob();
                    runButton.setText("Run");
                }
//            model.setTrainingInProgress(false);
            }
        });

        Button cancelButton = new Button("Cancel");

        ProgressBar trainingProgress = new ProgressBar();
        trainingProgress.visibleProperty().bindBidirectional(model.trainingInProgress);
        trainingProgress.prefWidthProperty().bind(root.widthProperty().subtract(20));

//        trainingProgress.setVisible(false);

        model.progress.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                trainingProgress.setProgress(newValue.doubleValue());
            }
        });

//        TitledPane topicResults = buildTopicResults();
        topicResults = initializeTopicTopWordsLineCharts();

        ButtonBar controlButtons = new ButtonBar();
        controlButtons.getButtons().add(runButton);
        controlButtons.getButtons().add(cancelButton);

        root.getChildren().addAll(
            topicCountRoot, advancedPane,
                trainingProgress,
                topicResults,
                controlButtons
        );

        Scene scene = new Scene(root, 800, 600);

        stage.setScene(scene);
        stage.setTitle("Topic Model settings");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    private TitledPane initializeTopicTopWordsLineCharts () {
        TitledPane pane = new TitledPane();
        pane.setText("Results");
        pane.setCollapsible(true);
        topicScrollPane = new ScrollPane();
        topicScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        pane.setContent(topicScrollPane);
        return pane;
    }

    public int updateTopicResults (List<Topic> topics) {
//        HBox root = new HBox();
        topicScrollPane.setContent(null);
        GridPane pane = new GridPane();
        int widthLimit = 3;
        int widthCounter = 0;
        int heightCounter = 0;
        for (Topic topic : topics) {
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();

            LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

            XYChart.Series series = new XYChart.Series();
            StringBuilder titleBuilder = new StringBuilder();
            titleBuilder.append(topic.getTopWords().get(0));
            titleBuilder.append("/");
            titleBuilder.append(topic.getTopWords().get(1));
            titleBuilder.append("/");
            titleBuilder.append(topic.getTopWords().get(2));

            series.setName("Topic " + Integer.toString(topic.getId()));
            series.setName(titleBuilder.toString());

            for (int i = 0; i < topic.getTopWords().size() && i < 10; i++) {
                series.getData().add(
                    new XYChart.Data<String, Number>(
                        topic.getTopWords().get(i),
                        topic.getTopWordCounts().get(i)
                    )
                );
            }

            lineChart.getData().add(series);

            pane.add(lineChart, widthCounter, heightCounter);
            widthCounter += 1;
            if (widthCounter >= widthLimit) {
                widthCounter = 0;
                heightCounter += 1;
            }
        }

        topicScrollPane.setContent(pane);

        return 0;
    }

//    String updateTopicTopWordsLineCharts (List <) {
//
//    }

    private TitledPane buildTopicResults() {
        TitledPane pane = new TitledPane();
        pane.setText("Results");
        pane.setCollapsible(true);

        TableView<TopicSettingsWindowController.TopTenWords> topicTopWordsTable = new TableView<>();

        for (int i = 0; i < 10; i++) {
            TableColumn wordColumn = new TableColumn("Word " + i);
            wordColumn.setCellValueFactory(new PropertyValueFactory<TopicSettingsWindowController.TopTenWords, String>("word" + i));
            topicTopWordsTable.getColumns().add(wordColumn);
        }

        topicTopWordsTable.setItems(controller.getTopWords());

        pane.setContent(topicTopWordsTable);


        /*
        controller.getTopWords().addListener(new ListChangeListener<ObservableList<String>>() {
            @Override
            public void onChanged(Change<? extends ObservableList<String>> c) {
                if (c.wasAdded()) {
                    int paneWidth = 5;
                    int currentColumn = 0;
                    int currentRow = 0;
                    GridPane newRoot = new GridPane();
                    for (int i = 0; i < controller.getTopWords().size(); i++) {
                        TableView<String> tableView = new TableView<>();
                        TableColumn wordColumn = new TableColumn("Word");
                        tableView.getColumns().addAll(wordColumn);
                        tableView.setItems(controller.getTopWords().get(i));

                        newRoot.add(tableView, currentColumn, currentRow);
                        currentColumn += 1;
                        if (currentColumn > paneWidth - 1) {
                            currentColumn = 0;
                            currentRow += 1;
                        }
                    }
                    pane.setContent(newRoot);
                } else if (c.wasRemoved()) {
                    int paneWidth = 5;
                    int currentColumn = 0;
                    int currentRow = 0;
                    GridPane newRoot = new GridPane();
                    for (int i = 0; i < controller.getTopWords().size(); i++) {
                        TableView<String> tableView = new TableView<>();
                        TableColumn wordColumn = new TableColumn("Word");
                        tableView.getColumns().addAll(wordColumn);
                        tableView.setItems(controller.getTopWords().get(i));

                        newRoot.add(tableView, currentColumn, currentRow);
                        currentColumn += 1;
                        if (currentColumn > paneWidth - 1) {
                            currentColumn = 0;
                            currentRow += 1;
                        }
                    }
                    pane.setContent(newRoot);
                }
            }
        });

         */

        return pane;
    }

    private TitledPane buildAdvancedOptions() {
        TitledPane pane = new TitledPane();
        pane.setText("Advanced ...");
        pane.setCollapsible(true);


        TextField alphaField = new TextField();
        alphaField.setText(Double.toString(model.getAlpha()));
        alphaField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            try {
                double alpha = Double.parseDouble(newValue);
                alphaField.setText(newValue);
                model.setAlpha(alpha);
            } catch (NumberFormatException ex) {
                alphaField.setText(oldValue);
            }
        });
        Label alphaLabel = new Label("alpha");

        Label betaLabel = new Label("beta");
        TextField betaField = new TextField();
        betaField.setText(Double.toString(model.getBeta()));
        betaField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            try {
                double beta = Double.parseDouble(newValue);
                betaField.setText(newValue);
                model.setBeta(beta);
            } catch (NumberFormatException ex) {
                betaField.setText(oldValue);
            }
        });

        Label randomSeedLabel = new Label("Random Seed");
        TextField randomSeedField = new TextField();
        randomSeedField.setText(Integer.toString(model.getRandomSeed()));
        randomSeedField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            try {
                int randomSeed = Integer.parseInt(newValue);
                randomSeedField.setText(newValue);
                model.setRandomSeed(randomSeed);
            } catch (NumberFormatException ex) {
                randomSeedField.setText(oldValue);
            }
        });

        Label iterationsLabel = new Label("Iterations");
        TextField iterationCountField = new TextField();
        iterationCountField.setText(Integer.toString(model.getIterationCount()));
        iterationCountField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int iterationCount = Integer.parseInt(newValue);
                iterationCountField.setText(newValue);
                model.setIterationCount(iterationCount);
            } catch (NumberFormatException ex) {
                iterationCountField.setText(oldValue);
            }
        });

        Label optimizeLabel = new Label("Optimize Interval");
        TextField optimizeIntervalField = new TextField();
        optimizeIntervalField.setText(Integer.toString(model.getOptimizeInterval()));
        optimizeIntervalField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int optimizeInterval = Integer.parseInt(newValue);
                optimizeIntervalField.setText(newValue);
                model.setOptimizeInterval(optimizeInterval);
            } catch (NumberFormatException ex) {
                optimizeIntervalField.setText(oldValue);
            }
        });

        Label burnInPeriodLabel = new Label("Burn in period");
        TextField burnInPeriodField = new TextField();
        burnInPeriodField.setText(Integer.toString(model.getBurnInPeriod()));
        burnInPeriodField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int burnInPeriod = Integer.parseInt(newValue);
                burnInPeriodField.setText(newValue);
                model.setBurnInPeriod(burnInPeriod);
            } catch (NumberFormatException ex) {
                burnInPeriodField.setText(oldValue);
            }
        });

        Label threadCountLabel = new Label("Threads");
        TextField threadCountField = new TextField();
        threadCountField.setText(Integer.toString(model.getThreadCount()));
        threadCountField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int threadCount = Integer.parseInt(newValue);
                threadCountField.setText(newValue);
                model.setThreadCount(threadCount);
            } catch (NumberFormatException ex) {
                threadCountField.setText(oldValue);
            }
        });

        GridPane root = new GridPane();
        root.setAlignment(Pos.CENTER);

        root.add(alphaLabel, 0, 1);
        root.add(alphaField, 1, 1);
        root.add(betaLabel, 0, 2);
        root.add(betaField, 1, 2);
        root.add(randomSeedLabel, 0, 3);
        root.add(randomSeedField, 1, 3);
        root.add(iterationsLabel, 0, 4);
        root.add(iterationCountField, 1, 4);
        root.add(optimizeLabel, 0, 5);
        root.add(optimizeIntervalField, 1, 5);
        root.add(burnInPeriodLabel, 0, 6);
        root.add(burnInPeriodField, 1, 6);
        root.add(threadCountLabel, 0, 7);
        root.add(threadCountField, 1, 7);

        pane.setContent(root);

        return pane;
    }
}
