package com.example.student_protect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TeacherActivity extends AppCompatActivity {
    Button btn_gpsTracer, btn_realgpsTracer, btn_allrealgpsTracer;
    Button btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        btn_gpsTracer = this.findViewById(R.id.GPStracer);
        btn_realgpsTracer = this.findViewById(R.id.RealGPStracer);
        btn_allrealgpsTracer = this.findViewById(R.id.AllStudentGPS);
        btn_logout = this.findViewById(R.id.logout);

        btn_gpsTracer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefManager.getInstance(getApplicationContext()).setMapType(0);
                Intent intent = new Intent(TeacherActivity.this, SelectGps.class);
                startActivity(intent);
            }
        });

        btn_realgpsTracer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefManager.getInstance(getApplicationContext()).setMapType(1);
                Intent intent = new Intent(TeacherActivity.this, SelectGps.class);
                startActivity(intent);
            }
        });

        btn_allrealgpsTracer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                SharedPrefManager.getInstance(getApplicationContext()).setMapType(2);
                Intent intent = new Intent(TeacherActivity.this, AllRealLocation.class);
                startActivity(intent);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefManager.getInstance(getApplicationContext()).logout();
                finish();
            }
        });
    }
}
