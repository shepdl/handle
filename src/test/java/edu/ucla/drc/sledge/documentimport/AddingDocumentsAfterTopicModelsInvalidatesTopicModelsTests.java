package edu.ucla.drc.sledge.documentimport;

import cc.mallet.topics.TopicModel;
import edu.ucla.drc.sledge.project.ProjectModel;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AddingDocumentsAfterTopicModelsInvalidatesTopicModelsTests extends ApplicationTest {

    private DocumentListComponent controller;
    private ProjectModel project;
    private Parent mainNode;

    @Ignore
    @Test
    public void addingDocumentsWhenTopicModelsHaveBeenGeneratedShowsWarning () {
        List<File> newFiles = new ArrayList<>();
        try {
            newFiles.add(File.createTempFile("test-4", "txt"));
            newFiles.add(File.createTempFile("test-5", "txt"));
            newFiles.add(File.createTempFile("test-6", "txt"));
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException when creating files in test method");
        }
//        Dragboard db = controller.getParent()
//                .getScene().startDragAndDrop(TransferMode.NONE);
        interact(() -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            Map<DataFormat, Object> content = new HashMap<>();
            content.put(DataFormat.FILES, newFiles);
//            db.setContent(content);
            clipboard.setContent(content);
            System.out.println("Dropping");
            FxRobot dragger = drag(mainNode, MouseButton.PRIMARY);
            System.out.println(lookup("#documentList").query());
//            drag(mainNode).dropTo("#documentList");
//            dragger.dropTo(mainNode);
            drag(mainNode);
            Dragboard db = mainNode.startDragAndDrop(TransferMode.COPY);
            DragEvent dragEvent = new DragEvent(null, mainNode, DragEvent.DRAG_DROPPED,
                    db,
                    0, 0, 0, 0,
                    TransferMode.COPY,
                    null, mainNode, null
            );
            controller.fireEvent(dragEvent);
                assertTrue(controller.eraseModelsConfirmationBox.isShowing());
        });
    }

    @Ignore
    @Test
    public void cancelingAddingDocumentsDoesNotInvalidateTopicModels () {
        List<File> newFiles = new ArrayList<>();
        try {
            newFiles.add(File.createTempFile("test-4", "txt"));
            newFiles.add(File.createTempFile("test-5", "txt"));
            newFiles.add(File.createTempFile("test-6", "txt"));
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException when creating files in test method");
        }
        interact(() -> {
            controller.prepareToAddFiles(newFiles);
            clickOn(lookup(ButtonType.CANCEL.getText()).queryButton());
            assertThat(project.getTopicModels(), hasSize(1));
            assertThat(project.getDocuments(), hasSize(3));
            assertThat(controller.getRoot().getChildren(), hasSize(3));
        });
    }

    @Test
    public void confirmingAddingDocumentsEmptiesTopicModels () {
        List<File> newFiles = new ArrayList<>();
        try {
            newFiles.add(File.createTempFile("test-4", "txt"));
            newFiles.add(File.createTempFile("test-5", "txt"));
            newFiles.add(File.createTempFile("test-6", "txt"));
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException when creating files in test method");
        }
        interact(() -> {
            controller.prepareToAddFiles(newFiles);
            System.out.println(controller.eraseModelsConfirmationBox.getDialogPane().lookupButton(ButtonType.OK));
            clickOn(controller.eraseModelsConfirmationBox.getDialogPane().lookupButton(ButtonType.OK));
            assertThat(project.getTopicModels(), hasSize(0));
            assertThat(project.getDocuments(), hasSize(6));
            assertThat(controller.getRoot().getChildren(), hasSize(6));
        });
    }

    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(DocumentListComponent.class.getResource("DocumentListComponent.fxml"));
        mainNode = loader.load();
        AnchorPane pane = new AnchorPane();
        pane.setPrefHeight(400);
        pane.setPrefWidth(600);
        pane.getChildren().add(mainNode);
        mainNode.setId("documentList");
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        stage.toFront();
        project = ProjectModel.blank();
        controller = loader.getController();
//        controller.setId();
    }

    @Before
    public void setUp () throws Exception {
        controller.setData(project, new SimpleObjectProperty<>());
        List<File> inFiles = new ArrayList<>();
        try {
            inFiles.add(File.createTempFile("test-1", "txt"));
            inFiles.add(File.createTempFile("test-2", "txt"));
            inFiles.add(File.createTempFile("test-3", "txt"));
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException when creating files in setUp method");
        }
        controller.prepareToAddFiles(inFiles);
        project.getTopicModels().add(new TopicModel(20));
    }

    @After
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[] {});
    }

}
