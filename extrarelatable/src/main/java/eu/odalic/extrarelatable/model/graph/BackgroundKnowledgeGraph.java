package eu.odalic.extrarelatable.model.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

public final class BackgroundKnowledgeGraph implements Iterable<PropertyTree> {
	
	public static final class Builder {
		
		private final ImmutableSet.Builder<PropertyTree> propertyTreesBuilder = ImmutableSet.builder();
		
		public Builder add(final PropertyTree propertyTree) {
			checkNotNull(propertyTree);
			
			propertyTreesBuilder.add(propertyTree);
			
			return this;
		}
		
		public Builder addAll(final Collection<? extends PropertyTree> propertyTrees) {
			checkNotNull(propertyTrees);
			
			this.propertyTreesBuilder.addAll(propertyTrees);
			
			return this;
		}
		
		public BackgroundKnowledgeGraph build() {
			return new BackgroundKnowledgeGraph(propertyTreesBuilder.build());
		}
	}
	
	private final Set<PropertyTree> propertyTrees;

	public static Builder builder() {
		return new Builder();
	}
	
	public BackgroundKnowledgeGraph(final Set<? extends PropertyTree> propertyTrees) {
		checkNotNull(propertyTrees);
		
		this.propertyTrees = ImmutableSet.copyOf(propertyTrees);
	}

	public Set<PropertyTree> getPropertyTrees() {
		return propertyTrees;
	}

	@Override
	public Iterator<PropertyTree> iterator() {
		return propertyTrees.iterator();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + propertyTrees.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final BackgroundKnowledgeGraph other = (BackgroundKnowledgeGraph) obj;
		if (!propertyTrees.equals(other.propertyTrees)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "BackgroundKnowledgeGraph [propertyTrees=" + propertyTrees + "]";
	}
}
