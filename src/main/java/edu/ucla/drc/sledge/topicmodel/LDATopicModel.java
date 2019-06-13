package edu.ucla.drc.sledge.topicmodel;

import cc.mallet.topics.MarginalProbEstimator;
import cc.mallet.topics.TopicAssignment;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.*;
import cc.mallet.util.Randoms;
import edu.ucla.drc.sledge.topicsettings.TopicDocumentContainerSummary;

import java.text.NumberFormat;
import java.util.*;


public class LDATopicModel implements TopicModel {

    public static final int UNASSIGNED_TOPIC = -1;

    public ArrayList<TopicAssignment> data;  // the training instances and their topic assignments
    public Alphabet alphabet; // the alphabet for the input data
    public LabelAlphabet topicAlphabet;  // the alphabet for the topics

    public int numTopics; // Number of topics to be fit

    // These values are used to encode type/topic counts as
    //  count/topic pairs in a single int.
    public int topicMask;
    public int topicBits;

    public int numTypes;
    public int totalTokens;

    public double[] alpha;	 // Dirichlet(alpha,alpha,...) is the distribution over topics
    private double alphaSum;
    private double beta;   // Prior on per-topic multinomial distribution over words
    public double betaSum;

    public boolean usingSymmetricAlpha = false;

    public static final double DEFAULT_BETA = 0.01;

    public int[][] typeTopicCounts; // indexed by <feature index, topic index>
    public int[] tokensPerTopic; // indexed by <topic index>

    // for dirichlet estimation
    public int[] docLengthCounts; // histogram of document sizes
    public int[][] topicDocCounts; // histogram of document/topic counts, indexed by <topic index, sequence position index>

    public int numIterations = 1000;
    public int burninPeriod = 200;
    public int saveSampleInterval = 10;
    public int optimizeInterval = 50;
    public int temperingInterval = 0;

    public int showTopicsInterval = 50;
    public int wordsPerTopic = 7;

    public int saveStateInterval = 0;
    public String stateFilename = null;

    public int saveModelInterval = 0;
    public String modelFilename = null;

    public int randomSeed = -1;
    public NumberFormat formatter;
    public boolean printLogLikelihood = true;

    int[] typeTotals;
    // The max over typeTotals, used for beta optimization
    int maxTypeCount;

    public LDATopicModel (int numberOfTopics, double alphaSum, double beta) {
        this (newLabelAlphabet (numberOfTopics), alphaSum, beta);
    }

    private static LabelAlphabet newLabelAlphabet (int numTopics) {
        LabelAlphabet ret = new LabelAlphabet();
        for (int i = 0; i < numTopics; i++)
            ret.lookupIndex("topic"+i);
        return ret;
    }

    public LDATopicModel (LabelAlphabet topicAlphabet, double alphaSum, double beta) {

        this.data = new ArrayList<TopicAssignment>();
        this.topicAlphabet = topicAlphabet;
        this.setAlphaSum(alphaSum);
        this.setBeta(beta);

        setNumTopics(topicAlphabet.size());

        formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(5);

    }

    public Alphabet getAlphabet() { return alphabet; }
    public LabelAlphabet getTopicAlphabet() { return topicAlphabet; }
    public int getNumTopics() { return numTopics; }

    @Override
    public int getNumTypes() {
        return 0;
    }

    /** Set or reset the number of topics. This method will not change any token-topic assignments,
     so it should only be used before initializing or restoring a previously saved state. */
    public void setNumTopics(int numTopics) {
        this.numTopics = numTopics;

        if (Integer.bitCount(numTopics) == 1) {
            // exact power of 2
            topicMask = numTopics - 1;
            topicBits = Integer.bitCount(topicMask);
        }
        else {
            // otherwise add an extra bit
            topicMask = Integer.highestOneBit(numTopics) * 2 - 1;
            topicBits = Integer.bitCount(topicMask);
        }

        this.alpha = new double[numTopics];
        Arrays.fill(alpha, getAlphaSum() / numTopics);

        tokensPerTopic = new int[numTopics];
    }

    public ArrayList<TopicAssignment> getData() { return data; }

    public int[][] getTypeTopicCounts() { return typeTopicCounts; }

