package edu.ucla.drc.sledge;

import edu.ucla.drc.sledge.documents.Document;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Pattern;

public class ImportFileSettings implements Serializable {

    private final static long SerialVersionUID = 1;

    private boolean preserveCase = true;

    private Pattern tokenRegexPattern;
    private boolean keepSequenceBigrams;

    private List<String> stopwordFilenames;

    public static final Pattern defaultRegex = Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}");
    public static final Pattern nonEnglishRegex = Pattern.compile("[\\p{L}\\p{M}]+");

    public enum DocumentIterationSchema {
        ONE_DOC_PER_FILE, ONE_DOC_PER_LINE
    };

    private DocumentIterationSchema schema;

    public ImportFileSettings () {
        preserveCase = false;
        tokenRegexPattern = defaultRegex;
    }

    public ImportFileSettings (boolean preserveCase, Pattern tokenRegexPattern) {
        this.preserveCase = preserveCase;
        this.tokenRegexPattern = tokenRegexPattern;
    }

    public static ImportFileSettings withDefaults () {
        return new ImportFileSettings(
            false,
            defaultRegex
        );
    }

    public void setIterationSchema (DocumentIterationSchema schema) {
        this.schema = schema;
    }

    public DocumentIterationSchema getIterationSchema () {
        return schema;
    }

    public boolean preserveCase() {
        return preserveCase;
    }

    public Pattern getTokenRegexPattern() {
        return tokenRegexPattern;
    }

    public void setTokenRegexPattern (Pattern tokenRegexPattern) {
        this.tokenRegexPattern = tokenRegexPattern;
    }

    public String toString () {
        return defaultRegex.toString() + "/" + (preserveCase ? "preserve case" : "insensitive");
    }

    public void updateFrom (ImportFileSettings settings) {
        preserveCase = settings.preserveCase;
        tokenRegexPattern = settings.tokenRegexPattern;
    }

    public interface Importer {
        boolean providePreserveCase();
        Pattern provideTokenRegexPattern();
        boolean provideKeepSequenceBigrams();
        DocumentIterationSchema provideIterationSchema();
    }

    public ImportFileSettings (Importer importer) {
        preserveCase = importer.providePreserveCase();
        tokenRegexPattern = importer.provideTokenRegexPattern();
        keepSequenceBigrams = importer.provideKeepSequenceBigrams();
        schema = importer.provideIterationSchema();
    }

    public interface Exporter {
        void addPreserveCase (boolean preserveCase);
        void addTokenRegexPattern (Pattern pattern);
        void addKeepSequenceBigrams (boolean keepSequence);
        void addDocumentIterationSchema (DocumentIterationSchema schema);
    }

    public void exportTo (Exporter exporter) {
        exporter.addPreserveCase(preserveCase);
        exporter.addTokenRegexPattern(tokenRegexPattern);
        exporter.addKeepSequenceBigrams(keepSequenceBigrams);
        exporter.addDocumentIterationSchema(schema);
    }

}
