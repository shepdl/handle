package edu.ucla.drc.sledge.documentimport.stopwords;

import edu.ucla.drc.sledge.documentimport.stopwords.ListStopwordsList;
import edu.ucla.drc.sledge.documentimport.stopwords.StopwordListsSource;
import edu.ucla.drc.sledge.documentimport.stopwords.StopwordSource;

import java.util.ArrayList;
import java.util.List;

public class ListStopwordListSource extends ArrayList<StopwordSource> implements StopwordListsSource {

    @Override
    public List<StopwordSource> list() {
        return this;
    }

    public void add (String name, String items) {
        this.add(new ListStopwordsList(name, items));
    }

}