    @Override
    public double[] getAlpha() {
        return new double[0];
    }

    public int[] getTokensPerTopic() { return tokensPerTopic; }

    public void setNumIterations (int numIterations) {
        this.numIterations = numIterations;
    }

    public void setBurninPeriod (int burninPeriod) {
        this.burninPeriod = burninPeriod;
    }

    public void setTopicDisplay(int interval, int n) {
        this.showTopicsInterval = interval;
        this.wordsPerTopic = n;
    }

    public void setRandomSeed(int seed) {
        randomSeed = seed;
    }

    /** Interval for optimizing Dirichlet hyperparameters */
    public void setOptimizeInterval(int interval) {
        this.optimizeInterval = interval;

        // Make sure we always have at least one sample
        //  before optimizing hyperparameters
        if (saveSampleInterval > optimizeInterval) {
            saveSampleInterval = optimizeInterval;
        }
    }

    public void setSymmetricAlpha(boolean b) {
        usingSymmetricAlpha = b;
    }

    public void setTemperingInterval(int interval) {
        temperingInterval = interval;
    }

    /** Define how often and where to save a text representation of the current state.
     *  Files are GZipped.
     *
     * @param interval Save a copy of the state every <code>interval</code> iterations.
     * @param filename Save the state to this file, with the iteration number as a suffix
     */
    public void setSaveState(int interval, String filename) {
        this.saveStateInterval = interval;
        this.stateFilename = filename;
    }

    /** Define how often and where to save a serialized model.
     *
     * @param interval Save a serialized model every <code>interval</code> iterations.
     * @param filename Save to this file, with the iteration number as a suffix
     */
    public void setSaveSerializedModel(int interval, String filename) {
        this.saveModelInterval = interval;
        this.modelFilename = filename;
    }

    public void addInstances (InstanceList training) {

        alphabet = training.getDataAlphabet();
        numTypes = alphabet.size();

        betaSum = getBeta() * numTypes;

        Randoms random = null;
        if (randomSeed == -1) {
            random = new Randoms();
        }
        else {
            random = new Randoms(randomSeed);
        }

        for (Instance instance : training) {
            FeatureSequence tokens = (FeatureSequence) instance.getData();
            LabelSequence topicSequence =
                    new LabelSequence(topicAlphabet, new int[ tokens.size() ]);

            int[] topics = topicSequence.getFeatures();
            for (int position = 0; position < topics.length; position++) {

                int topic = random.nextInt(numTopics);
                topics[position] = topic;

            }

            TopicAssignment t = new TopicAssignment(instance, topicSequence);
            data.add(t);
        }

        buildInitialTypeTopicCounts();
        initializeHistograms();
    }

