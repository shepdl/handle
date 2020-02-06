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

    interface Exporter {
        void addContent (String content);
        void addName(String name);
        void addUri(URI uri);
        void addFile(File file);
    }

    interface Importer {
        String provideContent ();
        String provideName();
        URI provideUri();
        File provideFile();
    }

    default void exportTo (Exporter exporter) throws FileNotFoundException {
        exporter.addContent(getContent());
        exporter.addName(getName());
        exporter.addUri(getUri());
        exporter.addFile(getFile());
    }

}
