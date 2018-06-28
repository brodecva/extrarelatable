package eu.odalic.extrarelatable.experiments;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multiset;

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
	private Map<Integer, Multiset<URI>> nonmatchingSolutions = new HashMap<>();// HashMultiset.create();
	private Map<Integer, Multiset<URI>> instanceNonmatchingSolutions = new HashMap<>();// HashMultiset.create();
	private int noPropertyLearningNumericColumns;
	private int noPropertyTestingNumericColums;
	private int inTestMissingColumns;
	private int inLearningMissingColumns;
	private Map<Integer, Set<URI>> uniqueProperties = new HashMap<>();
	private Map<Integer, Set<URI>> uniquePropertiesLearnt = new HashMap<>();
	private Map<Integer, Set<URI>> uniquePropertiesTested = new HashMap<>();// = new HashSet<>();
	private long learningTime;
	private long testingTime;
	private int repetitions;
	
	private Map<Integer, Map<URI, Integer>> truePositives = new HashMap<>();
	private Map<Integer, Map<URI, Integer>> falsePositives = new HashMap<>();
	private Map<Integer, Map<URI, Integer>> falseNegatives = new HashMap<>();
	private Map<Integer, Map<URI, Integer>> occurencesCount = new HashMap<>();
	private Map<Integer, Integer> totalOccurencesCount = new HashMap<>();
	private Map<Integer, Integer> errors = new HashMap<>();
	
	public final static class Builder {

		private TestStatistics testStatistics = new TestStatistics();
		
		public TestStatistics build() {
			final TestStatistics built = testStatistics;
			this.testStatistics = new TestStatistics();
			
			return built;
		}
		
		public Builder setSeed(long seed) {
			testStatistics.seed = seed;
			
			return this;
		}

		public Builder addFilesCount(int filesCount) {
			testStatistics.filesCount += filesCount;
			
			return this;
		}

		public Builder addTestFilesCount(int testFilesCount) {
			testStatistics.testFilesCount += testFilesCount;
			
			return this;
		}

		public Builder addLearningFilesCount(int learningFilesCount) {
			testStatistics.learningFilesCount += learningFilesCount;
			
			return this;
		}

		public Builder addIrregularHeaderFile() {
			testStatistics.irregularHeaderFiles++;
			
			return this;
		}

		public Builder addFewRowsFile() {
			testStatistics.fewRowsFiles++;
			
			return this;				
		}

		public Builder addFewTypedRowsFile() {
			testStatistics.fewTypedRowsFiles++;
			
			return this;				
		}

		public Builder addLearntFile() {
			testStatistics.learntFiles++;
			
			return this;				
		}

		public Builder addLearningColumnsCount(int learningColumnsCount) {
			testStatistics.learningColumnsCount += learningColumnsCount;
			
			return this;
		}

		public Builder addLearntContextColumnsCount(int learntContextColumnsCount) {
			testStatistics.learntContextColumnsCount += learntContextColumnsCount;
			
			return this;				
		}
		
		public Builder addTestingColumnsCount(int testingColumnsCount) {
			testStatistics.testingColumnsCount += testingColumnsCount;
			
			return this;
		}

		public Builder addTestedContextColumnsCount(int testedContextColumnsCount) {
			testStatistics.testedContextColumnsCount += testedContextColumnsCount;
			
			return this;
		}

		public Builder addLearntNumericColumn() {
			testStatistics.learntNumericColumns++;
			
			return this;				
		}

		public Builder addAttemptedLearntNumericColumn() {
			testStatistics.attemptedLearntNumericColumns++;
			
			return this;				
		}
		
		public Builder addAttemptedTestedNumericColumn() {
			testStatistics.attemptedTestedNumericColumns++;
			
			return this;				
		}

		public Builder addTooSmallLearningNumericColumn() {
			testStatistics.tooSmallLearningNumericColumns++;
			
			return this;				
		}
		
		public Builder addTooSmallTestingNumericColumn() {
			testStatistics.tooSmallTestingNumericColumns++;
			
			return this;				
		}

		public Builder addTestedFile() {
			testStatistics.testedFiles++;
			
			return this;				
		}

		public Builder addAnnotatedNumericColumn() {
			testStatistics.annotatedNumericColumns++;
			
			return this;				
		}

		public Builder addMissingSolution() {
			testStatistics.missingSolutions++;
			
			return this;				
		}

		public Builder addMatchingSolution() {
			testStatistics.matchingSolutions++;
			
			return this;				
		}
		
		public Builder addInstanceMatchingSolution() {
			testStatistics.instanceMatchingSolutions++;
			
			return this;				
		}

		public Builder addNonmatchingSolution(int repetition, URI columnSolution) {
			testStatistics.nonmatchingSolutions.get(repetition).add(columnSolution);
			
			return this;
		}
		
		public Builder addInstanceNonmatchingSolution(int repetition, URI columnSolution) {
			testStatistics.instanceNonmatchingSolutions.get(repetition).add(columnSolution);
			
			return this;
		}

		public Builder addNoPropertyLearningNumericColumn() {
			testStatistics.noPropertyLearningNumericColumns++;
			
			return this;
		}
		
		public Builder addInTestMissingColumn() {
			testStatistics.inTestMissingColumns++;
			
			return this;
		}
		
		public Builder addInLearningMissingColumn() {
			testStatistics.inLearningMissingColumns++;
			
			return this;
		}

		public Builder addNoPropertyTestingNumericColumn() {
			testStatistics.noPropertyTestingNumericColums++;
			
			return this;
		}

		public Builder addUniqueProperty(int repetition, @Nullable final URI propertyUri) {
			testStatistics.uniqueProperties.get(repetition).add(propertyUri);
			
			return this;
		}

		public Builder addUniquePropertyLearnt(int repetition, URI propertyUri) {
			testStatistics.uniquePropertiesLearnt.get(repetition).add(propertyUri);
			
			return this;
		}
		
		public Set<URI> getUniquePropertiesLearnt(final int repetition) {
			return testStatistics.uniquePropertiesLearnt.get(repetition);
		}
		
		public Set<URI> getUniquePropertiesTested(final int repetition) {
			return testStatistics.uniquePropertiesTested.get(repetition);
		}

		public Builder addUniquePropertyTested(int repetition, URI propertyUri) {
			testStatistics.uniquePropertiesTested.get(repetition).add(propertyUri);
			
			return this;
		}
		
		public Builder addTrue(int repetition, URI propertyUri) {
			final Map<URI, Integer> repetitionTruePositives = testStatistics.truePositives.get(repetition);
			
			repetitionTruePositives.compute(propertyUri, (oldKey, oldValue) -> (oldValue == null ?  1 : oldValue + 1));
			
			return this;
		}
		
		public Builder addFalse(int repetition, URI assigned, URI correct) {
			final Map<URI, Integer> repetitionFalsePositives = testStatistics.falsePositives.get(repetition);
			repetitionFalsePositives.compute(assigned, (oldKey, oldValue) -> (oldValue == null ?  1 : oldValue + 1));
			
			final Map<URI, Integer> repetitionFalseNegatives = testStatistics.falseNegatives.get(repetition);
			repetitionFalseNegatives.compute(correct, (oldKey, oldValue) -> (oldValue == null ?  1 : oldValue + 1));
			
			testStatistics.errors.compute(repetition, (oldKey, oldValue) -> (oldValue == null ?  1 : oldValue + 1));
			
			return this;
		}
		
		public Builder addPropertyOccurence(int repetition, URI propertyUri) {
			final Map<URI, Integer> repetitionOccurencesCount = testStatistics.occurencesCount.get(repetition);
			repetitionOccurencesCount.compute(propertyUri, (oldKey, oldValue) -> (oldValue == null ?  1 : oldValue + 1));
			
			testStatistics.totalOccurencesCount.compute(repetition, (oldKey, oldValue) -> (oldValue == null ?  1 : oldValue + 1));
			
			return this;
		}
		
		public Builder addLearningTime(long time) {
			testStatistics.learningTime += time;
			
			return this;
		}
		
		public Builder addTestingTime(long time) {
			testStatistics.testingTime += time;
			
			return this;
		}

		public Builder setRepetitions(int repetitions) {
			testStatistics.repetitions = repetitions;
			
			for (int repetition = 0; repetition < repetitions; repetition++) {
				testStatistics.uniquePropertiesLearnt.put(repetition, new HashSet<>());
				testStatistics.uniquePropertiesTested.put(repetition, new HashSet<>());
				testStatistics.uniqueProperties.put(repetition, new HashSet<>());
				testStatistics.nonmatchingSolutions.put(repetition, HashMultiset.create());
				testStatistics.instanceNonmatchingSolutions.put(repetition, HashMultiset.create());
				testStatistics.truePositives.put(repetition, new HashMap<>());
				testStatistics.falsePositives.put(repetition, new HashMap<>());
				testStatistics.falseNegatives.put(repetition, new HashMap<>());
				testStatistics.occurencesCount.put(repetition, new HashMap<>());
				testStatistics.totalOccurencesCount.put(repetition, 0);
				testStatistics.errors.put(repetition, 0);
			}
			
			return this;
		}
	}

	public static Builder builder() {
		return new Builder();
	}
	
	private TestStatistics() {
	}

	public long getSeed() {
		return seed;
	}

	public double getFilesCount() {
		return filesCount / (double) repetitions;
	}

	public double getTestFilesCount() {
		return testFilesCount / (double) repetitions;
	}

	public double getLearningFilesCount() {
		return learningFilesCount / (double) repetitions;
	}

	public double getIrregularHeaderFiles() {
		return irregularHeaderFiles / (double) repetitions;
	}

	public double getFewRowsFiles() {
		return fewRowsFiles / (double) repetitions;
	}

	public double getFewTypedRowsFiles() {
		return fewTypedRowsFiles / (double) repetitions;
	}

	public double getLearntFiles() {
		return learntFiles / (double) repetitions;
	}

	public double getLearningColumnsCount() {
		return learningColumnsCount / (double) repetitions;
	}

	public double getLearntContextColumnsCount() {
		return learntContextColumnsCount / (double) repetitions;
	}
	
	public double getTestingColumnsCount() {
		return testingColumnsCount / (double) repetitions;
	}

	public double getTestedContextColumnsCount() {
		return testedContextColumnsCount / (double) repetitions;
	}

	public double getLearntNumericColumns() {
		return learntNumericColumns / (double) repetitions;
	}

	public double getAttemptedLearntNumericColumns() {
		return attemptedLearntNumericColumns / (double) repetitions;
	}
	
	public double getAttemptedTestedNumericColumns() {
		return attemptedTestedNumericColumns / (double) repetitions;
	}

	public double getTooSmallLearningNumericColumns() {
		return tooSmallLearningNumericColumns / (double) repetitions;
	}
	
	public double getTooSmallTestingNumericColumns() {
		return tooSmallTestingNumericColumns / (double) repetitions;
	}

	public double getTestedFiles() {
		return testedFiles / (double) repetitions;
	}

	public double getAnnotatedNumericColumns() {
		return annotatedNumericColumns / (double) repetitions;
	}

	public double getMissingSolutions() {
		return missingSolutions / (double) repetitions;
	}

	public double getMatchingSolutions() {
		return matchingSolutions / (double) repetitions;
	}
	
	public double getInstanceMatchingSolutions() {
		return instanceMatchingSolutions / (double) repetitions;
	}

	public double getNonmatchingSolutions() {
		return nonmatchingSolutions.values().stream().mapToInt(e -> e.size()).sum() / (double) repetitions;
	}
	
	public double getInstanceNonmatchingSolutions() {
		return instanceNonmatchingSolutions.values().stream().mapToInt(e -> e.size()).sum() / (double) repetitions;
	}

	public double getNoPropertyLearningNumericColumns() {
		return noPropertyLearningNumericColumns / (double) repetitions;
	}

	public double getNoPropertyTestingNumericColums() {
		return noPropertyTestingNumericColums / (double) repetitions;
	}
	
	public double getInTestMissingColumns() {
		return inTestMissingColumns / (double) repetitions;
	}
	
	public double getInLearningMissingColumns() {
		return inLearningMissingColumns / (double) repetitions;
	}

	public double getUniqueProperties() {
		return uniqueProperties.values().stream().mapToInt(e -> e.size()).sum() / (double) repetitions;
	}

	public double getUniquePropertiesLearnt() {
		return uniquePropertiesLearnt.values().stream().mapToInt(e -> e.size()).sum() / (double) repetitions;
	}

	public double getUniquePropertiesTested() {
		return uniquePropertiesTested.values().stream().mapToInt(e -> e.size()).sum() / (double) repetitions;
	}

	public double getNonmatchingAvailableSolutions() {
		int nonmatchingAvailableSum = 0;
		
		for (int repetition = 0; repetition < repetitions; repetition++) {
			final Multiset<URI> nonmatchingAvailable = HashMultiset.create(nonmatchingSolutions.get(repetition));
			nonmatchingAvailable.retainAll(uniquePropertiesLearnt.get(repetition));
			
			nonmatchingAvailableSum += nonmatchingAvailable.size();
		}
				
		return nonmatchingAvailableSum / (double) repetitions;
	}
	
	public double getInstanceNonmatchingAvailableSolutions() {
		int nonmatchingAvailableSum = 0;
		
		for (int repetition = 0; repetition < repetitions; repetition++) {
			final Multiset<URI> nonmatchingAvailable = HashMultiset.create(instanceNonmatchingSolutions.get(repetition));
			nonmatchingAvailable.retainAll(uniquePropertiesLearnt.get(repetition));
			
			nonmatchingAvailableSum += nonmatchingAvailable.size();
		}
				
		return nonmatchingAvailableSum / (double) repetitions;
	}
	
	private double getAverageWeightedMeasure(final Map<Integer, Map<URI, Double>> measures) {
		double weightedMeasuresSum = 0;
		
		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			final Map<URI, Double> repetitionMeasures = measures.get(repetition);
			final Map<URI, Integer> repetitionInstancesCount = this.occurencesCount.get(repetition);
			
			final double repetitionMeasuresSum = repetitionMeasures.entrySet().stream().map(e -> {
				final Integer instancesCount = repetitionInstancesCount.get(e.getKey());
				
				return e.getValue() * (instancesCount == null ? 0 : instancesCount);
			}).collect(Collectors.summingDouble(Double::doubleValue));
			
			final int repetitionTotalInstancesCount = this.totalOccurencesCount.get(repetition);
			
			final double repetitionWeightedMeasure = repetitionMeasuresSum / (double) repetitionTotalInstancesCount;
			
			weightedMeasuresSum += repetitionWeightedMeasure;
		}
				
		return weightedMeasuresSum / (double) repetitions;
	}
	
	private Map<URI, Double> getAveragedMeasures(final Map<? extends Integer, ? extends Map<? extends URI, ? extends Number>> measures) {
		final Map<URI, Double> measuresSums = new HashMap<>();
		
		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			final Map<? extends URI, ? extends Number> repetitionMeasures = measures.get(repetition);
			
			repetitionMeasures.forEach((key, value) -> {
				measuresSums.compute(key, (oldKey, oldValue) -> (oldValue == null ?  value.doubleValue() : oldValue + value.doubleValue())); 
			});
		}
				
		return measuresSums.entrySet().stream().collect(ImmutableMap.toImmutableMap(e -> e.getKey(), e -> e.getValue() / (double) repetitions));
	}
	
	public Map<URI, Double> getAverageTruePositives() {
		return getAveragedMeasures(this.truePositives);
	}
	
	public Map<URI, Double> getAverageFalsePositives() {
		return getAveragedMeasures(this.falsePositives);
	}
	
	public Map<URI, Double> getAverageFalseNegatives() {
		return getAveragedMeasures(this.falseNegatives);
	}
	
	private Map<Integer, Map<URI, Double>> getRecalls() {
		return this.occurencesCount.entrySet().stream().collect(ImmutableMap.toImmutableMap(e -> e.getKey(), e -> {
			final int iteration = e.getKey();
			
			final Map<URI,Integer> iterationAll = e.getValue();
			
			final Map<URI, Integer> iterationTruePositives = this.truePositives.get(iteration);
			final Map<URI, Integer> iterationFalseNegatives = this.falseNegatives.get(iteration);
			
			return iterationAll.keySet().stream().collect(ImmutableMap.toImmutableMap(Function.identity(), property -> {
				final int propertyTruePositives = iterationTruePositives.get(property) == null ? 0 : iterationTruePositives.get(property);
				final int propertyFalseNegatives = iterationFalseNegatives.get(property) == null ? 0 : iterationFalseNegatives.get(property);
				
				final int propertyAllSum = propertyTruePositives + propertyFalseNegatives;
				if (propertyAllSum == 0) {
					return 0d;
				}
				
				return ((double) propertyTruePositives) / ((double) propertyAllSum);
			}));
		}));
	}
	
	private Map<Integer, Map<URI, Double>> getPrecisions() {
		return this.occurencesCount.entrySet().stream().collect(ImmutableMap.toImmutableMap(e -> e.getKey(), e -> {
			final int iteration = e.getKey();
			
			final Map<URI,Integer> iterationAll = e.getValue();
			
			final Map<URI, Integer> iterationTruePositives = this.truePositives.get(iteration);
			final Map<URI, Integer> iterationFalsePositives = this.falsePositives.get(iteration);
			
			return iterationAll.keySet().stream().collect(ImmutableMap.toImmutableMap(Function.identity(), property -> {
				final int propertyTruePositives = iterationTruePositives.get(property) == null ? 0 : iterationTruePositives.get(property);
				final int propertyFalsePositives = iterationFalsePositives.get(property) == null ? 0 : iterationFalsePositives.get(property);
				
				final int propertyAllSum = propertyTruePositives + propertyFalsePositives;
				if (propertyAllSum == 0) {
					return 1d;
				}
				
				return ((double) propertyTruePositives) / ((double) propertyAllSum);
			}));
		}));
	}
	
	private Map<Integer, Map<URI, Double>> getFMeasures() {
		final Map<Integer, Map<URI, Double>> precisions = getPrecisions();
		final Map<Integer, Map<URI, Double>> recalls = getRecalls();
		
		return this.occurencesCount.entrySet().stream().collect(ImmutableMap.toImmutableMap(e -> e.getKey(), e -> {
			final int iteration = e.getKey();
			
			final Map<URI,Integer> iterationAll = e.getValue();
			
			final Map<URI, Double> iterationPrecisions = precisions.get(iteration);
			final Map<URI, Double> iterationRecalls = recalls.get(iteration);
			
			return iterationAll.keySet().stream().collect(ImmutableMap.toImmutableMap(Function.identity(), property -> {
				final double propertyPrecision = iterationPrecisions.get(property);
				final double propertyRecall = iterationRecalls.get(property);
				
				final double propertyAllSum = propertyPrecision + propertyRecall;
				if (propertyAllSum == 0) {
					return 0d;
				}
				
				final double fMeasure = 2 * propertyPrecision * propertyRecall / (propertyPrecision + propertyRecall);
				
				return fMeasure;
			}));
		}));
	}
	
	public double getAverageWeightedPrecision() {
		return getAverageWeightedMeasure(getPrecisions());
	}
	
	public Map<URI, Double> getAveragePrecisions() {
		return getAveragedMeasures(getPrecisions());
	}
	
	public double getAverageWeightedRecall() {
		return getAverageWeightedMeasure(getRecalls());
	}
	
	public Map<URI, Double> getAverageRecalls() {
		return getAveragedMeasures(getRecalls());
	}
	
	public double getAverageWeightedFMeasure() {
		return getAverageWeightedMeasure(getFMeasures());
	}
	
	public Map<URI, Double> getAverageFMeasures() {
		return getAveragedMeasures(getFMeasures());
	}
	
	public double getAverageErrorRate() {
		double errorRatesSum = 0;
		
		for (int repetition = 0; repetition < this.repetitions; repetition++) {
			final int repetitionErrors = this.errors.get(repetition);
			final int repetitionInstancesCount = this.totalOccurencesCount.get(repetition);
			
			final double repetitionErrorRate = (double) repetitionErrors / (double) repetitionInstancesCount; 
			
			errorRatesSum += repetitionErrorRate;
		}
				
		return errorRatesSum / (double) repetitions;
	}
	
	public double getLearningTime() {
		return this.learningTime / (double) repetitions;
	}
	
	public double getTestingTime() {
		return this.testingTime / (double) repetitions;
	}
}