package eu.odalic.extrarelatable.model.bag;

import java.util.List;
import java.util.UUID;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;

@Immutable
public final class Label {
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
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		result = prime * result + ((firstRows == null) ? 0 : firstRows.hashCode());
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		result = prime * result + ((firstValues == null) ? 0 : firstValues.hashCode());
		result = prime * result + index;
		result = prime * result + (synthetic ? 1231 : 1237);
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		Label other = (Label) obj;
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (file == null) {
			if (other.file != null) {
				return false;
			}
		} else if (!file.equals(other.file)) {
			return false;
		}
		if (headers == null) {
			if (other.headers != null) {
				return false;
			}
		} else if (!headers.equals(other.headers)) {
			return false;
		}
		if (firstRows == null) {
			if (other.firstRows != null) {
				return false;
			}
		} else if (!firstRows.equals(other.firstRows)) {
			return false;
		}
		if (firstValues == null) {
			if (other.firstValues != null) {
				return false;
			}
		} else if (!firstValues.equals(other.firstValues)) {
			return false;
		}
		if (index != other.index) {
			return false;
		}
		if (synthetic != other.synthetic) {
			return false;
		}
		if (text == null) {
			if (other.text != null) {
				return false;
			}
		} else if (!text.equals(other.text)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Label [text=" + text + ", description=" + description + ", synthetic=" + synthetic + ", index=" + index
				+ ", file=" + file + ", firstValues=" + firstValues + ", headers=" + headers + ", firstRows=" + firstRows + "]";
	}
}
