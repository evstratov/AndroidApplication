package com.example.fitnesappv;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddExercise extends Activity {
    Button btn_add;
    Button btn_back;
    EditText editName;
    EditText editApproaches;
    EditText editContent;
    Spinner spinnerGroupId;
    Spinner spinnerComplexId;

    DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);

        dbHelper = new DBHelper(this);

        btn_add = (Button) findViewById(R.id.btn_add);
        editApproaches = (EditText) findViewById(R.id.editApproaches);
        editName = (EditText) findViewById(R.id.editName);
        editContent = (EditText) findViewById(R.id.editContent);
        spinnerGroupId = (Spinner) findViewById(R.id.spinner_Group_id);
        spinnerComplexId = (Spinner) findViewById(R.id.spinner_Complex_id);

        spinnerGroupId.setAdapter(new ArrayAdapter<DBHelper.GroupENUM>(this,
                android.R.layout.simple_spinner_item, DBHelper.GroupENUM.values()));
        spinnerComplexId.setAdapter(new ArrayAdapter<DBHelper.ComplexENUM>(this,
                android.R.layout.simple_spinner_item, DBHelper.ComplexENUM.values()));

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFillColums()){
                    SQLiteDatabase database = dbHelper.getWritableDatabase();
                    ContentValues contentValues = new ContentValues();

                    contentValues.put(DBHelper.KEY_NAME, editName.getText().toString());
                    contentValues.put(DBHelper.KEY_APPROACH, editApproaches.getText().toString());
                    contentValues.put(DBHelper.KEY_CONTENT, editContent.getText().toString());
                    contentValues.put(DBHelper.KEY_GROUP, spinnerGroupId.getSelectedItem().toString());
                    contentValues.put(DBHelper.KEY_COMPLEX, spinnerComplexId.getSelectedItem().toString());

                    database.insert(DBHelper.TABLE_EXERCISE, null, contentValues);


                    Toast toast = Toast.makeText(AddExercise.this, "Добавлено", Toast.LENGTH_SHORT);
                    toast.show();
                }

                dbHelper.close();
            }
        });

        btn_back = (Button) findViewById(R.id.btn_back_add);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private boolean isFillColums(){
        if(spinnerGroupId.getSelectedItem().toString().equals("ПУСТО") ||
                spinnerComplexId.getSelectedItem().toString().equals("ПУСТО")||
                editName.getText().toString().equals("") ||
                editApproaches.getText().toString().equals("") ||
                editContent.getText().toString().equals("")){

            new AlertDialog.Builder(AddExercise.this)
                    .setMessage("Не все поля заполнены")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return  false;
        } else
            return  true;
    }
}
