package edu.ucla.drc.sledge.documents;

import cc.mallet.types.Instance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class CsvDocumentIterator implements Iterator<Instance> {

    private final List<Document> documents;
    private int docIndex = 0;
    private int lineIndex = -1;
    private BufferedReader currentReader;
    private String nextLine;


    public CsvDocumentIterator (List<Document> documents) {
        this.documents = documents;
    }

    @Override
    public boolean hasNext () {
        try {
            if (currentReader == null) {
                currentReader = new BufferedReader(new FileReader(documents.get(docIndex).getFile()));
            }
            String line = currentReader.readLine();
            if (line != null) {
                nextLine = line;
                lineIndex += 1;
                return true;
            } else {
                currentReader.close();
                lineIndex = 0;
                docIndex += 1;
                if (docIndex < documents.size()) {
                    currentReader = new BufferedReader(new FileReader(documents.get(docIndex).getFile()));
                    nextLine = currentReader.readLine();
                    return true;
                } else {
                    return false;
                }
            }
        } catch (IOException e) {
            // TODO: Log this
            e.printStackTrace();
            return false;
        }
    }

    private static String docURI(int docIndex, int lineIndex) {
        return "doc-" + docIndex + "-line-" + lineIndex;
    }

    @Override
    public Instance next () {
        return new Instance(nextLine, null, docURI(docIndex, lineIndex), documents.get(docIndex).getUri());
    }

}
