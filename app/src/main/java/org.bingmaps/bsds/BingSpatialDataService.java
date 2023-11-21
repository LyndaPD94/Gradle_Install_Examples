package org.bingmaps.app.bsds;

import android.os.Handler;

import org.bingmaps.app.data.ContentTypes;
import org.bingmaps.app.data.RequestType;
import org.bingmaps.app.data.ServiceRequest;
import org.bingmaps.app.sdk.Coordinate;
import org.bingmaps.app.sdk.LocationRect;
import org.bingmaps.app.sdk.Utilities;

import java.util.List;

@SuppressWarnings("unused")
public class BingSpatialDataService {
	public Handler FindByAreaCompleted;
	public Handler FindByIDCompleted;
	public Handler FindByPropertyCompleted;

	private final String _queryKey;
	private final String _serviceURL;
	
	public BingSpatialDataService(String accessId, String dataSourceName, String entityTypeName, String queryKey){
		_queryKey = queryKey;		
		_serviceURL = "http://spatial.virtualearth.net/REST/v1/data/" + accessId + "/" + dataSourceName + "/" + entityTypeName;
	}

	public void FindByArea(Coordinate center, double distance, QueryOption options){
		StringBuilder sb = new StringBuilder();
		sb.append(_serviceURL);

		sb.append("?spatialFilter=nearby(");
		sb.append(center.Latitude);
		sb.append(",");
		sb.append(center.Longitude);
		sb.append(",");
		sb.append(distance);
		sb.append(")");
		if(options != null){
			sb.append(options);
		}else{
			sb.append("&$format=json");
		}
		sb.append("&key=");
		sb.append(_queryKey);
		ServiceRequest request = new ServiceRequest(sb.toString(), RequestType.GET, ContentTypes.JSON);
		QueryServiceAsyncTask service = new QueryServiceAsyncTask(FindByAreaCompleted);
		service.doInBackground(request);
	}

	public void FindByArea(LocationRect boundingBox, QueryOption options){
		StringBuilder sb = new StringBuilder();
		sb.append(_serviceURL);
		sb.append("?spatialFilter=bbox(");
		sb.append(boundingBox.getSouth());
		sb.append(",");
		sb.append(boundingBox.getWest());
		sb.append(",");
		sb.append(boundingBox.getNorth());
		sb.append(",");
		sb.append(boundingBox.getEast());
		sb.append(")");
		if(options != null){
			sb.append(options);
		}else{
			sb.append("&$format=json");
		}

		//Add Bing Maps Key
		sb.append("&key=");
		sb.append(_queryKey);

		//create service request
		ServiceRequest request = new ServiceRequest(sb.toString(), RequestType.GET, ContentTypes.JSON);
		QueryServiceAsyncTask service = new QueryServiceAsyncTask(FindByAreaCompleted);
		service.doInBackground(request);
	}

	//http://spatial.virtualearth.net/REST/v1/data/accessId/dataSourceName/entityTypeName(entityId)&key=queryKey
	public void FindByID(String entityID){
		if(!Utilities.isNullOrEmpty(entityID) && FindByIDCompleted != null){

			String sb = _serviceURL +

					//Add query
					"('" +
					entityID +
					"')" +
					"&$format=json" +

					//Add Bing Maps Key
					"&key=" +
					_queryKey;

			//create service request
			ServiceRequest request = new ServiceRequest(sb, RequestType.GET, ContentTypes.JSON);
			QueryServiceAsyncTask service = new QueryServiceAsyncTask(FindByAreaCompleted);
			service.doInBackground(request);
		}else{
			if(FindByIDCompleted != null){
				FindByIDCompleted.sendMessage(null);
			}
		}
	}

	//http://spatial.virtualearth.net/REST/v1/data/accessId/dataSourceName/entityTypeName?$filter=entityId in (entityId1,entityId2,entityIdN)&queryoption1&queryoption2&queryoptionN&key=queryKey
	/**
	 * Note that the filter property of the QueryOption object is ignored by this method.
	 */
	public void FindByID(List<String> entityIDs, QueryOption options){
		if(entityIDs != null && entityIDs.size() > 0 && FindByIDCompleted != null){
			StringBuilder sb = new StringBuilder();
			sb.append(_serviceURL);

			//Add query
			sb.append("?$filter=entityId in (");
			for(String entityID : entityIDs){
				sb.append("'");
				sb.append(entityID);
				sb.append("'");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append(")");
			if(options != null){
				options.Filters = null;
				sb.append(options);
			}else{
				sb.append("&$format=json");
			}
			sb.append("&key=");
			sb.append(_queryKey);
			ServiceRequest request = new ServiceRequest(sb.toString(), RequestType.GET, ContentTypes.JSON);
			QueryServiceAsyncTask service = new QueryServiceAsyncTask(FindByAreaCompleted);
			service.doInBackground(request);
		}else{
			if(FindByIDCompleted != null){
				FindByIDCompleted.sendMessage(null);
			}
		}
	}

	public void FindByProperty(QueryOption options){
		if(options != null && !Utilities.isNullOrEmpty(options.Filters)){
			String sb = _serviceURL +
					options +
					"&key=" +
					_queryKey;
			ServiceRequest request = new ServiceRequest(sb, RequestType.GET, ContentTypes.JSON);
			QueryServiceAsyncTask service = new QueryServiceAsyncTask(FindByAreaCompleted);
			service.doInBackground(request);
		}else{
			if(FindByPropertyCompleted != null){
				FindByPropertyCompleted.sendMessage(null);
			}
		}
	}
}
