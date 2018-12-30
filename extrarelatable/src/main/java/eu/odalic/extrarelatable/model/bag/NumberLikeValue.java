package eu.odalic.extrarelatable.model.bag;

/**
 * Sub-type of {@link AbstractValue} which instances can be naturally converted
 * to a number. Extending classes are required to override the
 * {@link AbstractValue#getFigure()} method to make it always return a double
 * value.
 * 
 * @author Václav Brodec
 *
 */
public abstract class NumberLikeValue extends AbstractValue {
	public boolean isNumberLike() {
		return true;
	}
}
