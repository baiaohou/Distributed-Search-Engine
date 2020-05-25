package autocomplete;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Autocomplete implements IAutocomplete {

	private Node root = new Node();
	private int numSuggestions;

	
	/**
	 * Adds a new word with its associated weight to the Trie
	 * 
	 * @param word   the word to be added to the Trie
	 * @param weight the weight of the word
	 */
	@Override
	public void addWord(String word, long weight) {
		
		if (this.root == null) {
			this.root = new Node();
		}
		
		if (word == null || word.equals("") || weight < 0) {
			return;
		}
		
		boolean isWord = word.matches("[a-zA-Z]+");
		if (isWord == false) {
			return;
		}
		
		word = word.toLowerCase();

		Node currPointer = this.root;

		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			Node temp = null;
			
			if (currPointer == null) {
				return;
			}
			
			if ((currPointer.getReferences())[(int) c - 97] == null) {
				// if sub-node is null, we need manually create it
				temp = new Node(); // temp = new Node(); 
				temp.setPrefixes(1);
				temp.setWords(0);
				(currPointer.getReferences())[(int) c - 97] = temp;

				if (i == word.length() - 1) {
					// reaching the end of word
					(currPointer.getReferences())[(int) c - 97].setWords(1);
					(currPointer.getReferences())[(int) c - 97].setTerm(new Term(word, weight));
					break;
				}

				// update pointer
				currPointer = (currPointer.getReferences())[(int) c - 97];
			} else {
				// if sub-node isn't null, we need update its attributes
				if (i == 0) {
					// update pointer
					currPointer = (currPointer.getReferences())[(int) c - 97];
					currPointer.setPrefixes(currPointer.getPrefixes() + 1);

					if (i == word.length() - 1) {
						currPointer.setWords(currPointer.getWords() + 1);
						currPointer.setTerm(new Term(word, weight));
					}
				}

				if (i != 0) {
					(currPointer.getReferences())[(int) c - 97]
							.setPrefixes((currPointer.getReferences())[(int) c - 97].getPrefixes() + 1);
				}

				if (i != 0 && i == word.length() - 1) {
					// reaching the end of word
					(currPointer.getReferences())[(int) c - 97]
							.setWords((currPointer.getReferences())[(int) c - 97].getWords() + 1);
					(currPointer.getReferences())[(int) c - 97].setTerm(new Term(word, weight));
					break;
				}

				if (i != 0) {
					// update pointer
					currPointer = (currPointer.getReferences())[(int) c - 97];
				}
			}
		}
		this.root.setPrefixes(this.root.getPrefixes() + 1);
	}
	

	/**
	 * Initializes the Trie
	 *
	 * @param filename the file to read all the autocomplete data from each line
	 *                 contains a word and its weight This method will call the
	 *                 addWord method
	 * @param k        the maximum number of suggestions that should be displayed
	 * @return the root of the Trie You might find the readLine() method in
	 *         BufferedReader useful in this situation as it will allow you to read
	 *         a file one line at a time.
	 */
	@Override
	public Node buildTrie(String filename, int k) {
		this.root = new Node();
		this.numSuggestions = k;
		ArrayList<String> lines = new ArrayList<String>();

		// Read file
		File f = new File(filename);
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		while (true) {
			try {
				String s = bufferedReader.readLine();
				if (s == null) {
					break;
				}
				lines.add(s);
				continue;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Node root = new Node(); // Node root = new Node();
		this.root = root;
		
		int totalWordCount = 0;

		// processing lines array
		String[] temp = lines.get(0).split("\\s+");
		String firstNumber = lines.remove(0); ///
		int firstNum = Integer.parseInt(firstNumber);
		for (String s : lines) {
			String[] splited = s.split("\\s+");
			if (splited.length == 1) {
				break;
			}
			
			boolean isWord = (splited[2]).matches("[a-zA-Z]+");
			if (isWord == false) {
				continue;
			}
			
			this.addWord(splited[2], Long.parseLong(splited[1]));
			totalWordCount++;
		}
		root.setPrefixes(totalWordCount);
		return root;
	}



	/**
	 * @return k the the maximum number of suggestions that should be displayed
	 */
	@Override
	public int numberSuggestions() {
		return this.numSuggestions;
	}

	/**
	 * @param prefix
	 * @return the root of the subTrie corresponding to the last character of the
	 *         prefix.
	 */
	@Override
	public Node getSubTrie(String prefix) {
		
		if (prefix == null) {
			return null;
		}
		
		if (prefix.equals("")) {
			return this.root;
		}
		
		prefix = prefix.toLowerCase();
		int firstNodeIndex = ((int) (prefix.charAt(0))) - 97;
		
		boolean isWord = prefix.matches("[a-zA-Z]+");
		if (isWord == false) {
			return null;
		}
		
		if (this.root == null) {
			return null;
		}
		Node pointer = this.root.getReferences()[firstNodeIndex];
		
		if (prefix.length() == 1) {
			return pointer;
		}
		
		for (int i = 1; i < prefix.length(); i++) {
			int currIndex = ((int) (prefix.charAt(i))) - 97;
			
			if (pointer == null) {
				return null;
			}
			
			pointer = pointer.getReferences()[currIndex];

		}
		return pointer;
	}

	/**
	 * @param prefix
	 * @return the number of words that start with prefix.
	 */
	@Override
	public int countPrefixes(String prefix) {
		if (prefix == null) {
			return 0;
		}
		
		if (prefix.contentEquals("")) {
			return this.root.getPrefixes();
		}
		
		boolean isWord = prefix.matches("[a-zA-Z]+");
		if (isWord == false) {
			return 0;
		}
		
		prefix = prefix.toLowerCase();
		int firstNodeIndex = ((int) (prefix.charAt(0))) - 97;
		
		if (this.root == null) {
			return 0;
			
		}
		
		Node pointer = this.root.getReferences()[firstNodeIndex];
		
		if (pointer == null) {
			return 0;
		}
		
		if (prefix.length() == 1) {
			return pointer.getPrefixes();
		}
		
		for (int i = 1; i < prefix.length(); i++) {
			int currIndex = ((int) (prefix.charAt(i))) - 97;
			pointer = pointer.getReferences()[currIndex];
			if (pointer == null) {
				return 0;
			}
		}
		return pointer.getPrefixes();
	}

	/**
	 * This method should not throw an exception
	 * 
	 * @param prefix
	 * @return a List containing all the ITerm objects with query starting with
	 *         prefix. Return an empty list if there are no ITerm object starting
	 *         with prefix.
	 */
	@Override
	public List<ITerm> getSuggestions(String prefix) {
		prefix = prefix.toLowerCase();
		List<ITerm> ret = new ArrayList<ITerm>();
		Node fakeRoot = this.getSubTrie(prefix);
		
		if (fakeRoot == null) {
			if (prefix.equals("") ) {
				return ret;
			}
			return ret;
		}
		
		// starting from this node and do BFS
		Queue<Node> q = new LinkedList<Node>();
		q.add(fakeRoot);

		while (!q.isEmpty()) {
			Node n = q.remove();
				for (Node node : n.getReferences()) {
					if (node != null) {
						q.add(node);
					}
				}
				if (n.getPrefixes() != 0 && n.getWords() != 0) {
					ret.add((ITerm) n.getTerm());
				}
		}
		ret.sort(ITerm.byPrefixOrder(1000));
		return ret;
	}
	
	/**
	 * get the root of tree
	 */
	public Node getRoot() {
		return this.root;
	}

}
