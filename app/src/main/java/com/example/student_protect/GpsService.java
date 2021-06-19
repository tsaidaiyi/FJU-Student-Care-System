package com.example.student_protect;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GpsService extends Service {
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private double lat, lng;

    @Override
    public void onCreate() {
        super.onCreate();
        setupGPSService();
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        Log.e("In Service", "GPS Service Attach");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mFusedLocationClient.removeLocationUpdates(locationCallback);
        Log.e("In Service", "GPS Service Detach");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void setupGPSService() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        lat = location.getLatitude();
                        lng = location.getLongitude();
                        LatLng cur = new LatLng(lat, lng);
                        Log.e("LATLON", String.format(Locale.US, "%s, %s", lat, lng));

                        Thread thread = new Thread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.e("Thread", "In GetGPS Service");

                                        String uid = SharedPrefManager.getInstance(getApplicationContext()).getUid();
                                        Date dNow = new Date();
                                        SimpleDateFormat ft =
                                                new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
                                        String dst = "24.977462, 121.541388";

                                        insertGPSInfo(uid, lat, lng, ft.format(dNow), dst);
                                    }
                                }
                        );
                        thread.start();
                    }
                }
            }
        };
    }

    private void insertGPSInfo(String uid, double lat, double lng, String datetime, String dst) {

        String gmapDistance="https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins="+lat+","+lng+"&destinations="+dst+"&key=AIzaSyDHZiszKY3BGvAOWscKo1ehR5rW4dq0eV0";
        String path="http://daiyi.hopto.org/android_connect/insert_gps_test.php?uid="+uid+"&lat="+lat+"&lng="+lng+"&date='"+datetime+"'";
        Log.e("Path", path);
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(gmapDistance).openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setDoOutput(true); // 允許輸出
            con.setDoInput(true); // 允許讀入
            con.setUseCaches(false);
            con.setRequestMethod("GET");
            con.connect();
            InputStream inputStream = con.getInputStream();
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(inputStream));
            String strJson = "";
            String line;
            while((line=bufferedReader.readLine()) != null)
                strJson += line;

            Log.e("JSON", strJson);
            JSONObject jobj = new JSONObject(strJson);
            int distance = jobj.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").getInt("value");
            Log.e("distance", distance + "m");
            if (distance <= 50)
                return;

            // write gps to database
            con= (HttpURLConnection) new URL(path).openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setDoOutput(true); // 允許輸出
            con.setDoInput(true); // 允許讀入
            con.setUseCaches(false);
            con.setRequestMethod("GET");
            con.connect();

            Log.e("getResponseCode", String.valueOf(con.getResponseCode()));
//            if(con.getResponseCode()==200){
////                Log.e("APP Insert", "insert OK!");
//////                System.out.println("insert OK!");
////            }
        } catch (IOException e) {
            Log.e("Exception", e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
            e.printStackTrace();
        }
    }
}
