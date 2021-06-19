package com.example.student_protect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ParentActivity extends AppCompatActivity {
    Button btn_gpsTracer, btn_realgpsTracer;
    Button btn_addStudent, btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        btn_gpsTracer = this.findViewById(R.id.GPStracer);
        btn_logout = this.findViewById(R.id.logout);
        btn_addStudent = this.findViewById(R.id.addStudent);
        btn_realgpsTracer = this.findViewById(R.id.RealGPStracer);

        btn_gpsTracer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefManager.getInstance(getApplicationContext()).setMapType(0);
                Intent intent = new Intent(ParentActivity.this, SelectGps.class);
                startActivity(intent);
            }
        });
        btn_realgpsTracer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefManager.getInstance(getApplicationContext()).setMapType(1);
                Intent intent = new Intent(ParentActivity.this, SelectGps.class);
                startActivity(intent);
            }
        });
        btn_addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ParentActivity.this, AddStudent.class);
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
