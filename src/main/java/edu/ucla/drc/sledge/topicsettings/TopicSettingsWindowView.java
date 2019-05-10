package edu.ucla.drc.sledge.topicsettings;

import edu.ucla.drc.sledge.TopicTrainingJob;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sun.font.TextLabel;

public class TopicSettingsWindowView {

    private final Stage stage;
    private final boolean initialized;
    private TopicSettingsWindowModel model;
    private TopicSettingsWindowController controller;

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

        HBox topicCountRoot = new HBox();
        TextField topicCountField = new TextField();
        topicCountField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    int numTopics = Integer.parseInt(newValue);
                    topicCountField.setText(newValue);
                    model.setNumTopics(numTopics);
                } catch (NumberFormatException ex) {
                    topicCountField.setText(oldValue);
                }
            }
        });
        topicCountRoot.getChildren().addAll(
                new Label("Topic Count"),
                topicCountField
        );


        TitledPane advancedPane = buildAdvancedOptions();

        Button runButton = new Button("Run");
        runButton.setOnMouseClicked((event) -> {
            // Get project model
            model.setTrainingInProgress(true);
            controller.executeJob();
            model.setTrainingInProgress(false);
        });

        Button cancelButton = new Button("Cancel");

        ProgressBar trainingProgress = new ProgressBar();
//        trainingProgress.visibleProperty().bindBidirectional(model.trainingInProgress);

//        trainingProgress.setVisible(false);

        root.getChildren().addAll(
            topicCountRoot, advancedPane, trainingProgress, runButton, cancelButton
        );

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Topic Model settings");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    private TitledPane buildAdvancedOptions() {
        TitledPane pane = new TitledPane();
        pane.setText("Advanced ...");
        pane.setCollapsible(true);


        TextField alphaField = new TextField();
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
        alphaField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            try {
                double alpha = Double.parseDouble(newValue);
                betaField.setText(newValue);
                model.setAlpha(alpha);
            } catch (NumberFormatException ex) {
                betaField.setText(oldValue);
            }
        });

        Label randomSeedLabel = new Label("Random Seed");
        TextField randomSeedField = new TextField();
        alphaField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            try {
                double alpha = Double.parseDouble(newValue);
                randomSeedField.setText(newValue);
                model.setAlpha(alpha);
            } catch (NumberFormatException ex) {
                randomSeedField.setText(oldValue);
            }
        });

        Label iterationsLabel = new Label("Iterations");
        TextField iterationCountField = new TextField();
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
