package lyn.bee.hive_record_app;


import static lyn.bee.hive_record_app.HiveDB_Helper.DATABASE_NAME;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.MapView;
import com.opencsv.CSVWriter;

import org.bingmaps.app.Constants;
import org.bingmaps.app.DialogLauncher;
import org.bingmaps.app.GPSManager;
import org.bingmaps.app.sdk.BingMapsView;
import org.bingmaps.app.sdk.Coordinate;
import org.bingmaps.app.sdk.EntityLayer;
import org.bingmaps.app.sdk.LocationRect;
import org.bingmaps.app.sdk.MapStyles;
import org.bingmaps.app.sdk.Point;
import org.bingmaps.app.sdk.Polyline;
import org.bingmaps.app.sdk.PolylineOptions;
import org.bingmaps.app.sdk.Pushpin;
import org.bingmaps.app.sdk.PushpinOptions;

import java.io.File;
import java.io.FileWriter;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import lyn.bee.hive_record_app.GPSRouter.GpsManager;

@SuppressWarnings("ALL")
public class GMapActivity extends AppCompatActivity {

    EditText date, month, year, id, hiveid, location, notes;
    Button btnupdate, bynsearch, btncreate, btn2csv;
    CharSequence[] _dataLayers;
    boolean[] _dataLayerSelections;
    private BingMapsView bingMapsView;
    private GpsManager _GPSManager;
    private EntityLayer _gpsLayer;
    private Activity _baseActivity;
    private MapView mapView;

    HiveDB_Helper db_helper;
    HiveListHelper helper;
    HiveDB_Helper hiveDB_helper;


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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        request();
        setContentView(R.layout.map);
        date = findViewById(R.id.date11);
        month = findViewById(R.id.month8);
        year = findViewById(R.id.year8);
        id = findViewById(R.id.id11);
        hiveid = findViewById(R.id.hiveid8);
        notes = findViewById(R.id.notes11);
        location = findViewById(R.id.getlocation);
        btncreate = findViewById(R.id.buttonncreatm);
        btnupdate = findViewById(R.id.btnupdatem);
        bynsearch = findViewById(R.id.btnsearchm);
        btn2csv = findViewById(R.id.btn2csvm);
        hiveDB_helper = new HiveDB_Helper(GMapActivity.this);
        helper = new HiveListHelper(getApplicationContext(), HiveListHelper.DATABASE_NAME, null, 1);


        EditText date = findViewById(R.id.date11);
        EditText month = findViewById(R.id.month8);
        EditText year = findViewById(R.id.year8);
        Calendar calendar = Calendar.getInstance(Locale.US);
        String Date = String.valueOf(calendar.get(Calendar.DATE));
        String Month = String.valueOf(MonthDay.now().getMonth().getValue());
        String Year = String.valueOf(calendar.get(Calendar.YEAR));
        date.setText(Date);
        month.setText(Month);
        year.setText(Year);

        ImageButton btnZoomIn = findViewById(R.id.btnZoomIn);
        btnZoomIn.setOnClickListener(view -> bingMapsView.zoomIn());

        ImageButton btnZoomOut = findViewById(R.id.btnZoomOut);
        btnZoomOut.setOnTouchListener((v, event) -> {
            bingMapsView.zoomOut();
            return false;
        });
        btnupdate.setOnClickListener(v -> Update());
        btncreate.setOnClickListener(v -> Insert());

        bynsearch.setOnClickListener(v -> Search());
        btn2csv.setOnClickListener(v -> ToCsv());


