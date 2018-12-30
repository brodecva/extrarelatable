package eu.odalic.extrarelatable.api.rest.values;

import java.io.Serializable;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.webcohesion.enunciate.metadata.DocumentationExample;

/**
 * Graph representation for the REST API.
 *
 * @author Václav Brodec
 */
@XmlRootElement(name = "graph")
public final class GraphValue implements Serializable {

	private static final long serialVersionUID = 1610346823333685091L;

	private String name;

	public GraphValue() {
	}

	/**
	 * the name of the graph
	 * 
	 * @return the name
	 */
	@XmlElement
	@Nullable
	@DocumentationExample("example_dataset__en-us")
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "GraphValue [name=" + this.name + "]";
	}
}
