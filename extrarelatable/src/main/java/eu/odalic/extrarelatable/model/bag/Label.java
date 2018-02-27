package eu.odalic.extrarelatable.model.bag;

import java.util.List;
import java.util.UUID;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.ImmutableList;

import eu.odalic.extrarelatable.api.rest.adapters.LabelAdapter;

@Immutable
@XmlJavaTypeAdapter(LabelAdapter.class)
public final class Label implements Comparable<Label> {
	
	private final UUID uuid = UUID.randomUUID();
	
	private final String text;
	private final String description;
	private final boolean synthetic;
	
	private final int index;
	private final String file;
	private final List<String> firstValues;
	private final List<String> headers;
	private final List<List<String>> firstRows; 
		
	public static Label synthetic(final String description, final int index, final String file, final List<? extends String> firstValues, final List<? extends String> headers, final List<? extends List<? extends String>> firstRows) {
		return new Label(UUID.randomUUID().toString(), description, true, index, file, firstValues, headers, firstRows);
	}
	
	public static Label synthetic(final int index, final String file, final List<? extends String> firstValues, final List<? extends String> headers, final List<? extends List<? extends String>> firstRows) {
		return synthetic(null, index, file, firstValues, headers, firstRows);
	}
	
	public static Label of(final String text, final String description, final boolean synthetic, final int index, final String file, final List<? extends String> firstValues, final List<? extends String> headers, final List<? extends List<? extends String>> firstRows) {
		return new Label(text, description, false, index, file, firstValues, headers, firstRows);
	}
	
	private Label(final String text, final String description, final boolean synthetic, final int index, final String file, final List<? extends String> firstValues, final List<? extends String> headers, final List<? extends List<? extends String>> firstRows) {
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
