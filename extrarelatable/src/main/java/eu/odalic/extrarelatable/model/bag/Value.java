package eu.odalic.extrarelatable.model.bag;

/**
 * Table cell value. It can be of various data types which are indicated by
 * present getter isXXX methods. Number-like values can be reduces to their
 * {@link Double} representation using {@link #getFigure()} method.
 * 
 * @author Václav Brodec
 *
 */
public interface Value {
	/**
	 * @return the text representation of the value
	 */
	String getText();

	/**
	 * @return double representation of a {@link NumberLikeValue} value,
	 * @throws UnsupportedOperationException
	 *             for {@link Value} instances not extending {@link NumberLikeValue}
	 */
	double getFigure();

	/**
	 * @return whether the value is {@link EmptyValue}
	 */
	boolean isEmpty();

	/**
	 * @return whether the value is {@link NumericValue}
	 */
	boolean isNumeric();

	/**
	 * @return whether the value is {@link TextValue}
	 */
	boolean isTextual();

	/**
	 * @return whether the value is {@link InstantValue}
	 */
	boolean isInstant();

	/**
	 * @return whether the value is {@link EntityValue}
	 */
	boolean isEntity();

	/**
	 * @return whether the value is {@link IdValue}
	 */
	boolean isId();

	/**
	 * @return whether the value is {@link UnitValue}
	 */
	boolean isUnit();

	/**
	 * @return whether the value is extending {@link NumberLikeValue}
	 */
	boolean isNumberLike();
}
