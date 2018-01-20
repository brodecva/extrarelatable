package eu.odalic.extrarelatable.experiments;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

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
	private int nonmatchingSolutions;
	private int noPropertyLearningNumericColumns;
	private int noPropertyTestingNumericColums;
	private Set<URI> uniqueProperties = new HashSet<>();
	private Set<URI> uniquePropertiesLearnt = new HashSet<>();
	private Set<URI> uniquePropertiesTested = new HashSet<>();
	
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

		public Builder setFilesCount(int filesCount) {
			testStatistics.filesCount = filesCount;
			
			return this;
		}

		public Builder setTestFilesCount(int testFilesCount) {
			testStatistics.testFilesCount = testFilesCount;
			
			return this;
		}

		public Builder setLearningFilesCount(int learningFilesCount) {
			testStatistics.learningFilesCount = learningFilesCount;
			
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

		public Builder addNonmatchingSolution() {
			testStatistics.nonmatchingSolutions++;
			
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

		public Builder addUniqueProperty(@Nullable final URI propertyUri) {
			testStatistics.uniqueProperties.add(propertyUri);
			
			return this;
		}

		public Builder addUniquePropertyLearnt(URI propertyUri) {
			testStatistics.uniquePropertiesLearnt.add(propertyUri);
			
			return this;
		}

		public Builder addUniquePropertyTested(URI propertyUri) {
			testStatistics.uniquePropertiesTested.add(propertyUri);
			
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

	public int getFilesCount() {
		return filesCount;
	}

	public int getTestFilesCount() {
		return testFilesCount;
	}

	public int getLearningFilesCount() {
		return learningFilesCount;
	}

	public int getIrregularHeaderFiles() {
		return irregularHeaderFiles;
	}

	public int getFewRowsFiles() {
		return fewRowsFiles;
	}

	public int getFewTypedRowsFiles() {
		return fewTypedRowsFiles;
	}

	public int getLearntFiles() {
		return learntFiles;
	}

	public int getLearningColumnsCount() {
		return learningColumnsCount;
	}

	public int getLearntContextColumnsCount() {
		return learntContextColumnsCount;
	}

	public int getLearntNumericColumns() {
		return learntNumericColumns;
	}

	public int getAttemptedLearntNumericColumns() {
		return attemptedLearntNumericColumns;
	}

	public int getTooSmallNumericColumns() {
		return tooSmallNumericColumns;
	}

	public int getTestedFiles() {
		return testedFiles;
	}

	public int getAnnotatedNumericColumns() {
		return annotatedNumericColumns;
	}

	public int getMissingSolutions() {
		return missingSolutions;
	}

	public int getMatchingSolutions() {
		return matchingSolutions;
	}

	public int getNonmatchingSolutions() {
		return nonmatchingSolutions;
	}

	public int getNoPropertyLearningNumericColumns() {
		return noPropertyLearningNumericColumns;
	}

	public int getNoPropertyTestingNumericColums() {
		return noPropertyTestingNumericColums;
	}

	public Set<URI> getUniqueProperties() {
		return uniqueProperties;
	}

	public Set<URI> getUniquePropertiesLearnt() {
		return uniquePropertiesLearnt;
	}

	public Set<URI> getUniquePropertiesTested() {
		return uniquePropertiesTested;
	}
}