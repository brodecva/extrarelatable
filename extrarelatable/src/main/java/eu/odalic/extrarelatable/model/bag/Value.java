package eu.odalic.extrarelatable.model.bag;

public interface Value {
	boolean isEmpty();
	
	boolean isNumeric();
	
	boolean isTextual();
	
	boolean isInstant();
}
