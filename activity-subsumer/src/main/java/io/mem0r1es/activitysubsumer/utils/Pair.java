package io.mem0r1es.activitysubsumer.utils;

/**
 * @author Sebastian Claici
 */
public class Pair<S, T> {
	public S first;
	public T second;

	public Pair(S first, T second) {
		this.first = first;
		this.second = second;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Pair pair = (Pair) o;

		if (!first.equals(pair.first))
			return false;
		if (!second.equals(pair.second))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = first.hashCode();
		result = 31 * result + second.hashCode();
		return result;
	}
}
