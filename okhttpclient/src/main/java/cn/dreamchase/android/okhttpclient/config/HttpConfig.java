package cn.dreamchase.android.okhttpclient.config;

import java.util.ArrayList;
import java.util.List;

import cn.dreamchase.android.okhttpclient.mode.NameValuePair;

public class HttpConfig {
    private boolean debug = false; //true:debug 模式
    private String userAgent = ""; // 用户代丽，它是一个特殊字符串头，是的服务器能够识别客户使用的操作系统及版本，
    // CPU类型、浏览器、浏览器渲染引擎、浏览器语言，浏览器插件
    private boolean agent = true; // 有代理的情况能不能访问。true：有代理能访问。false：有代理不能访问

    private String tarName = "Http";

    private int connectTimeOut = 10;

    private int writeTimeOut = 10;

    private int readTimeOut = 10;

    private List<NameValuePair> commonField = new ArrayList<>();

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public boolean isAgent() {
        return agent;
    }

    public void setAgent(boolean agent) {
        this.agent = agent;
    }

    public String getTarName() {
        return tarName;
    }

    public void setTarName(String tarName) {
        this.tarName = tarName;
    }

    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public int getWriteTimeOut() {
        return writeTimeOut;
    }

    public void setWriteTimeOut(int writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
    }

    public int getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public List<NameValuePair> getCommonField() {
        return commonField;
    }


    public void updateCommonField(String key, String value) {
        boolean result = true;

        for (int i = 0; i < commonField.size(); i++) {
            NameValuePair nameValuePair = commonField.get(i);
            if (nameValuePair.getName().equals(key)) {
                commonField.set(i, new NameValuePair(key, value));
                result = false;
                break;
            }
        }

        if (result) {
            commonField.add(new NameValuePair(key, value));
        }

    }

    public void removeCommonField(String key) {
        for (int i = commonField.size() - 1; i >= 0; i--) {
            if (commonField.get(i).equals(key)) {
                commonField.remove(i);
            }
        }
    }

    public void addCommonField(String key, String value) {
        commonField.add(new NameValuePair(key, value));
    }
}
