package edu.ucla.drc.sledge.project;

import edu.ucla.drc.sledge.documents.Document;

import java.io.*;
import java.net.URI;

public class CachedSourceDocument implements Document {

    private final String name;
    private final String content;
    private final URI uri;

    public CachedSourceDocument (String name, String content, URI uri) {
        this.name = name;
        this.content = content;
        this.uri = uri;

        try {
            File tempFile = File.createTempFile(name, ".cache.txt");
            tempFile.deleteOnExit();
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getContent() throws FileNotFoundException {
        return content;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public URI getUri() {
        return null;
    }

    @Override
    public File getFile() {
        return null;
    }
}
