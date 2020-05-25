package indexing;

import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class IndexBuilder implements IIndexBuilder {

	/**
	 * <parseFeed> Parse each document/rss feed in the list and return a Map of each
	 * document and all the words in it. (punctuation and special characters
	 * removed)
	 * 
	 * @param feeds a List of rss feeds to parse
	 * @return a Map of each documents (identified by its url) and the list of words
	 *         in it.
	 */

	@Override
	public Map<String, List<String>> parseFeed(List<String> feeds) {
		Map<String, List<String>> ret = new HashMap<String, List<String>>();
		for (String url : feeds) {
			try {
				Document doc = Jsoup.connect(url).get();
				Elements links = doc.getElementsByTag("link");
				for (Element link : links) {
					List<String> retList = new ArrayList<String>();
					String linkStr = null;
					String linkText = link.text();
					linkStr = linkText;
					Document innerDoc = Jsoup.connect(linkText).get();
					String bodyString = innerDoc.getElementsByTag("body").text();
					bodyString = bodyString.replaceAll("[^a-zA-Z0-9 ]", "");
					bodyString = bodyString.toLowerCase();
					String[] arr = bodyString.split(" ");
					for (String s : arr) {
						if (s == null || s.length() == 0)
							continue;
						if (Character.isDigit(s.charAt(0)))
							continue;
						retList.add(s);
					}
					ret.put(linkStr, retList);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	/**
	 * @param docs a map computed by {@parseFeed}
	 * @return the forward index: a map of all documents and their tags/keywords.
	 *         the key is the document, the value is a map of a tag term and its
	 *         TFIDF value. The values (Map<String, Double>) are sorted by
	 *         lexicographic order on the key (tag term)
	 * 
	 */

	@Override
	public Map<String, Map<String, Double>> buildIndex(Map<String, List<String>> docs) {
		Map<String, Map<String, Double>> ret = new TreeMap<>();

		for (String docName : docs.keySet()) {
			Map<String, Double> innerMap = new TreeMap<String, Double>();
			List<String> wordsInCurrDoc = new ArrayList<String>();
			wordsInCurrDoc = docs.get(docName);
			for (String word : wordsInCurrDoc) {
				if (!innerMap.containsKey(word)) {
					// does not appear in map. Need to be added
					int count = 0;
					for (String w : wordsInCurrDoc) {
						if (w.equals(word)) {
							count++;
						}
					}
					double TF = (double) count / (double) wordsInCurrDoc.size();
					int countDocWithCurrWord = 0;
					for (String currDoc : docs.keySet()) {
						if (docs.get(currDoc).contains(word)) {
							countDocWithCurrWord++;
						}
					}
					double IDF = Math.log((double) docs.size() / (double) countDocWithCurrWord);
					double TFIDF = TF * IDF;

					innerMap.put(word, TFIDF);
				}
			}
			ret.put(docName, innerMap);
		}

		return ret;
	}

	/**
	 * Build an inverted index consisting of a map of each tag term and a Collection
	 * (Java) of Entry objects mapping a document with the TFIDF value of the term
	 * (for that document) The Java collection (value) is sorted by reverse tag term
	 * TFIDF value (the document in which a term has the highest TFIDF should be
	 * listed first).
	 * 
	 * 
	 * @param index the index computed by {@buildIndex}
	 * @return inverted index - a sorted Map of the documents in which term is a
	 *         keyword
	 */

	@Override
	public Map<?, ?> buildInvertedIndex(Map<String, Map<String, Double>> index) {
		HashMap<String, LinkedHashMap<String, Double>> ret = new HashMap();
		for (String document : index.keySet()) {
			for (String documentWord : index.get(document).keySet()) {
				double value = index.get(document).get(documentWord);
				if (!ret.containsKey(documentWord)) {
					// come first time
					LinkedHashMap<String, Double> map = new LinkedHashMap<String, Double>();
					map.put(document, value);
					ret.put(documentWord, map);
				} else {
					ret.get(documentWord).put(document, value);
					List<Map.Entry<String, Double>> l = new ArrayList<Map.Entry<String, Double>>(
							ret.get(documentWord).entrySet());
					Collections.sort(l, new Comparator<Map.Entry<String, Double>>() {
						@Override
						public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
							return o2.getValue().compareTo(o1.getValue()); // descending order
						}
					});

					ret.get(documentWord).clear();
					for (Entry<String, Double> t : l) {
						ret.get(documentWord).put(t.getKey(), t.getValue());
					}
				}
			}
		}
		Comparator<Entry<String, Double>> comparator = new Comparator<Entry<String, Double>>() {
			@Override
			public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
				if (o2.getKey().equals(o1.getKey())) {
					return 0;
				}
				if (o2.getValue() - o1.getValue() == 0) {
					return 0;
				}
				if (o2.getValue() - o1.getValue() < 0) {
					return -1;
				}
				return 1;
			}
		};

		Map<String, Set<Entry<String, Double>>> returnMap = new HashMap<>();

		for (Entry<String, LinkedHashMap<String, Double>> entry : ret.entrySet()) {
			for (Entry<String, Double> e : entry.getValue().entrySet()) {
				if (returnMap.containsKey(entry.getKey()) == false) { // first time appear
					Set<Entry<String, Double>> temp = new TreeSet<Entry<String, Double>>(comparator);
					returnMap.put(entry.getKey(), temp);
				}
				// entry.getKey : word
				// entry.getValue : addresses set
				// e.getKey : each addr
				// e.getValue : weight
				Map<String, Double> map = index.get(e.getKey());
				returnMap.get(entry.getKey())
						.add(new AbstractMap.SimpleEntry<String, Double>(e.getKey(), map.get(entry.getKey())));
			}
		}
		return returnMap;
	}

	/**
	 * @param invertedIndex
	 * @return a sorted collection of terms and articles Entries are sorted by
	 *         number of articles. If two terms have the same number of articles,
	 *         then they should be sorted by reverse lexicographic order. The Entry
	 *         class is the Java abstract data type implementation of a tuple
	 *         https://docs.oracle.com/javase/9/docs/api/java/util/Map.Entry.html
	 *         One useful implementation class of Entry is AbstractMap.SimpleEntry
	 *         https://docs.oracle.com/javase/9/docs/api/java/util/AbstractMap.SimpleEntry.html
	 */

	@Override
	public Collection<Entry<String, List<String>>> buildHomePage(Map<?, ?> invertedIndex) {
		ArrayList<Entry<String, List<String>>> ret = new ArrayList<Entry<String, List<String>>>();

		// cast
		// from -> Map<String, Set<Entry<String, Double>>>
		// to -> HashMap<String, LinkedHashMap<String, Double>>
		HashMap<String, LinkedHashMap<String, Double>> converted = new HashMap<>();

		for (Entry<?, ?> entry : invertedIndex.entrySet()) {
			Set<Entry<String, Double>> currSet = new TreeSet<>();
			currSet = (Set<Entry<String, Double>>) entry.getValue();

			LinkedHashMap<String, Double> inner = new LinkedHashMap<String, Double>();
			for (Entry<String, Double> e : currSet) {
				inner.put(e.getKey(), e.getValue());
			}

			converted.put((String) entry.getKey(), inner);
		}

		// freq : list of words @ this freq
		TreeMap<String, Integer> freq = new TreeMap<String, Integer>();

		for (Object o : invertedIndex.keySet()) {
			String currWord = (String) o;
			Integer currFreq = ((HashMap<String, LinkedHashMap<String, Double>>) (converted)).get(currWord).size(); //
			freq.put(currWord, currFreq);
		}

		List<Entry<String, Integer>> listFreq = new ArrayList<Entry<String, Integer>>(freq.entrySet());
		Collections.sort(listFreq, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				if (o2.getValue() - o1.getValue() == 0) {
					return (o2.getKey().compareTo(o1.getKey()));
				}
				return (o2.getValue() - o1.getValue());
			}
		});

		for (Entry<String, Integer> t : listFreq) {
			// check if it is stopwords
			if (IIndexBuilder.STOPWORDS.contains(t.getKey())) {
				continue;
			}

			List<String> docList = new ArrayList<>();
			HashMap<String, LinkedHashMap<String, Double>> temp = (HashMap<String, LinkedHashMap<String, Double>>) converted; //
			LinkedHashMap<String, Double> tempMap = temp.get(t.getKey());
			ArrayList<String> docsOnThisWord = new ArrayList<String>();
			docsOnThisWord.addAll(tempMap.keySet());
			Map.Entry<String, List<String>> entry = new AbstractMap.SimpleEntry<String, List<String>>(t.getKey(),
					docsOnThisWord);
			ret.add(entry);
		}

		return ret;
	}

	/**
	 * Create a file containing all the words in the inverted index. Each word
	 * should occupy a line Words should be written in lexicographic order assign a
	 * weight of 0 to each word. The method must store the words into a file named
	 * autocomplete.txt
	 * 
	 * @param homepage the collection used to generate the homepage (buildHomePage)
	 * @return A collection containing all the words written into the file sorted by
	 *         lexicographic order
	 */

	@Override
	public Collection<?> createAutocompleteFile(Collection<Entry<String, List<String>>> homepage) {
		TreeSet<String> ret = new TreeSet<String>();

		ArrayList<Entry<String, List<String>>> task5 = new ArrayList<Entry<String, List<String>>>();
		task5 = (ArrayList<Entry<String, List<String>>>) homepage;
		int size = task5.size();
		String fileContent = "" + size + "\n";
		for (Entry<String, List<String>> e : homepage) {
			ret.add(e.getKey());
		}
		for (String word : ret) {
			fileContent = fileContent + "  0  " + word + "\n";
		}

		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter("autocomplete.txt");
			fileWriter.write(fileContent);
			fileWriter.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return ret;
	}

	/**
	 * The users should be able to enter a query term and our news aggregator will
	 * return all the articles related (tagged) to that term. The relevant articles
	 * are retrieved from the inverted index.
	 * 
	 * @param queryTerm
	 * @param invertedIndex
	 * @return return all the articles related (tagged) to that term
	 */
	
	@Override
	public List<String> searchArticles(String queryTerm, Map<?, ?> invertedIndex) {
		if (queryTerm == null || queryTerm.length() == 0)
			return null;

		// cast
		// from -> Map<String, Set<Entry<String, Double>>>
		// to -> HashMap<String, LinkedHashMap<String, Double>>
		HashMap<String, LinkedHashMap<String, Double>> task4 = new HashMap<>();

		for (Entry<?, ?> entry : invertedIndex.entrySet()) {
			Set<Entry<String, Double>> currSet = new TreeSet<>();
			currSet = (Set<Entry<String, Double>>) entry.getValue();

			LinkedHashMap<String, Double> inner = new LinkedHashMap<String, Double>();
			for (Entry<String, Double> e : currSet) {
				inner.put(e.getKey(), e.getValue());
			}
			task4.put((String) entry.getKey(), inner);
		}
		if (task4.containsKey(queryTerm)) {
			List<String> ret = new ArrayList<String>();
			ret.addAll(task4.get(queryTerm).keySet());
			return ret;
		}

		return null;
	}

}
