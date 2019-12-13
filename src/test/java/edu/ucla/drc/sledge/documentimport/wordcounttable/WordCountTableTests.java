package edu.ucla.drc.sledge.documentimport.wordcounttable;

import cc.mallet.pipe.*;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.TokenSequence;
import edu.ucla.drc.sledge.documentimport.stopwords.TokenSequenceMarkStopwords;
import edu.ucla.drc.sledge.project.ProjectModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class WordCountTableTests extends ApplicationTest {
    private Parent mainNode;
    private Scene scene;
    private ProjectModel project;
    private WordCountTable controller;
    private ObjectProperty<Instance> selectedDocument;
    private SerialPipes pipe;

    private Comparator<WordCountEntry> wordCountComparator = new Comparator<WordCountEntry>() {
        @Override
        public int compare(WordCountEntry o1, WordCountEntry o2) {
            return o1.getCount() - o2.getCount() - o1.getWord().compareTo(o2.getWord());
        }
    };

    @Test
    public void settingStopWordsShowsWordsInListWhenMultipleCountsArePresent() {
        InstanceList instances = new InstanceList(pipe);
        List<Instance> instanceList = new ArrayList<>();
        instanceList.add(new Instance("walnut apple apple banana walnut cherry apple almond", null, null, null));
        instances.addThruPipe(instanceList.iterator());
        interact(() -> {
            selectedDocument.setValue(instances.get(0));
            List<WordCountEntry> expected = FXCollections.observableArrayList();
            expected.add(new WordCountEntry("almond", 1));
            expected.add(new WordCountEntry("apple", 3));
            expected.add(new WordCountEntry("banana", 1));
            expected.add(new WordCountEntry("cherry", 1));
            expected.add(new WordCountEntry("walnut", 2));

            List<WordCountEntry> actual = controller.wordTable.getItems();
            expected.sort(wordCountComparator);
            actual.sort(wordCountComparator);
            assertThat(expected.size(), equalTo(actual.size()));
            for (int i = 0; i < expected.size(); i++) {
                assertThat(actual.get(i).getWord(), equalTo(expected.get(i).getWord()));
                assertThat(actual.get(i).getCount(), equalTo(expected.get(i).getCount()));
            }
        });
    }

    @Test
    public void settingCountWordsShowsWordsInListWhenSingleWordsArePresent() {
        InstanceList instances = new InstanceList(pipe);
        List<Instance> instanceList = new ArrayList<>();
        instanceList.add(new Instance("apple banana walnut cherry almond", null, null, null));
        instances.addThruPipe(instanceList.iterator());
        List<WordCountEntry> expected = FXCollections.observableArrayList();
        expected.add(new WordCountEntry("almond", 1));
        expected.add(new WordCountEntry("apple", 1));
        expected.add(new WordCountEntry("banana", 1));
        expected.add(new WordCountEntry("cherry", 1));
        expected.add(new WordCountEntry("walnut", 1));
        interact(() -> {
            selectedDocument.setValue(instances.get(0));
            List<WordCountEntry> actual = controller.wordTable.getItems();
            assertThat(expected.size(), equalTo(actual.size()));
            Map<String, Integer> actualCounts = new HashMap<>();
            for (WordCountEntry entry : actual) {
                actualCounts.put(entry.getWord(), entry.getCount());
            }
            for (WordCountEntry exp : expected) {
                assertThat(actualCounts.get(exp.getWord()), equalTo(exp.getCount()));
            }
        });
    }

    @Test
    public void doesNotShowStopWords () {
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
        SerialPipes pipe = new SerialPipes(pipes);
        InstanceList instances = new InstanceList(pipe);
        List<Instance> instanceList = new ArrayList<>();
        instanceList.add(new Instance("apple banana walnut cherry almond", null, null, null));
        instances.addThruPipe(instanceList.iterator());
        List<WordCountEntry> expected = FXCollections.observableArrayList();
        expected.add(new WordCountEntry("almond", 1));
        expected.add(new WordCountEntry("cherry", 1));
        expected.add(new WordCountEntry("walnut", 1));
        interact(() -> {
            selectedDocument.setValue(instances.get(0));
            List<WordCountEntry> actual = controller.wordTable.getItems();
            assertThat(actual.size(), equalTo(expected.size()));
            assertThat(actual, hasSize(3));
            Map<String, Integer> actualCounts = new HashMap<>();
            for (WordCountEntry entry : actual) {
                actualCounts.put(entry.getWord(), entry.getCount());
            }
            for (WordCountEntry exp : expected) {
                assertThat(actualCounts.get(exp.getWord()), equalTo(exp.getCount()));
            }
        });
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(WordCountTable.class.getResource("WordCountTable.fxml"));
        mainNode = loader.load();
        scene = new Scene(mainNode);
        stage.setScene(scene);
        stage.show();
        stage.toFront();
        project = ProjectModel.blank();
        controller = loader.getController();
    }

    @Before
    public void setUp() throws Exception {
        selectedDocument = new SimpleObjectProperty<>();
        controller.setData(project, selectedDocument);
        List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new SaveDataInSource());
        pipes.add(new Input2CharSequence());
        pipes.add(new CharSequenceLowercase());
        Pattern nonEnglishRegex = Pattern.compile("[\\p{L}\\p{M}]+");
        pipes.add(new CharSequence2TokenSequence(nonEnglishRegex));
        pipe = new SerialPipes(pipes);
    }

}
