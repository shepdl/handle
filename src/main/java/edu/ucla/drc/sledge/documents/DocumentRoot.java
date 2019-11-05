package edu.ucla.drc.sledge.documents;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;

public class DocumentRoot implements Document {
    @Override
    public String getContent() throws FileNotFoundException {
        return "Empty content";
    }

    @Override
    public String getName() {
        return "Files";
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
