package eu.odalic.extrarelatable.experiments;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multiset;

/**
 * <p>
 * Allows to collect and compute detailed statistics for the experiments. It
 * supports averaging of the results across multiple iterations for more precise
 * results.
 * </p>
 * 
 * <p>
 * The implemented formulae for the measures are described in the accompanying
 * thesis.
 * </p>
 * 
 * @author Václav Brodec
 *
 */
public final class TestStatistics {

	private long seed;
	private int filesCount;
	private int testFilesCount;
	private int learningFilesCount;
	private int irregularHeaderFiles;
	private int fewRowsFiles;
	private int fewTypedRowsFiles;
	private int learntFiles;
	private int learningColumnsCount;
	private int learntContextColumnsCount;
	public int testingColumnsCount;
	public int testedContextColumnsCount;
	private int learntNumericColumns;
	private int attemptedLearntNumericColumns;
	private int attemptedTestedNumericColumns;
	private int tooSmallLearningNumericColumns;
	private int tooSmallTestingNumericColumns;
	private int testedFiles;
	private int annotatedNumericColumns;
	private int missingSolutions;
	private int matchingSolutions;
	private int instanceMatchingSolutions;
	private Map<Integer, Multiset<URI>> nonmatchingSolutions = new HashMap<>();
	private Map<Integer, Multiset<URI>> instanceNonmatchingSolutions = new HashMap<>();
	private int noPropertyLearningNumericColumns;
	private int noPropertyTestingNumericColums;
	private int inTestMissingColumns;
	private int inLearningMissingColumns;
	private Map<Integer, Set<URI>> uniqueProperties = new HashMap<>();
	private Map<Integer, Set<URI>> uniquePropertiesLearnt = new HashMap<>();
	private Map<Integer, Set<URI>> uniquePropertiesTested = new HashMap<>();
	private Map<Integer, Long> learningTime = new HashMap<>();
	private Map<Integer, Long> testingTime = new HashMap<>();
	private int repetitions;

	private Map<Integer, Set<URI>> presentClasses = new HashMap<>();
	private Map<Integer, Map<URI, Integer>> truePositives = new HashMap<>();
	private Map<Integer, Map<URI, Integer>> falsePositives = new HashMap<>();
	private Map<Integer, Map<URI, Integer>> falseNegatives = new HashMap<>();

	/**
	 * Statistics builder.
	 * 
	 * @author Václav Brodec
	 *
	 */
	public final static class Builder {

		private TestStatistics testStatistics = new TestStatistics();

		public TestStatistics build() {
			final TestStatistics built = testStatistics;
			this.testStatistics = new TestStatistics();

			return built;
		}

		/**
		 * @param seed
		 *            seed assigned to random generator
		 * @return the builder
		 */
		public Builder setSeed(long seed) {
			testStatistics.seed = seed;

			return this;
		}

		/**
		 * @param filesCount
		 *            contribution to all files
		 * @return the builder
		 */
		public Builder addFilesCount(int filesCount) {
			testStatistics.filesCount += filesCount;

			return this;
		}

		/**
		 * @param testFilesCount
		 *            contribution to files in the test set
		 * @return the builder
		 */
		public Builder addTestFilesCount(int testFilesCount) {
			testStatistics.testFilesCount += testFilesCount;

			return this;
		}

		/**
		 * @param learningFilesCount
		 *            contribution to files in the learning set
		 * @return the builder
		 */
		public Builder addLearningFilesCount(int learningFilesCount) {
			testStatistics.learningFilesCount += learningFilesCount;

			return this;
		}

		/**
		 * Takes notice of a file that was discarded for having irregular header.
		 * 
		 * @return the builder
		 */
		public Builder addIrregularHeaderFile() {
			testStatistics.irregularHeaderFiles++;

			return this;
		}

		/**
		 * Takes notice of a file that was discarded for having too few rows.
		 * 
		 * @return the builder
		 */
		public Builder addFewRowsFile() {
			testStatistics.fewRowsFiles++;

			return this;
		}

		/**
		 * Takes notice of a file that was discarded for having too few rows with
		 * recognizable type.
		 * 
		 * @return the builder
		 */
		public Builder addFewTypedRowsFile() {
			testStatistics.fewTypedRowsFiles++;

			return this;
		}

		/**
		 * Takes notice of learned file.
		 * 
		 * @return the builder
		 */
		public Builder addLearntFile() {
			testStatistics.learntFiles++;

			return this;
		}

		/**
		 * @param learningColumnsCount
		 *            contribution to learned columns
		 * @return the builder
		 */
		public Builder addLearningColumnsCount(int learningColumnsCount) {
			testStatistics.learningColumnsCount += learningColumnsCount;

			return this;
		}

		/**
		 * @param learntContextColumnsCount
		 *            contribution to all columns providing row context
		 * @return the builder
		 */
		public Builder addLearntContextColumnsCount(int learntContextColumnsCount) {
			testStatistics.learntContextColumnsCount += learntContextColumnsCount;

			return this;
		}

		/**
		 * @param testingColumnsCount
		 *            contribution to column in the test set
		 * @return the builder
		 */
		public Builder addTestingColumnsCount(int testingColumnsCount) {
			testStatistics.testingColumnsCount += testingColumnsCount;

			return this;
		}

		/**
		 * @param testingColumnsCount
		 *            contribution to columns providing row context in the test set
		 * @return the builder
		 */
		public Builder addTestedContextColumnsCount(int testedContextColumnsCount) {
			testStatistics.testedContextColumnsCount += testedContextColumnsCount;

			return this;
		}

		/**
		 * Takes notice of a learned numeric column.
		 * 
		 * @return the builder
		 */
		public Builder addLearntNumericColumn() {
			testStatistics.learntNumericColumns++;

			return this;
		}

		/**
		 * Takes notice of an attempt to learn numeric column.
		 * 
		 * @return the builder
		 */
		public Builder addAttemptedLearntNumericColumn() {
			testStatistics.attemptedLearntNumericColumns++;

			return this;
		}

		/**
		 * Takes notice of an attempt to annotate numeric column.
		 * 
		 * @return the builder
		 */
		public Builder addAttemptedTestedNumericColumn() {
			testStatistics.attemptedTestedNumericColumns++;

			return this;
		}

		/**
		 * Takes notice of an attempt to learn a numeric column that contains too few
		 * cells.
		 * 
		 * @return the builder
		 */
		public Builder addTooSmallLearningNumericColumn() {
			testStatistics.tooSmallLearningNumericColumns++;

			return this;
		}

		/**
		 * Takes notice of an attempt to annotate a numeric columns that contains too
		 * few cells.
		 * 
		 * @return the builder
		 */
		public Builder addTooSmallTestingNumericColumn() {
			testStatistics.tooSmallTestingNumericColumns++;

			return this;
		}

		/**
		 * Takes notice of a tested file.
		 * 
		 * @return the builder
		 */
		public Builder addTestedFile() {
			testStatistics.testedFiles++;

			return this;
		}

