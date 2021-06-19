package com.example.student_protect;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SelectGps extends AppCompatActivity {
    Button btn_showMaps;

    String[] students;
    String[] dates;
    String selectedSid;
    Spinner sp_student_pick;
    Spinner sp_date_pick;

    ArrayAdapter<String> sidAdapter;
    ArrayAdapter<String> dateAdapter;
    //String URL = "http://192.168.1.107/";
    String URL = "http://daiyi.hopto.org/android_connect/";

    int mapType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_gps);

        btn_showMaps = this.findViewById(R.id.showMaps);
        sp_student_pick = this.findViewById(R.id.studentPick);
        sp_date_pick = this.findViewById(R.id.datePick);

        mapType = SharedPrefManager.getInstance(getApplicationContext()).getMapType();
        if (mapType == 1) {// real time
            sp_date_pick.setEnabled(false);
        }

//        final Activity activity = this;
        btn_showMaps.setEnabled(false);

        Thread thread = new Thread(
            new Runnable() {
                @Override
                public void run() {
//                    String tid = getIntent().getStringExtra("tid");
//                    String uid = SharedPrefManager.getInstance(getApplicationContext()).getUid();
////                    Log.e("UID", uid);
////                    get_studentlist(uid);
                    get_studentlist();

                    SelectGps.this.runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            Log.e("SP", "Set Adapter");
                            sidAdapter = new ArrayAdapter<String>(SelectGps.this,
                                    android.R.layout.simple_spinner_item, students);
                            sidAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

//                            for (int i=0; i<adapter.getCount(); i++)
//                                Log.e("Info", adapter.getItem(i));
                            sp_student_pick.setAdapter(sidAdapter);
                        }
                    });
                }});
        thread.start();

        sp_student_pick.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSid = parent.getItemAtPosition(position).toString();
                Log.e("Selected", selectedSid);

                Thread thread = new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            get_datelist(selectedSid);

                            SelectGps.this.runOnUiThread( new Runnable() {
                                @Override
                                public void run() {
                                    btn_showMaps.setEnabled(false);

                                    Log.e("SP", "Set Adapter");
                                    dateAdapter = new ArrayAdapter<String>(SelectGps.this,
                                            android.R.layout.simple_spinner_item, dates);
                                    dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

//                            for (int i=0; i<adapter.getCount(); i++)
//                                Log.e("Info", adapter.getItem(i));
                                    sp_date_pick.setAdapter(dateAdapter);

                                    if (dateAdapter.getCount() <= 0)
                                        btn_showMaps.setEnabled(false);
                                    else
                                        btn_showMaps.setEnabled(true);

                                }
                            });
                        }});
                thread.start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                btn_showMaps.setEnabled(false);
            }
        });

        btn_showMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mapType == 0) {  // tracer
                    Intent intent = new Intent(SelectGps.this, MapsActivity.class);
                    intent.putExtra("uid", sp_student_pick.getSelectedItem().toString());
                    intent.putExtra("date", sp_date_pick.getSelectedItem().toString());
                    startActivity(intent);
                } else if (mapType == 1) {
                    Intent intent = new Intent(SelectGps.this, RealLocation.class);
                    intent.putExtra("uid", sp_student_pick.getSelectedItem().toString());
                    startActivity(intent);
                }
            }
        });
    }

//    private void get_studentlist(String tid) {
    private void get_studentlist() {
        String uid = SharedPrefManager.getInstance(getApplicationContext()).getUid();
        String identity = SharedPrefManager.getInstance(getApplicationContext()).getIdentity();
        String path=null;
        if (identity.equals("1")) {
            path = URL+"get_students.php?tid="+uid;
        }
        else if (identity.equals("2")) {
            path = URL+"get_students.php?pid="+uid;
        }

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
                InputStream inputStream = con.getInputStream();
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(inputStream));
                String line = bufferedReader.readLine();

                Log.e("Info", line);
                JSONArray sidArray = new JSONArray(line);
                students = new String[sidArray.length()];
                for (int i = 0; i < sidArray.length(); i++) {
                    students[i] = sidArray.getString(i);
                    Log.e("DATA", students[i]);
                }
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

    private void get_datelist(String sid) {
        String path = URL+"get_dates.php?uid="+sid;
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
                InputStream inputStream = con.getInputStream();
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(inputStream));
                String line = bufferedReader.readLine();

                Log.e("Info", line);
                JSONArray dateArray = new JSONArray(line);
                dates = new String[dateArray.length()];
                for (int i = 0; i < dateArray.length(); i++) {
                    dates[i] = dateArray.getString(i);
                    Log.e("DATA", dates[i]);
                }
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
}
