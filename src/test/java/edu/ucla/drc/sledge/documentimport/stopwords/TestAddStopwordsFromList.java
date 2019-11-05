package edu.ucla.drc.sledge.documentimport.stopwords;


import edu.ucla.drc.sledge.project.ProjectModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class TestAddStopwordsFromList extends ApplicationTest {

    private StopWordsDialogComponent controller;
    private ProjectModel project;
    private Consumer nullCallback = new Consumer() {
        @Override
        public void accept(Object o) {
            return;
        }
    };

    @Test
    public void listIsEmptyOnInitializationWithBlankModel () {
        controller.initialize(project, new ListStopwordListSource(), nullCallback);
        clickOn(controller.saveButton);
        List<String> items = controller.stopwordsTable.getItems();
        assertThat(items, hasSize(0));
        assertThat(project.getStopwords(), is(empty()));
    }

    @Test
    public void showWordsProvidedByProjectModel () {
        Set<String> initialStopwords = new HashSet<String>();
        initialStopwords.add("banana");
        initialStopwords.add("apple");
        initialStopwords.add("cherry");
        project.setStopwords(initialStopwords);
        controller.initialize(project, new ListStopwordListSource(), nullCallback);
        List<String> items = controller.stopwordsTable.getItems();
        assertThat(items, hasSize(3));
        assertThat(items.get(0), equalTo("apple"));
        assertThat(items.get(1), equalTo("banana"));
        assertThat(items.get(2), equalTo("cherry"));
        clickOn(controller.saveButton);

        Set<String> finalStopwordds = project.getStopwords();
        assertThat(finalStopwordds, hasSize(3));
        assertThat(finalStopwordds, containsInAnyOrder("apple", "banana", "cherry"));
    }

    @Test
    public void projectModelStopwordsPreservedIfCancelClicked () {
        Set<String> initialStopwords = new HashSet<String>();
        initialStopwords.add("banana");
        initialStopwords.add("apple");
        initialStopwords.add("cherry");
        project.setStopwords(initialStopwords);
        controller.initialize(project, new ListStopwordListSource(), nullCallback);
        List<String> items = controller.stopwordsTable.getItems();
        assertThat(items, hasSize(3));
        assertThat(items.get(0), equalTo("apple"));
        assertThat(items.get(1), equalTo("banana"));
        assertThat(items.get(2), equalTo("cherry"));
        clickOn(controller.cancelButton);

        Set<String> finalStopwordds = project.getStopwords();
        assertThat(finalStopwordds, hasSize(3));
        assertThat(finalStopwordds, containsInAnyOrder("apple", "banana", "cherry"));
    }

    @Test
    public void addAllWordsFromNewList () {
        StopwordSource source = new ListStopwordsList("my-stopwords-list.txt", "apples bananas");
        controller.initialize(project, new ListStopwordListSource(), nullCallback);
        controller.mergeNewListWithExistingStopwords(source);
        assertThat(controller.stopwordsTable.getItems(), contains("apples", "bananas"));
        clickOn(controller.saveButton);
        assertThat(project.getStopwords(), hasSize(2));
        assertThat(project.getStopwords(), containsInAnyOrder("apples", "bananas"));
    }

    @Test
    public void allWordsPreservedWhenOneDefaultListSelectedAndNewFileAdded () {
        ListStopwordListSource stopwordListSource = new ListStopwordListSource();
        stopwordListSource.add("First List", "apple banana cherry");
        stopwordListSource.add("Second List", "tree peach pie");
        controller.initialize(project, stopwordListSource, nullCallback);
        clickOn(controller.defaultStopwordsComboBox);
        write("First");
        push(KeyCode.DOWN);
        push(KeyCode.ENTER);

        StopwordSource userSource = new ListStopwordsList("my-stopwords-list.txt", "grape peach");
        controller.mergeNewListWithExistingStopwords(userSource);
        assertThat(controller.stopwordsTable.getItems(), contains("apple", "banana", "cherry", "grape", "peach"));
        clickOn(controller.saveButton);
        assertThat(project.getStopwords(), hasSize(5));
        assertThat(project.getStopwords(), containsInAnyOrder("apple", "banana", "cherry", "grape", "peach"));
    }

    @Test
    public void removeWordsOnClearList () {
        Set<String> initialStopwords = new HashSet<String>();
        initialStopwords.add("banana");
        initialStopwords.add("apple");
        initialStopwords.add("cherry");
        project.setStopwords(initialStopwords);
        controller.initialize(project, new ListStopwordListSource(), nullCallback);
        clickOn(controller.clearStopwordsButton, MouseButton.PRIMARY);
        List<String> items = controller.stopwordsTable.getItems();
        assertThat(items, hasSize(0));
        clickOn(controller.cancelButton);

        Set<String> finalStopwords = project.getStopwords();
        assertThat(finalStopwords, hasSize(3));
        assertThat(finalStopwords, containsInAnyOrder("apple", "banana", "cherry"));
    }

    @Test
    public void removingWordsOnClearListButtonClickedAndThenClickingSaveEmptiesStopwordsFile () {
        Set<String> initialStopwords = new HashSet<String>();
        initialStopwords.add("banana");
        initialStopwords.add("apple");
        initialStopwords.add("cherry");
        project.setStopwords(initialStopwords);
        controller.initialize(project, new ListStopwordListSource(), nullCallback);
        clickOn(controller.clearStopwordsButton);
        List<String> items = controller.stopwordsTable.getItems();
        assertThat(items, hasSize(0));
        clickOn(controller.saveButton);

        Set<String> finalStopwords = project.getStopwords();
        assertThat(finalStopwords, hasSize(0));
    }

    @Test
    public void removingWordsOnClearListButtonClickedAndThenClickingCancelDoesNotEmptyStopwordsFile () {
        Set<String> initialStopwords = new HashSet<String>();
        initialStopwords.add("banana");
        initialStopwords.add("apple");
        initialStopwords.add("cherry");
        project.setStopwords(initialStopwords);
        controller.initialize(project, new ListStopwordListSource(), nullCallback);
        clickOn(controller.clearStopwordsButton);
        List<String> items = controller.stopwordsTable.getItems();
        assertThat(items, hasSize(0));
        clickOn(controller.cancelButton);
        Set<String> finalStopwords = project.getStopwords();
        assertThat(finalStopwords, hasSize(3));
    }

    @Test
    public void showAllStopwordListsInComboBox () {
        ListStopwordListSource stopwordListSource = new ListStopwordListSource();
        stopwordListSource.add("First List", "apple banana cherry");
        stopwordListSource.add("Second List", "tree peach pie");
        controller.initialize(project, stopwordListSource, nullCallback);
        assertThat(controller.defaultStopwordsComboBox.getItems(), hasSize(2));
        assertThat(controller.defaultStopwordsComboBox.getItems().get(0).getName(), equalTo("First List"));
        assertThat(controller.defaultStopwordsComboBox.getItems().get(1).getName(), equalTo("Second List"));
    }

    @Test
    public void addWordOnSelectionFromDefaultStopwordsList () {
        ListStopwordListSource stopwordListSource = new ListStopwordListSource();
        stopwordListSource.add("First List", "apple banana cherry");
        stopwordListSource.add("Second List", "tree peach pie");
        controller.initialize(project, stopwordListSource, nullCallback);
        clickOn(controller.defaultStopwordsComboBox);
        write("First");
        push(KeyCode.DOWN);
        push(KeyCode.ENTER);

        List<String> foundStopwords = controller.stopwordsTable.getItems();
        assertThat(foundStopwords, contains("apple", "banana", "cherry"));
    }

    @Test
    public void addWordOnSelectionFromDefaultStopwordListAndThenAddMoreFromStopwordsList () {
        ListStopwordListSource stopwordListSource = new ListStopwordListSource();
        stopwordListSource.add("First List", "apple banana cherry");
        stopwordListSource.add("Second List", "tree peach pie");
        controller.initialize(project, stopwordListSource, nullCallback);
        clickOn(controller.defaultStopwordsComboBox);
        write("First");
        push(KeyCode.DOWN);
        push(KeyCode.ENTER);

        clickOn(controller.defaultStopwordsComboBox);
        write("Second");
        push(KeyCode.DOWN);
        push(KeyCode.ENTER);

        List<String> foundStopwords = controller.stopwordsTable.getItems();
        assertThat(foundStopwords, contains("apple", "banana", "cherry", "peach", "pie", "tree"));
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

    }

    @After
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[] {});
    }

}
