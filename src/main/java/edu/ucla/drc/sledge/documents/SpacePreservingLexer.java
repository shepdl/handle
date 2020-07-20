//package edu.ucla.drc.sledge.documents;
package cc.mallet.util;

import java.util.logging.Logger;
import java.util.regex.Pattern;

public class SpacePreservingLexer extends CharSequenceLexer {

    private int spaceStart = -1;
    private int spaceEnd;
    private Object next;

    private boolean lastWasMatch = false;

    public SpacePreservingLexer (Pattern regex) {
        super(regex);
    }

    public SpacePreservingLexer () {
        super(Pattern.compile("\\p{L}+"));
//        super(Pattern.compile("[\\p{Ll}&&\\p{Lu}]+"));
    }

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public boolean hasNext () {
        if (!lastWasMatch) {
            return true;
        } else {
            return super.hasNext();
        }
    }

    @Override
    public CharSequence next () {
        if (next == null) {
            next = super.next();
        }
        // Space at the beginning of the string
        if (spaceStart == -1 && super.getStartOffset() > 0) {
            spaceStart = 0;
            lastWasMatch = false;
            spaceEnd = super.getStartOffset();
            return input.subSequence(spaceStart, super.getStartOffset());
        }
        if (lastWasMatch) {
            lastWasMatch = false;
            next = super.next();
            spaceEnd = (next == null) ? input.length() : super.getStartOffset();
            return input.subSequence(spaceStart, spaceEnd);
        } else {
            lastWasMatch = true;
            spaceStart = super.getEndOffset();
            return (CharSequence) next;
        }
    }

    @Override
    public int getStartOffset() {
        // if the last was a word, then we return the word (because we're getting the start offset)
        // otherwise, return the space
        if (lastWasMatch) {
            return super.getStartOffset();
        } else {
            return spaceStart;
        }
    }

    @Override
    public int getEndOffset() {
        if (lastWasMatch) {
            return super.getEndOffset();
        } else {
            return spaceEnd;
        }

    }


}
