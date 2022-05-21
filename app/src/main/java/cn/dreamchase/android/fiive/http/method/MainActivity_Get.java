package cn.dreamchase.android.fiive.http.method;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.dreamchase.android.fiive.R;

public class MainActivity_Get extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_get).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TestGetOrPostThread(v,"",MainActivity_Get.this,null);
            }
        });
    }

    public String getUserInfo(String usrId)  {
        String path = "http://139.196.35.30:8080/OkHttpTest/getUserInfo.do?userid=" + usrId;

        try {
            URL url = new URL(path);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setConnectTimeout(5000);   // 设置连接超时时间

            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                // 请求成功
                InputStream is = connection.getInputStream();

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