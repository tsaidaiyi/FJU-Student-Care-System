package com.example.student_protect;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class RealLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private String uid;
    private String date;
    private String word;
    private Timer timer;
    private LatLng center;
    private MarkerOptions userMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        userMarker = new MarkerOptions();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mMap != null) {
            mMap = null;
        }
    }

    @Override
    protected void onStop() {
        timer.cancel();
        super.onStop();
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMaxZoomPreference(17);
        mMap.setMinZoomPreference(13);

        TimerTask task= new TimerTask() {
            @Override
            public void run() {
                Log.e("Thread", "In thread");
                uid = getIntent().getStringExtra("uid");
                getRealTimeGPS(uid);
                word = uid + " " + date;
                RealLocation.this.runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        mMap.clear();
                        BitmapDescriptor bmp = BitmapDescriptorFactory.fromResource(R.drawable.circle);
                        userMarker.position(center).icon(bmp).title(word);
                        mMap.addMarker(userMarker);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(center));
                    }
                });
            }
        };

        timer = new Timer();
        timer.schedule(task, 0, 10000);
    }

    private void getRealTimeGPS(String uid) {
        String path = "http://daiyi.hopto.org/android_connect/get_real_time_gps.php?uid=" + uid;
        Log.e("Path", path);
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(path).openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setDoOutput(true); // 允許輸出
            con.setDoInput(true); // 允許讀入
            con.setUseCaches(false);
            con.setRequestMethod("GET");
            con.connect();

            if (con.getResponseCode() == 200) {
                Log.e("GPS", "Get GPS");
                InputStream inputStream = con.getInputStream();
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(inputStream));
                String line = bufferedReader.readLine();

                Log.e("Info", line);
                JSONObject gpsObj = new JSONObject(line);
                double lat = gpsObj.getDouble("lat");
                double lng = gpsObj.getDouble("lng");
                date = gpsObj.getString("date");

                Log.e("LOG TIME", date);

                center = new LatLng(lat, lng);
            }
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
            e.printStackTrace();
        }
    }
}