        ImageButton btnMyLocation = findViewById(R.id.btnMyLocation);
        btnMyLocation.setOnClickListener(view -> {
            Coordinate coord = _GPSManager.GetCoordinate();
            if (coord != null) {
                bingMapsView.setCenterAndZoom(coord,
                        Constants.DefaultGPSZoomLevel);
            }
            _GPSManager.GetCoordinate();
            double nor = bingMapsView.getBounds().getNorth();
            double east = bingMapsView.getBounds().getEast();
            double sur = bingMapsView.getBounds().getSouth();
            double west = bingMapsView.getBounds().getWest();
            bingMapsView.getCenter();
            LocationRect locationRect = bingMapsView.getBounds().join(new LocationRect(nor, east, sur, west));
            double lat = locationRect.getBottomRight().Latitude;
            double longi = locationRect.getBottomRight().Longitude;
            String locate = String.valueOf(lat);
            String locate2 = String.valueOf(longi);
            String fullocation = locate + getString(R.string.comma) + locate2;
            location.setText(fullocation);
        });

        Initialize();
        bingMapsView.setOnTouchListener((v, event) -> {
            double nor = bingMapsView.getBounds().getNorth();
            double east = bingMapsView.getBounds().getEast();
            double sur = bingMapsView.getBounds().getSouth();
            double west = bingMapsView.getBounds().getWest();
            LocationRect locationRect = bingMapsView.getBounds().join(new LocationRect(nor, east, sur, west));
            String lat = String.valueOf(locationRect.getBottomRight().Latitude);
            String lon = String.valueOf(locationRect.getBottomRight().Longitude);
            final String s = lat + "," + lon;
            location.setText(s);
            int lati = (int) locationRect.getBottomRight().Latitude;
            int longi = (int) locationRect.getBottomRight().Longitude;
            PushpinOptions opt = new PushpinOptions();
            opt.Icon = Constants.PushpinIcons.RedFlag;
            opt.Width = 10;
            opt.Height = 30;
            opt.Anchor = new Point(lati, longi);
            Pushpin p = new Pushpin();
            if (p.Location != null && _gpsLayer != null) {
                _gpsLayer.add(p);
                _gpsLayer.updateLayer();

            }

            return false;
        });

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
        _dataLayers = new String[]{getString(R.string.traffic)};
        _dataLayerSelections = new boolean[_dataLayers.length];
        bingMapsView = findViewById(R.id.mapView);
        bingMapsView.setMapLoadedListener(() -> {
            _gpsLayer = new EntityLayer(Constants.DataLayers.GPS);
            bingMapsView.getLayerManager().addLayer(_gpsLayer);
            UpdateGPSPin();
            updateMarker();
        });

        bingMapsView.setEntityClickedListener((layerName, entityId) -> {
            HashMap<String, Object> metadata = bingMapsView
                    .getLayerManager().GetMetadataByID(layerName, entityId);
            DialogLauncher.LaunchEntityDetailsDialog(_baseActivity,
                    metadata);
        });
        bingMapsView.loadMap(Constants.BingMapsKey,
                _GPSManager.GetCoordinate(), Constants.DefaultGPSZoomLevel);

