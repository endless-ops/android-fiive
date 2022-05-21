package cn.dreamchase.android.okhttpclient.handler;

import java.io.IOException;

import cn.dreamchase.android.okhttpclient.mode.Header;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;

public abstract class HttpResponseHandler implements Callback {

    public HttpResponseHandler() {

    }

    @Override
    public void onFailure(Call call, IOException e) {
        onFailure(-1, e.getMessage().getBytes());
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        int code = response.code();

        byte[] body = response.body().bytes();

        if (code > 299) {
            onFailure(response.code(), body);
        } else {
            Headers headers = response.headers();

            Header[] hs = new Header[headers.size()];

            for (int i = 0; i < headers.size(); i++) {
                hs[i] = new Header(headers.name(i), headers.value(i));
            }

            onSuccess(code, hs, body);
        }
    }

    public void onFailure(int status, byte[] bytes) {

    }

    public abstract void onSuccess(int statusCode, Header[] headers, byte[] responseBody);
}
