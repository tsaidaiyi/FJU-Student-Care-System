package com.example.student_protect;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddStudent extends AppCompatActivity {
    private Button btn_submit;
    private EditText sid, id;

    private String line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        sid = this.findViewById(R.id.editText_sid);
        id = this.findViewById(R.id.editText_id);
        btn_submit = this.findViewById(R.id.submit);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                Log.e("Thread", "Add Student");
                                String str_sid = sid.getText().toString();
                                String str_id = id.getText().toString();

                                addStudent(str_sid, str_id);

                                AddStudent.this.runOnUiThread( new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AddStudent.this, line, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                );
                thread.start();

                finish();
            }
        });
    }

    private void addStudent(String str_sid, String str_id) {
        String uid = SharedPrefManager.getInstance(getApplicationContext()).getUid();
        //String path = "http://192.168.1.107/addstudent.php?sid="+str_sid+"&id="+str_id+"&pid="+uid;
        String path = "http://daiyi.hopto.org/android_connect/addstudent.php?sid="+str_sid+"&id="+str_id+"&pid="+uid;
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
                line = bufferedReader.readLine();

                Log.e("Info", line);
            }
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
            e.printStackTrace();
        }
    }
}
