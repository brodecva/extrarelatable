package eu.odalic.extrarelatable.model.bag;

public interface Value {
	String getText();
	
	double getFigure();
	
	boolean isEmpty();
	
	boolean isNumeric();
	
	boolean isTextual();
	
	boolean isInstant();
	
	boolean isEntity();
	
	boolean isId();
	
	boolean isUnit();
	
	boolean isNumberLike();
}
