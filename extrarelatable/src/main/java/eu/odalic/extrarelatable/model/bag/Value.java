package eu.odalic.extrarelatable.model.bag;

public interface Value {
	String getText();
	
	boolean isEmpty();
	
	boolean isNumeric();
	
	boolean isTextual();
	
	boolean isInstant();
	
	boolean isEntity();
	
	boolean isId();
	
	boolean isUnit();
}
