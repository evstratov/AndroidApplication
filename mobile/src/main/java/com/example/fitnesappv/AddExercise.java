package com.example.fitnesappv;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddExercise extends Activity {
    Button btn_add;
    EditText editName;
    EditText editApproaches;

    DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);

        dbHelper = new DBHelper(this);

        btn_add = (Button) findViewById(R.id.btn_add);
        editApproaches = (EditText) findViewById(R.id.editApproaches);
        editName = (EditText) findViewById(R.id.editName);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();

                contentValues.put(DBHelper.KEY_NAME, editName.getText().toString());
                contentValues.put(DBHelper.KEY_APPROACH, editApproaches.getText().toString());

                database.insert(DBHelper.TABLE_EXERCISE, null, contentValues);

                dbHelper.close();
            }
        });


    }
}
