package com.example.fitnesappv;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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
        final int idEditExercise = (int) arguments.get("editIdExercise");

        dbHelper = new DBHelper(this);

        editApproaches = (EditText) findViewById(R.id.editApproaches);
        editName = (EditText) findViewById(R.id.editName);
        editContent = (EditText) findViewById(R.id.editContent);
        spinnerGroupId = (Spinner) findViewById(R.id.spinner_Group_id);
        spinnerComplexId = (Spinner) findViewById(R.id.spinner_Complex_id);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        PrintEditableExercise(idEditExercise);

        btn_back = (Button) findViewById(R.id.btn_back_delete);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void PrintEditableExercise(int idEditExercise){
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
                spinnerGroupId.setSelection(2);
                spinnerComplexId.setSelection(2);
            } while (cursor.moveToNext());
        }
        database.close();
        cursor.close();
    }

}
