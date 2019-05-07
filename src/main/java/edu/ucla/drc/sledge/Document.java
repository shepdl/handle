package edu.ucla.drc.sledge;

import cc.mallet.types.Instance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.stream.Collectors;

public class Document {

    private File file;
    private String textContent;
    private Instance instance;

    public Document(File file, String textContent) {
        this.file = file;
        this.textContent = textContent;
    }

    public String toString () {
        return this.file.getName();
    }

    public String getTextContent () throws FileNotFoundException {
        if (textContent == null) {
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            textContent = bufferedReader.lines().collect(Collectors.joining("\n"));
        }
        return textContent;
    }

    public File getFile() {
        return file;
    }

    public void setIngested(Instance instance) {
        this.instance = instance;
    }

    public Instance getIngested() {
        return this.instance;
    }
}
