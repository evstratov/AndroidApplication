package com.example.fitnesappv;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class Group extends Activity {
    TableLayout group_table;
    DBHelper dbHelper;
    private String selectedGroup;
    Button btn_startGroup;
    Button btn_back;
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

        btn_back = (Button) findViewById(R.id.btn_back_group);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
                    int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
                    int approachIndex = cursor.getColumnIndex(DBHelper.KEY_APPROACH);

                    do {
                        final TableRow tableRow = getTableRow();

                        // Creation textView with name
                        final TextView nameText = getTextView(cursor.getString(nameIndex));

                        // Creation textView with approach count
                        final TextView approachText = getTextView(cursor.getString(approachIndex));

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

    @SuppressLint("ResourceAsColor")
    private TextView getTextView(String text){
        final TextView textView = new TextView(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 0, 20, 0);
        textView.setLayoutParams(lp);
        textView.setTextAppearance(R.style.SimpleText);
        textView.setTextColor(ContextCompat.getColor(this.getApplicationContext(), R.color.colorText));
        textView.setText(text);

        return textView;
    }
    private TableRow getTableRow(){
        final TableRow tableRow = new TableRow(this);
        TableLayout.LayoutParams lp = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 10, 0, 0);
        tableRow.setLayoutParams(lp);

        return tableRow;
    }

    private final void ClearPreviousTable() {
        group_table.removeAllViews();
    }
}
