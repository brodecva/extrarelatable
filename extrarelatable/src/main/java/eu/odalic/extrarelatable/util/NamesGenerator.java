package eu.odalic.extrarelatable.util;

/**
 * Generator of unique names (within one running application instance).
 * 
 * @author Václav Brodec
 *
 */
public interface NamesGenerator {
	/**
	 * Generates a new name unique within one running application instance.
	 * 
	 * @return newly generated name
	 */
	String generate();
}
