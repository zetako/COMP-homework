package zetako;

import java.util.Objects;

public class Pair<T1, T2> {
    public T1 first;
    public T2 second;
    public Pair(T1 _first, T2 _second) {
		first  = _first;
        second = _second;
    }

    @Override
	public boolean equals(Object other) {
		Boolean result = false;
        if (other instanceof Pair<?, ?>) {
            Pair<T1, T2> otherPair = (Pair<T1, T2>) other;
        	result = first.equals(otherPair.first) && second.equals(otherPair.second);
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}