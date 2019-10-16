package edu.ucla.drc.sledge;

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

}
