package eu.odalic.extrarelatable.model.bag;

import javax.annotation.concurrent.Immutable;

@Immutable
public enum EmptyValue implements Value {
	
	INSTANCE;
	
	public static EmptyValue instance() {
		return INSTANCE;
	}

	@Override
	public double getFigure() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String getText() {
		throw new UnsupportedOperationException();
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

	@Override
	public boolean isNumberLike() {
		return false;
	}
}
