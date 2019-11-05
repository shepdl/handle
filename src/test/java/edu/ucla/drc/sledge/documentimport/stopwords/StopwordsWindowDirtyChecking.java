package edu.ucla.drc.sledge.documentimport.stopwords;

import edu.ucla.drc.sledge.project.ProjectModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class StopwordsWindowDirtyChecking extends ApplicationTest {

    private StopWordsDialogComponent controller;
    private ProjectModel project;
    private Consumer nullCallback = new Consumer() {
        @Override
        public void accept(Object o) {
            return;
        }
    };

    @Test
    public void confirmationNotShownIfCancelButtonClickedAndStopwordsHaveNotChanged () {
        clickOn(controller.cancelButton);
        assertThat(controller.confirmAlert, nullValue());
    }

    @Test
    public void confirmationShowsIfCancelButtonClickedAndStopwordsHaveChanged () {
        clickOn(controller.defaultStopwordsComboBox);
        write("First");
        push(KeyCode.DOWN);
        push(KeyCode.ENTER);
        clickOn(controller.cancelButton);
        assertThat(controller.confirmAlert.isShowing(), equalTo(true));
    }

    @Test
    public void changesSavedIfYesClicked () {
        Set<String> initialWords = project.getStopwords();
        clickOn(controller.defaultStopwordsComboBox);
        write("First");
        push(KeyCode.DOWN);
        push(KeyCode.ENTER);
        clickOn(controller.cancelButton);
        assertThat(controller.confirmAlert.isShowing(), equalTo(true));
        clickOn(lookup(ButtonType.OK.getText()).queryButton());
        assertThat(project.getStopwords(), equalTo(initialWords));
    }

    @Test
    public void changesNotSavedIfNoClicked () {
        Set<String> initialWords = project.getStopwords();
        clickOn(controller.defaultStopwordsComboBox);
        write("First");
        push(KeyCode.DOWN);
        push(KeyCode.ENTER);
        clickOn(controller.cancelButton);
        assertThat(controller.confirmAlert.isShowing(), equalTo(true));
        clickOn(lookup(ButtonType.CANCEL.getText()).queryButton());
        assertThat(project.getStopwords(), equalTo(initialWords));
    }

    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(StopWordsDialogComponent.class.getResource("StopWordsDialogComponent.fxml"));
        Parent mainNode = loader.load();
        stage.setScene(new Scene(mainNode));
        stage.show();
        stage.toFront();
        controller = loader.getController();
        project = ProjectModel.blank();
    }

    @Before
    public void setUp () throws Exception {
        ListStopwordListSource stopwordListSource = new ListStopwordListSource();
        stopwordListSource.add("First List", "apple banana cherry");
        stopwordListSource.add("Second List", "tree peach pie");

        Set<String> initialStopwords = new HashSet<String>();
        initialStopwords.add("quince");
        initialStopwords.add("kumquat");
        initialStopwords.add("orange");
        project.setStopwords(initialStopwords);
        controller.initialize(project, stopwordListSource, nullCallback);
    }

    @After
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[] {});
    }

}
