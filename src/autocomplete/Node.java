package autocomplete;
import java.util.Arrays;
import java.util.HashMap;

/**
 * ==== Attributes ==== - words: number of words - term: the ITerm object -
 * prefixes: number of prefixes - references: Array of references to
 * next/children Nodes
 * 
 * ==== Constructor ==== Node(String word, long weight)
 * 
 * @author Baiao Hou
 */
public class Node {
	// attributes
	private int words = 0;
	private Term term;
	private int prefixes = 0;
	private Node[] references;

	// constructors
	public Node() {
		this.term = null;
		this.words = 0;
		this.prefixes = 0;
		this.references = new Node[26];

	}

	public Node(String query, long weight) {
		if (query == null || weight < 0) {
			throw new IllegalArgumentException("Error: invalid query or weight.");
		}
		this.term = new Term(query, weight);
		this.words = 0;
		this.prefixes = 0;
		this.references = new Node[26];
	}

	// methods

	/**
	 * Getters and Setters
	 */

	/**
	 * 
	 * @Description get the words count of a node
	 * @author Leo Baiao Hou
	 * @date Apr 10, 20207:40:25 PM
	 * @return the words count
	 */
	public int getWords() {
		return words;
	}

	/**
	 * 
	 * @Description set the words count
	 * @author Leo Baiao Hou
	 * @date Apr 10, 20207:40:44 PM
	 * @param words to be set
	 */
	public void setWords(int words) {
		this.words = words;
	}

	/**
	 * 
	 * @Description get the term of the node
	 * @author Leo Baiao Hou
	 * @date Apr 10, 20207:40:56 PM
	 * @return the term
	 */
	public Term getTerm() {
		return term;
	}

	/**
	 * 
	 * @Description set the term of the node
	 * @author Leo Baiao Hou
	 * @date Apr 10, 20207:41:18 PM
	 * @param term to be set
	 */
	public void setTerm(Term term) {
		this.term = term;
	}

	/**
	 * 
	 * @Description get the prefixes count of the node
	 * @author Leo Baiao Hou
	 * @date Apr 10, 20207:41:30 PM
	 * @return the prefixes count
	 */
	public int getPrefixes() {
		return prefixes;
	}

	/**
	 * 
	 * @Description set the prefixes count of the node
	 * @author Leo Baiao Hou
	 * @date Apr 10, 20207:41:44 PM
	 * @param prefixes to be set
	 */
	public void setPrefixes(int prefixes) {
		this.prefixes = prefixes;
	}

	/**
	 * 
	 * @Description get the references Node array
	 * @author Leo Baiao Hou
	 * @date Apr 10, 20207:41:55 PM
	 * @return Node array
	 */
	public Node[] getReferences() {
		return references;
	}

	/**
	 * 
	 * @Description set the reference Node array
	 * @author Leo Baiao Hou
	 * @date Apr 10, 20207:42:09 PM
	 * @param references array to be set
	 */
	public void setReferences(Node[] references) {
		this.references = references;
	}

	/**
	 * 
	 * @Description Determine if a Node is leaf or not
	 * @author Leo Baiao Hou
	 * @date Apr 10, 20207:42:26 PM
	 * @return true if it's a leaf. Return false otherwise.
	 */
	public boolean isLeaf() {
		// return references.size() == 0;
		for (Node Node : this.references) {
			if (Node != null) {
				return false;
			}
		}
		return true;
	}

	/**
	 *  
	 * @Description get the weight of this Node
	 * @author Leo Baiao Hou
	 * @date Apr 10, 20207:42:45 PM
	 * @return the weight
	 */
	public long getWeight() {
		return this.term.getWeight();
	}

	/**
	 * 
	 * @Description set the weight of this node
	 * @author Leo Baiao Hou
	 * @date Apr 10, 20207:42:55 PM
	 * @param weight to be set
	 */
	public void setWeight(long weight) {
		this.term.setWeight(weight);
	}

	/**
	 * Convert into string
	 */
	@Override
	public String toString() {
		if (this.getTerm() == null) {
			return null;
		}
		String s = this.getTerm().getWeight() + "\t" + this.getTerm().getTerm();
		return s;
	}

}
