package edu.ucla.drc.sledge;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class SledgeApp extends Application {

//    @FXML private HBox documentList;
//    @FXML private HBox topicSetsList;

    private Stage rootStage;

    @Override
    public void start(final Stage primaryStage) throws Exception {
        rootStage = primaryStage;
        primaryStage.setTitle("Sledge");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("SledgeAppRoot.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.show();

    }

}
