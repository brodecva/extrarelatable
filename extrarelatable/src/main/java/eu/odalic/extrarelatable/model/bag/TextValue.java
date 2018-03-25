package eu.odalic.extrarelatable.model.bag;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class TextValue extends AbstractValue implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3455997307710854648L;
	private final String text;

	public static TextValue of(final String text) {
		return new TextValue(text);
	}
	
	private TextValue(final String text) {
		this.text = text;
	}	

	@Override
	public String getText() {
		return text;
	}

	@Override
	public boolean isTextual() {
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final TextValue other = (TextValue) obj;
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
		return "TextValue [text=" + text + "]";
	}
}
