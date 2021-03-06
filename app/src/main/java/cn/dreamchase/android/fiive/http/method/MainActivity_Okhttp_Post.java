package cn.dreamchase.android.fiive.http.method;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import cn.dreamchase.android.fiive.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity_Okhttp_Post extends AppCompatActivity {

    private OkHttpClient okHttpClient = new OkHttpClient();

    private TextView textView;


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            String result = (String) msg.obj;
            textView.setText(result);
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.tv_show);
    }



    private void login() {
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("username","ansen");
        formBuilder.add("password","123456");
        RequestBody requestBody = formBuilder.build();



        Request.Builder builder = new Request.Builder().url("http://139.196.35.30:8080/OkHttpTest/getUserInfo.do").post(requestBody);
        execute(builder);
    }

    private void execute(Request.Builder builder) {
        Call call = okHttpClient.newCall(builder.build());
        call.enqueue(callback);
    }


    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.i("MainActivity","onFailure");
            e.printStackTrace();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            // ???response?????????????????????????????????????????????????????????
            String str = new String(response.body().bytes(),"utf-8");
            Log.i("MainActivity","onResponse:" + str);


            Message message = handler.obtainMessage();

            message.obj = str;
            message.sendToTarget();
        }
    };


}