    public void buildInitialTypeTopicCounts () {

        typeTopicCounts = new int[numTypes][];
        tokensPerTopic = new int[numTopics];

        // Get the total number of occurrences of each word type
        //int[] typeTotals = new int[numTypes];
        typeTotals = new int[numTypes];

        // Create the type-topic counts data structure
        for (TopicAssignment document : data) {

            FeatureSequence tokens = (FeatureSequence) document.instance.getData();
            for (int position = 0; position < tokens.getLength(); position++) {
                int type = tokens.getIndexAtPosition(position);
                typeTotals[ type ]++;
            }
        }

        maxTypeCount = 0;

        // Allocate enough space so that we never have to worry about
        //  overflows: either the number of topics or the number of times
        //  the type occurs.
        for (int type = 0; type < numTypes; type++) {
            if (typeTotals[type] > maxTypeCount) { maxTypeCount = typeTotals[type]; }
            typeTopicCounts[type] = new int[ Math.min(numTopics, typeTotals[type]) ];
        }

        for (TopicAssignment document : data) {

            FeatureSequence tokens = (FeatureSequence) document.instance.getData();
            FeatureSequence topicSequence =  (FeatureSequence) document.topicSequence;

            int[] topics = topicSequence.getFeatures();
            for (int position = 0; position < tokens.size(); position++) {

                int topic = topics[position];

                if (topic == UNASSIGNED_TOPIC) { continue; }

                tokensPerTopic[topic]++;

                // The format for these arrays is
                //  the topic in the rightmost bits
                //  the count in the remaining (left) bits.
                // Since the count is in the high bits, sorting (desc)
                //  by the numeric value of the int guarantees that
                //  higher counts will be before the lower counts.

                int type = tokens.getIndexAtPosition(position);
                int[] currentTypeTopicCounts = typeTopicCounts[ type ];

                // Start by assuming that the array is either empty
                //  or is in sorted (descending) order.

                // Here we are only adding counts, so if we find
                //  an existing location with the topic, we only need
                //  to ensure that it is not larger than its left neighbor.

                int index = 0;
                int currentTopic = currentTypeTopicCounts[index] & topicMask;
                int currentValue;

                while (currentTypeTopicCounts[index] > 0 && currentTopic != topic) {
                    index++;
                    if (index == currentTypeTopicCounts.length) {
//						logger.info("overflow on type " + type);
                    }
                    currentTopic = currentTypeTopicCounts[index] & topicMask;
                }
                currentValue = currentTypeTopicCounts[index] >> topicBits;

                if (currentValue == 0) {
                    // new value is 1, so we don't have to worry about sorting
                    //  (except by topic suffix, which doesn't matter)

                    currentTypeTopicCounts[index] =
                            (1 << topicBits) + topic;
                }
                else {
                    currentTypeTopicCounts[index] =
                            ((currentValue + 1) << topicBits) + topic;

                    // Now ensure that the array is still sorted by
                    //  bubbling this value up.
                    while (index > 0 &&
                            currentTypeTopicCounts[index] > currentTypeTopicCounts[index - 1]) {
                        int temp = currentTypeTopicCounts[index];
                        currentTypeTopicCounts[index] = currentTypeTopicCounts[index - 1];
                        currentTypeTopicCounts[index - 1] = temp;

                        index--;
                    }
                }
            }
        }
    }


    /**
     *  Gather statistics on the size of documents
     *  and create histograms for use in Dirichlet hyperparameter
     *  optimization.
     */
    private void initializeHistograms() {

        int maxTokens = 0;
        totalTokens = 0;
        int seqLen;

        for (int doc = 0; doc < data.size(); doc++) {
            FeatureSequence fs = (FeatureSequence) data.get(doc).instance.getData();
            seqLen = fs.getLength();
            if (seqLen > maxTokens)
                maxTokens = seqLen;
            totalTokens += seqLen;
        }

//		logger.info("max tokens: " + maxTokens);
//		logger.info("total tokens: " + totalTokens);

        docLengthCounts = new int[maxTokens + 1];
        topicDocCounts = new int[numTopics][maxTokens + 1];
    }

