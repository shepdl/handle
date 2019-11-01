package edu.ucla.drc.sledge.documentimport;

import java.util.List;

public interface StopwordSource {
    String getName ();
    List<String> provideWords();
}
