/*
 * Copyright 2010 Nabeel Mukhtar 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 * 
 */
package com.googleapis.maps.services.impl;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.googleapis.maps.schema.ElevationResult;
import com.googleapis.maps.schema.GeoLocation;
import com.googleapis.maps.services.ElevationQuery;
import com.googleapis.maps.services.constant.GoogleMapsApiUrls;

/**
 * The Class DetectLanguageQueryImpl.
 */
public class ElevationQueryImpl extends BaseGoogleMapsApiQuery<ElevationResult> implements
	ElevationQuery {
	
	/**
	 * Instantiates a new detect language query impl.
	 * 
	 * @param applicationId the application id
	 */
	public ElevationQueryImpl(String applicationId) {
		super(applicationId);
	}
	
	
	/* (non-Javadoc)
	 * @see com.google.code.googlesearch.client.GoogleSearchQuery#reset()
	 */
	@Override
	public void reset() {
		apiUrlBuilder = createGoogleSearchApiUrlBuilder(GoogleMapsApiUrls.ELEVATION_URL);
	}

	/* (non-Javadoc)
	 * @see com.google.code.googlesearch.client.impl.BaseGoogleSearchApiQuery#unmarshall(com.google.gson.JsonElement)
	 */
	@Override
	protected ElevationResult unmarshall(JsonElement object) {
		Gson gson = getGsonBuilder().create();
		return gson.fromJson(object, ElevationResult.class);
	}


	@Override
	public ElevationQuery withLocations(List<GeoLocation> locations) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ElevationQuery withPath(List<GeoLocation> path) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ElevationQuery withSamples(int samples) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ElevationQuery withSensor(boolean sensor) {
		// TODO Auto-generated method stub
		return null;
	}
}
