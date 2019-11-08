package edu.ucla.drc.sledge.documentimport;

import cc.mallet.topics.TopicModel;
import edu.ucla.drc.sledge.DragboardMockProxy;
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
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AddingDocumentsAfterTopicModelsInvalidatesTopicModelsTests extends ApplicationTest {

    private DocumentListComponent controller;
    private ProjectModel project;
    private Parent mainNode;
    private Scene scene;

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

        interact(() -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            Map<DataFormat, Object> content = new HashMap<>();
            content.put(DataFormat.FILES, newFiles);
            clipboard.setContent(content);
            DragboardMockProxy mock = new DragboardMockProxy(clipboard);
            DragEvent dragStart = new DragEvent(null, mainNode, DragEvent.DRAG_OVER,
                    mock.getDragboard(),
                    0, 0, 0, 0,
                    TransferMode.COPY,
                    null, mainNode, null
            );

            controller.fireEvent(dragStart);
            DragEvent dragEvent = new DragEvent(null, mainNode, DragEvent.DRAG_DROPPED,
                    mock.getDragboard(),
                    0, 0, 0, 0,
                    TransferMode.COPY,
                    null, mainNode, null
            );
            controller.fireEvent(dragEvent);
            assertTrue(controller.eraseModelsConfirmationBox.isShowing());
        });
    }

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
            controller.eraseModelsConfirmationBox.setResult(ButtonType.CANCEL);
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
            controller.eraseModelsConfirmationBox.setResult(ButtonType.OK);
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
        scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        stage.toFront();
        project = ProjectModel.blank();
        controller = loader.getController();
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
