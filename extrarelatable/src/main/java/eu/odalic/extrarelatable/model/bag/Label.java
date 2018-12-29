package eu.odalic.extrarelatable.model.bag;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.ImmutableList;

import eu.odalic.extrarelatable.api.rest.adapters.LabelAdapter;

/**
 * Label of a property tree/a column in a table (originating from its header).
 * 
 * @author Václav Brodec
 *
 */
@Immutable
@XmlJavaTypeAdapter(LabelAdapter.class)
public final class Label implements Comparable<Label>, Serializable {

	private static final long serialVersionUID = -4083750297108671549L;

	private final UUID uuid;

	private final String text;
	private final String description;
	private final boolean synthetic;

	private final int index;
	private final String file;
	private final List<String> firstValues;
	private final List<String> headers;
	private final List<List<String>> firstRows;

	/**
	 * Creates a label using the provided {@link UUID} as its text value.
	 * 
	 * @param uuid
	 *            UUID of the label
	 * @param description
	 *            long form description
	 * @param index
	 *            index of the column header from which the label originated
	 * @param file
	 *            file name where the label originated
	 * @param firstValues
	 *            first values in the column from which the label originated
	 * @param headers
	 *            all the headers in the original file
	 * @param firstRows
	 *            first rows of the original file
	 * @return the label
	 */
	@XmlTransient
	public static Label synthetic(final UUID uuid, final String description, final int index, final String file,
			final List<? extends String> firstValues, final List<? extends String> headers,
			final List<? extends List<? extends String>> firstRows) {
		return new Label(uuid, uuid.toString(), description, true, index, file, firstValues, headers, firstRows);
	}

	/**
	 * Creates a label using the provided {@link UUID} as its text value.
	 * 
	 * @param uuid
	 *            UUID of the label
	 * @param index
	 *            index of the column header from which the label originated
	 * @param file
	 *            file name where the label originated
	 * @param firstValues
	 *            first values in the column from which the label originated
	 * @param headers
	 *            all the headers in the original file
	 * @param firstRows
	 *            first rows of the original file
	 * @return the label
	 */
	@XmlTransient
	public static Label synthetic(final UUID uuid, final int index, final String file,
			final List<? extends String> firstValues, final List<? extends String> headers,
			final List<? extends List<? extends String>> firstRows) {
		return synthetic(uuid, null, index, file, firstValues, headers, firstRows);
	}

	/**
	 * Creates a label from the text.
	 * 
	 * @param uuid
	 *            UUID of the label
	 * @param text
	 *            label text
	 * @param description
	 *            long form description
	 * @param synthetic
	 *            whether the label was meant as synthetic, it gets overridden to
	 *            {@code false}
	 * @param index
	 *            index of the column header from which the label originated
	 * @param file
	 *            file name where the label originated
	 * @param firstValues
	 *            first values in the column from which the label originated
	 * @param headers
	 *            all the headers in the original file
	 * @param firstRows
	 *            first rows of the original file
	 * @return the label
	 */
	@XmlTransient
	public static Label of(final UUID uuid, final String text, final String description, final boolean synthetic,
			final int index, final String file, final List<? extends String> firstValues,
			final List<? extends String> headers, final List<? extends List<? extends String>> firstRows) {
		return new Label(uuid, text, description, false, index, file, firstValues, headers, firstRows);
	}

	private Label() {
		this.uuid = null;
		this.text = null;
		this.description = null;
		this.synthetic = true;
		this.index = -1;
		this.file = null;
		this.firstValues = ImmutableList.of();
		this.headers = ImmutableList.of();
		this.firstRows = ImmutableList.of();
	}

	private Label(final UUID uuid, final String text, final String description, final boolean synthetic,
			final int index, final String file, final List<? extends String> firstValues,
			final List<? extends String> headers, final List<? extends List<? extends String>> firstRows) {
		checkNotNull(uuid);

		this.uuid = uuid;
		this.text = text;
		this.description = description;
		this.synthetic = synthetic;

		this.index = index;
		this.file = file;
		this.firstValues = ImmutableList.copyOf(firstValues);
		this.headers = ImmutableList.copyOf(headers);
		this.firstRows = firstRows.stream().map(e -> ImmutableList.<String>copyOf(e))
				.collect(ImmutableList.toImmutableList());
	}

	/**
	 * @return long form description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return label text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @return whether the label is synthetically made, that is without meaningful
	 *         label text
	 */
	public boolean isSynthetic() {
		return synthetic;
	}

	/**
	 * @return index of the column of origin
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return name of the file of origin
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @return first values of the column of origin
	 */
	public List<String> getFirstValues() {
		return firstValues;
	}

	/**
	 * @return the headers from the file of origin
	 */
	public List<String> getHeaders() {
		return headers;
	}

	/**
	 * @return first rows from the file of origin
	 */
	public List<List<String>> getFirstRows() {
		return firstRows;
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
		final Label other = (Label) obj;
		if (!uuid.equals(other.uuid)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(Label other) {
		return uuid.compareTo(other.uuid);
	}

	@Override
	public String toString() {
		return "Label [uuid=" + uuid + ", text=" + text + ", description=" + description + ", synthetic=" + synthetic
				+ ", index=" + index + ", file=" + file + ", firstValues=" + firstValues + ", headers=" + headers
				+ ", firstRows=" + firstRows + "]";
	}
}
