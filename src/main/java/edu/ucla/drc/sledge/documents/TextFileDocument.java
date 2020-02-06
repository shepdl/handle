package edu.ucla.drc.sledge.documents;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URI;
import java.util.stream.Collectors;

class TextFileDocument implements Document {

    private final File file;
    private String content = "";

    TextFileDocument(File file) {
        this.file = file;
    }

    public TextFileDocument (Importer importer) {
        // Copy file to temporary directory
        file = importer.provideFile();
        content = importer.provideContent();
    }

    @Override
    public String getContent() throws FileNotFoundException {
        if (content.isEmpty()) {
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            content = reader.lines().collect(Collectors.joining("\n"));
        }
        return content;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public URI getUri() {
        return file.toURI();
    }

    @Override
    public File getFile() {
        return file;
    }

}
