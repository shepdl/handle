package edu.ucla.drc.sledge.documents;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;

public interface Document {
    String getContent() throws FileNotFoundException;
    String getName();
    URI getUri();
    File getFile();
}
