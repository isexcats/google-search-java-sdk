package com.google.code.googlesearch.client.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.google.code.googlesearch.client.AsyncResponseHandler;
import com.google.code.googlesearch.client.GoogleSearchException;
import com.google.code.googlesearch.client.GoogleSearchQuery;
import com.google.code.googlesearch.client.constant.ApplicationConstants;
import com.google.code.googlesearch.client.constant.ParameterNames;
import com.google.code.googlesearch.client.constant.GoogleSearchApiUrls.GoogleSearchApiUrlBuilder;
import com.google.code.googlesearch.client.enumeration.ResultSetSize;
import com.google.code.googlesearch.common.PagedArrayList;
import com.google.code.googlesearch.common.PagedList;
import com.google.code.googlesearch.schema.GsearchResultClass;
import com.google.code.googlesearch.schema.ListingType;
import com.google.code.googlesearch.schema.PatentStatus;
import com.google.code.googlesearch.schema.VideoType;
import com.google.code.googlesearch.schema.ViewPortMode;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * The Class BaseGoogleSearchApiQuery.
 */
public abstract class BaseGoogleSearchApiQuery<T> extends GoogleSearchApiGateway implements GoogleSearchQuery<T> {
	
	/** The api url builder. */
	protected GoogleSearchApiUrlBuilder apiUrlBuilder;
    
    /** The parser. */
    private final JsonParser parser = new JsonParser();
    
    /** The handlers. */
    private List<AsyncResponseHandler<PagedList<T>>> handlers = new ArrayList<AsyncResponseHandler<PagedList<T>>>();
	
	/**
	 * Instantiates a new base google search api query.
	 * 
	 * @param applicationId the application id
	 */
	public BaseGoogleSearchApiQuery(String applicationId) {
		super.setApplicationKey(applicationId);
        requestHeaders = new HashMap<String, String>();

        // by default we compress contents
        requestHeaders.put("Accept-Encoding", "gzip, deflate");
        this.reset();
	}

	/**
	 * Instantiates a new base google search api query.
	 * 
	 * @param applicationId the application id
	 * @param apiVersion the api version
	 */
	public BaseGoogleSearchApiQuery(String applicationId, String apiVersion) {
		this(applicationId);
		super.setApiVersion(apiVersion);
	}
	
	/* (non-Javadoc)
	 * @see com.google.code.stackexchange.client.query.StackOverflowApiQuery#list()
	 */
	@Override
	public PagedList<T> list() {
		InputStream jsonContent = null;
        try {
        	jsonContent = callApiMethod(apiUrlBuilder.buildUrl());
        	JsonElement response = parser.parse(new InputStreamReader(jsonContent));
        	if (response.isJsonObject()) {
        		PagedList<T> responseList = unmarshallList(response.getAsJsonObject());
        		notifyObservers(responseList);
    			return responseList;
        	}
        	throw new GoogleSearchException("Unknown content found in response:" + response.toString());
        } catch (Exception e) {
            throw new GoogleSearchException(e);
        } finally {
	        closeStream(jsonContent);
	    }
	}

	/**
	 * Unmarshall list.
	 * 
	 * @param response the response
	 * 
	 * @return the paged list< t>
	 */
	private PagedList<T> unmarshallList(JsonObject response) {
		int status = response.get("responseStatus").getAsInt();
		if (status != 200) {
			throw new GoogleSearchException(String.valueOf(response.get("responseDetails").getAsString()));
		}
		JsonObject data = response.get("responseData").getAsJsonObject();
		PagedList<T> list = new PagedArrayList<T>();
		if (data != null) { 
			JsonArray results = data.get("results").getAsJsonArray();
			for (JsonElement object : results) {
				T element = unmarshall(object);
				list.add(element);
			}
		} 
		return list;
	}

	/**
	 * Unmarshall.
	 * 
	 * @param object the object
	 * 
	 * @return the t
	 */
	protected abstract T unmarshall(JsonElement object);

	/* (non-Javadoc)
	 * @see com.google.code.stackexchange.client.query.StackOverflowApiQuery#singleResult()
	 */
	@Override
	public T singleResult() {
		InputStream jsonContent = null;
        try {
        	jsonContent = callApiMethod(apiUrlBuilder.buildUrl());
        	JsonElement response = parser.parse(new InputStreamReader(jsonContent));
        	if (response.isJsonObject()) {
        		JsonObject json = response.getAsJsonObject();
        		int status = json.get("responseStatus").getAsInt();
        		if (status != 200) {
        			throw new GoogleSearchException(json.get("responseDetails").getAsString());
        		}
        		JsonElement data = json.get("responseData");
        		if (data != null) {
        			return unmarshall(data);
        		}
        	}
        	throw new GoogleSearchException("Unknown content found in response:" + response.toString());
        } catch (Exception e) {
            throw new GoogleSearchException(e);
        } finally {
	        closeStream(jsonContent);
	    }
	}
	