		/**
		 * Takes notice of annotated numeric column.
		 * 
		 * @return the builder
		 */
		public Builder addAnnotatedNumericColumn() {
			testStatistics.annotatedNumericColumns++;

			return this;
		}

		/**
		 * Takes notice of a numeric column which misses declared property.
		 * 
		 * @return the builder
		 */
		public Builder addMissingSolution() {
			testStatistics.missingSolutions++;

			return this;
		}

		/**
		 * Takes notice of a successful annotation (it mathced the declared property for
		 * the annotated column).
		 * 
		 * @return the builder
		 */
		public Builder addMatchingSolution() {
			testStatistics.matchingSolutions++;

			return this;
		}

		/**
		 * Takes notice of a successful annotation which succeeded without considering
		 * the done aggregation on property level.
		 * 
		 * @return the builder
		 */
		public Builder addInstanceMatchingSolution() {
			testStatistics.instanceMatchingSolutions++;

			return this;
		}

		/**
		 * Takes notice of incorrect annotation.
		 * 
		 * @param repetition
		 *            the index of the current iteration
		 * @param columnSolution
		 *            the correct annotation, which was not applied
		 * @return the builder
		 */
		public Builder addNonmatchingSolution(int repetition, URI columnSolution) {
			testStatistics.nonmatchingSolutions.get(repetition).add(columnSolution);

			return this;
		}

		/**
		 * Takes notice of incorrect annotation (when ignoring property level
		 * aggregations).
		 * 
		 * @param repetition
		 *            the index of the current iteration
		 * @param columnSolution
		 *            the correct annotation done without considering the aggregation on
		 *            property level, which was incorrectly not applied
		 * @return the builder
		 */
		public Builder addInstanceNonmatchingSolution(int repetition, URI columnSolution) {
			testStatistics.instanceNonmatchingSolutions.get(repetition).add(columnSolution);

			return this;
		}

		/**
		 * Takes notice of a numeric column in the learning set which lacks declared
		 * property.
		 * 
		 * @return the builder
		 */
		public Builder addNoPropertyLearningNumericColumn() {
			testStatistics.noPropertyLearningNumericColumns++;

			return this;
		}

		/**
		 * Takes notice of a learned column which property is entirely missing in the
		 * test set.
		 * 
		 * @return the builder
		 */
		public Builder addInTestMissingColumn() {
			testStatistics.inTestMissingColumns++;

			return this;
		}

		/**
		 * Takes notice of a learned column which property is entirely missing in the
		 * learning set.
		 * 
		 * @return the builder
		 */
		public Builder addInLearningMissingColumn() {
			testStatistics.inLearningMissingColumns++;

			return this;
		}

		/**
		 * Takes notice of a numeric column in the test set which lacks declared
		 * property.
		 * 
		 * @return the builder
		 */
		public Builder addNoPropertyTestingNumericColumn() {
			testStatistics.noPropertyTestingNumericColums++;

			return this;
		}

		/**
		 * Takes notice of an encountered, previously unseen property.
		 * 
		 * @param repetition
		 *            the index of the current iteration
		 * @param propertyUri
		 *            encountered property URI
		 * @return the builder
		 */
		public Builder addUniqueProperty(int repetition, @Nullable final URI propertyUri) {
			testStatistics.uniqueProperties.get(repetition).add(propertyUri);

			return this;
		}

		/**
		 * Takes notice of a learned property not met before.
		 * 
		 * @param repetition
		 *            the index of the current iteration
		 * @param propertyUri
		 *            encountered property URI
		 * @return the builder
		 */
		public Builder addUniquePropertyLearnt(int repetition, URI propertyUri) {
			testStatistics.uniquePropertiesLearnt.get(repetition).add(propertyUri);

			return this;
		}

		/**
		 * Provides a set of URIs belonging to learned properties so far.
		 * 
		 * @param repetition
		 *            the index of the current iteration
		 * @return the set of learned properties
		 */
		public Set<URI> getUniquePropertiesLearnt(final int repetition) {
			return testStatistics.uniquePropertiesLearnt.get(repetition);
		}

		/**
		 * Provides a set of URIs belonging to properties used in tested annotations so
		 * far.
		 * 
		 * @param repetition
		 *            the index of the current iteration
		 * @return the set of learned properties
		 */
		public Set<URI> getUniquePropertiesTested(final int repetition) {
			return testStatistics.uniquePropertiesTested.get(repetition);
		}

		/**
		 * Takes notice of a annotating property not used before.
		 * 
		 * @param repetition
		 *            the index of the current iteration
		 * @param propertyUri
		 *            encountered property URI
		 * @return the builder
		 */
		public Builder addUniquePropertyTested(int repetition, URI propertyUri) {
			testStatistics.uniquePropertiesTested.get(repetition).add(propertyUri);

			return this;
		}

		/**
		 * Adds to true positives for a property.
		 * 
		 * @param repetition
		 *            the index of the current iteration
		 * @param propertyUri
		 *            property URI
		 * @return the builder
		 */
		public Builder addTrue(int repetition, URI propertyUri) {
			final Map<URI, Integer> repetitionTruePositives = testStatistics.truePositives.get(repetition);

			repetitionTruePositives.compute(propertyUri, (oldKey, oldValue) -> (oldValue == null ? 1 : oldValue + 1));

			return this;
		}

		/**
		 * Adds to false positives for the assigned property and false negatives for the
		 * correct one.
		 * 
		 * @param repetition
		 *            the index of the current iteration
		 * @param assigned
		 *            property assigned by the algorithm
		 * @param correct
		 *            the correct property in that situation
		 * @return the builder
		 */
		public Builder addFalse(int repetition, URI assigned, URI correct) {
			if (assigned != null) {
				final Map<URI, Integer> repetitionFalsePositives = testStatistics.falsePositives.get(repetition);
				repetitionFalsePositives.compute(assigned, (oldKey, oldValue) -> (oldValue == null ? 1 : oldValue + 1));
			}

			final Map<URI, Integer> repetitionFalseNegatives = testStatistics.falseNegatives.get(repetition);
			repetitionFalseNegatives.compute(correct, (oldKey, oldValue) -> (oldValue == null ? 1 : oldValue + 1));

			return this;
		}

		/**
		 * Takes notice of property existence.
		 * 
		 * @param repetition
		 *            the index of the current iteration
		 * @param propertyUri
		 *            encountered property
		 * @return the builder
		 */
		public Builder addPropertyOccurence(int repetition, URI propertyUri) {
			if (propertyUri != null) {
				testStatistics.presentClasses.get(repetition).add(propertyUri);
			}

			return this;
		}

		/**
		 * Record the elapsed learning time.
		 * 
		 * @param repetition
		 *            the index of the current iteration
		 * @param time
		 *            taken time
		 * @return the builder
		 */
		public Builder addLearningTime(final int repetition, final long time) {
			testStatistics.learningTime.compute(repetition,
					(oldKey, oldValue) -> (oldValue == null ? time : oldValue + time));

			return this;
		}

