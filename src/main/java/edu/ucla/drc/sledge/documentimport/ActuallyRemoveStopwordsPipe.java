package edu.ucla.drc.sledge.documentimport;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;

public class ActuallyRemoveStopwordsPipe extends Pipe {

    public Instance pipe (Instance carrier) {
        TokenSequence ts = (TokenSequence)carrier.getData();
        TokenSequence ret = new TokenSequence();
        for (Token token : ts) {
            if (!isStopword(token)) {
                ret.add(token);
            }
        }
        carrier.setData(ret);
        return carrier;
    }

    private boolean isStopword (Token token) {
        return token.hasProperty(TokenSequenceMarkStopwords.IsStopword) &&
                ((boolean)token.getProperty(TokenSequenceMarkStopwords.IsStopword) == true);
    }
}
