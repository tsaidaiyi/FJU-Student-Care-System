package com.example.student_protect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login extends AppCompatActivity {
    private EditText username, password;
    private Button btn_login, btn_register;
    private String uid, name, identity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = this.findViewById(R.id.editText_id);
        password = this.findViewById(R.id.editText_password);

        btn_login = this.findViewById(R.id.login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int identity;
                Log.e("TEST", "In Here!!!");
                Thread thread = new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                String user = username.getText().toString();
                                String pw = password.getText().toString();
                                Log.e("TEST", "In Here"+user+pw);
                                if (checkLogin(user, pw)) {
                                    Intent intent = null;
                                   if (SharedPrefManager.getInstance(getApplicationContext()).getIdentity().equals("0")) {
                                        intent =  new Intent(Login.this, StudentActivity.class);
                                    } else if (SharedPrefManager.getInstance(getApplicationContext()).getIdentity().equals("1")) {
                                        intent =  new Intent(Login.this, TeacherActivity.class);
                                    } else if (SharedPrefManager.getInstance(getApplicationContext()).getIdentity().equals("2")) {
                                        intent =  new Intent(Login.this, ParentActivity.class);
                                    }
                                    startActivity(intent);
                                }
                            }
                        });
                thread.start();
            }
        });

        btn_register = this.findViewById(R.id.parent_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
    }

    private boolean checkLogin(String username, String password) {
        String path = "http://daiyi.hopto.org/android_connect/Login.php?username="+username+"&password="+password;
        //String path = "http://192.168.1.107/Login.php?username="+username+"&password="+password;
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
                JSONObject info = new JSONObject(line);
                int error =  info.getInt("error");
                if (error == 0) {
                    String uid = info.getString("uid");
                    String identity = info.getString("identity");
                    String name = info.getString("name");
                    String msg = info.getString("msg");

                    Log.e("MSG", msg);
                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(uid, name, identity);
                    return true;
                }
                else
                    return false;
            }
        } catch (IOException | JSONException e) {
            Log.e("IOException", e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
