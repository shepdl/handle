package edu.ucla.drc.sledge.documentimport.stopwords;

import java.util.List;

public interface StopwordSource {
    String getName ();
    List<String> provideWords();
}
