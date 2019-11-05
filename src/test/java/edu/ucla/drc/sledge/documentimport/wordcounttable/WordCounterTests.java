package edu.ucla.drc.sledge.documentimport.wordcounttable;

import cc.mallet.pipe.*;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.TokenSequence;
import edu.ucla.drc.sledge.documentimport.stopwords.TokenSequenceMarkStopwords;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class WordCounterTests {

    SerialPipes pipe;

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
    public void countsNonRepeatedWords () {
        InstanceList instances = new InstanceList(pipe);
        List<Instance> instanceList = new ArrayList<>();
        instanceList.add(new Instance("plum grapes kumquat lime", null, "First document", null));
        instances.addThruPipe(instanceList.iterator());
        WordCounter counter = new WordCounter((TokenSequence)instances.get(0).getData());
        Map<String, Integer> wordCounts = counter.count();
        assertThat(wordCounts.entrySet(), hasSize(4));
        assertThat(wordCounts.get("plum"), equalTo(1));
        assertThat(wordCounts.get("grapes"), equalTo(1));
        assertThat(wordCounts.get("kumquat"), equalTo(1));
        assertThat(wordCounts.get("lime"), equalTo(1));
    }

    @Test
    public void countsRepeatedWords () {
        InstanceList instances = new InstanceList(pipe);
        List<Instance> instanceList = new ArrayList<>();
        instanceList.add(new Instance("plum grapes plum kumquat plum lime kumquat lime", null, "First document", null));
        instances.addThruPipe(instanceList.iterator());
        WordCounter counter = new WordCounter((TokenSequence)instances.get(0).getData());
        Map<String, Integer> wordCounts = counter.count();
        assertThat(wordCounts.entrySet(), hasSize(4));
        assertThat(wordCounts.get("plum"), equalTo(3));
        assertThat(wordCounts.get("grapes"), equalTo(1));
        assertThat(wordCounts.get("kumquat"), equalTo(2));
        assertThat(wordCounts.get("lime"), equalTo(2));
    }

    @Test
    public void countsRepeatedWordsRightAfterEachOther () {
        InstanceList instances = new InstanceList(pipe);
        List<Instance> instanceList = new ArrayList<>();
        instanceList.add(new Instance("plum plum plum grapes plum kumquat plum lime kumquat lime", null, "First document", null));
        instances.addThruPipe(instanceList.iterator());
        WordCounter counter = new WordCounter((TokenSequence)instances.get(0).getData());
        Map<String, Integer> wordCounts = counter.count();
        assertThat(wordCounts.entrySet(), hasSize(4));
        assertThat(wordCounts.get("plum"), equalTo(5));
        assertThat(wordCounts.get("grapes"), equalTo(1));
        assertThat(wordCounts.get("kumquat"), equalTo(2));
        assertThat(wordCounts.get("lime"), equalTo(2));
    }

    @Test
    public void skipsStopWords () {
        InstanceList instances = new InstanceList(pipe);
        List<Instance> instanceList = new ArrayList<>();
        instanceList.add(new Instance("apple plum plum apple banana plum grapes banana plum kumquat plum lime kumquat lime", null, "First document", null));
        instances.addThruPipe(instanceList.iterator());
        WordCounter counter = new WordCounter((TokenSequence)instances.get(0).getData());
        Map<String, Integer> wordCounts = counter.count();
        assertThat(wordCounts.entrySet(), hasSize(4));
        assertThat(wordCounts.get("plum"), equalTo(5));
        assertThat(wordCounts.get("grapes"), equalTo(1));
        assertThat(wordCounts.get("kumquat"), equalTo(2));
        assertThat(wordCounts.get("lime"), equalTo(2));
    }

}