	/**
	 * Notify observers.
	 * 
	 * @param response the response
	 */
	protected void notifyObservers(PagedList<T> response) {
		for(AsyncResponseHandler<PagedList<T>> handler : handlers) {
			handler.handleResponse(response);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.google.code.stackexchange.client.query.StackExchangeApiQuery#addResonseHandler(com.google.code.stackexchange.client.AsyncResponseHandler)
	 */
	public void addResonseHandler(AsyncResponseHandler<PagedList<T>> handler) {
		handlers.add(handler);
	}
	
    /* (non-Javadoc)
     * @see com.google.code.stackexchange.client.impl.StackOverflowApiGateway#marshallObject(java.lang.Object)
     */
    protected String marshallObject(Object element) {
    	return null;
    }
    
	/**
	 * Gets the gson builder.
	 * 
	 * @return the gson builder
	 */
	protected GsonBuilder getGsonBuilder() {
		GsonBuilder builder = new GsonBuilder();
		builder.setDateFormat(ApplicationConstants.RFC822DATEFORMAT);
		builder.registerTypeAdapter(ListingType.class, new JsonDeserializer<ListingType>() {

			@Override
			public ListingType deserialize(JsonElement arg0, Type arg1,
					JsonDeserializationContext arg2) throws JsonParseException {
				return ListingType.fromValue(arg0.getAsString());
			}
			
		});
		builder.registerTypeAdapter(PatentStatus.class, new JsonDeserializer<PatentStatus>() {

			@Override
			public PatentStatus deserialize(JsonElement arg0, Type arg1,
					JsonDeserializationContext arg2) throws JsonParseException {
				return PatentStatus.fromValue(arg0.getAsString());
			}
			
		});
		builder.registerTypeAdapter(VideoType.class, new JsonDeserializer<VideoType>() {

			@Override
			public VideoType deserialize(JsonElement arg0, Type arg1,
					JsonDeserializationContext arg2) throws JsonParseException {
				return VideoType.fromValue(arg0.getAsString());
			}
			
		});
		builder.registerTypeAdapter(ViewPortMode.class, new JsonDeserializer<ViewPortMode>() {

			@Override
			public ViewPortMode deserialize(JsonElement arg0, Type arg1,
					JsonDeserializationContext arg2) throws JsonParseException {
				return ViewPortMode.fromValue(arg0.getAsString());
			}
			
		});
		builder.registerTypeAdapter(GsearchResultClass.class, new JsonDeserializer<GsearchResultClass>() {

			@Override
			public GsearchResultClass deserialize(JsonElement arg0, Type arg1,
					JsonDeserializationContext arg2) throws JsonParseException {
				return GsearchResultClass.fromValue(arg0.getAsString());
			}
			
		});
		
		return builder;
	}
    
	/* (non-Javadoc)
	 * @see com.google.code.googlesearch.client.impl.GoogleSearchApiGateway#unmarshallObject(java.lang.Class, java.io.InputStream)
	 */
	@Override
	protected <V> V unmarshallObject(Class<V> clazz, InputStream xmlContent) {
		return null;
	}
	
	/**
	 * Creates the google search api url builder.
	 * 
	 * @param urlFormat the url format
	 * 
	 * @return the google search api url builder
	 */
	protected GoogleSearchApiUrlBuilder createGoogleSearchApiUrlBuilder(String urlFormat) {
		return new GoogleSearchApiUrlBuilder(urlFormat);
	}

	/* (non-Javadoc)
	 * @see com.google.code.googlesearch.client.GoogleSearchQuery#withCallback(java.lang.String)
	 */
	@Override
	public GoogleSearchQuery<T> withCallback(String callback) {
		apiUrlBuilder.withParameter(ParameterNames.CALLBACK, callback);
		return this;
	}

	/* (non-Javadoc)
	 * @see com.google.code.googlesearch.client.GoogleSearchQuery#withContext(java.lang.String)
	 */
	@Override
	public GoogleSearchQuery<T> withContext(String context) {
		apiUrlBuilder.withParameter(ParameterNames.CONTEXT, context);
		return this;
	}

	/* (non-Javadoc)
	 * @see com.google.code.googlesearch.client.GoogleSearchQuery#withLocale(java.util.Locale)
	 */
	@Override
	public GoogleSearchQuery<T> withLocale(Locale locale) {
		apiUrlBuilder.withParameter(ParameterNames.HOST_LANGUAGE, locale.getLanguage());
		return this;
	}

	/* (non-Javadoc)
	 * @see com.google.code.googlesearch.client.GoogleSearchQuery#withQuery(java.lang.String)
	 */
	@Override
	public GoogleSearchQuery<T> withQuery(String query) {
		apiUrlBuilder.withParameter(ParameterNames.QUERY, query);
		return this;
	}

	/* (non-Javadoc)
	 * @see com.google.code.googlesearch.client.GoogleSearchQuery#withResultSetSize(com.google.code.googlesearch.client.enumeration.ResultSetSize)
	 */
	@Override
	public GoogleSearchQuery<T> withResultSetSize(ResultSetSize rsz) {
		apiUrlBuilder.withParameterEnum(ParameterNames.RESULTSET_SIZE, rsz);
		return this;
	}

	/* (non-Javadoc)
	 * @see com.google.code.googlesearch.client.GoogleSearchQuery#withSiteSearch(java.lang.String)
	 */
	@Override
	public GoogleSearchQuery<T> withSiteSearch(String site) {
		// TODO Auto-generated method stub
		return this;
	}

	/* (non-Javadoc)
	 * @see com.google.code.googlesearch.client.GoogleSearchQuery#withStartIndex(int)
	 */
	@Override
	public GoogleSearchQuery<T> withStartIndex(int startIndex) {
		apiUrlBuilder.withParameter(ParameterNames.START, String.valueOf(startIndex));
		return this;
	}
}