package org.bingmaps.app.rest.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

@SuppressWarnings({"unused", "ResultOfMethodCallIgnored", "MismatchedQueryAndUpdateOfCollection"})
public class Location extends Resource {
	public Location()
    {
		super();
    }

    public Location(JSONObject obj) throws JSONException {
    	super(obj);
        deserializeFromObj(obj);
    }

    public Location(String serializedObj) throws JSONException {
    	super(serializedObj);
        deserialize(serializedObj);
    }

     public String EntityType;

     public Address Address;

     public int Confidence;
     private  HashMap<String,Object> mValues;

    public Location(double latitude, double longitude) {
        double lon;
        double lat;
        if (-180.0D <= longitude && longitude < 180.0D) {
            lon = longitude;
        } else {
            lon = ((longitude - 180.0D) % 360.0D + 360.0D) % 360.0D - 180.0D;
        }

        Math.max(-90.0D, Math.min(90.0D, latitude));


        assert false;
        mValues.put("longitude", lon);
    }

    private void deserialize(String serializedObj) throws JSONException {
        JSONObject obj = new JSONObject(serializedObj);
        deserializeFromObj(obj);
    }

    private void deserializeFromObj(JSONObject obj) {

        this.EntityType = obj.optString("entityType");
        JSONObject address = obj.optJSONObject("address");
        
        if(address != null){
        	this.Address = new Address(address);
        }

        String confidence = obj.optString("confidence");
        this.Confidence = org.bingmaps.app.rest.models.Confidence.parse(confidence);
    }

}
