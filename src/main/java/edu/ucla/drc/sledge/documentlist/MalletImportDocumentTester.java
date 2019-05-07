package edu.ucla.drc.sledge.documentlist;


import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.pipe.iterator.FileIterator;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.InstanceList;
import cc.mallet.util.CharSequenceLexer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MalletImportDocumentTester {

    public static void main(String[] args) {
        List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new Target2Label());
        pipes.add(new SaveDataInSource());
        pipes.add(new Input2CharSequence());
        pipes.add(new CharSequenceLowercase());
        pipes.add(new CharSequence2TokenSequence(CharSequenceLexer.LEX_NONWHITESPACE_CLASSES));
        pipes.add(new TokenSequenceRemoveNonAlpha(true));

        TokenSequenceRemoveStopwords stopwordFilter = new TokenSequenceRemoveStopwords(
                new File("./en-stoplist.txt"),
                "UTF8",
                false,
                false,
                true
        );
        pipes.add(stopwordFilter);
        pipes.add(new TokenSequence2FeatureSequence());

        Pipe instancePipe = new SerialPipes(pipes);

        InstanceList instances = new InstanceList(instancePipe);
            /*
            instances.addThruPipe(new CsvIterator(
                    new InputStreamReader(new FileInputStream("./59390-0.txt"), "UTF8"),
                    Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
                    1,
                    0,
                    0
            ));
             */
            File[] dirs = new File[1];
            dirs[0] = new File("./sample-data/");
            instances.addThruPipe(
                    new FileIterator(dirs, FileIterator.STARTING_DIRECTORIES, true)
            );

        System.out.println(instances);
        FeatureSequence fs = (FeatureSequence)instances.get(0).getData();
        System.out.print(fs);
        Alphabet alphabet = fs.getAlphabet();
        int[] features = fs.getFeatures();
        System.out.println(fs.toFeatureIndexSequence());
        int[] x = fs.toFeatureIndexSequence();
        return;
//        for (int i = 0; i < features.length; i++) {
//            System.out.print(alphabet.lookupObject(features[i]));
//            System.out.print(" ");
//        }
    }
}
