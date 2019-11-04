package edu.ucla.drc.sledge.documents;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

class WordDocDocument implements Document {

    private final File file;
    private String content;

    WordDocDocument(File file) {
        this.file = file;
    }

    @Override
    public String getContent() throws FileNotFoundException {
        if (content.isEmpty()) {
            try {
                HWPFDocument document = new HWPFDocument(new FileInputStream(file));
                WordExtractor extractor = new WordExtractor(document);
                content = String.join("\n", extractor.getParagraphText());
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
