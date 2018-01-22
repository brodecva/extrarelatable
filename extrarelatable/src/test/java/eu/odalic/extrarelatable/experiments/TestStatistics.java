package eu.odalic.extrarelatable.experiments;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultiset;
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
	private int learntNumericColumns;
	private int attemptedLearntNumericColumns;
	private int tooSmallNumericColumns;
	private int testedFiles;
	private int annotatedNumericColumns;
	private int missingSolutions;
	private int matchingSolutions;
	private Map<Integer, Multiset<URI>> nonmatchingSolutions = new HashMap<>();// HashMultiset.create();
	private int noPropertyLearningNumericColumns;
	private int noPropertyTestingNumericColums;
	private Map<Integer, Set<URI>> uniqueProperties = new HashMap<>();
	private Map<Integer, Set<URI>> uniquePropertiesLearnt = new HashMap<>();
	private Map<Integer, Set<URI>> uniquePropertiesTested = new HashMap<>();// = new HashSet<>();
	private int repetitions;
	
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

		public Builder addLearntNumericColumn() {
			testStatistics.learntNumericColumns++;
			
			return this;				
		}

		public Builder addAttemptedLearntNumericColumn() {
			testStatistics.attemptedLearntNumericColumns++;
			
			return this;				
		}

		public Builder addTooSmallNumericColumn() {
			testStatistics.tooSmallNumericColumns++;
			
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

		public Builder addNonmatchingSolution(int repetition, URI columnSolution) {
			testStatistics.nonmatchingSolutions.get(repetition).add(columnSolution);
			
			return this;
		}

		public Builder addNoPropertyLearningNumericColumn() {
			testStatistics.noPropertyLearningNumericColumns++;
			
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

		public Builder addUniquePropertyTested(int repetition, URI propertyUri) {
			testStatistics.uniquePropertiesTested.get(repetition).add(propertyUri);
			
			return this;
		}

		public Builder setRepetitions(int repetitions) {
			testStatistics.repetitions = repetitions;
			
			for (int repetition = 0; repetition < repetitions; repetition++) {
				testStatistics.uniquePropertiesLearnt.put(repetition, new HashSet<>());
				testStatistics.uniquePropertiesTested.put(repetition, new HashSet<>());
				testStatistics.uniqueProperties.put(repetition, new HashSet<>());
				testStatistics.nonmatchingSolutions.put(repetition, HashMultiset.create());
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

	public double getLearntNumericColumns() {
		return learntNumericColumns / (double) repetitions;
	}

	public double getAttemptedLearntNumericColumns() {
		return attemptedLearntNumericColumns / (double) repetitions;
	}

	public double getTooSmallNumericColumns() {
		return tooSmallNumericColumns / (double) repetitions;
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

	public double getNonmatchingSolutions() {
		return nonmatchingSolutions.values().stream().mapToInt(e -> e.size()).sum() / (double) repetitions;
	}

	public double getNoPropertyLearningNumericColumns() {
		return noPropertyLearningNumericColumns / (double) repetitions;
	}

	public double getNoPropertyTestingNumericColums() {
		return noPropertyTestingNumericColums / (double) repetitions;
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
}