    /** This method implements iterated conditional modes, which is equivalent to Gibbs sampling,
     *   but replacing sampling from the conditional distribution with taking the maximum
     *   topic. It tends to converge within a small number of iterations for models that have
     *   reached a good state through Gibbs sampling. */
    public void maximize(int iterations) {

        int iteration = 0;

        int totalChange = Integer.MAX_VALUE;

        double[] topicCoefficients = new double[numTopics];

        int currentTopic, currentValue;

        while (iteration < iterations && totalChange > 0) {

            long iterationStart = System.currentTimeMillis();

            totalChange = 0;

            // Loop over every document in the corpus
            for (int doc = 0; doc < data.size(); doc++) {
                FeatureSequence tokenSequence =
                        (FeatureSequence) data.get(doc).instance.getData();
                LabelSequence topicSequence =
                        (LabelSequence) data.get(doc).topicSequence;

                int[] oneDocTopics = topicSequence.getFeatures();

                int[] currentTypeTopicCounts;
                int type, oldTopic, newTopic;

                int docLength = tokenSequence.getLength();

                int[] localTopicCounts = new int[numTopics];

                //populate topic counts
                for (int position = 0; position < docLength; position++) {
                    localTopicCounts[oneDocTopics[position]]++;
                }

                int globalMaxTopic = 0;
                double globalMaxScore = 0.0;
                for (int topic = 0; topic < numTopics; topic++) {
                    topicCoefficients[topic] = (alpha[topic] + localTopicCounts[topic]) / (betaSum + tokensPerTopic[topic]);
                    if (getBeta() * topicCoefficients[topic] > globalMaxScore) {
                        globalMaxTopic = topic;
                        globalMaxScore = getBeta() * topicCoefficients[topic];
                    }
                }

                double score, maxScore;
                double[] topicTermScores = new double[numTopics];

                //Iterate over the positions (words) in the document
                for (int position = 0; position < docLength; position++) {
                    type = tokenSequence.getIndexAtPosition(position);
                    oldTopic = oneDocTopics[position];

                    // Grab the relevant row from our two-dimensional array
                    currentTypeTopicCounts = typeTopicCounts[type];

                    //Remove this token from all counts.
                    localTopicCounts[oldTopic]--;
                    tokensPerTopic[oldTopic]--;

                    // Recalculate the word-invariant part
                    topicCoefficients[oldTopic] = (alpha[oldTopic] + localTopicCounts[oldTopic]) / (betaSum + tokensPerTopic[oldTopic]);

                    // If the topic we just decremented was the previous max topic, search
                    //  for a new max topic.
                    if (oldTopic == globalMaxTopic) {
                        globalMaxScore = getBeta() * topicCoefficients[oldTopic];
                        for (int topic = 0; topic < numTopics; topic++) {
                            if (getBeta() * topicCoefficients[topic] > globalMaxScore) {
                                globalMaxTopic = topic;
                                globalMaxScore = getBeta() * topicCoefficients[topic];
                            }
                        }
                    }

                    newTopic = globalMaxTopic;
                    maxScore = globalMaxScore;

                    assert(tokensPerTopic[oldTopic] >= 0) : "old Topic " + oldTopic + " below 0";

                    int index = 0;
                    boolean alreadyDecremented = false;

                    while (index < currentTypeTopicCounts.length &&
                            currentTypeTopicCounts[index] > 0) {
                        currentTopic = currentTypeTopicCounts[index] & topicMask;
                        currentValue = currentTypeTopicCounts[index] >> topicBits;

                        if (! alreadyDecremented && currentTopic == oldTopic) {

                            // We're decrementing and adding up the
                            //  sampling weights at the same time, but
                            //  decrementing may require us to reorder
                            //  the topics, so after we're done here,
                            //  look at this cell in the array again.

                            currentValue --;
                            if (currentValue == 0) {
                                currentTypeTopicCounts[index] = 0;
                            }
                            else {
                                currentTypeTopicCounts[index] = (currentValue << topicBits) + oldTopic;
                            }

                            // Shift the reduced value to the right, if necessary.

                            int subIndex = index;
                            while (subIndex < currentTypeTopicCounts.length - 1 &&
                                    currentTypeTopicCounts[subIndex] < currentTypeTopicCounts[subIndex + 1]) {
                                int temp = currentTypeTopicCounts[subIndex];
                                currentTypeTopicCounts[subIndex] = currentTypeTopicCounts[subIndex + 1];
                                currentTypeTopicCounts[subIndex + 1] = temp;

                                subIndex++;
                            }

                            alreadyDecremented = true;
                        }
                        else {
                            score =
                                    topicCoefficients[currentTopic] * (getBeta() + currentValue);
                            if (score > maxScore) {
                                newTopic = currentTopic;
                                maxScore = score;
                            }

                            index++;
                        }
                    }

                    // Put that new topic into the counts
                    oneDocTopics[position] = newTopic;
                    localTopicCounts[newTopic]++;
                    tokensPerTopic[newTopic]++;

                    index = 0;
                    boolean foundTopic = false;
                    while (! foundTopic && index < currentTypeTopicCounts.length) {
                        currentTopic = currentTypeTopicCounts[index] & topicMask;
                        currentValue = currentTypeTopicCounts[index] >> topicBits;

                        if (currentTopic == newTopic) {
                            currentTypeTopicCounts[index] = ((currentValue + 1) << topicBits) + newTopic;

                            while (index > 0 && currentTypeTopicCounts[index] > currentTypeTopicCounts[index - 1]) {
                                int temp = currentTypeTopicCounts[index];
                                currentTypeTopicCounts[index] = currentTypeTopicCounts[index - 1];
                                currentTypeTopicCounts[index - 1] = temp;
                            }
                            foundTopic = true;
                        }
                        else if (currentValue == 0) {
                            currentTypeTopicCounts[index] = (1 << topicBits) + newTopic;
                            foundTopic = true;
                        }

                        index++;
                    }

                    topicCoefficients[newTopic] = (alpha[newTopic] + localTopicCounts[newTopic]) / (betaSum + tokensPerTopic[newTopic]);
                    if (getBeta() * topicCoefficients[newTopic] > globalMaxScore) {
                        globalMaxScore = getBeta() * topicCoefficients[newTopic];
                        globalMaxTopic = newTopic;
                    }

                    if (newTopic != oldTopic) {
                        totalChange++;
                    }
                }


            }

            long elapsedMillis = System.currentTimeMillis() - iterationStart;
//			logger.info(iteration + "\t" + elapsedMillis + "ms\t" + totalChange + "\t" + (modelLogLikelihood() / totalTokens));

            iteration++;
        }
    }

