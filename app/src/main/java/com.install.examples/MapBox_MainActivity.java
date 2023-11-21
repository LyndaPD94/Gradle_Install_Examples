package com.install.example;




import android.Manifest;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;


import org.json.JSONObject;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

import timber.log.Timber;


@SuppressWarnings("ALL")
public class GMapActivity extends AppCompatActivity implements
        OnMapReadyCallback, PermissionsListener,MapboxMap.OnMapClickListener {

    private static final String JSON_CHARSET = "UTF-8";
    EditText date, month, year, id, hiveid, locate, notes;
    Button btnupdate, bynsearch, btncreate, btn2csv;

    private MapView mapView;

    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private Network network;
    long limit = 1500;
    long currentlimit = 500;
    String layerid="layer-id";
    String imageid="marker_icon";
    private final LocationListeningCallback callback = new LocationListeningCallback(this);
    private OfflineRegion offlineRegion;
    private ValueAnimator animator;
    private GeoJsonSource geoJsonSource;
    private SymbolManager symbolManager;
    private ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
        onMapReady(mapboxMap);
            Toast.makeText(getApplicationContext(), getString(R.string.wifimactivated),Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            mapboxMap.getStyle();
            mapboxMap.setStyle(offlineRegion.toString());
            Toast.makeText(getApplicationContext(), getString(R.string.nowifi),Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            final boolean unmetered = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);
            Toast.makeText(getApplicationContext(), getString(R.string.lowwifi),Toast.LENGTH_SHORT).show();
            GetCurrentLocationTile();
            mapboxMap.setStyle(new Style.Builder().fromUri(offlineRegion.toString()));
        }
    };
    private  void RegisterNerWork(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(ConnectivityManager.class);
        connectivityManager.requestNetwork(RequestNetwork(), networkCallback);
    }
    private static final TypeEvaluator<LatLng> latLngEvaluator = new TypeEvaluator<LatLng>() {

        private final LatLng latLng = new LatLng();

        @Override
        public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
            latLng.setLatitude(startValue.getLatitude()
                    + ((endValue.getLatitude() - startValue.getLatitude()) * fraction));
            latLng.setLongitude(startValue.getLongitude()
                    + ((endValue.getLongitude() - startValue.getLongitude()) * fraction));
            return latLng;
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedId = item.getItemId();
        if (selectedId == R.id.wifiBtn) {
            try{

                return true;
            }catch(Exception e){
                Toast.makeText(getApplicationContext(), getString(
                        R.string.exerror
                ), Toast.LENGTH_LONG).show();
            }
        }if (selectedId == R.id.mainmenu12) {
            try{
                MainMenu();
                return true;
            }catch(Exception e){
                Toast.makeText(getApplicationContext(), getString(
                        R.string.exerror
                ), Toast.LENGTH_LONG).show();
            }
        }if (selectedId == R.id.abttn) {
            try{
                HowTo();
                return true;
            }catch(Exception e) {
                Toast.makeText(getApplicationContext(), getString(
                        R.string.exerror
                ), Toast.LENGTH_LONG).show();
            }
        }if (selectedId == R.id.viewlocate) {
            try{
                ViewLocationRecords();
                return true;
            }catch(Exception e){
                Toast.makeText(getApplicationContext(), getString(
                        R.string.exerror
                ), Toast.LENGTH_LONG).show();
            }

        }return super.onOptionsItemSelected(item);
    }
    public void request() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.policy_message)).setPositiveButton(getString(R.string.accept), (dialogInterface, i) -> {
                    final boolean fl = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                    final boolean cl = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                    Toast.makeText(GMapActivity.this, getString(R.string.touchscreen), Toast.LENGTH_LONG).show();
                })
                .setNegativeButton(getString(R.string.deny), (dialogInterface, which) -> {
                    final boolean fl = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED;
                    final boolean cl = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED;
                    Intent intent = new Intent(GMapActivity.this, MainActivity.class);
                    startActivity(intent);
                }).setCancelable(false).show();
    }

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    ActivityResultLauncher<String[]> mPermissionResultlauncher;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.map);
        request();
        mapView = findViewById(R.id.mapView);
        ImageButton btnZoomIn = findViewById(R.id.btnZoomIn);
        ImageButton btnZoomOut = findViewById(R.id.btnZoomOut);
        ImageButton btnMyLocation = findViewById(R.id.btnMyLocation);


    }
    private void enableLocation() {
        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
        locationEngine.getLastLocation(callback);
    }




