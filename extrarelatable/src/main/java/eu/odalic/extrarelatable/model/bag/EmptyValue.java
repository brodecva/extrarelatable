package eu.odalic.extrarelatable.model.bag;

import javax.annotation.concurrent.Immutable;

@Immutable
public enum EmptyValue implements Value {
	
	INSTANCE;
	
	public static EmptyValue instance() {
		return INSTANCE;
	}

	@Override
	public String getText() {
		return null;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean isNumeric() {
		return false;
	}

	@Override
	public boolean isTextual() {
		return false;
	}

	@Override
	public boolean isInstant() {
		return false;
	}

	@Override
	public boolean isEntity() {
		return false;
	}

	@Override
	public boolean isId() {
		return false;
	}

	@Override
	public boolean isUnit() {
		return false;
	}
}
