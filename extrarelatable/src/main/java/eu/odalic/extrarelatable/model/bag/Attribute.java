package eu.odalic.extrarelatable.model.bag;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;

/**
 * Attribute is in the context of bottom-up approach of ERT equal to header of
 * the context column by which the numeric column in question was partitioned in
 * the end.
 * 
 * @author Václav Brodec
 *
 */
@Immutable
public final class Attribute implements Serializable {

	private static final long serialVersionUID = -6922739591510264619L;
	private final String name;

	@SuppressWarnings("unused")
	private Attribute() {
		this.name = null;
	}

	/**
	 * Creates an attribute.
	 * 
	 * @param name attribute name
	 */
	public Attribute(String name) {
		checkNotNull(name);

		this.name = name;
	}

	/**
	 * @return attribute name
	 */
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + name.hashCode();
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
		final Attribute other = (Attribute) obj;
		if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Attribute [name=" + name + "]";
	}
}
