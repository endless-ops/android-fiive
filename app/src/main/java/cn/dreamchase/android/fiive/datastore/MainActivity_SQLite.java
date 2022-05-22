package cn.dreamchase.android.fiive.datastore;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import cn.dreamchase.android.fiive.R;


public class MainActivity_SQLite extends AppCompatActivity {

    private SQLiteDatabase readableDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }


    public void createDb (View view) {
        SQLiteHelper sqLiteHelper = new SQLiteHelper(this);

        readableDatabase = sqLiteHelper.getReadableDatabase();
    }


    public void addRecord (View view) {
        ContentValues values = new ContentValues();
        values.put("id",1);
        values.put("name","test1");
        values.put("age",11);

        readableDatabase.insert("user",null,values);
    }



    public void updateRecord(View view) {
        ContentValues values = new ContentValues();

        values.put("age",18);

        readableDatabase.update("user",values,"name=?",new String []{"test1"});
    }


    public void findStudent(View view) {
        Cursor cursor = readableDatabase.query("user",new String[]{"id","name","age"},null,null,null,null,null);


        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            int age = cursor.getInt(2);
        }

        cursor.close();
    }

    public void delete(View view) {
        readableDatabase.delete("user","name=?",new String[]{"test1"});
    }
}