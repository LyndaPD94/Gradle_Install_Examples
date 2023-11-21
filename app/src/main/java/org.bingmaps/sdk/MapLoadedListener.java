package org.bingmaps.app.sdk;

import org.json.JSONException;

/**
 * Listener for when the map is loaded.
 * @author rbrundritt
 */
public interface MapLoadedListener {
	void onAvailableChecked() throws JSONException;
}
