package eu.odalic.extrarelatable.model.bag;

abstract class AbstractValue implements Value {
	public abstract String getText();
	
	public boolean isEmpty() {
		return false;
	}
	
	public boolean isNumeric() {
		return false;
	}
	
	public boolean isTextual() {
		return false;
	}
	
	public boolean isInstant() {
		return false;
	}
	
	public boolean isEntity() {
		return false;
	}
	
	public boolean isId() {
		return false;
	}
	
	public boolean isUnit() {
		return false;
	}
}
