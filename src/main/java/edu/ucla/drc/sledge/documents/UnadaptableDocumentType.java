package edu.ucla.drc.sledge.documents;


import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;

class UnadaptableDocumentType implements Document {

    private final File file;

    UnadaptableDocumentType (File file) {
        this.file = file;
    }

    @Override
    public String getContent() throws FileNotFoundException {
        return "";
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
