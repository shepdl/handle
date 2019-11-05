package edu.ucla.drc.sledge.documentimport.wordcounttable;

import cc.mallet.types.TokenSequence;
import edu.ucla.drc.sledge.documentimport.stopwords.TokenSequenceMarkStopwords;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class WordCounter {

    private final TokenSequence ts;

    public WordCounter (TokenSequence sequence) {
        this.ts = sequence;
    }

    public Map<String, Integer> count () {
        Map<String, Integer> counts = new HashMap<>();
        IntStream.range(0, ts.size()).forEach(i -> {
            String word = ts.get(i).getText();
            if (ts.get(i).hasProperty(TokenSequenceMarkStopwords.IsStopword)) {
                // TODO: display in the list, for example, italicized
                return;
            }
            if (!counts.containsKey(word)) {
                counts.put(word, 0);
            }
            counts.put(word, counts.get(word) + 1);
        });

        return counts;
    }
}
