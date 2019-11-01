package edu.ucla.drc.sledge.documentimport;

import cc.mallet.pipe.*;
import cc.mallet.types.InstanceList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;

public class TokenSequenceMarkStopwordsHandlesCaseInsensitiveTerms {
    private SerialPipes pipe;

    @Before
    public void setUp () {
        List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new SaveDataInSource());
        pipes.add(new Input2CharSequence());
        pipes.add(new CharSequenceLowercase());
        Pattern nonEnglishRegex = Pattern.compile("[\\p{L}\\p{M}]+");
        pipes.add(new CharSequence2TokenSequence(nonEnglishRegex));
        Set<String> stopwords = new HashSet<>();
        stopwords.add("apple");
        stopwords.add("banana");
        TokenSequenceMarkStopwords stopwordFilter = new TokenSequenceMarkStopwords(new HashSet<>(stopwords));
        pipes.add(stopwordFilter);
        pipe = new SerialPipes(pipes);
    }

    @Test
    public void removeStopwordsFromOneDocument () {
        InstanceList instances = new InstanceList(pipe);
//        instances.addThruPipe();
        Assert.fail("Not implemented yet");
    }

    @Test
    public void removeStopwordsFromManyDocuments () {
        Assert.fail("Not implemented yet");
    }

    @Test
    public void removeStopwordsFromNonEnglishDocuments () {
        Assert.fail("Not implemented yet");
    }

    @Test
    public void removeStopwordsWhenCaseInsensitive () {
        Assert.fail("Not implemented yet");
    }

    @Test
    public void removeStopwordsWhenCaseSensitive () {
        Assert.fail("Not implemented yet");
    }

}
