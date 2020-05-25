package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Test;

import indexing.IndexBuilder;

public class TestIndexBuilder {

	/*
	 *  ################### [IMPORTANT NOTE FOR TA GRADING] ###################
	 * 
	 *  Before running this test file, make sure that the provided directory 
	 *  will be up and running at local host port 8090. 
	 *  
	 *  I.e. use the feed "http://localhost:8090/sample_rss_feed.xml" as set-up.
	 *  
	 *  Thanks for grading my work :) Enjoy!
	 *  
	 */
	
	@Test
	public void testParseFeed() {
		IndexBuilder a = new IndexBuilder();
		List<String> list = new ArrayList<String>();
		list.add("http://localhost:8090/sample_rss_feed.xml");
		
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		map = a.parseFeed(list);
		
		assertTrue(map.size() == 5);
		assertTrue(map.containsKey("http://localhost:8090/page1.html"));
		assertTrue(map.containsKey("http://localhost:8090/page2.html"));
		assertTrue(map.containsKey("http://localhost:8090/page3.html"));
		assertTrue(map.containsKey("http://localhost:8090/page4.html"));
		assertTrue(map.containsKey("http://localhost:8090/page5.html"));
		assertTrue(map.get("http://localhost:8090/page1.html").size() == 10);
		assertTrue(map.get("http://localhost:8090/page2.html").size() == 55);
		assertTrue(map.get("http://localhost:8090/page3.html").size() == 33);
		assertTrue(map.get("http://localhost:8090/page4.html").size() == 22);
		assertTrue(map.get("http://localhost:8090/page5.html").size() == 18);
	}
	
	@Test
	public void testBuildIndex() {
		IndexBuilder a = new IndexBuilder();
		List<String> list = new ArrayList<String>();
		list.add("http://localhost:8090/sample_rss_feed.xml");
		
		Map<String, List<String>> task2 = new HashMap<String, List<String>>();
		task2 = a.parseFeed(list);
		
		Map<String, Map<String, Double>> task3 = new TreeMap<>();
		task3 = a.buildIndex(task2);
		
		assertTrue(task3.size() == 5);
		assertTrue(task3.get("http://localhost:8090/page1.html").size() == 8);
		assertTrue(task3.get("http://localhost:8090/page2.html").size() == 40);
		assertTrue(task3.get("http://localhost:8090/page3.html").size() == 29);
		assertTrue(task3.get("http://localhost:8090/page4.html").size() == 21);
		assertTrue(task3.get("http://localhost:8090/page5.html").size() == 18);
		
		assertEquals(task3.get("http://localhost:8090/page1.html").get("data"), 0.1021, 0.001);
		assertEquals(task3.get("http://localhost:8090/page1.html").get("structures"), 0.183, 0.001);
		assertEquals(task3.get("http://localhost:8090/page1.html").get("linkedlist"), 0.091, 0.001);
		assertEquals(task3.get("http://localhost:8090/page1.html").get("stacks"), 0.091, 0.001);
		assertEquals(task3.get("http://localhost:8090/page1.html").get("lists"), 0.0916, 0.001);
		
		assertEquals(task3.get("http://localhost:8090/page2.html").get("data"), 0.046, 0.001);
		assertEquals(task3.get("http://localhost:8090/page2.html").get("structures"), 0.0666, 0.001);
		assertEquals(task3.get("http://localhost:8090/page2.html").get("binary"), 0.0499, 0.001);
		
		assertEquals(task3.get("http://localhost:8090/page3.html").get("java"), 0.0487, 0.001);
		assertEquals(task3.get("http://localhost:8090/page3.html").get("trees"), 0.0832, 0.001);
		assertEquals(task3.get("http://localhost:8090/page3.html").get("binary"), 0.0277, 0.001);
	}
	
	@Test
	public void testBuildInvertedIndex() {
		IndexBuilder a = new IndexBuilder();
		List<String> list = new ArrayList<String>();
		list.add("http://localhost:8090/sample_rss_feed.xml");
		
		Map<String, List<String>> task2 = new HashMap<String, List<String>>();
		task2 = a.parseFeed(list);
		
		Map<String, Map<String, Double>> task3 = new TreeMap<>();
		task3 = a.buildIndex(task2);
		
		Map<String, Set<Entry<String, Double>>> task4 = new HashMap<>(); 
		task4 = (Map<String, Set<Entry<String, Double>>>) a.buildInvertedIndex(task3);
		
				
		assertTrue(task4 instanceof Map);
		assertTrue(task4 instanceof HashMap);		
		
		for (Entry<String, Set<Entry<String, Double>>> e1 : task4.entrySet()) {
			if (e1.getKey().contentEquals("be")) {
				assertTrue(e1.getValue().size() == 1);
				for (Entry<String, Double> e2 : e1.getValue()) {
					if (e2.getKey().contentEquals("http://localhost:8090/page2.html")) {
						assertEquals(e2.getValue(), 0.029262, 0.001);
					}
				}
			}
			
			if (e1.getKey().contentEquals("to")) {
				assertTrue(e1.getValue().size() == 2);
				for (Entry<String, Double> e2 : e1.getValue()) {
					if (e2.getKey().contentEquals("http://localhost:8090/page2.html")) {
						assertEquals(e2.getValue(), 0.01665, 0.001);
					}
					
					if (e2.getKey().contentEquals("http://localhost:8090/page4.html")) {
						assertEquals(e2.getValue(), 0.041649, 0.001);
					}
				}
			}
		}
	}
	
