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

public class Complex extends Activity {
    TableLayout complex_table;
    DBHelper dbHelper;
    private String selectedComplex;
    Button btn_startComplex;
    Spinner complex_Spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complex);

        btn_startComplex = (Button) findViewById(R.id.btn_start_complex);
        complex_table = (TableLayout) findViewById(R.id.complex_table);

        dbHelper = new DBHelper(this);
        CreateSpinner();
        dbHelper.close();

        View.OnClickListener btnStartComplexClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Complex.this, ExerciseActivity.class);
                intent.putExtra("type", "COMPLEX");
                intent.putExtra("key_value", selectedComplex);
                startActivity(intent);
            }
        };
        btn_startComplex.setOnClickListener(btnStartComplexClick);
    }

    private final void CreateSpinner() {
        complex_Spinner = (Spinner) findViewById(R.id.complexSpinner);
        complex_Spinner.setAdapter(new ArrayAdapter<DBHelper.ComplexENUM>(this,
                android.R.layout.simple_spinner_item, DBHelper.ComplexENUM.values()));
        complex_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ClearPreviousTable();
                String selection = DBHelper.KEY_COMPLEX + " = ?";
                selectedComplex = complex_Spinner.getSelectedItem().toString();
                String[] selectionArgs = new String[] { selectedComplex };

                SQLiteDatabase database = dbHelper.getWritableDatabase();
                Cursor cursor = database.query(DBHelper.TABLE_EXERCISE, null, selection, selectionArgs, null, null, null);

                if (cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
                    int approachIndex = cursor.getColumnIndex(DBHelper.KEY_APPROACH);

                    do {
                        final TableRow tableRow = new TableRow(Complex.this);
                        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                                TableLayout.LayoutParams.WRAP_CONTENT));

                        // Creation textView with name
                        final TextView nameText = new TextView(Complex.this);
                        nameText.setText(cursor.getString(nameIndex));
                        nameText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                TableRow.LayoutParams.WRAP_CONTENT));

                        // Creation textView with approach count
                        final TextView approachText = new TextView(Complex.this);
                        approachText.setText(cursor.getString(approachIndex));
                        approachText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                TableRow.LayoutParams.WRAP_CONTENT));

                        tableRow.addView(nameText);
                        tableRow.addView(approachText);

                        complex_table.addView(tableRow);
                    } while (cursor.moveToNext());
                }

                cursor.close();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private final void ClearPreviousTable(){
        complex_table.removeAllViews();
    }
}
