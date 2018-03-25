package eu.odalic.extrarelatable.model.bag;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class EntityValue extends AbstractValue implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1092664987967658783L;
	private final String entity;

	public static EntityValue of(final String entity) {
		return new EntityValue(entity);
	}
	
	private EntityValue(final String entity) {
		this.entity = entity;
	}	

	@Override
	public String getText() {
		return entity;
	}
	
	@Override
	public boolean isEntity() {
		return true;
	}

	public String getEntity() {
		return entity;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final EntityValue other = (EntityValue) obj;
		if (entity == null) {
			if (other.entity != null) {
				return false;
			}
		} else if (!entity.equals(other.entity)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "EntityValue [entity=" + entity + "]";
	}
}
