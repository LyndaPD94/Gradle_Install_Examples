package org.bingmaps.app.sdk;

/**
 * Constant values to be used by the Bing Maps SDK.
 * @author rbrundritt
 */
public class Constants {
	/**
	 * Local URL to the web page that contains the Bing Maps wrapper JavaScript and HTML.
	 */
	public static final String BingMapsURL = "file:///android_asset/map.html";
	
	/**
	 * URL to the Bing Maps Traffic tile layer.
	 */
	public static final String TrafficTileLayerURI = "http://ecn.t{subdomain}.tiles.virtualearth.net/tiles/dp/content?p=tf&a={quadkey}";
	
	//Add Bing Maps REST Service URLS
	public static class PanelIds{
		public static final int Splash = 0;
		public static final int About = 1;
		public static final int Map = 2;
	}

	public static class PushpinIcons{
		public static final String Start = "file:///android_asset/startPin.png";
		public static final String End = "file:///android_asset/endPin.png";
		public static final String GPS = "file:///android_asset/pin_gps.png";
		public static final String RedFlag = "file:///android_asset/pin_red_flag.png";
		public static final String BeeIcon = "file:///android_asset/bee_icon.png";

	}

	public static class DataLayers{
		public static final String Route = "route";
		public static final String GPS = "gps";
		public static final String Search = "search";
	}
}

