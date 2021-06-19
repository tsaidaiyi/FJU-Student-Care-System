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
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class AllRealLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Timer timer;
    private BitmapDescriptor bmp;
    private String line;
    private boolean isLockCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        bmp = BitmapDescriptorFactory.fromResource(R.drawable.circle);
        isLockCamera = false;
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

        //mMap.setMaxZoomPreference(17);
        //mMap.setMinZoomPreference(13);

        mMap.setMaxZoomPreference(17);
        mMap.setMinZoomPreference(10);

        TimerTask task= new TimerTask() {
            @Override
            public void run() {
                Log.e("Thread", "In thread");
                getRealTimeGPS();
                AllRealLocation.this.runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        mMap.clear();

                        try {
                            double avgLat = 0;
                            double avgLng = 0;
                            JSONArray gpsArray = new JSONArray(line);
                            for (int i = 0; i < gpsArray.length(); i++) {
                                JSONObject gps = gpsArray.getJSONObject(i);
                                String sid = gps.getString("uid");
                                double lat = gps.getDouble("lat");
                                double lng = gps.getDouble("lng");
                                String date = gps.getString("date");

                                avgLat += lat;
                                avgLng += lng;

//                                Log.e("LOG TIME", date);

                                String word = sid+" "+date;
                                mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).icon(bmp).title(word));
                            }

                            if (!isLockCamera) {
                                avgLat /= gpsArray.length();
                                avgLng /= gpsArray.length();
                                Log.e("LOG TIME", avgLat + " " + avgLng);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(avgLat, avgLng)));

                                isLockCamera = true;
                            }
                        }  catch (JSONException e) {
                            Log.e("JSONException", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        timer = new Timer();
        timer.schedule(task, 0, 10000);
    }

    private void getRealTimeGPS() {
        String uid = SharedPrefManager.getInstance(getApplicationContext()).getUid();
        String path = "http://daiyi.hopto.org/android_connect/get_all_real_time_gps.php?tid=" + uid;
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
                line = bufferedReader.readLine();

                Log.e("Info", line);
            }
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
            e.printStackTrace();
        }
    }
}
