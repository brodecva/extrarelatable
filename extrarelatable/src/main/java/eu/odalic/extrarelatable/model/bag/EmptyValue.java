package eu.odalic.extrarelatable.model.bag;

import javax.annotation.concurrent.Immutable;

@Immutable
public enum EmptyValue implements Value {
	
	INSTANCE;
	
	public static EmptyValue instance() {
		return INSTANCE;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean isNumeric() {
		return false;
	}

	@Override
	public boolean isTextual() {
		return false;
	}
}
