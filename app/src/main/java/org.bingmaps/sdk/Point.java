package org.bingmaps.app.sdk;

import androidx.annotation.NonNull;

/**
 * This class represents a simple point object.
 * @author rbrundritt
 */
public class Point {
	/**
	 * Constructor
	 * @param x The x coordinate.
	 * @param y The Y coordinate.
	 */
	public Point(int x, int y){
		X = x;
		Y = y;
	}
	
	/**
	 * The x coordinate.
	 */
	public int X;
	
	/**
	 * The y coordinate.
	 */
	public int Y;
	
	/**
	 * Returns a JSON representation of the Point object.
	 * @return A JSON representation of the Point object.
	 */
	@NonNull
	public String toString(){
		return "new MM.Point(" + X + "," + Y  + ")";
	}
}