	@Test
	public void testBuildHomePage() {
		IndexBuilder a = new IndexBuilder();
		List<String> list = new ArrayList<String>();
		list.add("http://localhost:8090/sample_rss_feed.xml");
		
		Map<String, List<String>> task2 = new HashMap<String, List<String>>();
		task2 = a.parseFeed(list);
		
		Map<String, Map<String, Double>> task3 = new TreeMap<>();
		task3 = a.buildIndex(task2);
		
		Map<String, Set<Entry<String, Double>>> task4 = new HashMap<>(); 
		task4 = (Map<String, Set<Entry<String, Double>>>) a.buildInvertedIndex(task3);
		
		ArrayList<Entry<String, List<String>>> task5 = new ArrayList<Entry<String, List<String>>>();
		task5 = (ArrayList<Entry<String, List<String>>>) a.buildHomePage(task4);
		
		assertTrue(task5 instanceof Collection);
		assertTrue(task5.get(0) instanceof Entry);
		assertTrue(task5.get(0).getKey() instanceof String);
		assertTrue(task5.get(0).getValue() instanceof List);
		assertTrue(task5.get(0).getValue().get(0) instanceof String);
		
		assertTrue(task5.get(0).getKey().contentEquals("data"));
		assertTrue(task5.get(0).getValue().size() == 3);
		assertTrue(task5.get(0).getValue().contains("http://localhost:8090/page1.html"));
		assertTrue(task5.get(0).getValue().contains("http://localhost:8090/page2.html"));
		assertTrue(task5.get(0).getValue().contains("http://localhost:8090/page3.html"));
		
		assertTrue(task5.get(1).getKey().contentEquals("trees"));
		assertTrue(task5.get(1).getValue().size() == 2);
		assertTrue(task5.get(1).getValue().contains("http://localhost:8090/page2.html"));
		assertTrue(task5.get(1).getValue().contains("http://localhost:8090/page3.html"));
		
		assertTrue(task5.get(2).getKey().contentEquals("structures"));
		assertTrue(task5.get(2).getValue().size() == 2);
		assertTrue(task5.get(2).getValue().contains("http://localhost:8090/page2.html"));
		assertTrue(task5.get(2).getValue().contains("http://localhost:8090/page1.html"));
		
		assertTrue(task5.get(3).getKey().contentEquals("stacks"));
		assertTrue(task5.get(3).getValue().size() == 2);
		assertTrue(task5.get(3).getValue().contains("http://localhost:8090/page2.html"));
		assertTrue(task5.get(3).getValue().contains("http://localhost:8090/page1.html"));
		
	}
	
	@Test
	public void testSearchArticles() {
		IndexBuilder a = new IndexBuilder();
		List<String> list = new ArrayList<String>();
		list.add("http://localhost:8090/sample_rss_feed.xml");
		
		Map<String, List<String>> task2 = new HashMap<String, List<String>>();
		task2 = a.parseFeed(list);
		
		Map<String, Map<String, Double>> task3 = new TreeMap<>();
		task3 = a.buildIndex(task2);
		
		HashMap<String, LinkedHashMap<String, Double>> task4 = new HashMap<>();
		task4 = (HashMap<String, LinkedHashMap<String, Double>>) a.buildInvertedIndex(task3);
		
		List<String> task6 = a.searchArticles("data", task4);
		assertTrue(task6.size() == 3);
		assertTrue(task6.contains("http://localhost:8090/page1.html"));
		assertTrue(task6.contains("http://localhost:8090/page2.html"));
		assertTrue(task6.contains("http://localhost:8090/page3.html"));
		assertTrue(!task6.contains("http://localhost:8090/page4.html"));
		assertTrue(!task6.contains("http://localhost:8090/page5.html"));
		
		task6 = a.searchArticles("allows", task4);
		assertTrue(task6.size() == 1);
		assertTrue(!task6.contains("http://localhost:8090/page1.html"));
		assertTrue(task6.contains("http://localhost:8090/page2.html"));
		
		task6 = a.searchArticles("allow", task4);
		assertTrue(task6 == null);
		
	}
}