        bingMapsView.setEntityClickedListener((layerName, entityId) -> {
            double nor = bingMapsView.getBounds().getNorth();
            double east = bingMapsView.getBounds().getEast();
            double sur = bingMapsView.getBounds().getSouth();
            double west = bingMapsView.getBounds().getWest();
            bingMapsView.getCenter();
            LocationRect locationRect = bingMapsView.getBounds().join(new LocationRect(nor, east, sur, west));
            locationRect.getBottomRight();
            _GPSManager.GetCoordinate();

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
            }
           if (selectedId == R.id.grayscaleBtn) {
                try{
                    bingMapsView.setMapStyle(MapStyles.Grayscale);
                    item.setChecked(!item.isChecked());
                    return true;
            }catch(Exception e){
                    Toast.makeText(getApplicationContext(), getString(
                            R.string.exerror
                    ), Toast.LENGTH_LONG).show();
                }

            }if (selectedId == R.id.tooffline) {
                try {
                    ToOfflineMap();
                    return true;
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), getString(
                            R.string.exerror
                    ), Toast.LENGTH_LONG).show();
                }
            }
            if (selectedId == R.id.aboutMenuBtn) {
                try{
                    DialogLauncher.LaunchAboutDialog(this);
                    return true;
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), getString(
                            R.string.exerror
                    ), Toast.LENGTH_LONG).show();
                }

            } if (selectedId == R.id.ollocationrecords) {
                try{
                   Intent intent=new Intent(GMapActivity.this,MainActivity6.class);
                   startActivity(intent);
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
        EntityLayer entityLayer = (EntityLayer) bingMapsView.getLayerManager()
                .getLayerByName(Constants.DataLayers.Search);
        if (entityLayer == null) {
            entityLayer = new EntityLayer(Constants.DataLayers.Search);
        }
        entityLayer.clear();
        PushpinOptions opt = new PushpinOptions();
        opt.Icon = Constants.PushpinIcons.RedFlag;
        opt.Width = 20;
        opt.Height = 35;
        opt.Anchor = new Point(11, 10);
        bingMapsView.getLayerManager().addLayer(entityLayer);
        entityLayer.updateLayer();


        Coordinate coordinate = _GPSManager.GetCoordinate();
        bingMapsView.setCenterAndZoom(coordinate, 11);


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
    private void Search() {
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            try {
                Cursor cursor = db.rawQuery( " SELECT Max(ID), Hive_ID, Date, Month, Year, Location, Notes FROM HMAP WHERE Hive_ID >=1 ",null);
                cursor.moveToFirst();
                hiveid.setText("");
                date.setText("");
                month.setText("");
                year.setText("");
                location.setText("");
                id.setText("");

                id.setText(cursor.getString(0));
                hiveid.setText(cursor.getString(1));
                date.setText(cursor.getString(2));
                month.setText(cursor.getString(3));
                year.setText(cursor.getString(4));
                location.setText(cursor.getString(5));
                notes.setText(cursor.getString(6));
                cursor.close();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.cantfind), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            System.out.println(e.getCause().toString());
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
        }
    }

    public void ToCsv() throws Resources.NotFoundException {
        try {
            File dbFile = getDatabasePath(DATABASE_NAME);
            File file = new File(Environment.getExternalStorageDirectory() + "/Documents");
            if (!file.exists()) file.mkdirs();
            Resources resources = Resources.getSystem();
            AssetManager assetManager = resources.getAssets();
            assetManager.getLocales();
            getResources();
            File recordfile = new File(file, getResources().getString(R.string.tp_hive_location) + ".csv");
            db_helper = new HiveDB_Helper(getApplicationContext(), "HREC.db");
            try {
                if (!recordfile.exists()) {
                    System.out.println(dbFile);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.writinword), Toast.LENGTH_SHORT).show();
                }
                SQLiteDatabase db;
                Cursor cur;
                helper = new HiveListHelper(getApplicationContext(), HiveListHelper.DATABASE_NAME, null, 1);
                db = db_helper.getReadableDatabase();
                cur = db.rawQuery(" SELECT * FROM "+  HiveDB_Helper.TABLE5, null);
                CSVWriter csvWriter = new CSVWriter(new FileWriter(recordfile));
                while (cur.moveToNext()) {
                    csvWriter.writeNext(cur.getColumnNames());
                    String[] gps = {
                            cur.getString(0),
                            cur.getString(1),
                            cur.getString(2),
                            cur.getString(3),
                            cur.getString(4),
                            cur.getString(5),
                            cur.getString(6)};

                    for (int l = 0; l < gps.length; l++) {
                        while (cur.moveToPosition(l++)) {
                            csvWriter.writeNext(gps);
                        }
                    }
                }
                csvWriter.close();
                cur.close();
                Toast.makeText(this, getResources().getString(R.string.expoto) + recordfile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                e.getCause();
            }
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    private void Insert() throws Resources.NotFoundException {
        try {

            if (!month.getText().toString().isEmpty()) {
                if (!year.getText().toString().isEmpty()) {
                    if (!date.getText().toString().isEmpty()) {
                        if (!id.getText().toString().isEmpty()) {
                            if (!hiveid.getText().toString().isEmpty()) {
                                if (!location.getText().toString().isEmpty()) {
                                    SQLiteDatabase db = helper.getWritableDatabase();
                                    ContentValues values = new ContentValues();
                                    values.put(Structure_BBDD.COLUMND2, hiveid.getText().toString());
                                    values.put(Structure_BBDD.COLUMND3, date.getText().toString());
                                    values.put(Structure_BBDD.COLUMND4, month.getText().toString());
                                    values.put(Structure_BBDD.COLUMND5, year.getText().toString());
                                    values.put(Structure_BBDD.COLUMND6, location.getText().toString());
                                    values.put(Structure_BBDD.COLUMND7, notes.getText().toString());

                                    long newRowId = db.insert(Structure_BBDD.TABLE5, null, values);
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.regsaved) + " " + newRowId, Toast.LENGTH_LONG).show();
                                    id.setText("");
                                    hiveid.setText("");
                                    date.setText("");
                                    month.setText("");
                                    year.setText("");
                                    location.setText("");
                                    notes.setText("");
                                }
                            } else {
                                Toast.makeText(this, R.string.insertdata, Toast.LENGTH_SHORT).show();
                                hiveid.setHint(R.string._x_);
                                hiveid.setHintTextColor(Color.RED);
                                date.setHint(R.string._x_);
                                date.setHintTextColor(Color.RED);
                                month.setHint(R.string._x_);
                                month.setHintTextColor(Color.RED);
                                year.setHint(R.string._x_);
                                year.setHintTextColor(Color.RED);
                                location.setHint(R.string._x_);
                                location.setHintTextColor(Color.RED);

                            }
                        } else {
                            Toast.makeText(this, R.string.insertdata, Toast.LENGTH_SHORT).show();
                            hiveid.setHint(R.string._x_);
                            hiveid.setHintTextColor(Color.RED);
                            date.setHint(R.string._x_);
                            date.setHintTextColor(Color.RED);
                            month.setHint(R.string._x_);
                            month.setHintTextColor(Color.RED);
                            year.setHint(R.string._x_);
                            year.setHintTextColor(Color.RED);
                            location.setHint(R.string._x_);
                            location.setHintTextColor(Color.RED);
                        }
                    } else {
                        Toast.makeText(this, R.string.insertdata, Toast.LENGTH_SHORT).show();
                        hiveid.setHint(R.string._x_);
                        hiveid.setHintTextColor(Color.RED);
                        date.setHint(R.string._x_);
                        date.setHintTextColor(Color.RED);
                        month.setHint(R.string._x_);
                        month.setHintTextColor(Color.RED);
                        year.setHint(R.string._x_);
                        year.setHintTextColor(Color.RED);
                        location.setHint(R.string._x_);
                    }

                } else {
                    Toast.makeText(this, R.string.insertdata, Toast.LENGTH_SHORT).show();
                    hiveid.setHint(R.string._x_);
                    hiveid.setHintTextColor(Color.RED);
                    date.setHint(R.string._x_);
                    date.setHintTextColor(Color.RED);
                    month.setHint(R.string._x_);
                    month.setHintTextColor(Color.RED);
                    year.setHint(R.string._x_);
                    year.setHintTextColor(Color.RED);
                    location.setHint(R.string._x_);
                }

            }
        } catch (Exception e) {
            e.getCause();
            e.printStackTrace();
        }

    }

    private void Update() throws Resources.NotFoundException {
        try {

            if (!month.getText().toString().isEmpty()) {
                if (!year.getText().toString().isEmpty()) {
                    if (!date.getText().toString().isEmpty()) {
                        if (!id.getText().toString().isEmpty()) {
                            if (!hiveid.getText().toString().isEmpty()) {
                                if (!location.getText().toString().isEmpty()) {
                                    SQLiteDatabase db = helper.getWritableDatabase();
                                    ContentValues values = new ContentValues();
                                    values.put(Structure_BBDD.COLUMND2, hiveid.getText().toString());
                                    values.put(Structure_BBDD.COLUMND3, date.getText().toString());
                                    values.put(Structure_BBDD.COLUMND4, month.getText().toString());
                                    values.put(Structure_BBDD.COLUMND5, year.getText().toString());
                                    values.put(Structure_BBDD.COLUMND6, location.getText().toString());
                                    values.put(Structure_BBDD.COLUMND7, notes.getText().toString());
                                    String selection = Structure_BBDD.COLUMNID + " LIKE ?";
                                    String[] selectionArgs = {id.getText().toString()};
                                    db.update(Structure_BBDD.TABLE5, values, selection, selectionArgs);
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.wasupdated) + " " + id.getText().toString() + " " + getResources().getString(R.string.successfullyU), Toast.LENGTH_LONG).show();

                                    long newRowId = db.insert(Structure_BBDD.TABLE5, null, values);
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.regsaved) + " " + newRowId, Toast.LENGTH_LONG).show();
                                    id.setText("");
                                    hiveid.setText("");
                                    date.setText("");
                                    month.setText("");
                                    year.setText("");
                                    location.setText("");
                                }
                            } else {
                                Toast.makeText(this, R.string.insertdata, Toast.LENGTH_SHORT).show();
                                hiveid.setHint(R.string._x_);
                                hiveid.setHintTextColor(Color.RED);
                                date.setHint(R.string._x_);
                                date.setHintTextColor(Color.RED);
                                month.setHint(R.string._x_);
                                month.setHintTextColor(Color.RED);
                                year.setHint(R.string._x_);
                                year.setHintTextColor(Color.RED);
                                location.setHint(R.string._x_);
                                location.setHintTextColor(Color.RED);

                            }
                        } else {
                            Toast.makeText(this, R.string.insertdata, Toast.LENGTH_SHORT).show();
                            hiveid.setHint(R.string._x_);
                            hiveid.setHintTextColor(Color.RED);
                            date.setHint(R.string._x_);
                            date.setHintTextColor(Color.RED);
                            month.setHint(R.string._x_);
                            month.setHintTextColor(Color.RED);
                            year.setHint(R.string._x_);
                            year.setHintTextColor(Color.RED);
                            location.setHint(R.string._x_);
                            location.setHintTextColor(Color.RED);
                        }
                    } else {
                        Toast.makeText(this, R.string.insertdata, Toast.LENGTH_SHORT).show();
                        hiveid.setHint(R.string._x_);
                        hiveid.setHintTextColor(Color.RED);
                        date.setHint(R.string._x_);
                        date.setHintTextColor(Color.RED);
                        month.setHint(R.string._x_);
                        month.setHintTextColor(Color.RED);
                        year.setHint(R.string._x_);
                        year.setHintTextColor(Color.RED);
                        location.setHint(R.string._x_);
                    }

                } else {
                    Toast.makeText(this, R.string.insertdata, Toast.LENGTH_SHORT).show();
                    hiveid.setHint(R.string._x_);
                    hiveid.setHintTextColor(Color.RED);
                    date.setHint(R.string._x_);
                    date.setHintTextColor(Color.RED);
                    month.setHint(R.string._x_);
                    month.setHintTextColor(Color.RED);
                    year.setHint(R.string._x_);
                    year.setHintTextColor(Color.RED);
                    location.setHint(R.string._x_);
                }

            }
        } catch (Exception e) {
            e.getCause();
            e.printStackTrace();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (mapView!= null) {
            mapView.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mapView != null) {
            mapView.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }
    private void ToOfflineMap(){
        try{
            Intent intent=new Intent(GMapActivity.this,Mapviewtst.class);
            startActivity(intent);
        }catch (Exception e){
            System.out.println(getString(R.string.loaderror));
        }
    }

}
