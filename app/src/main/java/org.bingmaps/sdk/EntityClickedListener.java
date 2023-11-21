package org.bingmaps.app.sdk;

/**
 * Listener for when an Entity object is clicked.
 * @author rbrundritt
 */
public interface EntityClickedListener {
	void onAvailableChecked(String layerName, int entityId);
}
