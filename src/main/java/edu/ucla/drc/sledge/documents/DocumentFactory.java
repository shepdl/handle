package edu.ucla.drc.sledge.documents;

import java.io.File;

public class DocumentFactory {

    public Document adaptDocument (File file) {

        int lastPosOfDot = file.getName().lastIndexOf('.');
        String extension = "txt";
        if (lastPosOfDot > 0) {
            extension = file.getName().substring(lastPosOfDot + 1);
        }

        switch (extension) {
            case "txt":
                return new TextFileDocument(file);
            case "doc":
                return new WordDocDocument(file);
            case "docx":
                return new WordDocXDocument(file);
            default:
                return new UnadaptableDocumentType(file);
        }

    }
}
