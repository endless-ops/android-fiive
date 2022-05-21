package cn.dreamchase.android.okhttpclient.mode;

public class NameValuePair {

    private String name;

    private String value;

    private boolean isFile = false;

    public NameValuePair() {

    }

    public NameValuePair(String name,String value) {
        this.name = name;
        this.value = value;
    }

    public NameValuePair(String name,String value,boolean isFile) {
        this.name = name;
        this.value = value;
        this.isFile = isFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }
}
