/*
 * Copyright 2012-2014 Institut National des Sciences Appliquées de Lyon (INSA-Lyon)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gololang;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Represents an tuple object.
 * <p>
 * A tuple essentially behaves like an immutable array. In Golo, tuples can be created as follows:
 * <pre>
 * # Short syntax
 * let t1 = [1, 2, 3]
 *
 * # Complete collection literal syntax
 * let t2 = tuple[1, 2, 3]
 * </pre>
 */
public final class Tuple implements Iterable<Object>, HeadTail<Object>, Comparable<Tuple> {

  private final Object[] data;

  /**
   * Creates a new tuple from values.
   *
   * @param values the tuple values.
   */
  public Tuple(Object... values) {
    data = Arrays.copyOf(values, values.length);
  }

  /**
   * Helper factory method.
   *
   * @param values the values as an array.
   * @return a tuple from the array values.
   */
  public static Tuple fromArray(Object[] values) {
    return new Tuple(values);
  }

  /**
   * Gives the number of elements in this tuple.
   *
   * @return the tuple size.
   */
  public int size() {
    return data.length;
  }

  /**
   * Checks whether the tuple is empty or not.
   *
   * @return {@code true} if the tuple has no element, {@code false} otherwise.
   */
  @Override
  public boolean isEmpty() {
    return data.length == 0;
  }

  /**
   * Gets the element at a specified index.
   *
   * @param index the element index.
   * @return the element at index {@code index}.
   * @throws IndexOutOfBoundsException if the specified {@code index} is not valid (negative value or above the size).
   */
  public Object get(int index) {
    if (index < 0 || index >= data.length) {
      throw new IndexOutOfBoundsException(index + " is outside the bounds of a " + data.length + "-tuple");
    }
    return data[index];
  }

  /**
   * Creates an iterator over the tuple.
   * <p>The iterator does not support removal.
   *
   * @return an iterator.
   */
  @Override
  public Iterator<Object> iterator() {
    return new Iterator<Object>() {

      private int i = 0;

      @Override
      public boolean hasNext() {
        return i < data.length;
      }

      @Override
      public Object next() {
        Object result = data[i];
        i = i + 1;
        return result;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException("Tuples are immutable");
      }
    };
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Tuple tuple = (Tuple) o;
    return Arrays.equals(data, tuple.data);
  }

  /**
   * Compares this tuple with the specified tuple for order.
   * <p>Returns a negative integer, zero, or a positive integer as this tuple is less than, equal to, or greater than the specified tuple.
   * <p>Two tuples are compared using the lexicographical (dictionary) order, that is:
   * {@code [1, 2] < [1, 3]} and {@code [2, 5] < [3, 1]}.
   * <p> Two tuples are comparable if they have the same size and their elements are pairwise comparable.
   *
   * @param other the tuple to be compared.
   * @return a negative integer, zero, or a positive integer as this tuple is less than, equal to, or greater than the specified tuple.
   * @throws NullPointerException if the specified tuple is null
   * @throws ClassCastException  if the type of the elements in the specified tuple prevent them from being compared to this tuple elements.
   * @throws IllegalArgumentException if the specified tuple has a different size than this tuple.
   */
  @Override
  public int compareTo(Tuple other) {
    if (this.equals(other)) {
      return 0;
    }
    if (this.size() != other.size()) {
      throw new IllegalArgumentException(String.format(
            "%s and %s can't be compared since of different size", this, other));
    }
    for (int i = 0; i < size(); i++) {
      if (!this.get(i).equals(other.get(i))) {
        @SuppressWarnings("unchecked")
        Comparable<Object> current = (Comparable<Object>) this.get(i);
        return current.compareTo(other.get(i));
      }
    }
    return 0;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(data);
  }

  @Override
  public String toString() {
    return "tuple" + Arrays.toString(data);
  }

  /**
   * Returns the first element of the tuple.
   *
   * @return the first element.
   */
  @Override
  public Object head() {
    if (this.isEmpty()) {
      return null;
    }
    return this.get(0);
  }

  /**
   * Returns a new tuple containg the remaining elements.
   *
   * @return a tuple.
   */
  @Override
  public Tuple tail() {
    if (this.isEmpty()) {
      // we can return this since a Tuple is immutable.
      return this;
    }
    return fromArray(Arrays.copyOfRange(data, 1, data.length));
  }
}
