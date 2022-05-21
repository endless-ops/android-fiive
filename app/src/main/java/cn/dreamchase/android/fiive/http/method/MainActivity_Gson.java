package cn.dreamchase.android.fiive.http.method;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

import cn.dreamchase.android.fiive.R;

public class MainActivity_Gson extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 解析对象
        Gson gson = new Gson();
        String jsonStr = "{'name':'Ansen','age':20}";

        User user = gson.fromJson(jsonStr,User.class);

        Log.i("MainActivity","parseObject user:" + user.toString());

        // 解析成为数组
        String jsonString = "[{'name':'Ansen','age':20},{'name':'Lina','age':10}]";
        List<User> users = gson.fromJson(jsonString,new TypeToken<List<User>>(){}.getType());
        for (int i = 0; i < users.size(); i++) {
            Log.i("MainActivity","parseArrayList user:" + users.get(i));
        }


        // 解析成Map
        String jsonSt = "{'1':{'name':'Ansen','age':20},'2':{'name':'Lina','age':20}}";
        Map<String,User> userMap = gson.fromJson(jsonSt,new TypeToken<Map<String,User>>(){}.getType());

        for (String key : userMap.keySet()) {
            Log.i("MainActivity","parseArrayList-->" + "key :" + key + "   user:" + userMap.get(key));
        }

        // 对象解析成JSON字符串
        User user1 = new User();

        user.setAge(11);
        user.setName("nime");
        String json = gson.toJson(user);

        Log.i("MainActivity","String :" + json);
    }


}