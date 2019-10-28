package edu.ucla.drc.sledge.documentimport;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TokenSequenceMarkStopwords extends Pipe {

    private final boolean caseSensitive;
    private HashSet<String> stoplist;

    public static String IsStopword = "IsStopword";

    public TokenSequenceMarkStopwords (boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public TokenSequenceMarkStopwords (boolean caseSensitive, Set<String> stoplist) {
        this(caseSensitive);
        this.stoplist = new HashSet<>(stoplist);
    }

    public TokenSequenceMarkStopwords(Set<String> stoplist) {
        this(false, stoplist);
    }

    public void addStopwords (Collection<String> newStopwords) {
        stoplist.addAll(newStopwords);
    }

    public void removeStopwords (Collection<String> stopwordsToRemove) {
        stoplist.removeAll(stopwordsToRemove);
    }

    public Instance pipe (Instance carrier) {
        TokenSequence ts = (TokenSequence)carrier.getData();
        TokenSequence ret = new TokenSequence();
        for (Token token : ts) {
            String word = token.getText();
            if (caseSensitive) {
                word = word.toLowerCase();
            }
            if (stoplist.contains(word)) {
                token.setProperty(IsStopword, true);
            }
            ret.add(token);
        }
        carrier.setData(ret);
        return carrier;
    }

}