		/**
		 * Record the elapsed test time.
		 * 
		 * @param repetition
		 *            the index of the current iteration
		 * @param time
		 *            taken time
		 * @return the builder
		 */
		public Builder addTestingTime(final int repetition, final long time) {
			testStatistics.testingTime.compute(repetition,
					(oldKey, oldValue) -> (oldValue == null ? time : oldValue + time));

			return this;
		}

		/**
		 * Sets the total number of iterations and initializes the recording structures
		 * for each of them. Must be done in advance before the experiment starts.
		 * 
		 * @param repetitions
		 *            the total number of iteration
		 * @return the builder
		 */
		public Builder setRepetitions(int repetitions) {
			testStatistics.repetitions = repetitions;

			for (int repetition = 0; repetition < repetitions; repetition++) {
				testStatistics.uniquePropertiesLearnt.put(repetition, new HashSet<>());
				testStatistics.uniquePropertiesTested.put(repetition, new HashSet<>());
				testStatistics.uniqueProperties.put(repetition, new HashSet<>());
				testStatistics.nonmatchingSolutions.put(repetition, HashMultiset.create());
				testStatistics.instanceNonmatchingSolutions.put(repetition, HashMultiset.create());
				testStatistics.presentClasses.put(repetition, new HashSet<>());
				testStatistics.truePositives.put(repetition, new HashMap<>());
				testStatistics.falsePositives.put(repetition, new HashMap<>());
				testStatistics.falseNegatives.put(repetition, new HashMap<>());
			}

			return this;
		}
	}

	/**
	 * @return the test statistic builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	private TestStatistics() {
	}

	/**
	 * @return the seed of the used random generator
	 */
	public long getSeed() {
		return seed;
	}

	/**
	 * @return the average number (over all iterations) of all files
	 */
	public double getFilesCount() {
		return filesCount / ((double) repetitions);
	}

	/**
	 * @return the average number (over all iterations) of all files in the test set
	 */
	public double getTestFilesCount() {
		return testFilesCount / ((double) repetitions);
	}

	/**
	 * @return the average number (over all iterations) of all files in the learning
	 *         set
	 */
	public double getLearningFilesCount() {
		return learningFilesCount / ((double) repetitions);
	}

	/**
	 * @return the average number (over all iterations) of all files discarded for
	 *         their irregular headers
	 */
	public double getIrregularHeaderFiles() {
		return irregularHeaderFiles / ((double) repetitions);
	}

	/**
	 * @return the average number (over all iterations) of all files discarded for
	 *         their small size
	 */
	public double getFewRowsFiles() {
		return fewRowsFiles / ((double) repetitions);
	}

	/**
	 * @return the average number (over all iterations) of all files discarded for
	 *         their small size after inferring the data types of cells
	 */
	public double getFewTypedRowsFiles() {
		return fewTypedRowsFiles / ((double) repetitions);
	}

	/**
	 * @return the average number (over all iterations) of all files in the learning
	 *         set
	 */
	public double getLearntFiles() {
		return learntFiles / ((double) repetitions);
	}

	/**
	 * @return the total number of columns in the learning set
	 */
	public double getLearningColumnsCount() {
		return learningColumnsCount / ((double) repetitions);
	}

	/**
	 * @return the number of columns from the learning set which provided row
	 *         context (averaged over all iterations)
	 */
	public double getLearntContextColumnsCount() {
		return learntContextColumnsCount / ((double) repetitions);
	}

	/**
	 * @return the number of columns in the test set (averaged over all iterations)
	 */
	public double getTestingColumnsCount() {
		return testingColumnsCount / ((double) repetitions);
	}

	/**
	 * @return the number of columns from the test set which provided row context
	 *         (averaged over all iterations)
	 */
	public double getTestedContextColumnsCount() {
		return testedContextColumnsCount / ((double) repetitions);
	}

	/**
	 * @return the number of processed numeric columns in the learning set (averaged
	 *         over all iterations)
	 */
	public double getLearntNumericColumns() {
		return learntNumericColumns / ((double) repetitions);
	}

	/**
	 * @return the number of numeric columns in the learning set where any effort
	 *         was spent to process them (averaged over all iterations)
	 */
	public double getAttemptedLearntNumericColumns() {
		return attemptedLearntNumericColumns / ((double) repetitions);
	}

	/**
	 * @return the number of numeric columns in the test set where any effort was
	 *         spent to process them (averaged over all iterations)
	 */
	public double getAttemptedTestedNumericColumns() {
		return attemptedTestedNumericColumns / ((double) repetitions);
	}

	/**
	 * @return the number of columns rejected from the learning set where the count
	 *         of of their cells was too low (averaged over all iterations)
	 */
	public double getTooSmallLearningNumericColumns() {
		return tooSmallLearningNumericColumns / ((double) repetitions);
	}

	/**
	 * @return the number of columns rejected from the test set where the count of
	 *         of their cells was too low (averaged over all iterations)
	 */
	public double getTooSmallTestingNumericColumns() {
		return tooSmallTestingNumericColumns / ((double) repetitions);
	}

	/**
	 * @return number of annotated files (averaged over all iterations)
	 */
	public double getTestedFiles() {
		return testedFiles / ((double) repetitions);
	}

	/**
	 * @return number of annotated numeric columns from the test set (averaged over
	 *         all iterations)
	 */
	public double getAnnotatedNumericColumns() {
		return annotatedNumericColumns / ((double) repetitions);
	}

	/**
	 * @return the number of missing declared properties in the test set (averaged
	 *         over all iterations)
	 */
	public double getMissingSolutions() {
		return missingSolutions / ((double) repetitions);
	}

	/**
	 * @return the number of correct annotations (averaged over all iterations)
	 */
	public double getMatchingSolutions() {
		return matchingSolutions / ((double) repetitions);
	}

	/**
	 * @return the number of correct annotations (when ignoring the property
	 *         aggregations; averaged over all iterations)
	 */
	public double getInstanceMatchingSolutions() {
		return instanceMatchingSolutions / ((double) repetitions);
	}

	/**
	 * @return the number of incorrect annotations (averaged over all iterations)
	 */
	public double getNonmatchingSolutions() {
		return nonmatchingSolutions.values().stream().mapToInt(e -> e.size()).sum() / ((double) repetitions);
	}

	/**
	 * @return the number of incorrect annotations (when ignoring the property
	 *         aggregations; averaged over all iterations)
	 */
	public double getInstanceNonmatchingSolutions() {
		return instanceNonmatchingSolutions.values().stream().mapToInt(e -> e.size()).sum() / ((double) repetitions);
	}

	/**
	 * @return number of columns in the learning set without declared property
	 *         (averaged over all iterations)
	 */
	public double getNoPropertyLearningNumericColumns() {
		return noPropertyLearningNumericColumns / ((double) repetitions);
	}

