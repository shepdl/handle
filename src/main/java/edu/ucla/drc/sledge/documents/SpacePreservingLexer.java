//package edu.ucla.drc.sledge.documents;
package cc.mallet.util;

import java.util.regex.Pattern;

public class SpacePreservingLexer extends CharSequenceLexer {

    private int spaceStart = -1;
    private Object next;

    private boolean lastWasMatch = false;

    public SpacePreservingLexer (Pattern regex) {
        super(regex);
    }

    public SpacePreservingLexer () {
        super(Pattern.compile("[\\p{Ll}\\p{Lu}]+"));
    }

    @Override
    public CharSequence next () {
        if (next == null) {
            System.out.println("Next was null; initializing ...");
            next = super.next();
        }
        // Space at the beginning of the string
        if (spaceStart == -1 && getStartOffset() > 0) {
            spaceStart = 0;
            lastWasMatch = false;
            System.out.println("Returning space at beginning to " + getStartOffset());
            return input.subSequence(spaceStart, getStartOffset());
        }
        if (lastWasMatch) {
            lastWasMatch = false;
            next = super.next();
            int spaceEnd = (next != null) ? getStartOffset() : input.length();
            System.out.println("Returning space with start " + spaceStart + " and end " + spaceEnd);
            return input.subSequence(spaceStart, spaceEnd);
        } else {
            lastWasMatch = true;
            spaceStart = getEndOffset();
            System.out.println("Returning " + next + " with next space starting at " + spaceStart);
            return (CharSequence) next;
        }
    }

}
