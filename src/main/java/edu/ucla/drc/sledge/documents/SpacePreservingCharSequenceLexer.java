package edu.ucla.drc.sledge.documents;

import cc.mallet.util.Lexer;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpacePreservingCharSequenceLexer implements Lexer {

    private final Pattern regex;
    private CharSequence input;
    private String matchText;
    private boolean matchTextFresh;
    private Matcher matcher;

    int spaceStart = -1;

    private Logger logger;


    public SpacePreservingCharSequenceLexer(Pattern regex) {
        logger = Logger.getLogger(this.getClass().getName());
        this.regex = regex;
    }

    public void setCharSequence(CharSequence input) {
        this.input = input;
        this.matchText = null;
        this.matchTextFresh = false;
        if (input != null) {
            this.matcher = this.regex.matcher(input);
        }
    }

    @Override
    public int getStartOffset() {
        return this.matchText == null ? -1 : this.matcher.start();
    }

    @Override
    public int getEndOffset() {
        return this.matchText == null ? -1 : this.matcher.end();
    }

    @Override
    public String getTokenString() {
        return this.matchText;
    }

    @Override
    public boolean hasNext() {
        if (!this.matchTextFresh) {
            this.updateMatchText();
        }
        return this.matchText != null;
    }

    @Override
    public Object next() {
        if (!this.matchTextFresh) {
            this.updateMatchText();
        }
        this.matchTextFresh = false;
        return this.matchText;
    }


    @Override
    public void remove() {

    }

    private void updateMatchText() {
        if (this.matcher != null && this.matcher.find()) {
            this.matchText = this.matcher.group();
            if (this.matchText.length() == 0) {
                this.updateMatchText();
            }
        } else {
            this.matchText = null;
        }

        this.matchTextFresh = true;
    }

}
