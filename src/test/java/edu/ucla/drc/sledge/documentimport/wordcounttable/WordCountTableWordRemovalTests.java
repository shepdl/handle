package edu.ucla.drc.sledge.documentimport.wordcounttable;

import cc.mallet.pipe.*;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import edu.ucla.drc.sledge.documentimport.stopwords.TokenSequenceMarkStopwords;
import edu.ucla.drc.sledge.project.ProjectModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableRow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class WordCountTableWordRemovalTests extends ApplicationTest {

    private Parent mainNode;
    private Scene scene;
    private ProjectModel project;
    private WordCountTable controller;
    private ObjectProperty<Instance> selectedDocument;
    private SerialPipes pipe;

    @Test
    public void removingWordRemovesWordFromList () {
        interact(() -> {
            rightClickOn(lookup(".table-row-cell").nth(0).<TableRow>query());
            press(KeyCode.DOWN);
            press(KeyCode.ENTER);
            clickOn(MouseButton.PRIMARY);
            System.out.println(project.getStopwords());
            assertThat(project.getStopwords(), hasSize(1));
            assertThat(project.getStopwords(), containsInAnyOrder("almond"));
        });
    }

    @Test
    public void removingWordDoesNotShowWarningWhenNoTopicModelsPresent () {
        fail("Not implemented");
    }

    @Test
    public void removingWordShowsWarningWhenTopicModelsPresent () {
        fail("Not implemented");
    }

    @Test
    public void clickingOkayRemovesStopWord () {
        fail("Not implemented");
    }

    @Test
    public void clickingCancelDoesNotRemoveStopWord () {
        fail("Not implemented");
    }

    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(WordCountTable.class.getResource("WordCountTable.fxml"));
        WordCountTable table = new WordCountTable();
        controller = table;
        AnchorPane pane = new AnchorPane();
        loader.setRoot(pane);
        mainNode = loader.load();
        scene = new Scene(mainNode);
        stage.setScene(scene);
        stage.show();
        stage.toFront();
        project = ProjectModel.blank();
        loader.setController(table);
    }

    @Before
    public void setUp() throws Exception {
        selectedDocument = new SimpleObjectProperty<>();
        List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new SaveDataInSource());
        pipes.add(new Input2CharSequence());
        pipes.add(new CharSequenceLowercase());
        Pattern nonEnglishRegex = Pattern.compile("[\\p{L}\\p{M}]+");
        pipes.add(new CharSequence2TokenSequence(nonEnglishRegex));
        Set<String> stopwords = new HashSet<>();
        stopwords.add("apple");
        stopwords.add("banana");
        TokenSequenceMarkStopwords stopwordFilter = new TokenSequenceMarkStopwords(new HashSet<>(stopwords));
        pipes.add(stopwordFilter);
        pipe = new SerialPipes(pipes);
        controller.setData(project, selectedDocument);
        InstanceList instances = new InstanceList(pipe);
        List<Instance> instanceList = new ArrayList<>();
        instanceList.add(new Instance("apple banana walnut cherry almond", null, null, null));
        instances.addThruPipe(instanceList.iterator());
        interact(() -> { selectedDocument.setValue(instances.get(0)); });
    }
}