	/**
	 * @return number of columns in the test set without declared property (averaged
	 *         over all iterations)
	 */
	public double getNoPropertyTestingNumericColums() {
		return noPropertyTestingNumericColums / ((double) repetitions);
	}

	/**
	 * @return number of learned columns which declared property is missing in the
	 *         test set (averaged over all iterations)
	 */
	public double getInTestMissingColumns() {
		return inTestMissingColumns / ((double) repetitions);
	}

	/**
	 * @return number of annotated columns which declared property is missing in the
	 *         learning set (averaged over all iterations)
	 */
	public double getInLearningMissingColumns() {
		return inLearningMissingColumns / ((double) repetitions);
	}

	/**
	 * @return average number of unique properties encountered per iteration
	 */
	public double getUniqueProperties() {
		return uniqueProperties.values().stream().mapToInt(e -> e.size()).sum() / ((double) repetitions);
	}

	/**
	 * @return average number of unique learned properties per iteration
	 */
	public double getUniquePropertiesLearnt() {
		return uniquePropertiesLearnt.values().stream().mapToInt(e -> e.size()).sum() / ((double) repetitions);
	}

	/**
	 * @return average number of unique annotated properties per iteration
	 */
	public double getUniquePropertiesTested() {
		return uniquePropertiesTested.values().stream().mapToInt(e -> e.size()).sum() / ((double) repetitions);
	}

	private Map<Integer, Map<URI, Integer>> getTrueNegatives() {
		return this.presentClasses.entrySet().stream().collect(ImmutableMap.toImmutableMap(e -> e.getKey(), e -> {
			final int repetition = e.getKey();
			final Map<URI, Integer> truePositives = this.truePositives.get(repetition);

			final Map<URI, Integer> trueNegatives = new HashMap<>();
			final Set<URI> presentClasses = this.presentClasses.get(repetition);
			truePositives.entrySet().stream().forEach(tp -> {
				final URI truePositiveClass = tp.getKey();
				final int truePositivesCount = tp.getValue();

				for (final URI presentClass : presentClasses) {
					if (!presentClass.equals(truePositiveClass)) {
						trueNegatives.compute(presentClass,
								(oldKey, oldValue) -> (oldValue == null ? 1 : oldValue + truePositivesCount));
					}
				}
			});

			return ImmutableMap.copyOf(trueNegatives);
		}));
	}

	/**
	 * @return number of incorrect annotations from a stratified set
	 */
	public double getNonmatchingAvailableSolutions() {
		int nonmatchingAvailableSum = 0;

		for (int repetition = 0; repetition < repetitions; repetition++) {
			final Multiset<URI> nonmatchingAvailable = HashMultiset.create(nonmatchingSolutions.get(repetition));
			nonmatchingAvailable.retainAll(uniquePropertiesLearnt.get(repetition));

			nonmatchingAvailableSum += nonmatchingAvailable.size();
		}

		return nonmatchingAvailableSum / ((double) repetitions);
	}

	/**
	 * @return number of correctly matched solutions from a stratified set without
	 *         taking property aggregations into account
	 */
	public double getInstanceNonmatchingAvailableSolutions() {
		int nonmatchingAvailableSum = 0;

		for (int repetition = 0; repetition < repetitions; repetition++) {
			final Multiset<URI> nonmatchingAvailable = HashMultiset
					.create(instanceNonmatchingSolutions.get(repetition));
			nonmatchingAvailable.retainAll(uniquePropertiesLearnt.get(repetition));

			nonmatchingAvailableSum += nonmatchingAvailable.size();
		}

		return nonmatchingAvailableSum / ((double) repetitions);
	}

	private double getAverageWeightedMeasure(final Map<Integer, Map<URI, Double>> measures,
			final double zeroTotalInstancesCountValue) {
		double weightedMeasuresSum = 0;

		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			weightedMeasuresSum += getRepetitionWeightedMeasure(measures, zeroTotalInstancesCountValue, repetition);
		}

