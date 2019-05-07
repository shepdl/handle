package edu.ucla.drc.sledge.importsettings;

import edu.ucla.drc.sledge.ImportFileSettings;

import java.util.regex.Pattern;

public class ImportSettingsModel {

    public enum DocumentSettings {
        OneFile, OneLine
    };

    public enum WordSettings {
        JustLetters, AllCharacters
    };

    public WordSettings getWordSettings() {
        return wordSettings;
    }

    public void setWordSettings(WordSettings wordSettings) {
        this.wordSettings = wordSettings;
    }

    private WordSettings wordSettings;
    private DocumentSettings documentSettings;

    private boolean preserveCase = true;

    public boolean getPreserveCase () {
        return preserveCase;
    }

    public void setPreserveCase(boolean preserveCase) {
        this.preserveCase = preserveCase;
    }

    public static ImportSettingsModel fromImportSettings (ImportFileSettings settings) {
        ImportSettingsModel model = new ImportSettingsModel();
        model.setPreserveCase(settings.preserveCase());
        if (settings.getTokenRegexPattern() == ImportFileSettings.defaultRegex) {
            model.wordSettings = WordSettings.JustLetters;
        } else {
            model.wordSettings = WordSettings.AllCharacters;
        }

        return model;
    }

    public ImportFileSettings toSettings () {
        Pattern tokenPattern;
        if (wordSettings == WordSettings.JustLetters) {
            tokenPattern = ImportFileSettings.defaultRegex;
        } else {
            tokenPattern = ImportFileSettings.nonEnglishRegex;
        }
        ImportFileSettings settings = new ImportFileSettings(
            preserveCase, tokenPattern
        );

        return settings;
    }
}
