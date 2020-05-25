package autocomplete;
import java.util.Arrays;

public class Term implements ITerm {

	// fields
	private String query;
	private long weight;

	// constructor
	public Term(String query, long weight) {
		if (query == null || weight < 0) {
			throw new IllegalArgumentException();
		}
		this.query = query;
		this.weight = weight;
	}

	// methods

	/**
	 * Getters and Setters
	 */

	/**
	 * 
	 * @Description get the query of a term
	 * @author Leo Baiao Hou
	 * @date Apr 10, 20207:44:05 PM
	 * @return the query
	 */
	public String getTerm() {
		return query;
	}

	/**
	 * 
	 * @Description set the query of a term
	 * @author Leo Baiao Hou
	 * @date Apr 10, 20207:43:55 PM
	 * @param query to be set
	 */
	public void setTerm(String query) {
		this.query = query;
	}

	/**
	 * 
	 * @Description get the weight of term
	 * @author Leo Baiao Hou
	 * @date Apr 10, 20207:43:47 PM
	 * @return the weight
	 */
	public long getWeight() {
		return weight;
	}

	/**
	 * 
	 * @Description set the weight of a term
	 * @author Leo Baiao Hou
	 * @date Apr 10, 20207:43:38 PM
	 * @param weight to be set
	 */
	public void setWeight(long weight) {
		this.weight = weight;
	}

	/*
	 * Compares the two terms in lexicographic order by query.
	 */
	@Override
	public int compareTo(ITerm that) {

		return this.query.compareTo(((Term) that).getTerm());

	}

	/*
	 * convert to String
	 */
	@Override
	public String toString() {
		String s = this.weight + "\t" + this.query;
		return s;
	}

}
