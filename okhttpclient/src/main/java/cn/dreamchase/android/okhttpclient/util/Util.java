package cn.dreamchase.android.okhttpclient.util;

import android.text.TextUtils;

import java.net.URLEncoder;
import java.util.List;

import cn.dreamchase.android.okhttpclient.mode.NameValuePair;

public class Util {


    public static String getFileName(String filename) {
        int start = filename.lastIndexOf("/");

        if (start != -1) {
            return filename.substring(start + 1, filename.length());
        } else {
            return null;
        }
    }

    public static String getMosaicParameter(String url, List<NameValuePair> commonField) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }

        if (url.contains("?")) {
            url = url + "&";
        }else {
            url = url + "?";
        }

        url += getCommonFieldString(commonField);
        return url;
    }

    private static String getCommonFieldString(List<NameValuePair> commonField) {
        StringBuffer buffer = new StringBuffer();

        try{
            int i = 0;

            for (NameValuePair item : commonField) {
                if (i > 0) {
                    buffer.append("&");
                }

                buffer.append(item.getName());
                buffer.append("=");
                buffer.append(URLEncoder.encode(item.getValue(),"utf-8"));
                i++;
            }


        }catch (Exception e){

        }
        return buffer.toString();
    }
}
