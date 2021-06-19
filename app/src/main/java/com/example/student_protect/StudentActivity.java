package com.example.student_protect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class StudentActivity extends AppCompatActivity {
    ToggleButton btn_gpsExtract;
    Button btn_logout;

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        btn_gpsExtract = this.findViewById(R.id.GPSExtract);
        btn_logout = this.findViewById(R.id.logout);

        btn_gpsExtract.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
                if (isChecked) {
//                    intent = new Intent(StudentActivity.this, Getgps_Service.class);
                    intent = new Intent(StudentActivity.this, GpsService.class);
                    Log.e("Service", intent.toString());
                    startService(intent);
                    Log.e("Service", "Start GPS Service");
                }
                else {
                    if (intent != null) {
                        stopService(intent);
                    }
                    Log.e("Service", "Stop GPS Service");
                    intent = null;
                }
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

    @Override
    protected void onDestroy() {
        if (intent != null) {
            stopService(intent);
        }
        super.onDestroy();
    }
}
