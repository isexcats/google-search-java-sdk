/*
 * Copyright 2010-2011 Nabeel Mukhtar 
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

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.googleapis.maps.schema.DistanceMatrixResult;
import com.googleapis.maps.schema.GeoLocation;
import com.googleapis.maps.schema.Language;
import com.googleapis.maps.schema.RouteType;
import com.googleapis.maps.schema.TravelMode;
import com.googleapis.maps.schema.UnitSystem;
import com.googleapis.maps.services.DistanceMatrixQuery;
import com.googleapis.maps.services.constant.GoogleMapsApiUrls;
import com.googleapis.maps.services.constant.ParameterNames;

/**
 * The Class DistanceMatrixQueryImpl.
 */
public class DistanceMatrixQueryImpl extends BaseGoogleMapsApiQuery<DistanceMatrixResult> implements
	DistanceMatrixQuery {
	
	/**
	 * Instantiates a new distance matrix query impl.
	 * 
	 * @param applicationId the application id
	 */
	public DistanceMatrixQueryImpl(String applicationId) {
		super(applicationId);
	}
	
	
	/* (non-Javadoc)
	 * @see com.google.code.googlesearch.client.GoogleSearchQuery#reset()
	 */
	@Override
	public void reset() {
		apiUrlBuilder = createGoogleSearchApiUrlBuilder(GoogleMapsApiUrls.DISTANCE_MATRIX_URL);
	}

	/* (non-Javadoc)
	 * @see com.googleapis.maps.services.DirectionsQuery#withAlternatives(boolean)
	 */
	@Override
	public DistanceMatrixQuery withAlternatives(boolean alternatives) {
		apiUrlBuilder.withParameter(ParameterNames.ALTERNATIVES, String.valueOf(alternatives));
		return this;
	}


	/* (non-Javadoc)
	 * @see com.googleapis.maps.services.DirectionsQuery#withAvoid(com.googleapis.maps.schema.RouteType)
	 */
	@Override
	public DistanceMatrixQuery withAvoid(RouteType avoid) {
		apiUrlBuilder.withParameterEnum(ParameterNames.AVOID, avoid);
		return this;
	}


	/* (non-Javadoc)
	 * @see com.googleapis.maps.services.DistanceMatrixQuery#withDestination(java.lang.String)
	 */
	@Override
	public DistanceMatrixQuery withDestinations(String... destinations) {
		apiUrlBuilder.withParameter(ParameterNames.DESTINATIONS, toParameterString(destinations));
		return this;
	}


	/* (non-Javadoc)
	 * @see com.googleapis.maps.services.DistanceMatrixQuery#withDestination(com.googleapis.maps.schema.GeoLocation)
	 */
	@Override
	public DistanceMatrixQuery withDestinations(GeoLocation... destinations) {
		apiUrlBuilder.withParameter(ParameterNames.DESTINATIONS, toParameterString(destinations));
		return this;
	}


	/* (non-Javadoc)
	 * @see com.googleapis.maps.services.DistanceMatrixQuery#withLanguage(com.googleapis.maps.schema.Language)
	 */
	@Override
	public DistanceMatrixQuery withLanguage(Language language) {
		apiUrlBuilder.withParameterEnum(ParameterNames.LANGUAGE, language);
		return this;
	}


	/* (non-Javadoc)
	 * @see com.googleapis.maps.services.DistanceMatrixQuery#withMode(com.googleapis.maps.schema.TravelMode)
	 */
	@Override
	public DistanceMatrixQuery withMode(TravelMode mode) {
		apiUrlBuilder.withParameterEnum(ParameterNames.MODE, mode);
		return this;
	}


	/* (non-Javadoc)
	 * @see com.googleapis.maps.services.DistanceMatrixQuery#withOrigin(java.lang.String)
	 */
	@Override
	public DistanceMatrixQuery withOrigins(String... origins) {
		apiUrlBuilder.withParameter(ParameterNames.ORIGINS, toParameterString(origins));
		return this;
	}


	/* (non-Javadoc)
	 * @see com.googleapis.maps.services.DistanceMatrixQuery#withOrigin(com.googleapis.maps.schema.GeoLocation)
	 */
	@Override
	public DistanceMatrixQuery withOrigins(GeoLocation... origins) {
		apiUrlBuilder.withParameter(ParameterNames.ORIGINS, toParameterString(origins));
		return this;
	}


	/* (non-Javadoc)
	 * @see com.googleapis.maps.services.DistanceMatrixQuery#withSensor(boolean)
	 */
	@Override
	public DistanceMatrixQuery withSensor(boolean sensor) {
		apiUrlBuilder.withParameter(ParameterNames.SENSOR, String.valueOf(sensor));
		return this;
	}


	/* (non-Javadoc)
	 * @see com.googleapis.maps.services.DistanceMatrixQuery#withUnits(com.googleapis.maps.schema.UnitSystem)
	 */
	@Override
	public DistanceMatrixQuery withUnits(UnitSystem units) {
		apiUrlBuilder.withParameterEnum(ParameterNames.UNITS, units);
		return this;
	}


	/* (non-Javadoc)
	 * @see com.googleapis.maps.services.DistanceMatrixQuery#withWaypoints(java.lang.String[])
	 */
	@Override
	public DistanceMatrixQuery withWaypoints(String... waypoints) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < waypoints.length; i++) {
			builder.append(waypoints[i]);
			if (i < waypoints.length - 1) {
				builder.append("|");
			}
		}
		apiUrlBuilder.withParameter(ParameterNames.WAYPOINTS, builder.toString());
		return this;
	}


	/* (non-Javadoc)
	 * @see com.googleapis.maps.services.DistanceMatrixQuery#withWaypoints(com.googleapis.maps.schema.GeoLocation[])
	 */
	@Override
	public DistanceMatrixQuery withWaypoints(GeoLocation... waypoints) {
		apiUrlBuilder.withParameter(ParameterNames.WAYPOINTS, toParameterString(waypoints));
		return this;
	}
	
	/* (non-Javadoc)
	 * @see com.googleapis.maps.services.impl.BaseGoogleMapsApiQuery#unmarshallList(com.google.gson.JsonObject)
	 */
	protected List<DistanceMatrixResult> unmarshallList(JsonObject response) {
		String status = response.get("status").getAsString();
		if (!"OK".equals(status) && !"ZERO_RESULTS".equals(status)) {
			throw createGoogleMapsException(status);
		}
		List<String> originAddresses = unmarshall(new TypeToken<List<String>>(){}, response.get("origin_addresses"));
		List<String> destinationAddresses = unmarshall(new TypeToken<List<String>>(){}, response.get("destination_addresses"));
		
		ArrayList<DistanceMatrixResult> list = new ArrayList<DistanceMatrixResult>();
		JsonArray results = response.get("rows").getAsJsonArray();
		for (int i = 0; i < results.size(); i++) {
			JsonElement object = results.get(i);
			List<DistanceMatrixResult> elements = unmarshall(new TypeToken<List<DistanceMatrixResult>>(){}, object.getAsJsonObject().get("elements"));
			for (int j = 0; j < elements.size(); j++) {
				DistanceMatrixResult element = elements.get(j);
				element.setOriginAddress(originAddresses.get(i));
				element.setDestinationAddress(destinationAddresses.get(j));
				list.add(element);				
			}
		}
		return list;
	}


	/* (non-Javadoc)
	 * @see com.googleapis.maps.services.impl.BaseGoogleMapsApiQuery#unmarshall(com.google.gson.JsonElement)
	 */
	@Override
	protected DistanceMatrixResult unmarshall(JsonElement object) {
		Gson gson = getGsonBuilder().create();
		return gson.fromJson(object, DistanceMatrixResult.class);
	}
}
