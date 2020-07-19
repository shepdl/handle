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
        super(Pattern.compile("[\\p{Ll}&&\\p{Lu}]+"));
    }

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public boolean hasNext () {
        // Rules:
        if (!lastWasMatch) {
            return true;
        } else {
            return super.hasNext();
        }
        //  update when we didn't return a space the last time
//        System.out.println("Before: " + this.matchText);
//        boolean result = super.hasNext();
//        System.out.println("After: " + this.matchText);
//        return result;
    }

    @Override
    public CharSequence next () {
//        logger.info("In next: " + this.matchText);
        if (next == null) {
//            logger.info("Next was null; initializing ...");
            next = super.next();
            if (next == null) {
                next = super.next();
            }
        }
        // Space at the beginning of the string
        if (spaceStart == -1 && super.getStartOffset() > 0) {
            spaceStart = 0;
            lastWasMatch = false;
//            logger.info("Returning space at beginning to " + super.getStartOffset());
            spaceEnd = super.getStartOffset();
            return input.subSequence(spaceStart, super.getStartOffset());
        }
        if (lastWasMatch) {
            lastWasMatch = false;
            next = super.next();
//            int spaceEnd = (next != null) ? getStartOffset() : input.length();
            spaceEnd = (next == null) ? input.length() : super.getStartOffset();
//            logger.info("Returning space with start " + spaceStart + " and end " + spaceEnd);
            CharSequence theSeq = input.subSequence(spaceStart, spaceEnd);
//            logger.info("The sequence:");
//            logger.info(theSeq);
            return theSeq;
        } else {
            lastWasMatch = true;
            spaceStart = super.getEndOffset();
//            logger.info("Returning " + next + " with next space starting at " + spaceStart);
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
//        logger.info("End: ");
//        logger.info(spaceEnd);
        if (lastWasMatch) {
            return super.getEndOffset();
        } else {
            return spaceEnd;
        }

    }


}
