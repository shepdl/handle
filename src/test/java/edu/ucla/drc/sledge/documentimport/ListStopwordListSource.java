package edu.ucla.drc.sledge.documentimport;

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
