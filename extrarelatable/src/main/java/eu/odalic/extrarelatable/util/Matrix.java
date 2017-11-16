package eu.odalic.extrarelatable.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.collect.ImmutableList;

public final class Matrix {

	/**
	 * <p>
	 * Checks whether the list of lists is a matrix (all the outer lists (rows)
	 * have the same number of cells).
	 * </p>
	 *
	 * <p>
	 * Please note that the method does not expect the elements of the nested
	 * lists to be also lists (in other words: it does not go beyond the second
	 * dimension).
	 * </p>
	 *
	 * <p>
	 * Also it accepts empty list of lists and lists of zero-length lists as a
	 * valid matrix.
	 * </p>
	 *
	 * @param listOfLists
	 *            list of lists
	 * @return true, if the array of arrays is a matrix (up to second dimension)
	 */
	public static <E> boolean isMatrix(final List<? extends List<? extends E>> listOfLists) {
		checkNotNull(listOfLists);
		
		if (listOfLists.isEmpty()) {
			return true;
		}

		final int columnsCount = listOfLists.get(0).size();
		for (final List<? extends E> list : listOfLists) {
			if (list.size() != columnsCount) {
				return false;
			}
		}

		return true;
	}
	
	public static <E> boolean isMatrix(final E[][] listOfLists) {
		checkNotNull(listOfLists);
		
		if (listOfLists.length == 0) {
			return true;
		}

		final int columnsCount = listOfLists[0].length;
		for (final E[] list : listOfLists) {
			if (list.length != columnsCount) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Transposes the matrix. The result is an immutable list of rows represented as lists.
	 * 
	 * @param matrix rectangular list of lists
	 * @return translated matrix
	 */
	public static <E> List<List<E>> transpose(final List<? extends List<? extends E>> matrix) {
		checkNotNull(matrix);
		checkArgument(isMatrix(matrix));
		
		if (matrix.isEmpty()) {
			return ImmutableList.of();
		}
		
		if (matrix.size() == 1) {
			return ImmutableList.of(ImmutableList.copyOf(matrix.get(0)));
		}
		
		final ImmutableList.Builder<List<E>> rowsBuilder = ImmutableList.builder();

		final int rowsCount = matrix.size();
		final int columnsCount = matrix.get(0).size();
		for (int columnIndex = 0; columnIndex < columnsCount; columnIndex++) {
			final ImmutableList.Builder<E> rowBuilder = ImmutableList.builder();
			
			for (int rowIndex = 0; rowIndex < rowsCount; rowIndex++) {
				rowBuilder.add(matrix.get(rowIndex).get(columnIndex));
			}
			
			rowsBuilder.add(rowBuilder.build());
		}
		
		return rowsBuilder.build();
	}
	
	/**
	 * Makes a copy of the list of lists. The copy is an immutable list of immutable lists.
	 * 
	 * @param listOfLists
	 * @return copy
	 */
	public static <E> List<List<E>> copy(final List<? extends List<? extends E>> listOfLists) {
		final ImmutableList.Builder<List<E>> builder = ImmutableList.builder();
		for (final List<? extends E> list : listOfLists) {
			checkNotNull(list);
			
			builder.add(ImmutableList.copyOf(list));
		}
		
		return builder.build();
	}
	
	private Matrix() {}
}
