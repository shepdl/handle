package edu.ucla.drc.sledge.documentimport.stopwords;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StopwordsDirectory implements StopwordListsSource {

    private File rootDirectory;

    public StopwordsDirectory (File rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    @Override
    public List<StopwordSource> list() {
        List<StopwordSource> sources = new ArrayList<>();
        try {
            for (File file : rootDirectory.listFiles()) {
                sources.add(new StopwordFile(file));
            }
        } catch (NullPointerException ex) {
            System.out.println("Stopwords directory not found");
        }

        return sources;
    }

}
