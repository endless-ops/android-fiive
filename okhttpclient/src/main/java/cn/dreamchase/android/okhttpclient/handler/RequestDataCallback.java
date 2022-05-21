package cn.dreamchase.android.okhttpclient.handler;

public abstract class RequestDataCallback<T> {

    public void dataCallback(T obj) {

    }

    public void dataCallback(int status, T obj) {
        dataCallback(obj);
    }

    public void dataCallback(int status, T obj, byte[] body) {
        dataCallback(status, obj);
    }
}