    /**
     *  Return an array of sorted sets (one set per topic). Each set
     *   contains IDSorter objects with integer keys into the alphabet.
     *   To get direct access to the Strings, use getTopWords().
     */
    public ArrayList<TreeSet<IDSorter>> getSortedWords () {

        ArrayList<TreeSet<IDSorter>> topicSortedWords = new ArrayList<TreeSet<IDSorter>>(numTopics);

        // Initialize the tree sets
        for (int topic = 0; topic < numTopics; topic++) {
            topicSortedWords.add(new TreeSet<IDSorter>());
        }

        // Collect counts
        for (int type = 0; type < numTypes; type++) {

            int[] topicCounts = typeTopicCounts[type];

            int index = 0;
            while (index < topicCounts.length &&
                    topicCounts[index] > 0) {

                int topic = topicCounts[index] & topicMask;
                int count = topicCounts[index] >> topicBits;

                topicSortedWords.get(topic).add(new IDSorter(type, count));

                index++;
            }
        }

        return topicSortedWords;
    }

    /** Return an array (one element for each topic) of arrays of words, which
     *  are the most probable words for that topic in descending order. These
     *  are returned as Objects, but will probably be Strings.
     *
     *  @param numWords The maximum length of each topic's array of words (may be less).
     */

    public Object[][] getTopWords(int numWords) {

        ArrayList<TreeSet<IDSorter>> topicSortedWords = getSortedWords();
        Object[][] result = new Object[ numTopics ][];

        for (int topic = 0; topic < numTopics; topic++) {

            TreeSet<IDSorter> sortedWords = topicSortedWords.get(topic);

            // How many words should we report? Some topics may have fewer than
            //  the default number of words with non-zero weight.
            int limit = numWords;
            if (sortedWords.size() < numWords) { limit = sortedWords.size(); }

            result[topic] = new Object[limit];

            Iterator<IDSorter> iterator = sortedWords.iterator();
            for (int i=0; i < limit; i++) {
                IDSorter info = iterator.next();
                result[topic][i] = alphabet.lookupObject(info.getID());
            }
        }

        return result;
    }

    public String displayTopWords (int numWords, boolean usingNewLines) {

        StringBuilder out = new StringBuilder();

        ArrayList<TreeSet<IDSorter>> topicSortedWords = getSortedWords();

        // Print results for each topic
        for (int topic = 0; topic < numTopics; topic++) {
            TreeSet<IDSorter> sortedWords = topicSortedWords.get(topic);
            int word = 0;
            Iterator<IDSorter> iterator = sortedWords.iterator();

            if (usingNewLines) {
                out.append (topic + "\t" + formatter.format(alpha[topic]) + "\n");
                while (iterator.hasNext() && word < numWords) {
                    IDSorter info = iterator.next();
                    out.append(alphabet.lookupObject(info.getID()) + "\t" + formatter.format(info.getWeight()) + "\n");
                    word++;
                }
            }
            else {
                out.append (topic + "\t" + formatter.format(alpha[topic]) + "\t");

                while (iterator.hasNext() && word < numWords) {
                    IDSorter info = iterator.next();
                    out.append(alphabet.lookupObject(info.getID()) + " ");
                    word++;
                }
                out.append ("\n");
            }
        }

        return out.toString();
    }


