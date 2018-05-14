package eu.odalic.extrarelatable.services.odalic.values;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Preconditions;

/**
 * <p>
 * Odalic domain class adapted for REST API.
 * </p>
 *
 * @author Václav Brodec
 *
 */
@XmlRootElement(name = "entityCandidate")
public final class EntityCandidateValue implements Serializable, Comparable<EntityCandidateValue> {

	private static final long serialVersionUID = 3072774254576336747L;

	private EntityValue entity;

	private ScoreValue score;

	public EntityCandidateValue() {
	}

	@Override
	public int compareTo(final EntityCandidateValue o) {
		final int scoreComparison = this.score.compareTo(o.score);

		if (scoreComparison == 0) {
			return this.entity.compareTo(o.entity);
		} else {
			return scoreComparison;
		}
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
		EntityCandidateValue other = (EntityCandidateValue) obj;
		if (entity == null) {
			if (other.entity != null) {
				return false;
			}
		} else if (!entity.equals(other.entity)) {
			return false;
		}
		if (score == null) {
			if (other.score != null) {
				return false;
			}
		} else if (!score.equals(other.score)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		result = prime * result + ((score == null) ? 0 : score.hashCode());
		return result;
	}

	/**
	 * @return the entity
	 */
	@XmlElement
	@Nullable
	public EntityValue getEntity() {
		return this.entity;
	}

	/**
	 * @return the score
	 */
	@XmlElement
	@Nullable
	public ScoreValue getScore() {
		return this.score;
	}

	/**
	 * @param entity
	 *            the entity to set
	 */
	public void setEntity(final EntityValue entity) {
		Preconditions.checkNotNull(entity, "The entity cannot be null!");

		this.entity = entity;
	}

	/**
	 * @param score
	 *            the score to set
	 */
	public void setScore(final ScoreValue score) {
		Preconditions.checkNotNull(score, "The score cannot be null!");

		this.score = score;
	}

	@Override
	public String toString() {
		return "EntityCandidateValue [entity=" + this.entity + ", score=" + this.score + "]";
	}
}
