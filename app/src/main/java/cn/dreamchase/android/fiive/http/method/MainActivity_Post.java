package cn.dreamchase.android.fiive.http.method;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.dreamchase.android.fiive.R;

public class MainActivity_Post extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TestGetOrPostThread(v,"",null,MainActivity_Post.this);
            }
        });

    }


    public String login(String username,String password) {
        String path = "http://139.196.35.30:8080/OkHttpTest/login.do";

        try {
            URL url = new URL(path);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");

            connection.setConnectTimeout(5000);

            String data = "username=" + username + "&password=" + password;

            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",data.length() + "");

            connection.setDoOutput(true);

            OutputStream outputStream = connection.getOutputStream();

            outputStream.write(data.getBytes());

            if (connection.getResponseCode() == 200) {
                InputStream is =  connection.getInputStream();

                return dealResponseResult(is);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String dealResponseResult(InputStream is) {
        String resultData = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        byte[] data = new byte[1024];

        int len = 0;

        try {
            while ((len = is.read(data)) != -1) {
                byteArrayOutputStream.write(data,0,len);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }
}