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
		
	@XmlTransient
	public static Label synthetic(final UUID uuid, final String description, final int index, final String file, final List<? extends String> firstValues, final List<? extends String> headers, final List<? extends List<? extends String>> firstRows) {
		return new Label(uuid, uuid.toString(), description, true, index, file, firstValues, headers, firstRows);
	}
	
	@XmlTransient
	public static Label synthetic(final UUID uuid, final int index, final String file, final List<? extends String> firstValues, final List<? extends String> headers, final List<? extends List<? extends String>> firstRows) {
		return synthetic(uuid, null, index, file, firstValues, headers, firstRows);
	}
	
	@XmlTransient
	public static Label of(final UUID uuid, final String text, final String description, final boolean synthetic, final int index, final String file, final List<? extends String> firstValues, final List<? extends String> headers, final List<? extends List<? extends String>> firstRows) {
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
	
	private Label(final UUID uuid, final String text, final String description, final boolean synthetic, final int index, final String file, final List<? extends String> firstValues, final List<? extends String> headers, final List<? extends List<? extends String>> firstRows) {
		checkNotNull(uuid);
		
		this.uuid = uuid;
		this.text = text;
		this.description = description;
		this.synthetic = synthetic;
		
		this.index = index;
		this.file = file;
		this.firstValues = ImmutableList.copyOf(firstValues);
		this.headers = ImmutableList.copyOf(headers);
		this.firstRows = firstRows.stream().map(e -> ImmutableList.<String>copyOf(e)).collect(ImmutableList.toImmutableList());
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getText() {
		return text;
	}

	public boolean isSynthetic() {
		return synthetic;
	}

	public int getIndex() {
		return index;
	}

	public String getFile() {
		return file;
	}

	public List<String> getFirstValues() {
		return firstValues;
	}

	public List<String> getHeaders() {
		return headers;
	}

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
		return "Label [uuid=" + uuid + ", text=" + text + ", description=" + description + ", synthetic=" + synthetic + ", index=" + index
				+ ", file=" + file + ", firstValues=" + firstValues + ", headers=" + headers + ", firstRows=" + firstRows + "]";
	}
}