		return weightedMeasuresSum / ((double) repetitions);
	}

	private List<Double> getAllRepetitionsAverageWeightedMeasure(final Map<Integer, Map<URI, Double>> measures,
			final double zeroTotalInstancesCountValue) {
		final ImmutableList.Builder<Double> result = ImmutableList.builder();

		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			result.add(getRepetitionWeightedMeasure(measures, zeroTotalInstancesCountValue, repetition));
		}

		return result.build();
	}

	private double getRepetitionWeightedMeasure(final Map<Integer, Map<URI, Double>> measures,
			final double zeroTotalInstancesCountValue, int repetition) {
		final Map<URI, Double> repetitionMeasures = measures.get(repetition);

		double repetitionMeasuresSum = 0;
		int repetitionTotalInstancesCount = 0;

		for (final Map.Entry<URI, Double> entry : repetitionMeasures.entrySet()) {
			final Integer truePositives = this.truePositives.get(repetition).get(entry.getKey());
			final Integer falseNegatives = this.falseNegatives.get(repetition).get(entry.getKey());
			final int classInstancesCount = (truePositives == null ? 0 : truePositives)
					+ (falseNegatives == null ? 0 : falseNegatives);

			repetitionTotalInstancesCount += classInstancesCount;
			repetitionMeasuresSum += (entry.getValue() * classInstancesCount);
		}
		;

		if (repetitionTotalInstancesCount == 0) {
			return zeroTotalInstancesCountValue;
		} else {
			return repetitionMeasuresSum / ((double) repetitionTotalInstancesCount);
		}
	}

	private double getAverageMacroAveragedMeasure(final Map<Integer, Map<URI, Double>> measures,
			final double zeroClassesCountValue) {
		double macroAveragedMeasuresSum = 0;

		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			macroAveragedMeasuresSum += getRepetitionMacroAveragedMeasure(measures, repetition, zeroClassesCountValue);
			;
		}

		return macroAveragedMeasuresSum / ((double) repetitions);
	}

	private List<Double> getAllRepetitionsMacroAveragedMeasure(final Map<Integer, Map<URI, Double>> measures,
			final double zeroClassesCountValue) {
		final ImmutableList.Builder<Double> result = ImmutableList.builder();

		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			result.add(getRepetitionMacroAveragedMeasure(measures, repetition, zeroClassesCountValue));
		}

		return result.build();
	}

	private double getRepetitionMacroAveragedMeasure(final Map<Integer, Map<URI, Double>> measures,
			final int repetition, final double zeroClassesCountValue) {
		final Map<URI, Double> repetitionMeasures = measures.get(repetition);

		final Set<URI> repetitionClasses = measures.get(repetition).keySet();
		final int repetitionClassesCount = repetitionClasses.size();
		if (repetitionClassesCount == 0) {
			return zeroClassesCountValue;
		}

		double repetitionMeasuresSum = 0;

		for (final URI repetitionClass : repetitionClasses) {
			repetitionMeasuresSum += repetitionMeasures.get(repetitionClass);
		}

		return repetitionMeasuresSum / ((double) repetitionClassesCount);
	}

	private double getAverageMicroAveragedMeasure(
			final Map<? extends Integer, ? extends Map<? extends URI, ? extends Number>> numerator,
			final Map<? extends Integer, ? extends Map<? extends URI, ? extends Number>> denominator,
			final double zeroDenominatorValue) {
		double microAveragedMeasuresSum = 0;

		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			final double repetitionResult = getRepetitionMicroAveragedMeasure(numerator, denominator,
					zeroDenominatorValue, repetition);

			microAveragedMeasuresSum += repetitionResult;
		}

		return microAveragedMeasuresSum / ((double) repetitions);
	}

	private List<Double> getAllRepetitionsAverageMicroAveragedMeasure(
			final Map<? extends Integer, ? extends Map<? extends URI, ? extends Number>> numerator,
			final Map<? extends Integer, ? extends Map<? extends URI, ? extends Number>> denominator,
			final double zeroDenominatorValue) {
		final ImmutableList.Builder<Double> result = ImmutableList.builder();

		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			result.add(getRepetitionMicroAveragedMeasure(numerator, denominator, zeroDenominatorValue, repetition));
		}

		return result.build();
	}

	private double getRepetitionMicroAveragedMeasure(
			final Map<? extends Integer, ? extends Map<? extends URI, ? extends Number>> numerator,
			final Map<? extends Integer, ? extends Map<? extends URI, ? extends Number>> denominator,
			final double zeroDenominatorValue, int repetition) {
		final Map<? extends URI, ? extends Number> repetitionNumerator = numerator.get(repetition);
		final Map<? extends URI, ? extends Number> repetitionDenominator = denominator.get(repetition);

		final Set<? extends URI> repetitionNumeratorClasses = repetitionNumerator.keySet();

		double repetitionNumeratorSum = 0;
		for (final URI repetitionClass : repetitionNumeratorClasses) {
			repetitionNumeratorSum += repetitionNumerator.get(repetitionClass).doubleValue();
		}

		final Set<? extends URI> repetitionDenominatorClasses = repetitionDenominator.keySet();

		double repetitionDenominatorSum = 0;
		for (final URI repetitionClass : repetitionDenominatorClasses) {
			repetitionDenominatorSum += repetitionDenominator.get(repetitionClass).doubleValue();
		}

		if (repetitionDenominatorSum == 0) {
			return zeroDenominatorValue;
		} else {
			return repetitionNumeratorSum / repetitionDenominatorSum;
		}
	}

	private double getRepetitionMicroAveragedPrecision(int repetition) {
		return getRepetitionMicroAveragedMeasure(this.truePositives, sum(this.truePositives, this.falsePositives), 0,
				repetition);
	}

	private double getRepetitionMicroAveragedRecall(int repetition) {
		return getRepetitionMicroAveragedMeasure(this.truePositives, sum(this.truePositives, this.falseNegatives), 1,
				repetition);
	}

	private double getRepetitionMacroAveragedPrecision(int repetition) {
		return getRepetitionMacroAveragedMeasure(getPrecisions(), repetition, 0);
	}

	private double getRepetitionMacroAveragedRecall(int repetition) {
		return getRepetitionMacroAveragedMeasure(getRecalls(), repetition, 1);
	}

	private Map<URI, Double> getAveragedMeasures(
			final Map<? extends Integer, ? extends Map<? extends URI, ? extends Number>> measures) {
		final Map<URI, Double> measuresSums = new HashMap<>();

		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			final Map<? extends URI, ? extends Number> repetitionMeasures = measures.get(repetition);

			repetitionMeasures.forEach((key, value) -> {
				measuresSums.compute(key, (oldKey,
						oldValue) -> (oldValue == null ? value.doubleValue() : oldValue + value.doubleValue()));
			});
		}

		return measuresSums.entrySet().stream()
				.collect(ImmutableMap.toImmutableMap(e -> e.getKey(), e -> e.getValue() / ((double) repetitions)));
	}

	/**
	 * @return true positivies for each property averaged over all iterations
	 */
	public Map<URI, Double> getAverageTruePositives() {
		return getAveragedMeasures(this.truePositives);
	}

	/**
	 * @return false positives for each property averaged over all iterations
	 */
	public Map<URI, Double> getAverageFalsePositives() {
		return getAveragedMeasures(this.falsePositives);
	}

	/**
	 * @return false negatives for each property averaged over all iterations
	 */
	public Map<URI, Double> getAverageFalseNegatives() {
		return getAveragedMeasures(this.falseNegatives);
	}

	private Map<Integer, Map<URI, Double>> getRecalls() {
		return this.presentClasses.entrySet().stream().collect(ImmutableMap.toImmutableMap(e -> e.getKey(), e -> {
			final int iteration = e.getKey();

			final Set<URI> iterationAll = e.getValue();

			final Map<URI, Integer> iterationTruePositives = this.truePositives.get(iteration);
			final Map<URI, Integer> iterationFalseNegatives = this.falseNegatives.get(iteration);

			return iterationAll.stream().collect(ImmutableMap.toImmutableMap(Function.identity(), property -> {
				final int propertyTruePositives = iterationTruePositives.get(property) == null ? 0
						: iterationTruePositives.get(property);
				final int propertyFalseNegatives = iterationFalseNegatives.get(property) == null ? 0
						: iterationFalseNegatives.get(property);

				final int propertyAllSum = propertyTruePositives + propertyFalseNegatives;
				if (propertyAllSum == 0) {
					return 1d;
				}

				return ((double) propertyTruePositives) / ((double) propertyAllSum);
			}));
		}));
	}

	private Map<Integer, Map<URI, Double>> getPrecisions() {
		return this.presentClasses.entrySet().stream().collect(ImmutableMap.toImmutableMap(e -> e.getKey(), e -> {
			final int iteration = e.getKey();

			final Set<URI> iterationAll = e.getValue();

			final Map<URI, Integer> iterationTruePositives = this.truePositives.get(iteration);
			final Map<URI, Integer> iterationFalsePositives = this.falsePositives.get(iteration);

			return iterationAll.stream().collect(ImmutableMap.toImmutableMap(Function.identity(), property -> {
				final int propertyTruePositives = iterationTruePositives.get(property) == null ? 0
						: iterationTruePositives.get(property);
				final int propertyFalsePositives = iterationFalsePositives.get(property) == null ? 0
						: iterationFalsePositives.get(property);

				final int propertyAllSum = propertyTruePositives + propertyFalsePositives;
				if (propertyAllSum == 0) {
					return 0d;
				}

				return ((double) propertyTruePositives) / ((double) propertyAllSum);
			}));
		}));
	}

	private Map<Integer, Map<URI, Double>> getFMeasures() {
		final Map<Integer, Map<URI, Double>> precisions = getPrecisions();
		final Map<Integer, Map<URI, Double>> recalls = getRecalls();

		return this.presentClasses.entrySet().stream().collect(ImmutableMap.toImmutableMap(e -> e.getKey(), e -> {
			final int iteration = e.getKey();

			final Set<URI> iterationAll = e.getValue();

			final Map<URI, Double> iterationPrecisions = precisions.get(iteration);
			final Map<URI, Double> iterationRecalls = recalls.get(iteration);

			return iterationAll.stream().collect(ImmutableMap.toImmutableMap(Function.identity(), property -> {
				final double propertyPrecision = iterationPrecisions.get(property);
				final double propertyRecall = iterationRecalls.get(property);

				final double propertyAllSum = propertyPrecision + propertyRecall;
				if (propertyAllSum == 0) {
					return 0d;
				}

				final double fMeasure = 2 * propertyPrecision * propertyRecall / propertyAllSum;

				return fMeasure;
			}));
		}));
	}

	private Map<Integer, Map<URI, Double>> sum(
			final Map<? extends Integer, ? extends Map<? extends URI, ? extends Number>> first,
			final Map<? extends Integer, ? extends Map<? extends URI, ? extends Number>> second) {
		final Map<Integer, Map<URI, Double>> result = new HashMap<>();

		first.entrySet().stream().forEach(iterationEntry -> {
			final Integer iteration = iterationEntry.getKey();

			final Map<? extends URI, ? extends Number> iterationFirst = iterationEntry.getValue();
			final Map<? extends URI, ? extends Number> iterationSecond = second.get(iteration);

			final Map<URI, Double> iterationResult = new HashMap<>();

			iterationFirst.entrySet().stream().forEach(firstPropertyEntry -> {
				final URI property = firstPropertyEntry.getKey();
				final Number firstPropertyValue = firstPropertyEntry.getValue();

				final Number secondPropertyValue = iterationSecond.get(property);
				if (secondPropertyValue == null) {
					iterationResult.put(property, firstPropertyValue.doubleValue());
				} else {
					iterationResult.put(property, firstPropertyValue.doubleValue() + secondPropertyValue.doubleValue());
				}
			});
			iterationSecond.entrySet().stream().forEach(secondPropertyEntry -> {
				final URI property = secondPropertyEntry.getKey();
				final Number secondPropertyValue = secondPropertyEntry.getValue();

				if (!iterationFirst.containsKey(property)) {
					iterationResult.put(property, secondPropertyValue.doubleValue());
				}
			});

			result.put(iteration, ImmutableMap.copyOf(iterationResult));
		});

		return ImmutableMap.copyOf(result);
	}

	/**
	 * @return weighted precision averaged over all iterations
	 */
	public double getAverageWeightedPrecision() {
		return getAverageWeightedMeasure(getPrecisions(), 0);
	}

	/**
	 * @return weighted precision for each iteration
	 */
	public List<Double> getAllRepetitionsAverageWeightedPrecision() {
		return getAllRepetitionsAverageWeightedMeasure(getPrecisions(), 0);
	}

	/**
	 * @return macro-averaged precision averaged over all iterations
	 */
	public double getAverageMacroAveragedPrecision() {
		return getAverageMacroAveragedMeasure(getPrecisions(), 0);
	}

	/**
	 * @return macro-averaged precision for each iteration
	 */
	public List<Double> getAllRepetitionsAverageMacroAveragedPrecision() {
		return getAllRepetitionsMacroAveragedMeasure(getPrecisions(), 0);
	}

	/**
	 * @return micro-averaged precision averaged over all iterations
	 */
	public double getAverageMicroAveragedPrecision() {
		return getAverageMicroAveragedMeasure(this.truePositives, sum(this.truePositives, this.falsePositives), 1);
	}

	/**
	 * @return micro-averaged precision for each iteration
	 */
	public List<Double> getAllRepetitionsAverageMicroAveragedPrecision() {
		return getAllRepetitionsAverageMicroAveragedMeasure(this.truePositives,
				sum(this.truePositives, this.falsePositives), 1);
	}

	/**
	 * @return macro-averaged recall averaged over all iterations
	 */
	public double getAverageMacroAveragedRecall() {
		return getAverageMacroAveragedMeasure(getRecalls(), 1);
	}

	/**
	 * @return macro-averaged recall for each iteration
	 */
	public List<Double> getAllRepetitionsAverageMacroAveragedRecall() {
		return getAllRepetitionsMacroAveragedMeasure(getRecalls(), 1);
	}

	/**
	 * @return micro-averaged recall averaged over all iterations
	 */
	public double getAverageMicroAveragedRecall() {
		return getAverageMicroAveragedMeasure(this.truePositives, sum(this.truePositives, this.falseNegatives), 0);
	}

	/**
	 * @return micro-averaged recall for each iteration
	 */
	public List<Double> getAllRepetitionsAverageMicroAveragedRecall() {
		return getAllRepetitionsAverageMicroAveragedMeasure(this.truePositives,
				sum(this.truePositives, this.falseNegatives), 0);
	}

	/**
	 * @return precision for each property averaged over all iterations
	 */
	public Map<URI, Double> getAveragePrecisions() {
		return getAveragedMeasures(getPrecisions());
	}

	/**
	 * @return weighted recall averaged over all iterations
	 */
	public double getAverageWeightedRecall() {
		return getAverageWeightedMeasure(getRecalls(), 1);
	}

	/**
	 * @return weighted recall for each iteration
	 */
	public List<Double> getAllRepetitionsAverageWeightedRecall() {
		return getAllRepetitionsAverageWeightedMeasure(getRecalls(), 1);
	}

	/**
	 * @return recall of each property averaged over all iterations
	 */
	public Map<URI, Double> getAverageRecalls() {
		return getAveragedMeasures(getRecalls());
	}

	/**
	 * @return weighted f-measure averaged over all iterations
	 */
	public double getAverageWeightedFMeasure() {
		return getAverageWeightedMeasure(getFMeasures(), 0.5);
	}

	/**
	 * @return weighted f-measure for each iteration
	 */
	public List<Double> getAllRepetitionsAverageWeightedFMeasure() {
		return getAllRepetitionsAverageWeightedMeasure(getFMeasures(), 0.5);
	}

	/**
	 * @return f-measure for each property (averaged over all iterations)
	 */
	public Map<URI, Double> getAverageFMeasures() {
		return getAveragedMeasures(getFMeasures());
	}

	/**
	 * @return macro-averaged f-measure averaged over all iterations
	 */
	public double getAverageMacroAveragedFMeasure() {
		double microAveragedMeasuresSum = 0;

		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			microAveragedMeasuresSum += getIterationMacroAveragedFMeasure(repetition);
		}

		return microAveragedMeasuresSum / ((double) repetitions);
	}

	/**
	 * @return macro-averaged f-measure for each iteration
	 */
	public List<Double> getAllRepetitionsAverageMacroAveragedFMeasure() {
		final ImmutableList.Builder<Double> result = ImmutableList.builder();

		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			result.add(getIterationMacroAveragedFMeasure(repetition));
		}

		return result.build();
	}

	private double getIterationMacroAveragedFMeasure(int repetition) {
		final double precision = getRepetitionMacroAveragedPrecision(repetition);
		final double recall = getRepetitionMacroAveragedRecall(repetition);

		final double denominator = precision + recall;
		if (denominator == 0) {
			return 0;
		} else {
			return 2 * precision * recall / denominator;
		}
	}

	/**
	 * @return micro-averaged f-measure averaged over all iterations
	 */
	public double getAverageMicroAveragedFMeasure() {
		double microAveragedMeasuresSum = 0;

		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			microAveragedMeasuresSum += getIterationMicroAveragedFMeasure(repetition);
		}

		return microAveragedMeasuresSum / ((double) repetitions);
	}

	/**
	 * @return micro-averaged f-measure for each iteration
	 */
	public List<Double> getAllRepetitionsAverageMicroAveragedFMeasure() {
		final ImmutableList.Builder<Double> result = ImmutableList.builder();

		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			result.add(getIterationMicroAveragedFMeasure(repetition));
		}

		return result.build();
	}

	private double getIterationMicroAveragedFMeasure(int repetition) {
		final double precision = getRepetitionMicroAveragedPrecision(repetition);
		final double recall = getRepetitionMicroAveragedRecall(repetition);

		final double denominator = precision + recall;
		if (denominator == 0) {
			return 0;
		} else {
			return 2 * precision * recall / denominator;
		}
	}

	/**
	 * @return averaged average accuracy (over all iterations)
	 */
	public double getAverageAccuracy() {
		double averageAccuraciesSum = 0;

		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			final double repetitionAverageAccuracy = getRepetitionAverageAccuracy(repetition);

			averageAccuraciesSum += repetitionAverageAccuracy;
		}

		return averageAccuraciesSum / ((double) repetitions);
	}

	/**
	 * @return average accuracy for each iteration
	 */
	public List<Double> getAllRepetitionsAverageAccuracy() {
		final ImmutableList.Builder<Double> result = ImmutableList.builder();

		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			result.add(getRepetitionAverageAccuracy(repetition));
		}

		return result.build();
	}

	/**
	 * @return the average overall accuracy (over all iterations)
	 */
	public double getAverageOverallAccuracy() {
		double averageAccuraciesSum = 0;

		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			final double repetitionAverageAccuracy = getRepetitionOverallAccuracy(repetition);

			averageAccuraciesSum += repetitionAverageAccuracy;
		}

		return averageAccuraciesSum / ((double) repetitions);
	}

	/**
	 * @return overall accuracy for each iteration
	 */
	public List<Double> getAllRepetitionsAverageOverallAccuracy() {
		final ImmutableList.Builder<Double> result = ImmutableList.builder();

		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			result.add(getRepetitionOverallAccuracy(repetition));
		}

		return result.build();
	}

	/**
	 * @return the average overall error rate (over all iterations)
	 */
	public double getAverageOverallErrorRate() {
		double averageAccuraciesSum = 0;

		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			final double repetitionAverageAccuracy = getRepetitionOverallErrorRate(repetition);

			averageAccuraciesSum += repetitionAverageAccuracy;
		}

		return averageAccuraciesSum / ((double) repetitions);
	}

	/**
	 * @return overall error rate for each iteration
	 */
	public List<Double> getAllRepetitionsAverageOverallErrorRate() {
		final ImmutableList.Builder<Double> result = ImmutableList.builder();

		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			result.add(getRepetitionOverallErrorRate(repetition));
		}

		return result.build();
	}

	private double getRepetitionAverageAccuracy(int repetition) {
		final Set<URI> repetitionClasses = this.presentClasses.get(repetition);
		final int repetitionClassesCount = repetitionClasses.size();

		double repetitionAccuraciesSum = 0;

		final Map<Integer, Map<URI, Integer>> computedTrueNegatives = getTrueNegatives();

		for (final URI repetitionClass : repetitionClasses) {
			final Integer truePositives = this.truePositives.get(repetition).get(repetitionClass);
			final Integer trueNegatives = computedTrueNegatives.get(repetition).get(repetitionClass);
			final Integer falsePositives = this.falsePositives.get(repetition).get(repetitionClass);
			final Integer falseNegatives = this.falseNegatives.get(repetition).get(repetitionClass);
			final int classTrueCount = (truePositives == null ? 0 : truePositives)
					+ (trueNegatives == null ? 0 : trueNegatives);
			final int classFalseCount = (falsePositives == null ? 0 : falsePositives)
					+ (falseNegatives == null ? 0 : falseNegatives);
			final int classPredicitionsCount = classTrueCount + classFalseCount;

			if (classPredicitionsCount == 0) {
				repetitionAccuraciesSum += 1;
			} else {
				repetitionAccuraciesSum += (((double) classTrueCount) / ((double) classPredicitionsCount));
			}
		}

		if (repetitionClassesCount == 0) {
			return 1;
		}

		final double repetitionAverageAccuracy = repetitionAccuraciesSum / repetitionClassesCount;
		return repetitionAverageAccuracy;
	}

	private double getRepetitionOverallErrorRate(int repetition) {
		final Set<URI> repetitionClasses = this.presentClasses.get(repetition);

		int repetitionFailuresSum = 0;
		int repetitionPredicitionsCountSum = 0;

		final Map<Integer, Map<URI, Integer>> computedTrueNegatives = getTrueNegatives();

		for (final URI repetitionClass : repetitionClasses) {
			final Integer truePositives = this.truePositives.get(repetition).get(repetitionClass);
			final Integer trueNegatives = computedTrueNegatives.get(repetition).get(repetitionClass);
			final Integer falsePositives = this.falsePositives.get(repetition).get(repetitionClass);
			final Integer falseNegatives = this.falseNegatives.get(repetition).get(repetitionClass);
			final int classTrueCount = (truePositives == null ? 0 : truePositives)
					+ (trueNegatives == null ? 0 : trueNegatives);
			final int classFalseCount = (falsePositives == null ? 0 : falsePositives)
					+ (falseNegatives == null ? 0 : falseNegatives);
			final int classPredicitionsCount = classTrueCount + classFalseCount;

			repetitionFailuresSum += classFalseCount;
			repetitionPredicitionsCountSum += classPredicitionsCount;
		}

		if (repetitionPredicitionsCountSum == 0) {
			return 0;
		}

		final double repetitionOverallErrorRate = ((double) repetitionFailuresSum)
				/ ((double) repetitionPredicitionsCountSum);
		return repetitionOverallErrorRate;
	}

	private double getRepetitionOverallAccuracy(int repetition) {
		final Set<URI> repetitionClasses = this.presentClasses.get(repetition);

		int repetitionSuccessesSum = 0;
		int repetitionPredicitionsCountSum = 0;

		final Map<Integer, Map<URI, Integer>> computedTrueNegatives = getTrueNegatives();

		for (final URI repetitionClass : repetitionClasses) {
			final Integer truePositives = this.truePositives.get(repetition).get(repetitionClass);
			final Integer trueNegatives = computedTrueNegatives.get(repetition).get(repetitionClass);
			final Integer falsePositives = this.falsePositives.get(repetition).get(repetitionClass);
			final Integer falseNegatives = this.falseNegatives.get(repetition).get(repetitionClass);
			final int classTrueCount = (truePositives == null ? 0 : truePositives)
					+ (trueNegatives == null ? 0 : trueNegatives);
			final int classFalseCount = (falsePositives == null ? 0 : falsePositives)
					+ (falseNegatives == null ? 0 : falseNegatives);
			final int classPredicitionsCount = classTrueCount + classFalseCount;

			repetitionSuccessesSum += classTrueCount;
			repetitionPredicitionsCountSum += classPredicitionsCount;
		}

		if (repetitionPredicitionsCountSum == 0) {
			return 1;
		}

		final double repetitionOverallAccuracy = ((double) repetitionSuccessesSum)
				/ ((double) repetitionPredicitionsCountSum);
		return repetitionOverallAccuracy;
	}

	/**
	 * @return the average error rate (over all iterations)
	 */
	public double getAverageErrorRate() {
		double averageErrorRatesSum = 0;

		final Map<Integer, Map<URI, Integer>> computedTrueNegatives = getTrueNegatives();

		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			averageErrorRatesSum += getIterationAverageErrorRate(computedTrueNegatives, repetition);
		}

		return averageErrorRatesSum / ((double) repetitions);
	}

	/**
	 * @return error rate for each iteration
	 */
	public List<Double> getAllRepetitionsAverageErrorRate() {
		ImmutableList.Builder<Double> result = ImmutableList.builder();

		final Map<Integer, Map<URI, Integer>> computedTrueNegatives = getTrueNegatives();

		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			result.add(getIterationAverageErrorRate(computedTrueNegatives, repetition));
		}

		return result.build();
	}

	private double getIterationAverageErrorRate(final Map<Integer, Map<URI, Integer>> computedTrueNegatives,
			int repetition) {
		final Set<URI> repetitionClasses = this.presentClasses.get(repetition);
		final int repetitionClassesCount = repetitionClasses.size();

		double repetitionAccuraciesSum = 0;

		for (final URI repetitionClass : repetitionClasses) {
			final Integer truePositives = this.truePositives.get(repetition).get(repetitionClass);
			final Integer trueNegatives = computedTrueNegatives.get(repetition).get(repetitionClass);
			final Integer falsePositives = this.falsePositives.get(repetition).get(repetitionClass);
			final Integer falseNegatives = this.falseNegatives.get(repetition).get(repetitionClass);
			final int classTrueCount = (truePositives == null ? 0 : truePositives)
					+ (trueNegatives == null ? 0 : trueNegatives);
			final int classFalseCount = (falsePositives == null ? 0 : falsePositives)
					+ (falseNegatives == null ? 0 : falseNegatives);
			final int classPredicitionsCount = classTrueCount + classFalseCount;

			if (classPredicitionsCount == 0) {
				repetitionAccuraciesSum += 0;
			} else {
				repetitionAccuraciesSum += (((double) classFalseCount) / ((double) classPredicitionsCount));
			}
		}

		if (repetitionClassesCount == 0) {
			return 0;
		} else {
			return repetitionAccuraciesSum / repetitionClassesCount;
		}
	}

	/**
	 * @return the average Cohen's kappa (over all iterations)
	 */
	public double getAverageKappa() {
		double kappasSum = 0;

		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			final double repetitionKappa = getRepetitionKappa(repetition);

			kappasSum += repetitionKappa;
		}

		return kappasSum / ((double) repetitions);
	}

	/**
	 * @return Cohen's kappa for all iterations
	 */
	public List<Double> getAllRepetitionsKappa() {
		final ImmutableList.Builder<Double> result = ImmutableList.builder();

		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			result.add(getRepetitionKappa(repetition));
		}

		return result.build();
	}

	/**
	 * @param repetition
	 *            the index of the requested iteration
	 * @return the value of Cohen's kappa for that iteration
	 */
	private double getRepetitionKappa(final int repetition) {
		final double pObserved = getPObserved(repetition);
		final double pExpected = getPExpected(repetition);

		final double pDifference = pObserved - pExpected;

		if (pExpected == 1) {
			if (pDifference == 0) {
				return 0;
			} else if (pDifference > 0) {
				return 1;
			} else {
				return Double.NEGATIVE_INFINITY;
			}
		}

		return pDifference / (1 - pExpected);
	}

	private double getPObserved(final int repetition) {
		return getRepetitionOverallAccuracy(repetition);
	}

	private double getPExpected(final int repetition) {
		final Set<URI> repetitionClasses = this.presentClasses.get(repetition);

		int cumulativeProduct = 0;
		int N = 0;
		for (final URI repetitionClass : repetitionClasses) {
			final Integer truePositives = this.truePositives.get(repetition).get(repetitionClass);
			final Integer falsePositives = this.falsePositives.get(repetition).get(repetitionClass);
			final Integer falseNegatives = this.falseNegatives.get(repetition).get(repetitionClass);

			final int nClassAlgo = (truePositives == null ? 0 : truePositives)
					+ (falsePositives == null ? 0 : falsePositives);
			final int nClassManual = (truePositives == null ? 0 : truePositives)
					+ (falseNegatives == null ? 0 : falseNegatives);

			cumulativeProduct += nClassAlgo * nClassManual;

			final int classInstancesCount = nClassManual;
			N += classInstancesCount;
		}

		if (N == 0) {
			return 1;
		}

		return ((double) (cumulativeProduct)) / ((double) (N * N));
	}

	/**
	 * @return the average learning time (over all iterations)
	 */
	public double getLearningTime() {
		long learningTimeSum = 0;

		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			learningTimeSum += this.learningTime.get(repetition);
		}

		return learningTimeSum / ((double) repetitions);
	}

	/**
	 * @return the learning time for each of the iterations
	 */
	public List<Long> getAllRepetitionsLearningTime() {
		final ImmutableList.Builder<Long> result = ImmutableList.builder();

		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			result.add(this.learningTime.get(repetition));
		}

		return result.build();
	}

	/**
	 * @return the average testing time (over all iterations)
	 */
	public double getTestingTime() {
		double testingTimeSum = 0;

		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			testingTimeSum += this.testingTime.get(repetition);
		}

		return testingTimeSum / ((double) repetitions);
	}

	/**
	 * @return the testing time for each of the iterations
	 */
	public List<Long> getAllRepetitionsTestingTime() {
		final ImmutableList.Builder<Long> result = ImmutableList.builder();

		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			result.add(this.testingTime.get(repetition));
		}

		return result.build();
	}
}