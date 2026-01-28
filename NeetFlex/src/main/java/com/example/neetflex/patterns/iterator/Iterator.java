package com.example.neetflex.patterns.iterator;

/**
 * Custom Iterator interface that defines the contract for iterating over collections.
 * @param <E> The type of elements this iterator returns.
 */
public interface Iterator<E> {

    /**
     * Returns true if the iteration has more elements.
     * @return true if the iteration has more elements, false otherwise.
     */
    boolean hasNext();

    /**
     * Returns the next element in the iteration.
     * @return the next element in the iteration.
     * @throws java.util.NoSuchElementException if the iteration has no more elements.
     */
    E next();

    /**
     * Removes from the underlying collection the last element returned by this iterator (Optional operation).
     * This method can be called only once per call to next().
     * @throws UnsupportedOperationException if the remove operation is not supported by this iterator.
     * @throws IllegalStateException if the next method has not yet been called, or the remove method has already
     *         been called after the last call to the next method.
     */
    default void remove() {
        throw new UnsupportedOperationException("Remove operation is not supported by default.");
    }
}