package edu.ucla.drc.sledge;

import java.util.List;

public class SledgeUIModel {

    private String projectName;
    private List<String> inputDocumentFilenames;
    private ImportFileSettings importFileSettings;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<String> getInputDocumentFilenames() {
        return inputDocumentFilenames;
    }

    public void setInputDocumentFilenames(List<String> inputDocumentFilenames) {
        this.inputDocumentFilenames = inputDocumentFilenames;
    }

    public ImportFileSettings getImportFileSettings() {
        return importFileSettings;
    }

    public void setImportFileSettings(ImportFileSettings importFileSettings) {
        this.importFileSettings = importFileSettings;
    }

    public List<TopicTrainingJob> getTopicTrainingJobs() {
        return topicTrainingJobs;
    }

    public void setTopicTrainingJobs(List<TopicTrainingJob> topicTrainingJobs) {
        this.topicTrainingJobs = topicTrainingJobs;
    }

    private List<TopicTrainingJob> topicTrainingJobs;
}

