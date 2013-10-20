package io.mem0r1es.activitysubsumer.wordnet;

import java.util.HashSet;
import java.util.Set;

/**
 * Node in the WordNet sub-graphs. A node contains multiple words (with senses) based on synonym
 * sets and on eventual cycles in the WordNet graph.
 * 
 * @author horiaradu
 */
public class WordNetNode {
	/**
	 * set of synonyms
	 */
	private Set<String> words = new HashSet<String>();

	public WordNetNode(String... words) {
		for (String word : words) {
			this.words.add(word);
		}
	}

	public WordNetNode(Set<String> words) {
		this.words.addAll(words);
	}

	public void addWords(String... words) {
		for (String word : words) {
			this.words.add(word);
		}
	}

	public boolean synonyms(WordNetNode other) {
		for (String word : words) {
			if (other.words.contains(word)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((words == null) ? 0 : words.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WordNetNode other = (WordNetNode) obj;
		if (words == null) {
			if (other.words != null)
				return false;
		} else if (!words.equals(other.words))
			return false;
		return true;
	}

	public boolean hasWord(String word) {
		return words.contains(word);
	}

	public Set<String> getWords() {
		return words;
	}

	@Override
	public String toString() {
		return words.toString();
	}
}