    /** Get the smoothed distribution over topics for a training instance.
     */
    public double[] getTopicProbabilities(int instanceID) {
        LabelSequence topics = data.get(instanceID).topicSequence;
        return getTopicProbabilities(topics);
    }

    /** Get the smoothed distribution over topics for a topic sequence,
     * which may be from the training set or from a new instance with topics
     * assigned by an inferencer.
     */
    public double[] getTopicProbabilities(LabelSequence topics) {
        double[] topicDistribution = new double[numTopics];

        // Loop over the tokens in the document, counting the current topic
        //  assignments.
        for (int position = 0; position < topics.getLength(); position++) {
            topicDistribution[ topics.getIndexAtPosition(position) ]++;
        }

        // Add the smoothing parameters and normalize
        double sum = 0.0;
        for (int topic = 0; topic < numTopics; topic++) {
            topicDistribution[topic] += alpha[topic];
            sum += topicDistribution[topic];
        }

        // And normalize
        for (int topic = 0; topic < numTopics; topic++) {
            topicDistribution[topic] /= sum;
        }

        return topicDistribution;
    }


    public int[] getTopicWeightsForDocument (int docIndex) {
        int[] topicWeights = new int[numTopics];

        LabelSequence topicSequence = (LabelSequence)data.get(docIndex).topicSequence;
        int[] currentDocTopics = topicSequence.getFeatures();
        for (int currentDocTopic : currentDocTopics) {
            topicWeights[currentDocTopic]++;
        }

        return topicWeights;
    }

    public double[][] getSubCorpusTopicWords(boolean[] documentMask, boolean normalized, boolean smoothed) {
        double[][] result = new double[numTopics][numTypes];
        int[] subCorpusTokensPerTopic = new int[numTopics];

        for (int doc = 0; doc < data.size(); doc++) {
            if (documentMask[doc]) {
                int[] words = ((FeatureSequence) data.get(doc).instance.getData()).getFeatures();
                int[] topics = data.get(doc).topicSequence.getFeatures();
                for (int position = 0; position < topics.length; position++) {
                    result[ topics[position] ][ words[position] ]++;
                    subCorpusTokensPerTopic[ topics[position] ]++;
                }
            }
        }

        if (smoothed) {
            for (int topic = 0; topic < numTopics; topic++) {
                for (int type = 0; type < numTypes; type++) {
                    result[topic][type] += getBeta();
                }
            }
        }

        if (normalized) {
            double[] topicNormalizers = new double[numTopics];
            if (smoothed) {
                for (int topic = 0; topic < numTopics; topic++) {
                    topicNormalizers[topic] = 1.0 / (subCorpusTokensPerTopic[topic] + numTypes * getBeta());
                }
            }
            else {
                for (int topic = 0; topic < numTopics; topic++) {
                    topicNormalizers[topic] = 1.0 / subCorpusTokensPerTopic[topic];
                }
            }

            for (int topic = 0; topic < numTopics; topic++) {
                for (int type = 0; type < numTypes; type++) {
                    result[topic][type] *= topicNormalizers[topic];
                }
            }
        }

        return result;
    }

    public double[][] getTopicWords(boolean normalized, boolean smoothed) {
        double[][] result = new double[numTopics][numTypes];

        for (int type = 0; type < numTypes; type++) {
            int[] topicCounts = typeTopicCounts[type];

            int index = 0;
            while (index < topicCounts.length &&
                    topicCounts[index] > 0) {

                int topic = topicCounts[index] & topicMask;
                int count = topicCounts[index] >> topicBits;

                result[topic][type] += count;

                index++;
            }
        }

        if (smoothed) {
            for (int topic = 0; topic < numTopics; topic++) {
                for (int type = 0; type < numTypes; type++) {
                    result[topic][type] += getBeta();
                }
            }
        }

        if (normalized) {
            double[] topicNormalizers = new double[numTopics];
            if (smoothed) {
                for (int topic = 0; topic < numTopics; topic++) {
                    topicNormalizers[topic] = 1.0 / (tokensPerTopic[topic] + numTypes * getBeta());
                }
            }
            else {
                for (int topic = 0; topic < numTopics; topic++) {
                    topicNormalizers[topic] = 1.0 / tokensPerTopic[topic];
                }
            }

            for (int topic = 0; topic < numTopics; topic++) {
                for (int type = 0; type < numTypes; type++) {
                    result[topic][type] *= topicNormalizers[topic];
                }
            }
        }

        return result;
    }

