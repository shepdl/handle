package edu.ucla.drc.sledge;

import cc.mallet.types.Instance;
import cc.mallet.util.ArrayUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;
import java.util.stream.Collectors;

public class Document {

    private File file;
    private String textContent;
    private Instance instance;

    public Document(File file, String textContent) {
        this.file = file;
        this.textContent = textContent;
    }

    public Document (File file) {
        this.file = file;
    }

    public String toString () {
        return this.file.getName();
    }

    public String getTextContent () throws FileNotFoundException {
        if (textContent == null) {
            int lastPosOfDot = file.getName().lastIndexOf('.');
            String extension = "txt";
            if (lastPosOfDot > 0) {
                extension = file.getName().substring(lastPosOfDot + 1);
            }
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            switch (extension) {
                case "txt":
                    textContent = bufferedReader.lines().collect(Collectors.joining("\n"));
                    break;
                case "doc":
                    try {
                        HWPFDocument document = new HWPFDocument(new FileInputStream(file));
                        WordExtractor extractor = new WordExtractor(document);
                        textContent = String.join("\n", extractor.getParagraphText());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "docx":
                    try {
                        XWPFDocument document = new XWPFDocument(new FileInputStream(file));
                        XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                        textContent = extractor.getText();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        return textContent;
    }

    public File getFile() {
        return file;
    }

    public void setIngested(Instance instance) {
        this.instance = instance;
    }

    public Instance getIngested() {
        return this.instance;
    }
}
