package org.bingmaps.app.rest.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Instruction {
	public Instruction()
    {
    }

    public Instruction(JSONObject obj){
        deserializeFromObj(obj);
    }

    public Instruction(String serializedObj) throws JSONException {
        deserialize(serializedObj);
    }

    public String ManeuverType;

    public String Text;

    private void deserialize(String serializedObj) throws JSONException {
        JSONObject obj = new JSONObject(serializedObj);
        deserializeFromObj(obj);
    }

    private void deserializeFromObj(JSONObject obj){
        this.ManeuverType = obj.optString("maneuverType");
        this.Text = obj.optString("text");
    }
}
