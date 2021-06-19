package com.example.student_protect;

import androidx.fragment.app.FragmentActivity;

import android.app.TaskStackBuilder;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private GoogleMap mMap;
    private ArrayList<LatLng> traceLoc;
    private ArrayList<String> traceDate;

    private LatLng center;
//    private MarkerOptions userMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        traceLoc = new ArrayList();
        traceDate = new ArrayList();
//        userMarker = new MarkerOptions();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mMap != null) {
            mMap = null;
//            mMap.clear();

//            onMapReady(mMap);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLoadedCallback(this);

        mMap.setMaxZoomPreference(17);
        mMap.setMinZoomPreference(13);

        traceLoc.clear();
        traceDate.clear();

        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        String uid = getIntent().getStringExtra("uid");
                        String date = getIntent().getStringExtra("date");
                        Log.e("Thread", "In thread");
                        get_GPSs(uid, date);
                    }
                }
        );
        thread.start();
    }

    private void get_GPSs(String uid, String date) {
        // ?Sid=123456789&lat=123.45678&lng=65.345&date=2001-1-4%2012:3:50
        //String path="http://192.168.1.107/get_gps.php?uid="+uid+"&date="+date;
        String path="http://daiyi.hopto.org/android_connect/get_gps.php?uid="+uid+"&date="+date;
        Log.e("Path", path);
        try {
            HttpURLConnection con= (HttpURLConnection) new URL(path).openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setDoOutput(true); // 允許輸出
            con.setDoInput(true); // 允許讀入
            con.setUseCaches(false);
            con.setRequestMethod("GET");
            con.connect();

            if(con.getResponseCode()==200){
                Log.e("GPS", "Get GPS");
                InputStream inputStream = con.getInputStream();
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(inputStream));
                String line = bufferedReader.readLine();

                double avgLat = 0;
                double avgLng = 0;
                Log.e("Info", line);
                JSONArray gpsArray = new JSONArray(line);
                for (int i = 0; i < gpsArray.length(); i++) {
                    JSONObject gps = gpsArray.getJSONObject(i);
//                    String sid = gps.getString("Sid");
                    double lat = gps.getDouble("lat");
                    double lng = gps.getDouble("lng");
                    String time = gps.getString("time");

                    avgLat += lat;
                    avgLng += lng;
                    traceLoc.add(new LatLng(lat, lng));
                    traceDate.add(time);
                    Log.e("LOG TIME", time);
                }

                avgLat /= gpsArray.length();
                avgLng /= gpsArray.length();
                Log.e("gps", avgLat + " " + avgLng);
                center = new LatLng(avgLat, avgLng);
            }
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
            e.printStackTrace();
        }
        catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
            e.printStackTrace();
        }
    }

    private void drawPolyLine() {
        Log.e("Map Draw", "DrawPolyLine");
        PolylineOptions polylineOpt = new PolylineOptions();
        polylineOpt.addAll(traceLoc);
//        for (int i=0; i < traceLoc.size(); i++)
//            polylineOpt.add(traceLoc.get(i));

        polylineOpt.color(Color.RED);

        Polyline polyline = mMap.addPolyline(polylineOpt);
        polyline.setWidth(10);
    }
    private void drawCircle() {
        Log.e("Map Draw", "DrawCircle");

//        mMap.addCircle(new CircleOptions().center(traceLoc.get(0)).radius(4));
        BitmapDescriptor bmp = BitmapDescriptorFactory.fromResource(R.drawable.circle);
        mMap.addMarker(new MarkerOptions().position(traceLoc.get(0)).icon(bmp).title(traceDate.get(0)));

        int last = traceLoc.size()-1;
//        mMap.addCircle(new CircleOptions().center(traceLoc.get(last)).radius(4).);
        mMap.addMarker(new MarkerOptions().position(traceLoc.get(last)).icon(bmp).title(traceDate.get(last)));
    }

    @Override
    public void onMapLoaded() {
        Log.e("MapLoaded", "Set Center : " + center.latitude + " " + center.longitude);
        drawPolyLine();
        drawCircle();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(center));
    }
}
