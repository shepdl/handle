package edu.ucla.drc.sledge.documentimport.stopwords;

import cc.mallet.pipe.*;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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
        stopwords.add("これ");
        stopwords.add("だれ");
        stopwords.add("それ");
        stopwords.add("あれ");
        TokenSequenceMarkStopwords stopwordFilter = new TokenSequenceMarkStopwords(new HashSet<>(stopwords));
        pipes.add(stopwordFilter);
        pipe = new SerialPipes(pipes);
    }

    @Test
    public void removeStopwordsFromOneDocument () {
        InstanceList instances = new InstanceList(pipe);
        List<Instance> instanceList = new ArrayList<>();
        instanceList.add(new Instance(
                "apple banana plum grapes bananas kumquat lime apple lime apple",
                null, "First document", null
        ));
        instances.addThruPipe(instanceList.iterator());
        TokenSequence data = (TokenSequence)instances.get(0).getData();
        for (int i = 0; i < data.size(); i++) {
            Token word = data.get(i);
            if (word.getText().equals("apple") || word.getText().equals("banana")) {
                assertThat(word.hasProperty(TokenSequenceMarkStopwords.IsStopword), equalTo(true));
                assertThat(word.getProperty(TokenSequenceMarkStopwords.IsStopword), equalTo(true));
            } else {
                assertThat(word.hasProperty(TokenSequenceMarkStopwords.IsStopword), equalTo(false));
            }
        }
    }

    @Test
    public void removeStopwordsFromManyDocuments () {
        InstanceList instances = new InstanceList(pipe);
        List<Instance> instanceList = new ArrayList<>();
        instanceList.add(new Instance(
                "apple banana plum grapes bananas kumquat lime apple lime apple",
                null, "First document", null
        ));
        instanceList.add(new Instance(
                "plum grapes bananas sequins kumquat banana plum banana apple apples lime lime grapes",
                null, "Second document", null
        ));
        instances.addThruPipe(instanceList.iterator());
        for (int j = 0; j < instances.size(); j++) {
            TokenSequence data = (TokenSequence)instances.get(j).getData();
            for (int i = 0; i < data.size(); i++) {
                Token word = data.get(i);
                if (word.getText().equals("apple") || word.getText().equals("banana")) {
                    assertThat(word.hasProperty(TokenSequenceMarkStopwords.IsStopword), equalTo(true));
                    assertThat(word.getProperty(TokenSequenceMarkStopwords.IsStopword), equalTo(true));
                } else {
                    assertThat(word.hasProperty(TokenSequenceMarkStopwords.IsStopword), equalTo(false));
                }
            }
        }
    }

    @Test
    public void removeStopwordsFromNonEnglishDocuments () {
        InstanceList instances = new InstanceList(pipe);
        List<Instance> instanceList = new ArrayList<>();
        instanceList.add(new Instance(
                "これ　は　林檎　です",
                null, "First document", null
        ));
        instanceList.add(new Instance(
                "あれ　は　林檎　でわ　ありません",
                null, "Second document", null
        ));
        instances.addThruPipe(instanceList.iterator());
        for (int j = 0; j < instances.size(); j++) {
            TokenSequence data = (TokenSequence)instances.get(j).getData();
            for (int i = 0; i < data.size(); i++) {
                Token word = data.get(i);
                if (word.getText().equals("これ") || word.getText().equals("あれ")) {
                    assertThat(word.hasProperty(TokenSequenceMarkStopwords.IsStopword), equalTo(true));
                    assertThat(word.getProperty(TokenSequenceMarkStopwords.IsStopword), equalTo(true));
                } else {
                    assertThat(word.hasProperty(TokenSequenceMarkStopwords.IsStopword), equalTo(false));
                }
            }
        }
    }

    @Test
    public void removeStopwordsWhenCaseInsensitive () {
        List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new SaveDataInSource());
        pipes.add(new Input2CharSequence());
        pipes.add(new CharSequenceLowercase());
        Pattern nonEnglishRegex = Pattern.compile("[\\p{L}\\p{M}]+");
        pipes.add(new CharSequence2TokenSequence(nonEnglishRegex));

        Set<String> stopwords = new HashSet<>();
        stopwords.add("apple");
        stopwords.add("banana");
        TokenSequenceMarkStopwords stopwordFilter = new TokenSequenceMarkStopwords(true, new HashSet<>(stopwords));
        pipes.add(stopwordFilter);
        pipe = new SerialPipes(pipes);

        InstanceList instances = new InstanceList(pipe);
        List<Instance> instanceList = new ArrayList<>();
        instanceList.add(new Instance(
                "Apple banana plum grapes bananas kumquat lime apple lime Apple",
                null, "First document", null
        ));
        instanceList.add(new Instance(
                "plum grapes bananas sequins kumquat banana plum Banana apple apples lime lime grapes",
                null, "Second document", null
        ));
        instances.addThruPipe(instanceList.iterator());
        for (int j = 0; j < instances.size(); j++) {
            TokenSequence data = (TokenSequence)instances.get(j).getData();
            for (int i = 0; i < data.size(); i++) {
                Token word = data.get(i);
                if (word.getText().equals("apple") || word.getText().equals("banana")) {
                    assertThat(word.hasProperty(TokenSequenceMarkStopwords.IsStopword), equalTo(true));
                    assertThat(word.getProperty(TokenSequenceMarkStopwords.IsStopword), equalTo(true));
                } else {
                    assertThat(word.hasProperty(TokenSequenceMarkStopwords.IsStopword), equalTo(false));
                }
            }
        }
    }

    @Test
    public void removeStopwordsWhenCaseSensitive () {
        InstanceList instances = new InstanceList(pipe);
        List<Instance> instanceList = new ArrayList<>();
        instanceList.add(new Instance(
                "apple banana plum grapes bananas kumquat lime apple lime apple",
                null, "First document", null
        ));
        instanceList.add(new Instance(
                "plum grapes bananas sequins kumquat banana plum banana apple apples lime lime grapes",
                null, "Second document", null
        ));
        instances.addThruPipe(instanceList.iterator());
        for (int j = 0; j < instances.size(); j++) {
            TokenSequence data = (TokenSequence)instances.get(j).getData();
            for (int i = 0; i < data.size(); i++) {
                Token word = data.get(i);
                if (word.getText().equals("apple") || word.getText().equals("banana")) {
                    assertThat(word.hasProperty(TokenSequenceMarkStopwords.IsStopword), equalTo(true));
                    assertThat(word.getProperty(TokenSequenceMarkStopwords.IsStopword), equalTo(true));
                } else {
                    assertThat(word.hasProperty(TokenSequenceMarkStopwords.IsStopword), equalTo(false));
                }
            }
        }
    }

}
