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
