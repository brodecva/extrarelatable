package eu.odalic.extrarelatable.api.rest.values;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.ImmutableList;
import com.webcohesion.enunciate.metadata.DocumentationExample;
import com.webcohesion.enunciate.metadata.json.JsonSeeAlso;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

import eu.odalic.extrarelatable.api.rest.adapters.MetadataAdapter;
import eu.odalic.extrarelatable.model.table.Metadata;
import eu.odalic.extrarelatable.model.table.ParsedTable;

/**
 * <p>
 * A table which has already been parsed into logical cells containing the
 * original text, grouped into table rows and columns. It can be accompanied by
 * meta-data.
 * </p>
 * 
 * <p>
 * {@link ParsedTable} adapted for REST API.
 * </p>
 * 
 * @author Václav Brodec
 *
 */
@XmlRootElement(name = "parsedTable")
@XmlAccessorType(XmlAccessType.NONE)
@Immutable
@JsonSeeAlso({ FormatValue.class })
public final class ParsedTableValue implements Serializable {

	private static final long serialVersionUID = 4101912998363935336L;

	private List<List<String>> rows;

	private List<String> headers;

	private Metadata metadata;

	public ParsedTableValue() {
		this.rows = ImmutableList.of();
		this.headers = ImmutableList.of();
		this.metadata = null;
	}

	public ParsedTableValue(final ParsedTable adaptee) {
		this.rows = adaptee.getRows();
		this.headers = adaptee.getHeaders();
		this.metadata = adaptee.getMetadata();
	}

	/**
	 * array of string arrays [[...,...],[...,...]] representing the table
	 * 
	 * @return array of string arrays representing the table
	 */
	@XmlElement
	@DocumentationExample("")
	@TypeHint(String[][].class)
	public List<List<String>> getRows() {
		return rows;
	}

	public void setRows(List<List<String>> rows) {
		checkNotNull(rows);

		this.rows = rows.stream().map(row -> ImmutableList.copyOf(row)).collect(ImmutableList.toImmutableList());
	}

	/**
	 * column headers
	 * 
	 * @return column headers
	 */
	@XmlElement
	@TypeHint(String[].class)
	public List<String> getHeaders() {
		return headers;
	}

	public void setHeaders(List<String> headers) {
		checkNotNull(headers);

		this.headers = ImmutableList.copyOf(headers);
	}

	/**
	 * the table meta-data
	 * 
	 * @return the table meta-data
	 */
	@XmlElement
	@Nullable
	@XmlJavaTypeAdapter(MetadataAdapter.class)
	@TypeHint(MetadataValue.class)
	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	@Override
	public String toString() {
		return "ParsedTableValue [rows=" + rows + ", headers=" + headers + ", metadata=" + metadata + "]";
	}
}
