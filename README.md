# Distributed Search Engine

### Author: Leo Baiao Hou ***(Implemented in Java)***

Many people get their news from online news aggregators and search engines. Those sites can return very different results as shown in the above pictures. News aggregators sites get their content from media outlets websites and often different news sites use different sources. This project is implementation of a news search engine from top media websites with autocomplete capability (Mercator-style crawler, indexer/TF-IDF retrieval engine, pagerank, result quality optimizer).

## Overview

**Algorithms:** The algorithms used to rank news articles. Also, news sites can automatically collect data about the user (like the location) and use that information to customize what is presented to users.

**Personalization:** User personalization as many news sites allow the user to specify their interests.

A news aggregator is a type of search engine and it has three components:

  **crawler:** downloading web page content that we wish to search for.

  **indexer:** taking downloaded pages and create an inverted index.

  **retrieval:** answering a user’s query by talking to the user’s browser. The browser will show the search results and allow the user to interact with the web. 


This project first parses RSS and HTML documents, that is to implement the crawler. RSS is a Web content syndication format and it stands for Really Simple Syndication, which provides a summary of a website using a syndication format. News organizations use RSS to provide information about the content of their website. Then the forward indexer is created, which is used for applications such as topic modeling and most classification tasks. In this program, it will be used for classification (tagging) of news articles. Besides, an inverted indexer is implemented, since it is the main data structure used in a search engine. It allows for a quick lookup of documents that contain any given term. An inverted index is a map of a tag term (the key) and a Collection (value) of entries consisting of a document and the TF-IDF value of the term. The collection is sorted by reverse tag term TF-IDF value (the document in which a term has the highest TF-IDF should be visited first). Next, homepage is generated, which displays the tag terms and their associated articles. Tag terms are sorted by the number of articles. If two terms have the same number of articles, then they should be sorted by reverse lexicographic order. The users should be able to enter a query term and our news aggregator will return all the articles related (tagged) to that term. The relevant articles are retrieved from the inverted index. Lastly, we integrate the autocomplete feature within this project.
