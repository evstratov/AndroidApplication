package com.example.fitnesappv;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Group extends Activity {
    TableLayout group_table;
    DBHelper dbHelper;
    private String selectedGroup;
    Button btn_startGroup;
    Spinner group_Spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        btn_startGroup = (Button) findViewById(R.id.btn_start_group);
        group_table = (TableLayout) findViewById(R.id.group_table);

        dbHelper = new DBHelper(this);
        CreateSpinner();
        dbHelper.close();

        View.OnClickListener btnStartGroupClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Group.this, ExerciseActivity.class);
                intent.putExtra("type", "GROUP");
                intent.putExtra("key_value", selectedGroup);
                startActivity(intent);
            }
        };
        btn_startGroup.setOnClickListener(btnStartGroupClick);
    }

    private final void CreateSpinner() {
        group_Spinner = (Spinner) findViewById(R.id.groupSpinner);
        group_Spinner.setAdapter(new ArrayAdapter<DBHelper.GroupENUM>(this,
                android.R.layout.simple_spinner_item, DBHelper.GroupENUM.values()));
        group_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ClearPreviousTable();
                String selection = DBHelper.KEY_GROUP + " = ?";
                selectedGroup = group_Spinner.getSelectedItem().toString();
                String[] selectionArgs = new String[]{selectedGroup};

                SQLiteDatabase database = dbHelper.getWritableDatabase();
                Cursor cursor = database.query(DBHelper.TABLE_EXERCISE, null, selection, selectionArgs, null, null, null);

                if (cursor.moveToFirst()) {
                    int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                    int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
                    int approachIndex = cursor.getColumnIndex(DBHelper.KEY_APPROACH);
                    int groupIndex = cursor.getColumnIndex(DBHelper.KEY_GROUP);
                    int complexIndex = cursor.getColumnIndex(DBHelper.KEY_COMPLEX);

                    do {
                        final TableRow tableRow = new TableRow(Group.this);
                        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                                TableLayout.LayoutParams.WRAP_CONTENT));

                        // Creation textView with name
                        final TextView nameText = new TextView(Group.this);
                        nameText.setText(cursor.getString(nameIndex));
                        nameText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                TableRow.LayoutParams.WRAP_CONTENT));

                        // Creation textView with approach count
                        final TextView approachText = new TextView(Group.this);
                        approachText.setText(cursor.getString(approachIndex));
                        approachText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                TableRow.LayoutParams.WRAP_CONTENT));

                        tableRow.addView(nameText);
                        tableRow.addView(approachText);

                        group_table.addView(tableRow);
                    } while (cursor.moveToNext());
                }

                cursor.close();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private final void ClearPreviousTable() {
        group_table.removeAllViews();
    }
}
