package edu.ucla.drc.sledge.documents;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

class WordDocXDocument implements Document {

    private final File file;
    private String content = "";

    WordDocXDocument(File file) {
        this.file = file;
    }

    @Override
    public String getContent() throws FileNotFoundException {
        if (content.isEmpty()) {
            try {
                XWPFDocument document = new XWPFDocument(new FileInputStream(file));
                XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                content = String.join("\n", extractor.getText());
            } catch (IOException ex) {
                throw new FileNotFoundException(file.getName());
            }
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
