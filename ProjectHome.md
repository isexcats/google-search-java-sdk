This project provides a simple Java wrapper for the Google AJAX Search API and Google AJAX Language API.

A simple use of the API is as follows:
```
GoogleSearchQueryFactory factory = GoogleSearchQueryFactory.newInstance("applicationKey");
WebSearchQuery query = factory.newWebSearchQuery();
PagedList<WebResult> response = query.withQuery("hadoop").list();
System.out.println(response.getCurrentPageIndex());
System.out.println(response.getEstimatedResultCount());
System.out.println(response.getMoreResultsUrl());
System.out.println(response.getPages());
for (WebResult result : response) {
	System.out.println(result.getTitle());			
	System.out.println(result.getContent());			
	System.out.println(result.getUrl());			
	System.out.println("=======================================");			
}
```