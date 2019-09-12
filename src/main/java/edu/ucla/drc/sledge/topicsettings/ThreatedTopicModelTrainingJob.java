package edu.ucla.drc.sledge.topicsettings;

public class ThreatedTopicModelTrainingJob {

    /*
    private final TopicModelResults model;
    private int numThreads;

    private boolean running = false;
    private boolean complete = false;

    private Consumer<Integer> setProgress;

    private int topicMask;
    private int topicBits;

    public ThreatedTopicModelTrainingJob (TopicModelResults model) {
        this.model = model;
        numThreads = 8; // TODO: Get from runtime
    }

    public void run () {
        this.running = true;
        topicMask = model.getNumTopics() - 1;
        topicBits = Integer.bitCount(topicMask);
        this.estimate();
        this.running = false;
        this.complete = true;
    }

    public void estimate () {

        long startTime = System.currentTimeMillis();

        WorkerRunnable[] runnables = new WorkerRunnable[numThreads];

        int docsPerThread = model.getData().size() / numThreads;
        int offset = 0;

        if (numThreads > 1) {

            for (int thread = 0; thread < numThreads; thread++) {
                int[] runnableTotals = new int[model.getNumTopics()];
                System.arraycopy(model.getTokensPerTopic(), 0, runnableTotals, 0, model.getNumTopics());

                int[][] runnableCounts = new int[model.getNumTypes()][];
                for (int type = 0; type < model.getNumTypes(); type++) {
                    int[] counts = new int[model.getTypeTopicCounts()[type].length];
                    System.arraycopy(model.getTypeTopicCounts()[type], 0, counts, 0, counts.length);
                    runnableCounts[type] = counts;
                }

                // some docs may be missing at the end due to integer division
                if (thread == numThreads - 1) {
                    docsPerThread = model.getData().size() - offset;
                }

                Randoms random = null;
                if (model.getRandomSeed() == -1) {
                    random = new Randoms();
                } else {
                    random = new Randoms(model.getRandomSeed());
                }

                runnables[thread] = new WorkerRunnable(model.getNumTopics(),
                        model.getAlpha(), model.getAlphaSum(), model.getBeta(),
                        random, model.getData(),
                        runnableCounts, runnableTotals,
                        offset, docsPerThread);

                runnables[thread].initializeAlphaStatistics(model.getDocLengthCounts().length);

                offset += docsPerThread;

            }
        } else {

            // If there is only one thread, copy the typeTopicCounts
            //  arrays directly, rather than allocating new memory.

            Randoms random = null;
            if (model.getRandomSeed() == -1) {
                random = new Randoms();
            } else {
                random = new Randoms(model.getRandomSeed());
            }

            runnables[0] = new WorkerRunnable(model.getNumTopics(),
                    model.getAlpha(), model.getAlphaSum(), model.getBeta(),
                    random, model.getData(),
                    model.getTypeTopicCounts(), model.getTokensPerTopic(),
                    offset, docsPerThread);

            runnables[0].initializeAlphaStatistics(model.getDocLengthCounts().length);

            // If there is only one thread, we
            //  can avoid communications overhead.
            // This switch informs the thread not to
            //  gather statistics for its portion of the data.
            runnables[0].makeOnlyThread();
        }

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (int iteration = 1; running && iteration <= model.getNumIterations(); iteration++) {

            long iterationStart = System.currentTimeMillis();

            if (iteration % 10 == 0) {
                setProgress.accept(iteration);
            }
            if (iteration % 50 == 0) {
                updateTopWords.accept(this);
            }

//            if (showTopicsInterval != 0 && iteration != 0 && iteration % showTopicsInterval == 0) {
//				logger.info("\n" + displayTopWords (wordsPerTopic, false));
//            }

//            if (saveStateInterval != 0 && iteration % saveStateInterval == 0) {
//				this.printState(new File(stateFilename + '.' + iteration));
//            }

//            if (saveModelInterval != 0 && iteration % saveModelInterval == 0) {
//				this.write(new File(modelFilename + '.' + iteration));
//            }

            if (numThreads > 1) {

                // Submit runnables to thread pool

                for (int thread = 0; thread < numThreads; thread++) {
                    if (iteration > burninPeriod && optimizeInterval != 0 &&
//                            iteration % saveSampleInterval == 0
                    ) {
                        runnables[thread].collectAlphaStatistics();
                    }

//					logger.fine("submitting thread " + thread);
                    executor.submit(runnables[thread]);
                    //runnables[thread].run();
                }

                // I'm getting some problems that look like
                //  a thread hasn't started yet when it is first
                //  polled, so it appears to be finished.
                // This only occurs in very short corpora.
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {

                }

                boolean finished = false;
                while (!finished) {

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {

                    }

                    finished = true;

                    // Are all the threads done?
                    for (int thread = 0; thread < numThreads; thread++) {
//						logger.info("thread " + thread + " done? " + runnables[thread].isFinished);
                        finished = finished && runnables[thread].isFinished;
                    }

                }

                //System.out.print("[" + (System.currentTimeMillis() - iterationStart) + "] ");

                sumTypeTopicCounts(runnables);

                //System.out.print("[" + (System.currentTimeMillis() - iterationStart) + "] ");

                for (int thread = 0; thread < numThreads; thread++) {
                    int[] runnableTotals = runnables[thread].getTokensPerTopic();
                    System.arraycopy(tokensPerTopic, 0, runnableTotals, 0, numTopics);

                    int[][] runnableCounts = runnables[thread].getTypeTopicCounts();
                    for (int type = 0; type < numTypes; type++) {
                        int[] targetCounts = runnableCounts[type];
                        int[] sourceCounts = typeTopicCounts[type];

                        int index = 0;
                        while (index < sourceCounts.length) {

                            if (sourceCounts[index] != 0) {
                                targetCounts[index] = sourceCounts[index];
                            } else if (targetCounts[index] != 0) {
                                targetCounts[index] = 0;
                            } else {
                                break;
                            }

                            index++;
                        }
                        //System.arraycopy(typeTopicCounts[type], 0, counts, 0, counts.length);
                    }
                }
            } else {
                if (iteration > burninPeriod && optimizeInterval != 0 &&
                        iteration % saveSampleInterval == 0) {
                    runnables[0].collectAlphaStatistics();
                }
                runnables[0].run();
            }

            long elapsedMillis = System.currentTimeMillis() - iterationStart;
            if (elapsedMillis < 1000) {
//				logger.fine(elapsedMillis + "ms ");
            } else {
//				logger.fine((elapsedMillis/1000) + "s ");
            }

            if (iteration > burninPeriod && optimizeInterval != 0 &&
                    iteration % optimizeInterval == 0) {

//                int[][] sourceLengthCounts = new int[numThreads][runnables[0].getDocLengthCounts().length];
                List<int[]> sourceLengthCounts = new ArrayList<>();
//                int[][][] sourceTopicCounts = new int[numThreads][runnables[0]]
                List<int[][]> sourceTopicCounts = new ArrayList<>();
                for (int i = 0; i < numThreads; i++) {
                    sourceLengthCounts.add(runnables[i].getDocLengthCounts());
                    sourceTopicCounts.add(runnables[i].getTopicDocCounts());
                }

                optimizeAlpha(runnables);
                optimizeBeta(runnables);

//				logger.fine("[O " + (System.currentTimeMillis() - iterationStart) + "] ");
            }

//            if (iteration % 10 == 0) {
//                if (printLogLikelihood) {
//					logger.info ("<" + iteration + "> LL/token: " + formatter.format(modelLogLikelihood() / totalTokens));
//                } else {
//					logger.info ("<" + iteration + ">");
//                }
//            }
        }

        executor.shutdownNow();

        long seconds = Math.round((System.currentTimeMillis() - startTime) / 1000.0);
        long minutes = seconds / 60;
        seconds %= 60;
        long hours = minutes / 60;
        minutes %= 60;
        long days = hours / 24;
        hours %= 24;

        StringBuilder timeReport = new StringBuilder();
        timeReport.append("\nTotal time: ");
        if (days != 0) {
            timeReport.append(days);
            timeReport.append(" days ");
        }
        if (hours != 0) {
            timeReport.append(hours);
            timeReport.append(" hours ");
        }
        if (minutes != 0) {
            timeReport.append(minutes);
            timeReport.append(" minutes ");
        }
        timeReport.append(seconds);
        timeReport.append(" seconds");

//		logger.info(timeReport.toString());
    }

    public void sumTypeTopicCounts(WorkerRunnable[] runnables) {

        // Clear the topic totals
        Arrays.fill(model.getTokensPerTopic(), 0);

        // Clear the type/topic counts, only
        //  looking at the entries before the first 0 entry.

        for (int type = 0; type < model.getNumTypes(); type++) {

            int[] targetCounts = model.getTypeTopicCounts()[type];

            int position = 0;
            while (position < targetCounts.length &&
                    targetCounts[position] > 0) {
                targetCounts[position] = 0;
                position++;
            }

        }

        for (int thread = 0; thread < numThreads; thread++) {

            // Handle the total-tokens-per-topic array

            int[] sourceTotals = runnables[thread].getTokensPerTopic();
            for (int topic = 0; topic < model.getNumTypes(); topic++) {
                model.getTokensPerTopic()[topic] += sourceTotals[topic];
            }

            // Now handle the individual type topic counts

            int[][] sourceTypeTopicCounts =
                    runnables[thread].getTypeTopicCounts();

            for (int type = 0; type < model.getNumTypes(); type++) {

                // Here the source is the individual thread counts,
                //  and the target is the global counts.

                int[] sourceCounts = sourceTypeTopicCounts[type];
                int[] targetCounts = model.getTypeTopicCounts()[type];

                int sourceIndex = 0;
                while (sourceIndex < sourceCounts.length &&
                        sourceCounts[sourceIndex] > 0) {

                    int topic = sourceCounts[sourceIndex] & topicMask;
                    int count = sourceCounts[sourceIndex] >> topicBits;

                    int targetIndex = 0;
                    int currentTopic = targetCounts[targetIndex] & topicMask;
                    int currentCount;

                    while (targetCounts[targetIndex] > 0 && currentTopic != topic) {
                        targetIndex++;
                        if (targetIndex == targetCounts.length) {
//							logger.info("overflow in merging on type " + type);
                        }
                        currentTopic = targetCounts[targetIndex] & topicMask;
                    }
                    currentCount = targetCounts[targetIndex] >> topicBits;

                    targetCounts[targetIndex] =
                            ((currentCount + count) << topicBits) + topic;

                    // Now ensure that the array is still sorted by
                    //  bubbling this value up.
                    while (targetIndex > 0 &&
                            targetCounts[targetIndex] > targetCounts[targetIndex - 1]) {
                        int temp = targetCounts[targetIndex];
                        targetCounts[targetIndex] = targetCounts[targetIndex - 1];
                        targetCounts[targetIndex - 1] = temp;

                        targetIndex--;
                    }

                    sourceIndex++;
                }

            }
        }

		/* // Debuggging code to ensure counts are being
		   // reconstructed correctly.

		for (int type = 0; type < numTypes; type++) {

			int[] targetCounts = typeTopicCounts[type];

			int index = 0;
			int count = 0;
			while (index < targetCounts.length &&
				   targetCounts[index] > 0) {
				count += targetCounts[index] >> topicBits;
				index++;
			}

			if (count != typeTotals[type]) {
				System.err.println("Expected " + typeTotals[type] + ", found " + count);
			}

		}
		*/
//    }

}
