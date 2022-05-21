package cn.dreamchase.android.fiive.http.method;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;

import cn.dreamchase.android.fiive.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity_Okhttp_UploadFile extends AppCompatActivity {

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


    private void updateFile() {
        MultipartBody.Builder builder = new MultipartBody.Builder();

        builder.addFormDataPart("username","ansen");
        builder.addFormDataPart("password","123456");

        builder.setType(MultipartBody.FORM);

        MediaType mediaType = MediaType.parse("application/octet-stream");

        byte[] bytes = getUploadFileBytes();

        builder.addFormDataPart("upload_file","ansen.txt",RequestBody.create(mediaType,bytes));

        RequestBody requestBody = builder.build();

        Request.Builder builder1 = new Request.Builder();
        builder1.url("http://139.196.35.30.8080/OkHttpTest/uploadFile.do");
        builder1.post(requestBody);

        execute(builder1);


    }

    private byte[] getUploadFileBytes() {
        byte[] bytes = null;
        try {
            InputStream inputStream = getAssets().open("ansen.txt");
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return bytes;
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
            // 从response获取服务器返回的数据，转换成字符串处理
            String str = new String(response.body().bytes(),"utf-8");
            Log.i("MainActivity","onResponse:" + str);


            Message message = handler.obtainMessage();

            message.obj = str;
            message.sendToTarget();
        }
    };


}