package mmu.ac.uk.hackathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,Style.OnStyleLoaded
{
    private CheckBox _mapCheckBox;
    private LinearLayout _displayBox;
    private MapView mapView;
    private MapboxMap map;
    private double _latitude;
    private double _longitude;
    private boolean _locationChanged;
    private boolean ok = true;
    private ArrayList<Station> _stations;
    private ArrayList<TableLayout> _tables;
    private TableLayout _highlightedStation;
    
    /**
     * Called when an activity is created finding all the appropriate views and asking for the required permissions if needed.
     * @param savedInstanceState Bundle to save state when app is closed
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this,getString(R.string.api_key));
        setContentView(R.layout.activity_main);
        
        
        _mapCheckBox = findViewById(R.id.mapCheck);
        _displayBox = findViewById(R.id.displayLayout);
        
        
        mapView = new MapView(this);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        
        _tables = new ArrayList<>();
        
        String[] requiredPerms = {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
        for(int i=0;i< requiredPerms.length;i++)
        {
            int result = ActivityCompat.checkSelfPermission(this,requiredPerms[i]);
            if(result != PackageManager.PERMISSION_GRANTED)
            {
                ok = false;
            }
            if(!ok)
            {
                ActivityCompat.requestPermissions(this,requiredPerms,1);
                System.exit(0);
            }
            else
            {
                try {
                    LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            _locationChanged = true;
                            _latitude = location.getLatitude();
                            _longitude = location.getLongitude();
                        }
                    
                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }
                    
                        @Override
                        public void onProviderEnabled(String provider) {
                        }
                    
                        @Override
                        public void onProviderDisabled(String provider) {
                        }
                    });
                }
                catch(Exception e)
                {
                
                }
            }
        }
    }
    
    /**
     * Called to set the style when map is ready. Part of implementation from OnMapReadyCallback.
     * @param mapBoxMap the instance of the map.
     */
    @Override
    public void onMapReady(@NonNull MapboxMap mapBoxMap)
    {
        map = mapBoxMap;
        mapBoxMap.setStyle(Style.OUTDOORS,this);
    }
    
    /**
     * Called when map is started. Part of implementation from OnMapReadyCallback.
     */
    @Override
    public void onStart()
    {
        super.onStart();
        mapView.onStart();
        _locationChanged = false;
    }
    
    /**
     * Called after the map is ready. Part of the OnStyleLoaded implementation.
     * @param style Style of the MapBoxMap
     */
    @Override
    public void onStyleLoaded(@NonNull Style style)
    {
        SymbolManager sm = new SymbolManager(mapView,map,style);
        sm.setIconAllowOverlap(true);
        sm.setTextAllowOverlap(true);
        sm.setIconOptional(false);
        
        SymbolOptions so = new SymbolOptions()
                .withLatLng(new LatLng(_latitude,_longitude))
                .withIconImage("suitcase-15")
                .withIconColor("black")
                .withIconSize(2f)
                .withTextField("You Are Here")
                .withTextSize(12f);
        sm.create(so);
    
        ArrayList<LatLng> latLngs = new ArrayList<>();
        for(Station s : _stations)
        {
            LatLng latLng = new LatLng(s.get_Latitude(),s.get_Longitude());
            latLngs.add(latLng);
                so = new SymbolOptions()
                    .withLatLng(latLng)
                    .withIconImage("suitcase-15")
                        //Wont let me change from this icon when i try any from their website.
                    .withIconColor("black")
                    .withIconSize(2f)
                    .withTextField(s.get_StationName())
                    .withTextSize(12f);
            Symbol sym =  sm.create(so);
            
            sm.addClickListener(new OnSymbolClickListener()
            {
                @Override
                public void onAnnotationClick(Symbol symbol)
                {
                    highlightStation(symbol.getTextField());
                }
            });
        }
        LatLngBounds bounds = new LatLngBounds.Builder()
                .includes(latLngs)
                .build();
        map.setCameraPosition(map.getCameraForLatLngBounds(bounds,new int[]{170,170,170,170}));
    }
    
    /**
     * Highlights the station in the table which the user has clicked the map icon of.
     * @param name Name of the station which has been clicked.
     */
    
    public void highlightStation(String name)
    {
        if (_highlightedStation != null)
        {
            _highlightedStation.setBackgroundColor(Color.WHITE);
        }
        for(int i =0; i < _stations.size(); i++)
        {
            if(_stations.get(i).get_StationName().equals(name))
            {
                _highlightedStation = _tables.get(i);
                _highlightedStation.setBackgroundColor(Color.GREEN);
            }
        }
    }
    
    /**
     * Calls the AsyncTask which runs the fetching of data from the server in a new thread.
     * @param view The view which has called the event listener
     */
    public void onSearchClick(View view)
    {
        /**
         * Runs the fetching of data from the server in a different thread.
         */
        class RunTask extends AsyncTask<Void,Void,Void>// Tried implementing with an anonymous inner class but complained about data leaks.
        {
            private StationsWebServer webServer = new StationsWebServer();
            private ArrayList<Station> stations;
            @Override
            protected Void doInBackground(Void[] values)
            {
                stations =  webServer.getAllStationsByLocation(_latitude,_longitude);
                return null;
            }
        
            @Override
            protected void onPostExecute(Void v)
            {
                displayResults(stations);
            }
        }
        RunTask runTask = new RunTask();
        runTask.execute();

    }
    
    /**
     * Takes the returned results from the user and outputs them in either a list or as a map.
     * @param stations A list of all the stations which the query from the server has returned.
     */
    private void displayResults(ArrayList<Station> stations)
    {
        _tables.clear();
        _displayBox.removeAllViews();
        for (int i = 0; i < stations.size(); i++)
        {
        
            TableLayout table = new TableLayout(this);
            table.setStretchAllColumns(true);
            
            TableRow tr1 = new TableRow(this);
            tr1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT));
        
            TextView stationName = new TextView(tr1.getContext());
            stationName.setText(stations.get(i).get_StationName());
            stationName.setTextSize(15);
        
            TextView stationDistance = new TextView(this);
            String distance = stations.get(i).get_Distance() + "km";
            stationDistance.setText(distance);
            stationDistance.setTextSize(15);
            stationDistance.setGravity(Gravity.END);
        
            TableRow tr2 = new TableRow(table.getContext());
            tr2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT));
        
            TextView stationLatitude = new TextView(tr2.getContext());
            stationLatitude.setText(String.valueOf(stations.get(i).get_Latitude()));
            stationLatitude.setTextSize(12);
        
            TextView stationLongitude = new TextView(tr2.getContext());
            stationLongitude.setText(String.valueOf(stations.get(i).get_Longitude()));
            stationLongitude.setTextSize(12);
            stationLongitude.setGravity(Gravity.END);
        
        
            tr1.addView(stationName);
            tr1.addView(stationDistance);
        
            tr2.addView(stationLatitude);
            tr2.addView(stationLongitude);
        
            table.addView(tr1);
            table.addView(tr2);
        
            _displayBox.addView(table);
            _tables.add(table);
        
        }
        if (_mapCheckBox.isChecked())
        {
            _stations = stations;
            if(_locationChanged)
            {
                mapView = new MapView(this);
                mapView.getMapAsync(this);
            }
            _displayBox.addView(mapView);
        }
    }
}
