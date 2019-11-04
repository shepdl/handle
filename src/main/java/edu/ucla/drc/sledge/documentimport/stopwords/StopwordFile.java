package edu.ucla.drc.sledge.documentimport.stopwords;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StopwordFile implements StopwordSource {

    private final File file;

    public StopwordFile (File file) {
        this.file = file;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public List<String> provideWords() {
        List<String> stopwords = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            List<String> newStopwords = new ArrayList<>();
            while (line != null) {
                newStopwords.add(line);
                line = reader.readLine();
            }
            stopwords.addAll(newStopwords);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopwords;
    }
}
