package cn.dreamchase.android.fiive.http.method;

import android.util.Log;
import android.view.View;
import cn.dreamchase.android.fiive.R;

/**
 * -两种网络访问方式，，在MainActivity中通过点击按钮调用，
 * 在Android中不能在主线程中访问网络（防止UI阻塞），所以需要开启一个线程来访问网络
 */
public class TestGetOrPostThread extends Thread{

    private View view;
    private String data;

    private final MainActivity_Get mainActivityGet;
    private final MainActivity_Post mainActivityPost;

    public TestGetOrPostThread(View view,String data,MainActivity_Get mainActivityGet,MainActivity_Post mainActivityPost) {
        this.view = view;
        this.data = data;
        this.mainActivityGet = mainActivityGet;
        this.mainActivityPost = mainActivityPost;
    }

    @Override
    public void run() {
        switch (view.getId()) {
            case R.id.btn_get:
                String getResult = mainActivityGet.getUserInfo(data);
                Log.i("TestGetOrPostThread","GET获取用户信息：" + getResult);
                break;
            case R.id.btn_post:
                String getRes = mainActivityPost.login("","");
                Log.i("TestGetOrPostThread","POST登录成功：" + getRes);
                break;
        }
    }
}
