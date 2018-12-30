package eu.odalic.extrarelatable.api.rest.values;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.google.common.collect.ImmutableList;
import com.webcohesion.enunciate.metadata.DocumentationExample;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

import eu.odalic.extrarelatable.model.bag.Label;

/**
 * <p>
 * Label of a column and in turn of the derived property tree. Apart from the
 * label text it contains various samples from the original file to better
 * contextualize the label.
 * </p>
 * 
 * <p>
 * {@link Label} adapted for REST API.
 * </p>
 * 
 * @author Václav Brodec
 *
 */
@XmlRootElement(name = "label")
public final class LabelValue implements Serializable {

	private static final long serialVersionUID = 9122163600617727467L;

	private String text;
	private String description;
	private boolean synthetic;

	private int index;
	private String file;
	private List<String> firstValues;
	private List<String> headers;
	private List<List<String>> firstRows;

	public LabelValue() {
		this.text = null;
		this.description = null;
		this.synthetic = false;
		this.index = Integer.MIN_VALUE;
		this.file = null;
		this.firstValues = ImmutableList.of();
		this.headers = ImmutableList.of();
		this.firstRows = ImmutableList.of();
	}

	public LabelValue(Label adaptee) {
		this.text = adaptee.getText();
		this.description = adaptee.getDescription();
		this.synthetic = adaptee.isSynthetic();

		this.index = adaptee.getIndex();
		this.file = adaptee.getFile();
		this.firstValues = adaptee.getFirstValues();
		this.headers = adaptee.getHeaders();
		this.firstRows = adaptee.getFirstRows();
	}

	/**
	 * label text
	 * 
	 * @return label text
	 */
	@XmlElement
	@Nullable
	@DocumentationExample("City")
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	/**
	 * long-form description
	 * 
	 * @return long-form description
	 */
	@XmlElement
	@Nullable
	@DocumentationExample("Large settlement, sometimes having its own form of government.")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * automatically created label
	 * 
	 * @return automatically created label
	 */
	@XmlElement
	public boolean isSynthetic() {
		return synthetic;
	}

	public void setSynthetic(boolean synthetic) {
		this.synthetic = synthetic;
	}

	/**
	 * index of the column of origin
	 * 
	 * @return index of the column of origin
	 */
	@XmlElement
	@DocumentationExample("3")
	@TypeHint(Integer.class)
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		checkArgument(index >= 0, "The index must be non-negative!");

		this.index = index;
	}

	/**
	 * file of origin
	 * 
	 * @return file of origin
	 */
	@XmlElement
	@Nullable
	@DocumentationExample("cities.csv")
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		checkNotNull(file);

		this.file = file;
	}

	/**
	 * first values in the originating column
	 * 
	 * @return first values in the originating column
	 */
	@XmlElement
	@DocumentationExample(value = "New York", value2 = "Austin")
	@TypeHint(String[].class)
	public List<String> getFirstValues() {
		return firstValues;
	}

	public void setFirstValues(List<String> firstValues) {
		this.firstValues = ImmutableList.copyOf(firstValues);
	}

	/**
	 * the table headers
	 * 
	 * @return the table headers
	 */
	@XmlElement
	@DocumentationExample(value = "City", value2 = "State")
	@TypeHint(String[].class)
	public List<String> getHeaders() {
		return headers;
	}

	public void setHeaders(List<String> headers) {
		this.headers = ImmutableList.copyOf(headers);
	}

	/**
	 * Provides sample of first few rows (array of string arrays [[...,...],[...,...]]).
	 * 
	 * @return the array of string arrays representing rows
	 */
	@XmlElement
	@DocumentationExample("")
	@TypeHint(String[][].class)
	public List<List<String>> getFirstRows() {
		return firstRows;
	}

	public void setFirstRows(List<List<String>> firstRows) {
		this.firstRows = firstRows.stream().map(row -> ImmutableList.copyOf(row))
				.collect(ImmutableList.toImmutableList());
	}

	@Override
	public String toString() {
		return "LabelValue [text=" + text + ", description=" + description + ", synthetic=" + synthetic + ", index="
				+ index + ", file=" + file + ", firstValues=" + firstValues + ", headers=" + headers + ", firstRows="
				+ firstRows + "]";
	}
}
