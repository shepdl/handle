package edu.ucla.drc.sledge.documentlist;

import cc.mallet.pipe.*;
import edu.ucla.drc.sledge.ImportFileSettings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImportPipeBuilder {

    private ImportFileSettings settings;

    private Pipe pipe;
    private List<String> stopwords;

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
//        pipes.add(new Target2Label());
        pipes.add(new SaveDataInSource());
        pipes.add(new Input2CharSequence());
        if (!settings.preserveCase()) {
            pipes.add(new CharSequenceLowercase());
        }

        pipes.add(new CharSequence2TokenSequence(settings.getTokenRegexPattern()));
        pipes.add(new TokenSequenceRemoveNonAlpha(true));

        /*
        TokenSequenceRemoveStopwords stopwordFilter = new TokenSequenceRemoveStopwords(
            false,
            true
        );
        pipes.add(stopwordFilter);

         */
        pipes.add(new TokenSequence2FeatureSequence());

        this.pipe = new SerialPipes(pipes);
        return this.pipe;
    }

}
