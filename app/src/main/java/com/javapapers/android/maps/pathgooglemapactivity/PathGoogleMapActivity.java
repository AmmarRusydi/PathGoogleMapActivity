package com.javapapers.android.maps.pathgooglemapactivity;

//Author : Abdul Ammar Rusydi Bin Sapeai
//Date   : 28th Jan 2016
// Reference : http://javapapers.com/android/draw-path-on-google-maps-android-api/
// Reference : https://developers.google.com/maps/documentation/directions/intro#Waypoints

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

import android.graphics.Color;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PathGoogleMapActivity extends FragmentActivity {

    private static final LatLng Post1 = new LatLng(2.922961, 101.661912);
    private static final LatLng Post2 = new LatLng(2.927269, 101.656859);
    private static final LatLng Post3 = new LatLng(2.927440, 101.644328);
    private static final LatLng Post4 = new LatLng(2.918097,101.650722);

    GoogleMap googleMap;
    final String TAG = "PathGoogleMapActivity";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_google_map);

        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        googleMap = fm.getMap();

        MarkerOptions options = new MarkerOptions();
        options.position(Post1);
        options.position(Post2);
        options.position(Post3);
        options.position(Post4);
        googleMap.addMarker(options);
        String url = getMapsApiDirectionsUrl();
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Post1, 14));
        addMarkers();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private String getMapsApiDirectionsUrl() {

        String sensor = "sensor=false";
        String origin = "origin=" + Post1.latitude + "," + Post1.longitude;
        String destination = "destination=" + Post3.latitude + "," + Post3.longitude;
        String waypoints =  "waypoints=" + Post2.latitude + "," + Post2.longitude + "|" + Post4.latitude + "," + Post4.longitude;
        String key = "key=" + "AIzaSyC5lgzcmyU7HrJ6Rnk9mJbWTxXZufWU1Zk" ;
        String params = origin + "&" + destination + "&" + waypoints + "&" + sensor;
        String output = "json";
        String urls = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + params + key;

        return urls;
    }

    public void addMarkers(){

        Marker post1 = googleMap.addMarker(new MarkerOptions()
                .position(Post1)
                .title("TimeTecCloud")
                .snippet("FingerTec Research & Development Centre")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        Marker post2 = googleMap.addMarker(new MarkerOptions()
                .position(Post2)
                .title("T-System")
                .snippet("T-System Research & Development Centre")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

        Marker post3 = googleMap.addMarker(new MarkerOptions()
                .position(Post3)
                .title("MMU Univ")
                .snippet("Multimedia University")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        Marker post4 = googleMap.addMarker(new MarkerOptions()
                .position(Post4)
                .title("Shopping Mall")
                .snippet("Adidas, Nike, Al-Ikhsan")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
    }


    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }

        @Override
        protected void onPreExecute() {

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(4);
                polyLineOptions.color(Color.RED);
            }

            googleMap.addPolyline(polyLineOptions);
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_path_google_map, menu);
        return true;
    }

}