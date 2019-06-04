package edu.ucla.drc.sledge.topicsettings;

import java.util.ArrayList;
import java.util.List;

public class TopicDocumentContainerSummary {

    private List<TopicDocumentSummary> items = new ArrayList<>();
    private int id;

    public TopicDocumentContainerSummary (int id) {
        this.id = id;
    }

    public void add (String documentName, double proportion) {
        items.add(new TopicDocumentSummary(documentName, proportion));
    }

    public List<TopicDocumentSummary> getItems () {
        return items;
    }

}
