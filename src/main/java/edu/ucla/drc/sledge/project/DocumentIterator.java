package edu.ucla.drc.sledge.project;

import cc.mallet.types.Instance;
import edu.ucla.drc.sledge.documents.Document;

import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

public class DocumentIterator implements Iterator<Instance> {

    private final List<Document> documents;
    private int index = 0;

    public DocumentIterator(List<Document> documents) {
        this.documents = documents;
    }

    @Override
    public boolean hasNext() {
        return index < documents.size();
    }

    @Override
    public Instance next() {
        Document document = documents.get(index);
        index++;
        URI uri = document.getUri();
        try {
            return new Instance(
                    document.getContent(), null, uri, null
            );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
