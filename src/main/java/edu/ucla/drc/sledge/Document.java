package edu.ucla.drc.sledge;

import java.io.File;

public class Document {

    private File file;
    private String textContent;

    public Document(File file, String textContent) {
        this.file = file;
        this.textContent = textContent;
    }
}
