package edu.ucla.drc.sledge;

import edu.ucla.drc.sledge.documentlist.DocumentTabController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;

public class SledgeApp extends Application {

    private HBox documentList;
    private HBox topicSetsList;

    private Stage rootStage;

    @Override
    public void start(final Stage primaryStage) throws Exception {
        rootStage = primaryStage;
        primaryStage.setTitle("Sledge");

        final VBox root = new VBox();

        final TabPane tabPane = new TabPane();
        final Tab documentListTab = new Tab("Documents");
        final Tab topicSetTab = new Tab("Topics");

        ProjectModel projectModel = new ProjectModel();

        DocumentTabController tabController = new DocumentTabController(documentListTab, projectModel);
        tabController.initialize(documentListTab);

        tabPane.getTabs().add(documentListTab);
        tabPane.getTabs().add(topicSetTab);

        Scene rootScene = new Scene(tabPane);
        primaryStage.setScene(rootScene);

        primaryStage.show();
    }

}
