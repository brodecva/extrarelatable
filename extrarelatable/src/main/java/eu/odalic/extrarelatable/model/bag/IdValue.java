package eu.odalic.extrarelatable.model.bag;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;

/**
 * Value representing artificially made identifier.
 * 
 * @author Václav Brodec
 *
 */
@Immutable
public final class IdValue extends AbstractValue implements Serializable {
	private static final long serialVersionUID = 2331954295033377038L;
	
	private final String id;

	/**
	 * Creates the value.
	 * 
	 * @param id the ID text
	 * @return the value
	 */
	public static IdValue of(final String id) {
		return new IdValue(id);
	}
	
	private IdValue(final String id) {
		this.id = id;
	}	

	@Override
	public String getText() {
		return id;
	}

	@Override
	public boolean isId() {
		return true;
	}
	
	public String getId() {
		return id;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		final IdValue other = (IdValue) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "IdValue [id=" + id + "]";
	}
}
