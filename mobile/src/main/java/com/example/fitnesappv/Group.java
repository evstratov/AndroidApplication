package com.example.fitnesappv;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Group extends Activity {
    TableLayout group_table;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        group_table = (TableLayout) findViewById(R.id.group_table);

        dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query(DBHelper.TABLE_EXERCISE, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            int approachIndex = cursor.getColumnIndex(DBHelper.KEY_APPROACH);
            do {
                final TableRow tableRow = new TableRow(this);
                tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

                // Creation textView with name
                final TextView nameText = new TextView(this);
                nameText.setText(cursor.getString(nameIndex));
                nameText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                // Creation textView with approach count
                final TextView approachText = new TextView(this);
                approachText.setText(cursor.getString(approachIndex));
                approachText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                tableRow.addView(nameText);
                tableRow.addView(approachText);

                group_table.addView(tableRow);
            } while (cursor.moveToNext());
        }

        cursor.close();
        dbHelper.close();
    }
}
