package eu.odalic.extrarelatable.api.rest.values;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.webcohesion.enunciate.metadata.DocumentationExample;

import eu.odalic.extrarelatable.model.bag.AttributeValuePair;

/**
 * <p>
 * Pair consisting of an attribute and a value which were used to create the
 * distinct sub-partitions of a numeric-column.
 * </p>
 * 
 * <p>
 * {@link AttributeValuePair} adapted for REST API.
 * </p>
 * 
 * @author Václav Brodec
 *
 */
@XmlRootElement(name = "attributeValuePair")
public final class AttributeValuePairValue implements Serializable {

	private static final long serialVersionUID = 6583830370968803352L;

	private String attribute;
	private String value;

	public AttributeValuePairValue(final AttributeValuePair adaptee) {
		this.attribute = adaptee.getAttribute().getName();
		this.value = adaptee.getValue() == null ? null : adaptee.getValue().toString();
	}

	/**
	 * the attribute part
	 * 
	 * @return the attribute part
	 */
	@XmlElement
	@Nullable
	@DocumentationExample("state")
	public String getAttribute() {
		return attribute;
	}

	/**
	 * the value part
	 * 
	 * @return the value part
	 */
	@XmlElement
	@Nullable
	@DocumentationExample("Idaho")
	public String getValue() {
		return value;
	}

	public void setAttribute(@Nullable String attribute) {
		this.attribute = attribute;
	}

	public void setValue(@Nullable String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "AttributeValuePairValue [attribute=" + attribute + ", value=" + value + "]";
	}
}
