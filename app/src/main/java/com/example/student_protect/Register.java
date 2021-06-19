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

public class Register extends AppCompatActivity {
    private Button btn_submit, btn_back;
    private EditText name, tel, username, pw;
    private String line;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = this.findViewById(R.id.editText_name);
        tel = this.findViewById(R.id.editText_sid);
        username = this.findViewById(R.id.editText_id);
        pw = this.findViewById(R.id.editText_password);

        btn_submit = this.findViewById(R.id.submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                Log.e("Thread", "In thread");
                                String sname = name.getText().toString();
                                String stel = tel.getText().toString();
                                String susername = username.getText().toString();
                                String spw = pw.getText().toString();

                                registerParent(sname, stel, susername, spw);

                                Register.this.runOnUiThread( new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Register.this, line, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                );
                thread.start();

                finish();
            }
        });

        btn_back = this.findViewById(R.id.back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void registerParent(String name, String tel, String username, String pw) {
        //String path="http://192.168.1.107/parent_register.php?name="+name+"&tel="+tel+"&username="+username+"&password="+pw;
        String path="http://daiyi.hopto.org/android_connect/parent_register.php?name="+name+"&tel="+tel+"&username="+username+"&password="+pw;
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