    public double[][] getDocumentTopics(boolean normalized, boolean smoothed) {
        double[][] result = new double[data.size()][numTopics];

        for (int doc = 0; doc < data.size(); doc++) {
            int[] topics = data.get(doc).topicSequence.getFeatures();
            for (int position = 0; position < topics.length; position++) {
                result[doc][ topics[position] ]++;
            }

            if (smoothed) {
                for (int topic = 0; topic < numTopics; topic++) {
                    result[doc][topic] += alpha[topic];
                }
            }

            if (normalized) {
                double sum = 0.0;
                for (int topic = 0; topic < numTopics; topic++) {
                    sum += result[doc][topic];
                }
                double normalizer = 1.0 / sum;
                for (int topic = 0; topic < numTopics; topic++) {
                    result[doc][topic] *= normalizer;
                }
            }
        }

        return result;
    }

    public ArrayList<TreeSet<IDSorter>> getTopicDocuments(double smoothing) {
        ArrayList<TreeSet<IDSorter>> topicSortedDocuments = new ArrayList<TreeSet<IDSorter>>(numTopics);

        // Initialize the tree sets
        for (int topic = 0; topic < numTopics; topic++) {
            topicSortedDocuments.add(new TreeSet<IDSorter>());
        }

        int[] topicCounts = new int[numTopics];

        for (int doc = 0; doc < data.size(); doc++) {
            int[] topics = data.get(doc).topicSequence.getFeatures();
            for (int position = 0; position < topics.length; position++) {
                topicCounts[ topics[position] ]++;
            }

            for (int topic = 0; topic < numTopics; topic++) {
                topicSortedDocuments.get(topic).add(new IDSorter(doc, (topicCounts[topic] + smoothing) / (topics.length + numTopics * smoothing) ));
                topicCounts[topic] = 0;
            }
        }

        return topicSortedDocuments;
    }

    public List<TopicDocumentContainerSummary> getSummary () {
        ArrayList<TreeSet<IDSorter>> topicSortedDocuments = getTopicDocuments(10.0);

        List<TopicDocumentContainerSummary> report = new ArrayList<>();

        for (int topic = 0; topic < numTopics; topic++) {
            TreeSet<IDSorter> sortedDocuments = topicSortedDocuments.get(topic);

            TopicDocumentContainerSummary summary = new TopicDocumentContainerSummary(topic);

            int i = 0;
            for (IDSorter sorter: sortedDocuments) {

                int doc = sorter.getID();
                double proportion = sorter.getWeight();
                String name = (String) data.get(doc).instance.getName().toString();
                if (name == null) {
                    name = "no-name";
                }
                summary.add(name, proportion);
//				out.format("%d %d %s %f\n", topic, doc, name, proportion);

                i++;
            }

            report.add(summary);
        }

        return report;
    }

