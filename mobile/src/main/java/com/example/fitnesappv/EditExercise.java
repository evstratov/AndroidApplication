package com.example.fitnesappv;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;

public class EditExercise extends Activity {
    Button btn_ok;
    Button btn_back;

    DBHelper dbHelper;
    private EditText editApproaches;
    private EditText editName;
    private EditText editContent;
    private Spinner spinnerGroupId;
    private Spinner spinnerComplexId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exercise);

        Bundle arguments = getIntent().getExtras();
        final int idEditExercise = Integer.parseInt(arguments.getString("editIdExercise"));

        dbHelper = new DBHelper(this);

        editApproaches = (EditText) findViewById(R.id.editApproaches_edit);
        editName = (EditText) findViewById(R.id.editName_edit);
        editContent = (EditText) findViewById(R.id.editContent_edit);
        spinnerGroupId = (Spinner) findViewById(R.id.spinner_Group_id_edit);
        spinnerComplexId = (Spinner) findViewById(R.id.spinner_Complex_id_edit);

        spinnerGroupId.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, DBHelper.GroupArr));
        spinnerComplexId.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, DBHelper.ComplexArr));

        btn_back = (Button) findViewById(R.id.btn_back_edit);
        btn_ok = (Button) findViewById(R.id.btn_ok_edit);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateExerciseOnDB(idEditExercise);
            }
        });

        printEditableExercise(idEditExercise);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void printEditableExercise(int idEditExercise){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String selection = DBHelper.KEY_ID + " = ?";
        String[] selectionArgs = new String[]{Integer.toString(idEditExercise)};

        final Cursor cursor = database.query(DBHelper.TABLE_EXERCISE, null, selection,
                selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            int approachIndex = cursor.getColumnIndex(DBHelper.KEY_APPROACH);
            int contentIndex = cursor.getColumnIndex(DBHelper.KEY_CONTENT);
            int complexIndex = cursor.getColumnIndex(DBHelper.KEY_COMPLEX);
            int groupIndex = cursor.getColumnIndex(DBHelper.KEY_GROUP);

            do {
                editName.setText(cursor.getString(nameIndex));
                editApproaches.setText(cursor.getString(approachIndex));
                editContent.setText(cursor.getString(contentIndex));
                spinnerGroupId.setSelection(Arrays.asList(DBHelper.GroupArr).indexOf(cursor.getString(groupIndex)), true);
                spinnerComplexId.setSelection(Arrays.asList(DBHelper.ComplexArr).indexOf(cursor.getString(complexIndex)), true);
            } while (cursor.moveToNext());
        }
        database.close();
        cursor.close();
    }

    void updateExerciseOnDB(int idEditExercise){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String selection = DBHelper.KEY_ID + " = ?";
        String[] selectionArgs = new String[]{Integer.toString(idEditExercise)};

        final Cursor cursor = database.query(DBHelper.TABLE_EXERCISE, null, selection,
                selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            ContentValues contentValues = new ContentValues();

            contentValues.put(DBHelper.KEY_NAME, String.valueOf(editName.getText()));
            contentValues.put(DBHelper.KEY_CONTENT, String.valueOf(editContent.getText()));
            contentValues.put(DBHelper.KEY_APPROACH, Integer.parseInt(editApproaches.getText().toString()));
            contentValues.put(DBHelper.KEY_GROUP, String.valueOf(spinnerGroupId.getSelectedItem()));
            contentValues.put(DBHelper.KEY_COMPLEX, String.valueOf(spinnerGroupId.getSelectedItem()));
            contentValues.put(DBHelper.KEY_USERRECORD, 1);

            database.update(DBHelper.TABLE_EXERCISE, contentValues, DBHelper.KEY_ID +" = ?", new String[] { String.valueOf(idEditExercise) });

            Toast toast = Toast.makeText(EditExercise.this, "Обновлено", Toast.LENGTH_SHORT);
            toast.show();
        }
        database.close();
        cursor.close();
    }

}
