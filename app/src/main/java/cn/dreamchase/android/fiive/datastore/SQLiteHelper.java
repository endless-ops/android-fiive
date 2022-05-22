package cn.dreamchase.android.fiive.datastore;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.DelayQueue;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1; // 数据库的版本号
    private static final String DB_NAME = "test"; // 数据库名称

    public SQLiteHelper(Context context) {
        super(context,DB_NAME,null,VERSION);
    }


    public SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public SQLiteHelper(@Nullable Context context, @Nullable String name, int version, @NonNull SQLiteDatabase.OpenParams openParams) {
        super(context, name, version, openParams);
    }

    /**
     * -当第一次建库的时候，调用该方法
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table user(id int ,name varchar(20),age int)";

        db.execSQL(sql);


    }

    /**
     * -当更新数据库版本号的时候就会执行该方法
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
