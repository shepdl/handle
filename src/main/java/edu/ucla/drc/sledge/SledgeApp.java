package edu.ucla.drc.sledge;

import edu.ucla.drc.sledge.documentlist.DocumentTabController;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
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

    @FXML private HBox documentList;
    @FXML private HBox topicSetsList;

    private Stage rootStage;

    @Override
    public void start(final Stage primaryStage) throws Exception {
        rootStage = primaryStage;
//        primaryStage.setTitle("Sledge");


        FXMLLoader loader = new FXMLLoader(getClass().getResource("SledgeAppRoot.fxml"));
//        Parent root = loader.getRoot();
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.show();


//        final VBox root = new VBox();
//
//        final TabPane tabPane = new TabPane();
//        final Tab documentListTab = new Tab("Documents");
//        final Tab topicSetTab = new Tab("Topics");
//
//        ProjectModel projectModel = new ProjectModel();
//
//        DocumentTabController tabController = new DocumentTabController(documentListTab, projectModel);
//        tabController.initialize(documentListTab);
//
//        tabPane.getTabs().add(documentListTab);
//        tabPane.getTabs().add(topicSetTab);
//
//        Scene rootScene = new Scene(tabPane);
//        primaryStage.setScene(rootScene);
//
//        primaryStage.show();
    }

}
