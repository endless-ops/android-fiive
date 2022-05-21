package cn.dreamchase.android.okhttpclient.method;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import cn.dreamchase.android.okhttpclient.config.HttpConfig;
import cn.dreamchase.android.okhttpclient.handler.HttpResponseHandler;
import cn.dreamchase.android.okhttpclient.handler.RequestDataCallback;
import cn.dreamchase.android.okhttpclient.mode.Header;
import cn.dreamchase.android.okhttpclient.mode.NameValuePair;
import cn.dreamchase.android.okhttpclient.progress.ProgressHelper;
import cn.dreamchase.android.okhttpclient.progress.ProgressUIListener;
import cn.dreamchase.android.okhttpclient.util.Util;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class HttpCaller {

    private static HttpCaller _instance = null;

    private OkHttpClient client;

    private Map<String, Call> requestHandleMap = null;


    private CacheControl cacheControl = null;

    private Gson gson = null;

    private HttpConfig httpConfig = new HttpConfig();

    private HttpCaller() {
    }

    public static HttpCaller getInstance() {
        if (_instance == null) {
            _instance = new HttpCaller();
        }
        return _instance;
    }

    public void setHttpConfig(HttpConfig httpConfig) {
        this.httpConfig = httpConfig;

        client = new OkHttpClient.Builder().connectTimeout(httpConfig.getConnectTimeOut(), TimeUnit.SECONDS)
                .writeTimeout(httpConfig.getWriteTimeOut(), TimeUnit.SECONDS)
                .readTimeout(httpConfig.getReadTimeOut(), TimeUnit.SECONDS)
                .build();


        gson = new Gson();

        requestHandleMap = Collections.synchronizedMap(new WeakHashMap<String, Call>());

        cacheControl = new CacheControl.Builder().noStore().noCache().build();
    }

    public <T> void get(Class<T> clazz, final String url, Header[] headers, final RequestDataCallback<T> callback) {
        this.get(clazz, url, headers, callback, true);
    }

    public <T> void get(Class<T> clazz, final String url, Header[] headers, final RequestDataCallback<T> callback, boolean autoCancel) {
        if (checkAgent()) {
            return;
        }
        add(url, getBuilder(url, headers, new MyHttpResponseHandler(clazz, url, callback)), autoCancel);
    }

    private Call getBuilder(String url, Header[] headers, HttpResponseHandler responseHandler) {
        url = Util.getMosaicParameter(url, httpConfig.getCommonField());

        Request.Builder builder = new Request.Builder();

        builder.url(url);

        builder.get();

        return execute(builder, headers, responseHandler);
    }

    private Call execute(Request.Builder builder, Header[] headers, Callback responseCallback) {
        boolean hasUa = false;

        if (headers == null) {
            builder.header("Connection", "close");

            builder.header("Accept", "*/*");

        } else {
            for (Header h : headers) {
                builder.header(h.getName(), h.getValue());
                if (!hasUa && h.getName().equals("User-Agent")) {
                    hasUa = true;
                }
            }
        }

        if (!hasUa && !TextUtils.isEmpty(httpConfig.getUserAgent())) {
            builder.header("User-Agent", httpConfig.getUserAgent());
        }

        Request request = builder.cacheControl(cacheControl).build();

        Call call = client.newCall(request);

        call.enqueue(responseCallback);

        return call;
    }


    public class MyHttpResponseHandler<T> extends HttpResponseHandler {
        private Class<T> clazz;

        private String url;

        private RequestDataCallback<T> callback;

        public MyHttpResponseHandler(Class<T> clazz, String url, RequestDataCallback<T> callback) {
            this.callback = callback;
            this.clazz = clazz;
            this.url = url;
        }

        @Override
        public void onFailure(int status, byte[] bytes) {

            clear(url);

            try {
                printLog(url + " " + status + " " + new String(bytes, "utf-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            sendCallback(callback);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            try {
                clear(url);

                String str = new String(responseBody, "utf-8");

                printLog(url + " " + statusCode + "  " + str);

                T t = gson.fromJson(str, clazz);

                sendCallback(statusCode, t, responseBody, callback);
            } catch (Exception e) {
                if (httpConfig.isAgent()) {
                    e.printStackTrace();
                    printLog("自动解析错误:  " + e.toString());
                }
                sendCallback(callback);
            }
        }


    }


    private void autoCancel(String function) {
        Call call = requestHandleMap.remove(function);
        if (call != null) {
            call.cancel();
        }
    }

    private void add(String url, Call call) {
        add(url, call, true);
    }

    private void add(String url, Call call, boolean autoCancel) {
        if (!TextUtils.isEmpty(url)) {
            if (url.contains("?")) {
                url = url.substring(0, url.indexOf("?"));
            }

            if (autoCancel) {
                autoCancel(url);
            }

            requestHandleMap.put(url, call);
        }
    }

    private void clear(String url) {
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }

        requestHandleMap.remove(url);
    }

    private void printLog(String content) {
        if (httpConfig.isDebug()) {
            Log.i(httpConfig.getTarName(), content);
        }
    }

    private boolean checkAgent() {
        if (httpConfig.isAgent()) {
            return false;
        } else {
            String proHost = android.net.Proxy.getDefaultHost();
            int proPort = android.net.Proxy.getDefaultPort();

            if (proHost == null || proPort < 0) {
                return false;
            } else {
                Log.i(httpConfig.getTarName(), "有代理，不能访问");
                return true;
            }
        }
    }


    public void updateCommonField(String key, String value) {
        httpConfig.updateCommonField(key, value);
    }

    public void removeCommonField(String key) {
        httpConfig.removeCommonField(key);
    }

    public void addCommonField(String key, String value) {
        httpConfig.addCommonField(key, value);
    }

    private <T> void sendCallback(RequestDataCallback<T> callback) {
        sendCallback(-1, null, null, callback);
    }

    private <T> void sendCallback(int status, T data, byte[] body, RequestDataCallback<T> callback) {
        CallbackMessage<T> message = new CallbackMessage<T>();

        message.body = body;
        message.status = status;
        message.data = data;
        message.callback = callback;
        Message msg = handler.obtainMessage();
        msg.obj = message;
        handler.sendMessage(msg);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            CallbackMessage data = (CallbackMessage) msg.obj;
            data.callback();
            return true;
        }
    });

    private class CallbackMessage<T> {
        public RequestDataCallback<T> callback;

        public T data;

        public byte[] body;

        public int status;

        public void callback() {
            if (callback != null) {
                if (data == null) {
                    callback.dataCallback(null);
                } else {
                    callback.dataCallback(status, data, body);
                }
            }
        }
    }


    /**
     * -以上是get请求封装
     */


    /**
     * -以下为post请求封装
     */

    public <T> void post(final Class<T> clazz, final String url, Header[] headers, List<NameValuePair> params, final RequestDataCallback<T> callback) {
        this.post(clazz, url, headers, params, callback, true);
    }

    public <T> void post(final Class<T> clazz, final String url, Header[] headers, List<NameValuePair> params, final RequestDataCallback<T> callback, boolean autoCancel) {
        if (checkAgent()) {
            return;
        }

        add(url, postBuilder(url, headers, params, new HttpCaller.MyHttpResponseHandler(clazz, url, callback)), autoCancel);

    }

    private Call postBuilder(String url, Header[] headers, List<NameValuePair> form, HttpResponseHandler responseHandler) {
        try {

            if (form == null) {
                form = new ArrayList<>();
            }


            form.addAll(httpConfig.getCommonField());
            FormBody.Builder builder = new FormBody.Builder();

            for (NameValuePair item : form) {
                builder.add(item.getName(), item.getValue());
            }

            RequestBody requestBody = builder.build();
            Request.Builder builder1 = new Request.Builder();

            builder1.url(url);
            builder1.post(requestBody);

            return execute(builder1, headers, responseHandler);
        } catch (Exception e) {
            if (responseHandler != null) {
                responseHandler.onFailure(-1, e.getMessage().getBytes());
            }

        }

        return null;
    }

    public <T> void postFile(final Class<T> clazz, final String url, Header[] headers, List<NameValuePair> form, final RequestDataCallback<T> callback) {
        postFile(url, headers, form, new HttpCaller.MyHttpResponseHandler(clazz, url, callback), null);
    }


    public <T> void postFile(final Class<T> clazz, final String url, Header[] headers, List<NameValuePair> form, final RequestDataCallback<T> callback,
                             ProgressUIListener progressUIListener) {
        add(url, postFile(url, headers, form, new HttpCaller.MyHttpResponseHandler(clazz, url, callback), progressUIListener));
    }

    public <T> void postFile(final Class<T> clazz, final String url, Header[] headers, String name, String fileName, byte[] fileContent, final RequestDataCallback<T> callback) {

        postFile(clazz, url, headers, name, fileName, fileContent, callback, null);
    }

    public <T> void postFile(final Class<T> clazz, final String url, Header[] headers, String name, String fileName, byte[] fileContent, final RequestDataCallback<T> callback, ProgressUIListener progressUIListener) {
        add(url, postFile(url, headers, name, fileName, fileContent, new HttpCaller.MyHttpResponseHandler(clazz, url, callback), progressUIListener));
    }

    private Call postFile(String url, Header[] headers, List<NameValuePair> form, HttpResponseHandler responseHandler, ProgressUIListener progressUIListener) {
        try {
            MultipartBody.Builder builder = new MultipartBody.Builder();

            builder.setType(MultipartBody.FORM);

            MediaType mediaType = MediaType.parse("application/octet-stream");

            form.addAll(httpConfig.getCommonField());

            for (int i = form.size() - 1; i >= 0; i--) {
                NameValuePair item = form.get(i);
                if (item.isFile()) {
                    File file = new File(item.getValue());

                    if (file.exists()) {
                        String fileName = Util.getFileName(item.getValue());
                        builder.addFormDataPart(item.getValue(), fileName, RequestBody.create(mediaType, file));
                    }
                } else {
                    builder.addFormDataPart(item.getName(), item.getValue());
                }
            }

            RequestBody requestBody;

            if (progressUIListener == null) {
                requestBody = builder.build();
            } else {
                requestBody = ProgressHelper.withProgress(builder.build(), progressUIListener);


            }

            Request.Builder requestBuilder = new Request.Builder();

            requestBuilder.url(url);

            requestBuilder.post(requestBody);

            return execute(requestBuilder, headers, responseHandler);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(httpConfig.getTarName(), e.toString());
            if (responseHandler != null) {
                responseHandler.onFailure(-1, e.getMessage().getBytes());
            }
        }
        return null;
    }

    private Call postFile(String url, Header[] headers, String name, String fileName, byte[] fileContent, HttpResponseHandler responseHandler, ProgressUIListener progressUIListener) {
        try {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            MediaType mediaType = MediaType.parse("application/octet-stream");
            builder.addFormDataPart(name, fileName, RequestBody.create(mediaType, fileContent));

            List<NameValuePair> form = new ArrayList<>(2);
            form.addAll(httpConfig.getCommonField());

            for (NameValuePair item : form) {
                builder.addFormDataPart(item.getName(), item.getValue());
            }

            RequestBody requestBody;

            if (progressUIListener == null) {
                requestBody = builder.build();
            } else {
                requestBody = ProgressHelper.withProgress(builder.build(), progressUIListener);
            }


            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url(url);
            requestBuilder.post(requestBody);
            return execute(requestBuilder, headers, responseHandler);
        } catch (Exception e) {
            if (httpConfig.isDebug()) {
                e.printStackTrace();
                Log.e(httpConfig.getTarName(), e.toString());
            }

            if (responseHandler != null) {
                responseHandler.onFailure(-1, e.getMessage().getBytes());
            }

        }
        return null;
    }

    /**
     * -下载文件
     *
     * @param url
     * @param saveFilePath
     */
    public void downloadFile(String url, String saveFilePath, Header[] headers, ProgressUIListener progressUIListener) {
        downloadFile(url, saveFilePath, headers, progressUIListener, true);
    }

    public void downloadFile(String url, String saveFilePath, Header[] headers, ProgressUIListener progressUIListener, boolean autoCancel) {
        if (checkAgent()) {
            return;
        }
        add(url, downloadFileSendRequest(url, saveFilePath, headers, progressUIListener), autoCancel);
    }

    private Call downloadFileSendRequest(String url, String saveFilePath, Header[] headers, ProgressUIListener progressUIListener) {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.get();
        return execute(builder, headers, new HttpCaller.DownloadFileResponseHandler(url, saveFilePath, progressUIListener));
    }

    public class DownloadFileResponseHandler implements Callback {
        private String saveFilePath;
        private ProgressUIListener progressUIListener;
        private String url;

        public DownloadFileResponseHandler(String url, String saveFilePath, ProgressUIListener progressUIListener) {
            this.url = url;
            this.saveFilePath = saveFilePath;
            this.progressUIListener = progressUIListener;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            clear(url);

            try {
                printLog(url + " " + -1 + " " + new String(e.getMessage().getBytes(), "utf-8"));
            } catch (UnsupportedEncodingException encodingException) {
                encodingException.printStackTrace();
            }
        }


        @Override
        public void onResponse(Call call, Response response) throws IOException {
            printLog(url + " code: " + response.code());

            clear(url);

            ResponseBody responseBody = ProgressHelper.withProgress(response.body(), progressUIListener);

            BufferedSource source = responseBody.source();

            File outFile = new File(saveFilePath);

            outFile.delete();

            outFile.createNewFile();

            BufferedSink sink = Okio.buffer(Okio.sink(outFile));

            source.readAll(sink);

            sink.flush();

            source.close();
        }
    }

}
