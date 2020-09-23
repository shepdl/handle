package edu.ucla.drc.sledge.project;

import cc.mallet.topics.TopicModel;
import edu.ucla.drc.sledge.IOHelper;
import edu.ucla.drc.sledge.ImportFileSettings;
import edu.ucla.drc.sledge.documents.Document;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class SaveAndLoadProjectFileDocumentSavingTests {

    private ProjectModel saveAndLoad(ProjectModel toSave) {
        File tempOutput = null;
        try {
            tempOutput = File.createTempFile("test-temp-file", "hand");
            tempOutput.deleteOnExit();
        } catch (IOException e) {
            fail("Could not create temporary model file.");
        }
        IOHelper helper = new IOHelper();
        try {
            helper.saveModelToFile(toSave, tempOutput);
        } catch (IOException e) {
            fail("Saving temporary model file failed.");
        }
        try {
            return helper.loadModelFromFile(tempOutput);
        } catch (IOHelper.InvalidFileFormatException e) {
            fail("Parsing JSON from temporary model file failed.");
        } catch (IOException e) {
            fail("Loading temporary model file failed.");
        }
        fail("It should be impossible to get here.");
        return null;
    }

    @Test
    public void documentContentPreservedWhenFourFilesPresent () throws IOException {
        ProjectModel toSave = new ProjectModel();
        toSave.getDocuments().addAll(
            new StringBackedDocument("This is some test content", "Test Doc 1"),
            new StringBackedDocument("And some more test content", "Test Doc 2"),
            new StringBackedDocument("Yet even more test content", "Test Doc 3"),
            new StringBackedDocument("And this is the last document here", "Test Doc 4")
        );
        ProjectModel loaded = saveAndLoad(toSave);
        assertThat(loaded.getDocuments(), hasSize(toSave.getDocuments().size()));
        List<Document> loadedDocuments = loaded.getDocuments();
        assertThat(loadedDocuments.get(0).getName(), equalTo(toSave.getDocuments().get(0).getName()));
        assertThat(loadedDocuments.get(0).getContent(), equalTo(toSave.getDocuments().get(0).getContent()));
        assertThat(loadedDocuments.get(1).getName(), equalTo(toSave.getDocuments().get(1).getName()));
        assertThat(loadedDocuments.get(1).getContent(), equalTo(toSave.getDocuments().get(1).getContent()));
        assertThat(loadedDocuments.get(2).getName(), equalTo(toSave.getDocuments().get(2).getName()));
        assertThat(loadedDocuments.get(2).getContent(), equalTo(toSave.getDocuments().get(2).getContent()));
        assertThat(loadedDocuments.get(3).getName(), equalTo(toSave.getDocuments().get(3).getName()));
        assertThat(loadedDocuments.get(3).getContent(), equalTo(toSave.getDocuments().get(3).getContent()));
    }

    @Test
    public void documentContentPreservedWhenOneFilePresent () throws IOException {
        ProjectModel toSave = new ProjectModel();
        toSave.getDocuments().addAll(new StringBackedDocument("This is some test content", "Test Doc 1"));
        ProjectModel loaded = saveAndLoad(toSave);
        assertThat(loaded.getDocuments(), hasSize(toSave.getDocuments().size()));
        List<Document> loadedDocuments = loaded.getDocuments();
        assertThat(loadedDocuments.get(0).getName(), equalTo(toSave.getDocuments().get(0).getName()));
        assertThat(loadedDocuments.get(0).getContent(), equalTo(toSave.getDocuments().get(0).getContent()));
    }

    @Test
    public void documentContentPreservedWhenNoFilesPresent () {
        ProjectModel toSave = new ProjectModel();
        ProjectModel loaded = saveAndLoad(toSave);
        assertThat(loaded.getDocuments(), hasSize(toSave.getDocuments().size()));
    }

    @Test
    public void importFileSettingsPreserved () {
        ProjectModel toSave = new ProjectModel();
        ImportFileSettings settings = toSave.getImportFileSettings();
        settings.setIterationSchema(ImportFileSettings.DocumentIterationSchema.ONE_DOC_PER_LINE);
        settings.setTokenRegexPattern(ImportFileSettings.nonEnglishRegex);
        ProjectModel loaded = saveAndLoad(toSave);
        ImportFileSettings loadedSettings = loaded.getImportFileSettings();
        assertThat(loadedSettings.preserveCase(), equalTo(settings.preserveCase()));
        assertThat(loadedSettings.getIterationSchema(), equalTo(settings.getIterationSchema()));
        assertThat(loadedSettings.getTokenRegexPattern().pattern(), equalTo(settings.getTokenRegexPattern().pattern()));
    }

    @Test
    public void topicModelsLoadedWhenOneTopicModelPresent () {
        ProjectModel toSave = new ProjectModel();
        toSave.getTopicModels().addAll(new TopicModel(10) );
        ProjectModel loaded = saveAndLoad(toSave);
        assertThat(loaded.getTopicModels(), hasSize(1));
    }

    @Test
    public void topicModelsLoadedWhenFourTopicModelsPresent () {
        ProjectModel toSave = new ProjectModel();
        toSave.getTopicModels().addAll(
                new TopicModel(10),
                new TopicModel(20),
                new TopicModel(30),
                new TopicModel(40)
        );
        ProjectModel loaded = saveAndLoad(toSave);
        assertThat(loaded.getTopicModels(), hasSize(4));
    }

    @Test
    public void topicModelsLoadedWhenNoTopicModelsPresent () {
        ProjectModel toSave = new ProjectModel();
        ProjectModel loaded = saveAndLoad(toSave);
        assertThat(loaded.getTopicModels(), hasSize(0));
    }

    @Test
    public void stopwordsLoadedWhenStopwordsPresent () {
        ProjectModel toSave = new ProjectModel();
        toSave.addStopword("apple");
        toSave.addStopword("banana");
        toSave.addStopword("cherry");
        ProjectModel loaded = saveAndLoad(toSave);
        assertThat(loaded.getStopwords(), hasSize(3));
        assertThat(loaded.getStopwords(), hasItems("apple", "banana", "cherry"));
    }

    @Test
    public void stopwordsLoadedWhenOneStopwordPresent () {
        ProjectModel toSave = new ProjectModel();
        toSave.addStopword("apple");
        ProjectModel loaded = saveAndLoad(toSave);
        assertThat(loaded.getStopwords(), hasSize(1));
        assertThat(loaded.getStopwords(), hasItems("apple"));
    }

    @Test
    public void stopwordsListLoadedWhenNoStopwordsArePresent () throws IOException, IOHelper.InvalidFileFormatException {
        ProjectModel toSave = new ProjectModel();
        File tempOutput = File.createTempFile("test-temp-file", "hand");
        tempOutput.deleteOnExit();
        IOHelper ioHelper = new IOHelper();
        ioHelper.saveModelToFile(toSave, tempOutput);
        ProjectModel loaded = ioHelper.loadModelFromFile(tempOutput);
        assertThat(loaded.getStopwords(), hasSize(0));
    }

}
