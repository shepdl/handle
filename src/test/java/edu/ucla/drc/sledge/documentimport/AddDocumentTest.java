package edu.ucla.drc.sledge.documentimport;

import cc.mallet.types.Instance;
import edu.ucla.drc.sledge.documents.Document;
import edu.ucla.drc.sledge.documents.DocumentFactory;
import edu.ucla.drc.sledge.project.ProjectModel;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


public class AddDocumentTest extends ApplicationTest {

    private DocumentListComponent controller;
    private ProjectModel project;
    private SimpleObjectProperty<Instance> selectedDocument;

    @Test
    public void addingSingleDocumentAddsDocumentsToProject () {
        controller.setData(project, selectedDocument);
        List<File> inFiles = new ArrayList<>();
        try {
            inFiles.add(File.createTempFile("test-1", ".txt"));
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException when creating files");
        }
        controller.prepareToAddFiles(inFiles);
        List<TreeItem<Document>> addedDocuments = controller.getRoot().getChildren();
        assertThat(addedDocuments, hasSize(1));
        assertThat(addedDocuments.get(0).getValue().getFile(), equalTo(inFiles.get(0)));

        assertThat(project.getDocuments(), hasSize(1));
        List<Document> projectDocuments = project.getDocuments();
        assertThat(projectDocuments.get(0).getFile(), equalTo(inFiles.get(0)));
        assertThat(controller.invalidFilesBox.isShowing(), equalTo(false));
    }

    @Test
    public void addingMultipleDocumentsAddsAllDocumentsToProject () {
        controller.setData(project, selectedDocument);
        List<File> inFiles = new ArrayList<>();
        try {
            inFiles.add(File.createTempFile("test-1", ".txt"));
            inFiles.add(File.createTempFile("test-2", ".txt"));
            inFiles.add(File.createTempFile("test-3", ".txt"));
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException when creating files");
        }
        controller.prepareToAddFiles(inFiles);
        List<TreeItem<Document>> addedDocuments = controller.getRoot().getChildren();
        assertThat(addedDocuments, hasSize(3));
        assertThat(addedDocuments.get(0).getValue().getFile(), equalTo(inFiles.get(0)));
        assertThat(addedDocuments.get(1).getValue().getFile(), equalTo(inFiles.get(1)));
        assertThat(addedDocuments.get(2).getValue().getFile(), equalTo(inFiles.get(2)));

        assertThat(project.getDocuments(), hasSize(3));
        List<Document> projectDocuments = project.getDocuments();
        assertThat(projectDocuments.get(0).getFile(), equalTo(inFiles.get(0)));
        assertThat(projectDocuments.get(1).getFile(), equalTo(inFiles.get(1)));
        assertThat(projectDocuments.get(2).getFile(), equalTo(inFiles.get(2)));
        assertThat(controller.invalidFilesBox.isShowing(), equalTo(false));
    }

    @Test
    public void initializingWithDocumentsRendersDocuments () {
        List<File> inFiles = new ArrayList<>();
        try {
            inFiles.add(File.createTempFile("test-1", ".txt"));
            inFiles.add(File.createTempFile("test-2", ".txt"));
            inFiles.add(File.createTempFile("test-3", ".txt"));
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException when creating files");
        }
        DocumentFactory factory = new DocumentFactory();
        for (File inFile: inFiles) {
            project.getDocuments().add(factory.adaptDocument(inFile));
        }
        controller.setData(project, selectedDocument);
        controller.prepareToAddFiles(inFiles);
        List<TreeItem<Document>> addedDocuments = controller.getRoot().getChildren();
        assertThat(addedDocuments, hasSize(3));
        assertThat(addedDocuments.get(0).getValue().getFile(), equalTo(inFiles.get(0)));
        assertThat(addedDocuments.get(1).getValue().getFile(), equalTo(inFiles.get(1)));
        assertThat(addedDocuments.get(2).getValue().getFile(), equalTo(inFiles.get(2)));
        assertThat(controller.invalidFilesBox.isShowing(), equalTo(false));
    }

    @Test
    public void showAlertIfOneIncorrectFileTypeSubmitted () {
        controller.setData(project, selectedDocument);
        int fileCount = project.getDocuments().size();
        List<File> inFiles = new ArrayList<>();
        try {
            inFiles.add(File.createTempFile("test-4", ".xlsx"));
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("IOException when creating files");
        }
        interact(() -> {
            controller.prepareToAddFiles(inFiles);
            assertThat(controller.invalidFilesBox.isShowing(), equalTo(true));
            assertThat(project.getDocuments(), hasSize(fileCount));
        });
    }

    @Test
    public void showAlertIfManyIncorrectFileTypesSubmitted () {
        controller.setData(project, selectedDocument);
        controller.setData(project, selectedDocument);
        int fileCount = project.getDocuments().size();
        List<File> inFiles = new ArrayList<>();
        try {
            inFiles.add(File.createTempFile("test-4", ".pdf"));
            inFiles.add(File.createTempFile("test-5", ".mp3"));
            inFiles.add(File.createTempFile("test-6", ".mov"));
            inFiles.add(File.createTempFile("test-7", ".psd"));
            inFiles.add(File.createTempFile("test-8", ".qwerty"));
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("IOException when creating files");
        }
        interact(() -> {
            controller.prepareToAddFiles(inFiles);
            assertThat(controller.invalidFilesBox.isShowing(), equalTo(true));
            assertThat(project.getDocuments(), hasSize(fileCount));
        });
    }

    @Test
    public void showAlertIfOneFileWithIncorrectTypeSubmittedWithManyOtherFilesWithCorrectTypes () {
        controller.setData(project, selectedDocument);
        List<File> inFiles = new ArrayList<>();
        int fileCount = project.getDocuments().size();
        try {
            inFiles.add(File.createTempFile("test-4", ".txt"));
            inFiles.add(File.createTempFile("test-5", ".xlsx"));
            inFiles.add(File.createTempFile("test-6", ".txt"));
            inFiles.add(File.createTempFile("test-7", ".txt"));
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("IOException when creating files");
        }
        interact(() -> {
            controller.prepareToAddFiles(inFiles);
            assertThat(controller.invalidFilesBox.isShowing(), equalTo(true));
            assertThat(project.getDocuments(), hasSize(fileCount + 3));
        });
    }

    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(DocumentListComponent.class.getResource("DocumentListComponent.fxml"));
        Parent mainNode = loader.load();
        stage.setScene(new Scene(mainNode));
        stage.show();
        stage.toFront();
        controller = loader.getController();
        project = ProjectModel.blank();
    }

    @Before
    public void setUp () throws Exception {
        selectedDocument = new SimpleObjectProperty<>();
    }

    @After
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[] {});
    }

}