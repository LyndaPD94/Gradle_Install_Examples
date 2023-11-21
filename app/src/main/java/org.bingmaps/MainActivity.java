package org.bingmaps.app;

import static org.bingmaps.app.Constants.PERMISSION_LOCATION_REQUEST_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.bingmaps.app.sdk.BingMapsView;
import org.bingmaps.app.sdk.Coordinate;
import org.bingmaps.app.sdk.EntityLayer;
import org.bingmaps.app.sdk.MapMovedListener;
import org.bingmaps.app.sdk.MapStyles;
import org.bingmaps.app.sdk.Point;
import org.bingmaps.app.sdk.Polyline;
import org.bingmaps.app.sdk.PolylineOptions;
import org.bingmaps.app.sdk.Pushpin;
import org.bingmaps.app.sdk.PushpinOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lyn.bee.hive_record_app.R;



public class MainActivity extends AppCompatActivity {
    @SuppressWarnings("unused")
    private final MapMovedListener mapMovedListener = () -> {
  };
    CharSequence[] _dataLayers;
    boolean[] _dataLayerSelections;
    private BingMapsView bingMapsView;
    private GPSManager _GPSManager;
    private EntityLayer _gpsLayer;


    private Activity _baseActivity;

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> throwable.printStackTrace());

        requestWindowFeature(Window.FEATURE_NO_TITLE);

       
        setContentView(R.layout.main);
        Toolbar toolbar =  findViewById(R.id.toolbar);
       setSupportActionBar(toolbar);

        
        ImageButton btnZoomIn = findViewById(R.id.btnZoomIn);
        btnZoomIn.setOnClickListener(view -> bingMapsView.zoomIn());

        ImageButton btnZoomOut = findViewById(R.id.btnZoomOut);
        btnZoomOut.setOnClickListener(view -> bingMapsView.zoomOut());

        ImageButton btnMyLocation = findViewById(R.id.btnMyLocation);
        btnMyLocation.setOnClickListener(view -> {
            Coordinate coord = _GPSManager.GetCoordinate();
            if (coord != null) {
                bingMapsView.setCenterAndZoom(coord,
                        Constants.DefaultGPSZoomLevel);
            }
        });

        Initialize();
    }

    private void setSupportActionBar(Toolbar toolbar) {
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                _GPSManager.refresh();
            }
        }
    }

    private void Initialize() {
        _baseActivity = this;

        if (!checkPermission(this)) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_LOCATION_REQUEST_CODE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            _GPSManager = new GPSManager(this, new GPSLocationListener());
        }

        // Add more data layers here
        _dataLayers = new String[]{getString(R.string.traffic)};
        _dataLayerSelections = new boolean[_dataLayers.length];
        bingMapsView = findViewById(R.id.mapView);
        // Add a map loaded event handler
        bingMapsView.setMapLoadedListener(() -> {

            // Add GPS layer
            _gpsLayer = new EntityLayer(Constants.DataLayers.GPS);
            bingMapsView.getLayerManager().addLayer(_gpsLayer);
            UpdateGPSPin();
            updateMarker();
        });

        // Add a entity clicked event handler
        bingMapsView.setEntityClickedListener((layerName, entityId) -> {
            HashMap<String, Object> metadata = bingMapsView
                    .getLayerManager().GetMetadataByID(layerName, entityId);
            DialogLauncher.LaunchEntityDetailsDialog(_baseActivity,
                    metadata);
        });

        // Load the map
        bingMapsView.loadMap(Constants.BingMapsKey,
                _GPSManager.GetCoordinate(), Constants.DefaultGPSZoomLevel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int selectedId = item.getItemId();
        if (selectedId == R.id.roadBtn) {
            try{
                bingMapsView.setMapStyle(MapStyles.Road);
                item.setChecked(!item.isChecked());
                return true;
            }catch(Exception e ){
                Toast.makeText(getApplicationContext(), getString(
                        R.string.exerror
                ), Toast.LENGTH_LONG).show();
            }
        }if (selectedId == R.id.aerialBtn) {
            try{
                bingMapsView.setMapStyle(MapStyles.Aerial);
                item.setChecked(!item.isChecked());
                return true;
            }catch(Exception e ){
                Toast.makeText(getApplicationContext(), getString(
                        R.string.exerror
                ), Toast.LENGTH_LONG).show();
            }
        }if (selectedId == R.id.streetSideBtn) {
            try {
                bingMapsView.setMapStyle(MapStyles.StreetSide);
                item.setChecked(!item.isChecked());
                return true;
            }catch(Exception e){
                Toast.makeText(getApplicationContext(), getString(
                        R.string.exerror
                ), Toast.LENGTH_LONG).show();
            }
        }if (selectedId == R.id.ordnanceSurveyBtn) {
            try{
                bingMapsView.setMapStyle(MapStyles.OrdnanceSurvey);
                item.setChecked(!item.isChecked());
                return true;
            }catch(Exception e){
                Toast.makeText(getApplicationContext(), getString(
                        R.string.exerror
                ), Toast.LENGTH_LONG).show();
            }

        }if (selectedId == R.id.ordnanceSurveyBtn) {
            try{
                bingMapsView.setMapStyle(MapStyles.OrdnanceSurvey);
                item.setChecked(!item.isChecked());
                return true;
            }catch(Exception e){
                Toast.makeText(getApplicationContext(), getString(
                        R.string.exerror
                ), Toast.LENGTH_LONG).show();
            }

        }if (selectedId == R.id.grayscaleBtn) {
            try{
                bingMapsView.setMapStyle(MapStyles.Grayscale);
                item.setChecked(!item.isChecked());
                return true;
            }catch(Exception e){
                Toast.makeText(getApplicationContext(), getString(
                        R.string.exerror
                ), Toast.LENGTH_LONG).show();
            }

        }if (selectedId == R.id.aboutMenuBtn) {
            try{
                DialogLauncher.LaunchAboutDialog(this);
                return true;
            }catch(Exception e){
                Toast.makeText(getApplicationContext(), getString(
                        R.string.exerror
                ), Toast.LENGTH_LONG).show();
            }

        }return super.onOptionsItemSelected(item);

    }


    private void UpdateGPSPin() {
        PushpinOptions opt = new PushpinOptions();
        opt.Icon = Constants.PushpinIcons.GPS;
        Pushpin p = new Pushpin(_GPSManager.GetCoordinate(), opt);
        if (p.Location != null && _gpsLayer != null) {
            _gpsLayer.clear();
            _gpsLayer.add(p);
            _gpsLayer.updateLayer();
        }
    }

    public void updateMarker() {
        List<Coordinate> listCoord = new ArrayList<>();
        // EntityLayer is used for map overlay
        EntityLayer entityLayer = (EntityLayer) bingMapsView.getLayerManager()
                .getLayerByName(Constants.DataLayers.Search);
        if (entityLayer == null) {
            entityLayer = new EntityLayer(Constants.DataLayers.Search);
        }
        entityLayer.clear();
        // Use Pushpin to mark on the map
        // PushpinOptions is used to set attributes for Pushpin
        // opt.Icon - The icon of PushPin, opt.Anchor - The position to display Pushpin
        PushpinOptions opt = new PushpinOptions();
        opt.Icon = Constants.PushpinIcons.RedFlag;
        opt.Width = 20;
        opt.Height = 35;
        opt.Anchor = new Point(11, 10);

        // Add the entityLayer to mapView's LayerManager
        bingMapsView.getLayerManager().addLayer(entityLayer);
        entityLayer.updateLayer();

        // set the center location and zoom level of map
        Coordinate coordinate = _GPSManager.GetCoordinate();
        bingMapsView.setCenterAndZoom(coordinate, 11);

        // Polyline used to draw lines on the MapView
        // PolylineOptions have multiple attributes for the line
        // polylineOptions.StrokeThickness
        // polylineOptions.StrokeColor
        Polyline routeLine = new Polyline(listCoord);
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.StrokeThickness = 3;
        routeLine.Options = polylineOptions;
        entityLayer.add(routeLine);
    }

    public class GPSLocationListener implements LocationListener {
        public void onLocationChanged(Location arg0) {
            UpdateGPSPin();
        }

        public void onProviderDisabled(String arg0) {
        }

        public void onProviderEnabled(String arg0) {
        }

    }

    private Bitmap generateBitmap(@DrawableRes int drawableRes) {
        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getDrawable(R.drawable.bee_icon);
        return getBitmapFromDrawable(drawable);
    }
    static Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.getBounds().setEmpty();
            drawable.draw(canvas);
            return bitmap;
        }
    }
}