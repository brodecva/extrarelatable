package eu.odalic.extrarelatable.services.odalic.values;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

import eu.odalic.extrarelatable.services.odalic.conversions.EntityCandidateValueNavigableSetDeserializer;
import eu.odalic.extrarelatable.services.odalic.conversions.EntityCandidateValueSetDeserializer;
import eu.odalic.extrarelatable.services.odalic.conversions.EntityCandidateValueSetSerializer;

/**
 * Odalic domain class adapted for REST API.
 *
 * @author Václav Brodec
 *
 */
@XmlRootElement(name = "headerAnnotation")
public final class HeaderAnnotationValue {

	private Map<String, NavigableSet<EntityCandidateValue>> candidates;

	private Map<String, Set<EntityCandidateValue>> chosen;

	public HeaderAnnotationValue() {
		this.candidates = ImmutableMap.of();
		this.chosen = ImmutableMap.of();
	}

	/**
	 * @return the candidates
	 */
	@XmlAnyElement
	@JsonDeserialize(contentUsing = EntityCandidateValueNavigableSetDeserializer.class)
	@JsonSerialize(contentUsing = EntityCandidateValueSetSerializer.class)
	public Map<String, NavigableSet<EntityCandidateValue>> getCandidates() {
		return this.candidates;
	}

	/**
	 * @return the chosen
	 */
	@XmlAnyElement
	@JsonDeserialize(contentUsing = EntityCandidateValueSetDeserializer.class)
	@JsonSerialize(contentUsing = EntityCandidateValueSetSerializer.class)
	public Map<String, Set<EntityCandidateValue>> getChosen() {
		return this.chosen;
	}

	/**
	 * @param candidates
	 *            the candidates to set
	 */
	public void setCandidates(
			final Map<? extends String, ? extends NavigableSet<? extends EntityCandidateValue>> candidates) {
		this.candidates = candidates.entrySet().stream()
				.collect(ImmutableMap.toImmutableMap(e -> e.getKey(), e -> ImmutableSortedSet.copyOf(e.getValue())));
	}

	/**
	 * @param chosen
	 *            the chosen to set
	 */
	public void setChosen(final Map<? extends String, ? extends Set<? extends EntityCandidateValue>> chosen) {
		checkNotNull(chosen);

		this.chosen = chosen.entrySet().stream()
				.collect(ImmutableMap.toImmutableMap(e -> e.getKey(), e -> ImmutableSet.copyOf(e.getValue())));
	}

	@Override
	public String toString() {
		return "HeaderAnnotationValue [candidates=" + this.candidates + ", chosen=" + this.chosen + "]";
	}
}
