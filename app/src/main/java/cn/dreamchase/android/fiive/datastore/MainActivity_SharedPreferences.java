package cn.dreamchase.android.fiive.datastore;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import cn.dreamchase.android.fiive.R;


public class MainActivity_SharedPreferences extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取SharedPreferences 对象的方法一：调用 Activity 对象的 getPreferences 方法
        SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();


        edit.putString("username","ansen");
        edit.commit();



        // 获取SharedPreferences 对象的方法二：调用 Context 对象的 getPreferences 方法
        /**
         * Context.MODE_PRIVATE：为默认操作模式,代表该文件是私有数据,只能被应用本身访问,在该模式下,写入的内容会覆盖原文件的内容
         * Context.MODE_APPEND：模式会检查文件是否存在,存在就往文件追加内容,否则就创建新文件.
         * Context.MODE_WORLD_READABLE和Context.MODE_WORLD_WRITEABLE用来控制其他应用是否有权限读写该文件.
         * MODE_WORLD_READABLE：表示当前文件可以被其他应用读取.
         * MODE_WORLD_WRITEABLE：表示当前文件可以被其他应用写入.
         */
        SharedPreferences filename = getApplicationContext().getSharedPreferences("filename", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = filename.edit();


        editor.putString("username","ansen");
        editor.commit();



        // 获取
        String username = filename.getString("username","");

    }








}