package org.bingmaps.app.sdk;

import androidx.annotation.NonNull;

/**
 * An object that represents a rectangle on the map.
 */
public class LocationRect {

	private final double _north;
	private final double _south;
	private final double _east;
	private final double _west;
	

	/**
	 * An object that represents a rectangle on the map.
	 * @param topLeft - Top Left Coordinate
	 * @param bottomRight -Bottom Right Coordinate 
	 */
	public LocationRect(Coordinate topLeft, Coordinate bottomRight){
		_north = topLeft.Latitude;
		_west = topLeft.Longitude;
		_south = bottomRight.Latitude;
		_east = bottomRight.Longitude;
	}

	/**
	 * An object that represents a rectangle on the map.
	 * @param north - Maximum Latitude Coordinate
	 * @param east - Minimum Latitude Coordinate
	 * @param south - Maximum Longitude Coordinate
	 * @param west - Minimum Longitude Coordinate
	 */
	public LocationRect(double north, double east, double south, double west){
		_north = north;
		_east = east;
		_south = south;
		_west = west;
	}
	
	/* Public Properties */
	
	public double getNorth(){
		return _north;
	}
	
	public double getSouth(){
		return _south;
	}
	
	public double getWest(){
		return _west;
	}
	
	public double getEast(){
		return _east;
	}
	
	public Coordinate getTopLeft(){
		return new Coordinate(_north, _west);
	}
	
	public Coordinate getBottomRight(){
		return new Coordinate(_south, _east);
	}
	
	/* Public Methods */
	
	/**
	 * Joins two LocationRect's together and returns a 
	 * new locationRect that encloses both LocationRect's
	 * @param locationRect - LocationRect to join with.
	 */
	public LocationRect join(LocationRect locationRect){
		if(locationRect != null){
			double north = Math.max(_north, locationRect.getNorth());
			double south = Math.max(_south, locationRect.getSouth());
			double east = Math.max(_east, locationRect.getEast());
			double west = Math.max(_north, locationRect.getWest());
			return new LocationRect(north, east, south, west);
		}
		
		return this;
	}
	
	@NonNull
	public String toString(){
		return "MM.LocationRect.fromCorners(" + getTopLeft().toString() + "," + getBottomRight().toString() + ")";
	}
}
