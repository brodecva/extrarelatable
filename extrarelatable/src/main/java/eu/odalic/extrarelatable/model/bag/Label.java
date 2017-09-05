package eu.odalic.extrarelatable.model.bag;

import java.util.UUID;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Label {
	private final String text;
	private final String description;
	private final boolean synthetic;
		
	public static Label synthetic(String description) {
		return new Label(UUID.randomUUID().toString(), description, true);
	}
	
	public static Label synthetic() {
		return synthetic(null);
	}
	
	public static Label of(String text, String description) {
		return new Label(text, description, false);
	}
	
	public static Label of(String text) {
		return of(text, null);
	}
	
	private Label(String text, String description, boolean synthetic) {
		this.text = text;
		this.description = description;
		this.synthetic = synthetic;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
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
		final Label other = (Label) obj;
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
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
		return "Label [text=" + text + ", description=" + description + ", synthetic=" + synthetic + "]";
	}
}
