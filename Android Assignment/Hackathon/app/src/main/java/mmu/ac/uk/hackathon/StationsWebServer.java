package mmu.ac.uk.hackathon;

import android.location.Location;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;

public class StationsWebServer
{
    /**
     * Returns a list of the five closest train stations to the users current location. Returns null if there was an error retrieving the information.
     * @param latitude the users location latitude
     * @param longitude the users location longitude
     * @return ArrayList of the five nearest stations sorted from closest in distance to furthest.
     */
    public ArrayList<Station> getAllStationsByLocation(double latitude, double longitude)
    {
        ArrayList<Station> stations = new ArrayList<>();
        try
        {
            URL url = new URL("http://10.0.2.2:8080/stations?lat=" + URLEncoder.encode(String.valueOf(latitude),"UTF-8") + "&lng=" + URLEncoder.encode(String.valueOf(longitude),"UTF-8"));
            HttpURLConnection tc = (HttpURLConnection) url.openConnection();
            InputStreamReader isr = new InputStreamReader(tc.getInputStream());
            BufferedReader in = new BufferedReader(isr);
            String line;
            while ((line = in.readLine()) != null)
            {
                JSONArray ja = new JSONArray(line);
            
                for (int i = 0; i < ja.length(); i++)
                {
                    JSONObject jo = (JSONObject) ja.get(i);
                    double stationLat = jo.getDouble("Latitude");
                    double stationLng = jo.getDouble("Longitude");
                    float[] results = new float[1];
                    Location.distanceBetween(latitude,longitude,stationLat,stationLng,results);
                    Station s = new Station(jo.getString("StationName"),stationLat,stationLng,Math.round(results[0]));
                    stations.add(s);
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                stations.sort(new Comparator<Station>()
                {
                    @Override
                    public int compare(Station s1, Station s2)
                    {
                        return (Double.compare(s1.get_Distance(),s2.get_Distance()));
                    }
                });
            }
            return stations;
        }
        catch(Exception e)
        {
            return null;
        }
    }

}
