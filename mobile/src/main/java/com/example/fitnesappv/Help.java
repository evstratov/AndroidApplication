package com.example.fitnesappv;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Help extends Activity {
    Button btn_back;
    TextView header;
    TextView content;
    DBHelper dbHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Bundle arguments = getIntent().getExtras();
        String currentExercise = arguments.get("currentExercise").toString();

        header = (TextView) findViewById(R.id.txt_header);
        content = (TextView) findViewById(R.id.txt_approaches);

        dbHelper = new DBHelper(this);
        String contentStr = GetContentByName(currentExercise);
        header.setText(currentExercise);
        content.setText(contentStr);
        dbHelper.close();

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    String GetContentByName(String name){
        String content = "";
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String selection = DBHelper.KEY_NAME + " = ?";
        String[] selectionArgs = new String[]{name};
        Cursor cursor = database.query(DBHelper.TABLE_EXERCISE, null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            int contentIndex = cursor.getColumnIndex(DBHelper.KEY_CONTENT);
            do {
                content = cursor.getString(contentIndex);
            } while (cursor.moveToNext());
        }
        return content;
    }
}
