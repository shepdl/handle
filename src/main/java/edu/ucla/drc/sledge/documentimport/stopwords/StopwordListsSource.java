package edu.ucla.drc.sledge.documentimport.stopwords;

import java.util.List;

public interface StopwordListsSource {
    List<StopwordSource> list();
}
