package eu.odalic.extrarelatable.model.bag;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.UUID;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import eu.odalic.extrarelatable.api.rest.adapters.AttributeValuePairAdapter;

@Immutable
@XmlJavaTypeAdapter(AttributeValuePairAdapter.class)
public final class AttributeValuePair implements Comparable<AttributeValuePair>, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1003177810058153064L;

	private final UUID uuid = UUID.randomUUID();
	
	private final Attribute attribute;
	private final Value value;
	
	@SuppressWarnings("unused")
	private AttributeValuePair() {
		this.attribute = null;
		this.value = null;
	}
	
	public AttributeValuePair(Attribute attribute, Value value) {
		checkNotNull(attribute);
		checkNotNull(value);
		
		this.attribute = attribute;
		this.value = value;
	}

	@XmlTransient
	@JsonIgnore
	public Attribute getAttribute() {
		return attribute;
	}

	@XmlTransient
	@JsonIgnore
	public Value getValue() {
		return value;
	}	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + uuid.hashCode();
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
		final AttributeValuePair other = (AttributeValuePair) obj;
		if (!uuid.equals(other.uuid)) {
			return false;
		}
		return true;
	}
	
	@XmlTransient
	@Override
	public int compareTo(AttributeValuePair other) {
		return uuid.compareTo(other.uuid);
	}

	@Override
	public String toString() {
		return "AttributeValuePair [uuid=" + uuid + ", attribute=" + attribute + ", value=" + value + "]";
	}
}
