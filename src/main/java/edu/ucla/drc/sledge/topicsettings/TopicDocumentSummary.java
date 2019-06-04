package edu.ucla.drc.sledge.topicsettings;

public class TopicDocumentSummary {

    private final String documentName;
    private final double proportion;

    public TopicDocumentSummary (String documentName, double proportion) {
        this.documentName = documentName;
        this.proportion = proportion;
    }

    public String getDocumentName() {
        return documentName;
    }

    public double getProportion() {
        return proportion;
    }

}