    public double modelLogLikelihood() {
        double logLikelihood = 0.0;
        int nonZeroTopics;

        // The likelihood of the model is a combination of a
        // Dirichlet-multinomial for the words in each topic
        // and a Dirichlet-multinomial for the topics in each
        // document.

        // The likelihood function of a dirichlet multinomial is
        //	 Gamma( sum_i alpha_i )	 prod_i Gamma( alpha_i + N_i )
        //	prod_i Gamma( alpha_i )	  Gamma( sum_i (alpha_i + N_i) )

        // So the log likelihood is
        //	logGamma ( sum_i alpha_i ) - logGamma ( sum_i (alpha_i + N_i) ) +
        //	 sum_i [ logGamma( alpha_i + N_i) - logGamma( alpha_i ) ]

        // Do the documents first

        int[] topicCounts = new int[numTopics];
        double[] topicLogGammas = new double[numTopics];
        int[] docTopics;

        for (int topic=0; topic < numTopics; topic++) {
            topicLogGammas[ topic ] = Dirichlet.logGammaStirling( alpha[topic] );
        }

        for (int doc=0; doc < data.size(); doc++) {
            LabelSequence topicSequence =	(LabelSequence) data.get(doc).topicSequence;

            docTopics = topicSequence.getFeatures();

            for (int token=0; token < docTopics.length; token++) {
                topicCounts[ docTopics[token] ]++;
            }

            for (int topic=0; topic < numTopics; topic++) {
                if (topicCounts[topic] > 0) {
                    logLikelihood += (Dirichlet.logGammaStirling(alpha[topic] + topicCounts[topic]) -
                            topicLogGammas[ topic ]);
                }
            }

            // subtract the (count + parameter) sum term
            logLikelihood -= Dirichlet.logGammaStirling(getAlphaSum() + docTopics.length);

            Arrays.fill(topicCounts, 0);
        }

        // add the parameter sum term
        logLikelihood += data.size() * Dirichlet.logGammaStirling(getAlphaSum());

        // And the topics

        // Count the number of type-topic pairs that are not just (logGamma(beta) - logGamma(beta))
        int nonZeroTypeTopics = 0;

        for (int type=0; type < numTypes; type++) {
            // reuse this array as a pointer

            topicCounts = typeTopicCounts[type];

            int index = 0;
            while (index < topicCounts.length &&
                    topicCounts[index] > 0) {
                int topic = topicCounts[index] & topicMask;
                int count = topicCounts[index] >> topicBits;

                nonZeroTypeTopics++;
                logLikelihood += Dirichlet.logGammaStirling(getBeta() + count);

                if (Double.isNaN(logLikelihood)) {
//					logger.warning("NaN in log likelihood calculation");
                    return 0;
                }
                else if (Double.isInfinite(logLikelihood)) {
//					logger.warning("infinite log likelihood");
                    return 0;
                }

                index++;
            }
        }

        for (int topic=0; topic < numTopics; topic++) {
            logLikelihood -=
                    Dirichlet.logGammaStirling( (getBeta() * numTypes) +
                            tokensPerTopic[ topic ] );

            if (Double.isNaN(logLikelihood)) {
//				logger.info("NaN after topic " + topic + " " + tokensPerTopic[ topic ]);
                return 0;
            }
            else if (Double.isInfinite(logLikelihood)) {
//				logger.info("Infinite value after topic " + topic + " " + tokensPerTopic[ topic ]);
                return 0;
            }

        }

        // logGamma(|V|*beta) for every topic
        logLikelihood +=
                Dirichlet.logGammaStirling(getBeta() * numTypes) * numTopics;

        // logGamma(beta) for all type/topic pairs with non-zero count
        logLikelihood -=
                Dirichlet.logGammaStirling(getBeta()) * nonZeroTypeTopics;

        if (Double.isNaN(logLikelihood)) {
//			logger.info("at the end");
        }
        else if (Double.isInfinite(logLikelihood)) {
//			logger.info("Infinite value beta " + beta + " * " + numTypes);
            return 0;
        }

        return logLikelihood;
    }


    /** Return a tool for estimating topic distributions for new documents */
    public TopicInferencer getInferencer() {
        return new TopicInferencer(typeTopicCounts, tokensPerTopic,
                data.get(0).instance.getDataAlphabet(),
                alpha, getBeta(), betaSum);
    }

    /** Return a tool for evaluating the marginal probability of new documents
     *   under this model */
    public MarginalProbEstimator getProbEstimator() {
        return new MarginalProbEstimator(numTopics, alpha, getAlphaSum(), getBeta(),
                typeTopicCounts, tokensPerTopic);
    }

    public double getAlphaSum() {
        return alphaSum;
    }

    public void setAlphaSum(double alphaSum) {
        this.alphaSum = alphaSum;
    }

    public double getBeta() {
        return beta;
    }

    @Override
    public int getDocLengthCounts() {
        return 0;
    }

    @Override
    public int getNumIterations() {
        return 0;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

}
