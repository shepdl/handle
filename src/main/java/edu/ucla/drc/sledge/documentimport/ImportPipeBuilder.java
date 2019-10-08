package edu.ucla.drc.sledge.documentimport;

import cc.mallet.pipe.*;
import edu.ucla.drc.sledge.ImportFileSettings;

import java.util.ArrayList;
import java.util.List;

public class ImportPipeBuilder {

    private ImportFileSettings settings;

    private Pipe pipe;
    private List<String> stopwords = new ArrayList<>();

    public ImportPipeBuilder () {
    }

    public ImportPipeBuilder addSettings (ImportFileSettings settings) {
        this.settings = settings;
        return this;
    }

    public ImportPipeBuilder addStopwords (List<String> stopwords) {
        this.stopwords = stopwords;
        return this;
    }

    public Pipe getPipe() {
        if (pipe == null) {
            pipe = buildPipe();
        }
        return pipe;
    }

    public Pipe complete () {
        return buildPipe();
    }

    private Pipe buildPipe () {
        List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new SaveDataInSource());
        pipes.add(new Input2CharSequence());
        if (!settings.preserveCase()) {
            pipes.add(new CharSequenceLowercase());
        }
        pipes.add(new CharSequence2TokenSequence(settings.getTokenRegexPattern()));

        TokenSequenceRemoveStopwords stopwordFilter = new TokenSequenceRemoveStopwords(settings.preserveCase(), true);
        stopwordFilter.addStopWords(stopwords.toArray(new String[0]));
        pipes.add(stopwordFilter);

        this.pipe = new SerialPipes(pipes);
        return this.pipe;
    }

    public Pipe buildFeaturePipe() {
         List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new SaveDataInSource());
        pipes.add(new Input2CharSequence());
        if (!settings.preserveCase()) {
            pipes.add(new CharSequenceLowercase());
        }
        pipes.add(new CharSequence2TokenSequence(settings.getTokenRegexPattern()));
        TokenSequenceRemoveStopwords stopwordFilter = new TokenSequenceRemoveStopwords(settings.preserveCase(), true);
        stopwordFilter.addStopWords(stopwords.toArray(new String[0]));
        pipes.add(stopwordFilter);
        pipes.add(new TokenSequence2FeatureSequence());

        this.pipe = new SerialPipes(pipes);
        return this.pipe;
    }

}
