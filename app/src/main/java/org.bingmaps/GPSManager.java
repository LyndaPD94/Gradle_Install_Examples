package org.bingmaps.app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import org.bingmaps.app.sdk.Coordinate;

public class GPSManager {
    private final LocationManager _locationManager;
    private final Activity _activity;


    @RequiresApi(api = Build.VERSION_CODES.S)
    public GPSManager(Activity activity, LocationListener listener) {
        _activity = activity;
        _locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        }
    }

    public Coordinate GetCoordinate() {

            if (ActivityCompat.checkSelfPermission(_activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(_activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location location = _locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    return new Coordinate(lat, lon);
                }
            }

        return null;
    }

    public void refresh() {


        if (ActivityCompat.checkSelfPermission(_activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.checkSelfPermission(_activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        }
    }
}