private NetworkRequest RequestNetwork(){
    NetworkRequest networkRequest = new NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build();
    return networkRequest;
}


private void RefreshMap(){
        enableLocationComponent(mapboxMap.getStyle());
        onMapReady(mapboxMap);
        Toast.makeText(this,getString(R.string.refreshcompleted),Toast.LENGTH_SHORT).show();
}
    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING_GPS);
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.accept, Toast.LENGTH_LONG).show();
    }
    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocation();
        } else {
            Toast.makeText(this, R.string.successfully, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        RequestNetwork();
        mapboxMap.setStyle(Style.SATELLITE_STREETS, style -> {
            try {
                enableLocationComponent(style);

                symbolManager = new SymbolManager(mapView, mapboxMap, style);
                symbolManager.setIconAllowOverlap(true);
                symbolManager.setTextAllowOverlap(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        mapboxMap.addOnMapClickListener(point -> {
            geoJsonSource = new GeoJsonSource("source-id",
                    Feature.fromGeometry(Point.fromLngLat(point.getLongitude(),
                            point.getLatitude())));
            try {
                String showcood = point.getLatitude() + getString(R.string.comma) + point.getLongitude();
                locate.setText("");
                locate.setText(showcood);
                removeSymbol(point);
                Style style=mapboxMap.getStyle();
                SymbolLayer symbolLayer=new SymbolLayer("layer-id", "source-id");
                if(symbolLayer.getSourceLayer().isEmpty()) {
                    addSymbol(point);
                    mapView.getRenderView().getResources().flushLayoutCache();
                    assert style != null;
                    style.addImage(("id_icon"), BitmapFactory.decodeResource(
                            getResources(), R.drawable.bee_icon));
                    symbolLayer.getIconAllowOverlap().equals(false);
                }
            }catch (Exception e){
                Toast.makeText(this, getString(R.string.exerror), Toast.LENGTH_SHORT).show();
            }
            return true;
        });
        if(networkCallback.equals(true)){
            GetCurrentLocationTile();
            offlineRegion.getMetadata();
            mapboxMap.setOfflineRegionDefinition(offlineRegion.getDefinition());

        }
    }

   private boolean removeSymbol(LatLng point) {
        if (symbolManager == null) {
            return false;
        }
            symbolManager.getSymbolPlacement();
            symbolManager.deleteAll();
            return true;
    }

    private Style.Builder getStyleBuilder(@NonNull String styleUrl) {
        return new Style.Builder().fromUri(styleUrl)
                .withImage("id_icon", generateBitmap(R.drawable.bee_icon));
    }

private void HowTo(){
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(getString(R.string.maphowto)).setPositiveButton(getString(R.string.moreinfo), (dialogInterface, i) -> {
                try {
                    CustomTabsIntent.Builder builder1 = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder1.build();
                    customTabsIntent.launchUrl(GMapActivity.this, Uri.parse(url));
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), getString(R.string.loaderror), Toast.LENGTH_LONG).show();
                }
    })
            .setNegativeButton(getString(R.string.later), (dialogInterface, which) -> {
           }).setCancelable(false).show();
}
    private static class LocationListeningCallback
            implements LocationEngineCallback<LocationEngineResult> {

        LocationListeningCallback(GMapActivity activity) {
            WeakReference<GMapActivity> activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(LocationEngineResult result) {
            Location lastLocation = result.getLastLocation();

        }
        @Override
        public void onFailure(@NonNull Exception exception) {
            exception.getCause();
        }
    }

}


    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        return false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    protected void onStop(LocationEngine locationEngine) {
        super.onStop();  mapView.onStop();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
        animator.cancel();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapboxMap != null) {
            mapboxMap.removeOnMapClickListener(this);
            mapView.removeOnDidFinishLoadingStyleListener(this::onDestroy);
            mapView.onStop();
        }
        mapView.onDestroy();

    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

}
