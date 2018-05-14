package eu.odalic.extrarelatable.util;

import java.lang.reflect.Array;

/**
 * Utility class for -- you guessed it -- working with arrays.
 *
 * @author Václav Brodec
 *
 */
public final class Arrays {

  /**
   * Creates a deep copy of a matrix (up to second dimension).
   *
   * @param type type of elements
   * @param matrix the matrix
   * @return deep copy of the matrix
   * @throws IllegalArgumentException if either of dimensions of the matrix is zero, if the type of
   *         elements is Void.TYPE, or if the number of any dimension of the matrix exceeds 255.
   *
   * @param <T> type of elements
   */
  public static <T> T[][] deepCopy(final Class<T> type, final T[][] matrix)
      throws IllegalArgumentException {
    final int rowsCount = matrix.length;
    final int columnsCount = (rowsCount > 0) ? (matrix[0].length) : 0;

    @SuppressWarnings("unchecked")
    final T[][] copy = (T[][]) Array.newInstance(type, rowsCount, columnsCount);
    for (int i = 0; i < rowsCount; i++) {
      copy[i] = matrix[i].clone();
    }

    return copy;
  }

  /**
   * We want to keep this class uninstantiable, so no visible constructor is available.
   */
  private Arrays() {}

}
