package edu.ucla.drc.sledge.project;

import com.ctc.wstx.shaded.msv_core.util.Uri;
import edu.ucla.drc.sledge.documents.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

public class StringBackedDocument implements Document {

    private final String content;
    private final String name;
    private URI uri;

    public StringBackedDocument(String content, String name) {
        this.content = content;
        this.name = name;
        try {
            this.uri = new URI("file://undefined");
        } catch (URISyntaxException e) {
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
        return uri;
    }

    @Override
    public File getFile() {
        return null;
    }

    @Override
    public boolean equals (Object otherUnknown) {
        if (otherUnknown == null) {
            return false;
        }
        if (!(otherUnknown instanceof StringBackedDocument)) {
            return false;
        }
        StringBackedDocument otherDoc = (StringBackedDocument)otherUnknown;

        return this.content.equals(otherDoc.content) && this.name.equals(otherDoc.name);
    }
}
