package zetako;

import java.util.Objects;

/** 
 * implement of pair
 * @author zetako
 * @version 1.0
 */
public class Pair<T1, T2> {
    /**
     * first element of pair
     */
    public T1 first;
    /**
     * second element of pair
     */
    public T2 second;

    /**
     * Constructor, simply set values
     * @param _first first value to set
     * @param _second second value to set
     */
    public Pair(T1 _first, T2 _second) {
		first  = _first;
        second = _second;
    }

    /**
     * Overrided equals function
     * @param other another object
     * @return return true if equals
     */
    @Override
	public boolean equals(Object other) {
		Boolean result = false;
        if (other instanceof Pair<?, ?>) {
            Pair<T1, T2> otherPair = (Pair<T1, T2>) other;
        	result = first.equals(otherPair.first) && second.equals(otherPair.second);
        }
        return result;
    }

    /**
     * Overrided hashCode function
     * @return return the calculated hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}