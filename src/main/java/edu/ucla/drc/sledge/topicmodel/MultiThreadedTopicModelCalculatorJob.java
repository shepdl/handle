package edu.ucla.drc.sledge.topicmodel;

import java.util.function.Consumer;

public class MultiThreadedTopicModelCalculatorJob implements TopicModelCalculatorJob {
    @Override
    public void start() {

    }

    @Override
    public void cancel() {

    }

    @Override
    public void setSetProgress(Consumer<Integer> onSetProgress) {

    }

    @Override
    public void setUpdateTopWords(Consumer<TopicModelResults> onUpdateTopWords) {

    }

    @Override
    public void setOnCompletion(Consumer<TopicModelResults> onCompletion) {

    }

	/*
	private int numThreads;
	private int randomSeed;
	private TopicModelResults model;
	private int showTopicsInterval;
	private int saveStateInterval;
	private int saveModelInterval;
	private boolean printLogLikelihood;
	private int saveSampleInterval;

	public MultiThreadedTopicModelCalculatorJob (int numThreads, TopicModelResults model) {
		this.numThreads = numThreads;
		this.model = model;
	}

	public MultiThreadedTopicModelCalculatorJob (int numThreads, int randomSeed, TopicModelResults model) {
		this.numThreads = numThreads;
		this.randomSeed = randomSeed;
		this.model = model;
	}

    // The number of times each type appears in the corpus
    @Override
    public void start() {

    }

    @Override
    public void cancel() {

    }

    private void estimate () {

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
				if (randomSeed == -1) {
					random = new Randoms();
				}
				else {
					random = new Randoms(randomSeed);
				}

				runnables[thread] = new WorkerRunnable(model.getNumTopics(),
													   model.getAlpha(), model.getAlphaSum(), model.getBeta(),
													   random, model.getData(),
													   runnableCounts, runnableTotals,
													   offset, docsPerThread);

				runnables[thread].initializeAlphaStatistics(model.getDocLengthCounts().length);

				offset += docsPerThread;

			}
		}
		else {

			// If there is only one thread, copy the typeTopicCounts
			//  arrays directly, rather than allocating new memory.

			Randoms random = null;
			if (randomSeed == -1) {
				random = new Randoms();
			}
			else {
				random = new Randoms(randomSeed);
			}

			runnables[0] = new WorkerRunnable(model.getDocLengthCounts(),
											  model.getAlpha(), model.getAlphaSum(), model.getBeta(),
											  random, model.getData(),
											  model.getTypeTopicCounts(), model.getTokensPerTopic(),
											  offset, docsPerThread);

			runnables[0].initializeAlphaStatistics(docLengthCounts.length);

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

			if (showTopicsInterval != 0 && iteration != 0 && iteration % showTopicsInterval == 0) {
//				logger.info("\n" + displayTopWords (wordsPerTopic, false));
			}

			if (saveStateInterval != 0 && iteration % saveStateInterval == 0) {
//				this.printState(new File(stateFilename + '.' + iteration));
			}

			if (saveModelInterval != 0 && iteration % saveModelInterval == 0) {
//				this.write(new File(modelFilename + '.' + iteration));
			}

			if (numThreads > 1) {

				// Submit runnables to thread pool

				for (int thread = 0; thread < numThreads; thread++) {
					if (iteration > burninPeriod && optimizeInterval != 0 &&
						iteration % saveSampleInterval == 0) {
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
				while (! finished) {

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
							}
							else if (targetCounts[index] != 0) {
								targetCounts[index] = 0;
							}
							else {
								break;
							}

							index++;
						}
						//System.arraycopy(typeTopicCounts[type], 0, counts, 0, counts.length);
					}
				}
			}
			else {
				if (iteration > burninPeriod && optimizeInterval != 0 &&
					iteration % saveSampleInterval == 0) {
					runnables[0].collectAlphaStatistics();
				}
				runnables[0].run();
			}

			long elapsedMillis = System.currentTimeMillis() - iterationStart;
			if (elapsedMillis < 1000) {
//				logger.fine(elapsedMillis + "ms ");
			}
			else {
//				logger.fine((elapsedMillis/1000) + "s ");
			}

			if (iteration > burninPeriod && optimizeInterval != 0 &&
				iteration % optimizeInterval == 0) {

				optimizeAlpha(runnables);
				optimizeBeta(runnables);

//				logger.fine("[O " + (System.currentTimeMillis() - iterationStart) + "] ");
			}

			if (iteration % 10 == 0) {
				if (printLogLikelihood) {
//					logger.info ("<" + iteration + "> LL/token: " + formatter.format(modelLogLikelihood() / totalTokens));
				}
				else {
//					logger.info ("<" + iteration + ">");
				}
			}
		}

		executor.shutdownNow();

		long seconds = Math.round((System.currentTimeMillis() - startTime)/1000.0);
		long minutes = seconds / 60;	seconds %= 60;
		long hours = minutes / 60;	minutes %= 60;
		long days = hours / 24;	hours %= 24;

		StringBuilder timeReport = new StringBuilder();
		timeReport.append("\nTotal time: ");
		if (days != 0) { timeReport.append(days); timeReport.append(" days "); }
		if (hours != 0) { timeReport.append(hours); timeReport.append(" hours "); }
		if (minutes != 0) { timeReport.append(minutes); timeReport.append(" minutes "); }
		timeReport.append(seconds); timeReport.append(" seconds");

//		logger.info(timeReport.toString());
    }

	public void optimizeAlpha(WorkerRunnable[] runnables) {

		// First clear the sufficient statistic histograms

		Arrays.fill(docLengthCounts, 0);
		for (int topic = 0; topic < topicDocCounts.length; topic++) {
			Arrays.fill(topicDocCounts[topic], 0);
		}

		for (int thread = 0; thread < numThreads; thread++) {
			int[] sourceLengthCounts = runnables[thread].getDocLengthCounts();
			int[][] sourceTopicCounts = runnables[thread].getTopicDocCounts();

			for (int count=0; count < sourceLengthCounts.length; count++) {
				if (sourceLengthCounts[count] > 0) {
					docLengthCounts[count] += sourceLengthCounts[count];
					sourceLengthCounts[count] = 0;
				}
			}

			for (int topic=0; topic < numTopics; topic++) {

				if (! usingSymmetricAlpha) {
					for (int count=0; count < sourceTopicCounts[topic].length; count++) {
						if (sourceTopicCounts[topic][count] > 0) {
							topicDocCounts[topic][count] += sourceTopicCounts[topic][count];
							sourceTopicCounts[topic][count] = 0;
						}
					}
				}
				else {
					// For the symmetric version, we only need one
					//  count array, which I'm putting in the same
					//  data structure, but for topic 0. All other
					//  topic histograms will be empty.
					// I'm duplicating this for loop, which
					//  isn't the best thing, but it means only checking
					//  whether we are symmetric or not numTopics times,
					//  instead of numTopics * longest document length.
					for (int count=0; count < sourceTopicCounts[topic].length; count++) {
						if (sourceTopicCounts[topic][count] > 0) {
							topicDocCounts[0][count] += sourceTopicCounts[topic][count];
							//			 ^ the only change
							sourceTopicCounts[topic][count] = 0;
						}
					}
				}
			}
		}

		if (usingSymmetricAlpha) {
			setAlphaSum(Dirichlet.learnSymmetricConcentration(topicDocCounts[0],
					docLengthCounts,
					model.getNumTopics(),
					model.getAlphaSum()));
			for (int topic = 0; topic < model.getNumTopics(); topic++) {
				model.getAlpha()[topic] = model.getAlphaSum() / model.getNumTopics();
			}
		}
		else {
			try {
				setAlphaSum(Dirichlet.learnParameters(model.getAlpha(), topicDocCounts, docLengthCounts, 1.001, 1.0, 1));
			} catch (RuntimeException e) {
				// Dirichlet optimization has become unstable. This is known to happen for very small corpora (~5 docs).
//				logger.warning("Dirichlet optimization has become unstable. Resetting to alpha_t = 1.0.");
				setAlphaSum(model.getNumTopics());
				for (int topic = 0; topic < model.getNumTopics(); topic++) {
					alpha[topic] = 1.0;
				}
			}
		}
	}


	public void optimizeBeta(WorkerRunnable[] runnables) {
		// The histogram starts at count 0, so if all of the
		//  tokens of the most frequent type were assigned to one topic,
		//  we would need to store a maxTypeCount + 1 count.
		int[] countHistogram = new int[maxTypeCount + 1];

		// Now count the number of type/topic pairs that have
		//  each number of tokens.

		int index;
		for (int type = 0; type < numTypes; type++) {
			int[] counts = typeTopicCounts[type];
			index = 0;
			while (index < counts.length &&
					counts[index] > 0) {
				int count = counts[index] >> topicBits;
				countHistogram[count]++;
				index++;
			}
		}

		// Figure out how large we need to make the "observation lengths"
		//  histogram.
		int maxTopicSize = 0;
		for (int topic = 0; topic < numTopics; topic++) {
			if (tokensPerTopic[topic] > maxTopicSize) {
				maxTopicSize = tokensPerTopic[topic];
			}
		}

		// Now allocate it and populate it.
		int[] topicSizeHistogram = new int[maxTopicSize + 1];
		for (int topic = 0; topic < numTopics; topic++) {
			topicSizeHistogram[ tokensPerTopic[topic] ]++;
		}

		betaSum = Dirichlet.learnSymmetricConcentration(countHistogram,
				topicSizeHistogram,
				model.getNumTopics(),
				betaSum);
		model.setBeta(betaSum / numTypes);


//		logger.info("[beta: " + formatter.format(beta) + "] ");
		// Now publish the new value
		for (int thread = 0; thread < numThreads; thread++) {
			runnables[thread].resetBeta(model.getBeta(), betaSum);
		}

	}
    private boolean running = false;

    private Consumer<Integer> setProgress;
    private Consumer<TopicModelResults> updateTopWords;
    private Consumer<TopicModelResults> onCompletion;

    @Override
    public void setSetProgress(Consumer<Integer> setProgress) {
        this.setProgress = setProgress;
    }

    @Override
    public void setUpdateTopWords(Consumer<TopicModelResults> updateTopWords) {
        this.updateTopWords = updateTopWords;
    }

    @Override
    public void setOnCompletion(Consumer<TopicModelResults> onCompletion) {
        this.onCompletion = onCompletion;
    }


	 */
}
