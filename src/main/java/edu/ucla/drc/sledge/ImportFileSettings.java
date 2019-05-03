package edu.ucla.drc.sledge;

import java.util.List;
import java.util.regex.Pattern;

public class ImportFileSettings {

    private boolean preserveCase = true;

    private Pattern tokenRegexPattern;
    private boolean keepSequenceBigrams;

    private List<String> stopwordFilenames;

}
