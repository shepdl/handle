package edu.ucla.drc.sledge.documents;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;

public interface Document {
    String getContent() throws FileNotFoundException;
    String getName();
    URI getUri();
    File getFile();

    default String getExtension() {
        String name = getName();
        int lastPosOfDot = name.lastIndexOf('.');
        String extension = "";
        if (lastPosOfDot > 0) {
            extension = name.substring(lastPosOfDot + 1);
        }
        return extension;
    }

}
