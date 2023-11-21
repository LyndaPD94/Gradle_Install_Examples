package org.bingmaps.app.data;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GeoRssAsyncTask extends ConcurrentHashMap<ServiceRequest, List<GeoRssItem>> {


	protected List<GeoRssItem> doInBackground(ServiceRequest... params) {
		List<GeoRssItem> items = null;
		if(params != null && params.length > 0){
			ServiceRequest request = params[0];
			String result = request.execute();
			
			try {
				GeoRssFeedParser parser = new GeoRssFeedParser();
				items = parser.parse(new ByteArrayInputStream(result.getBytes()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return items;
	}
	

}
