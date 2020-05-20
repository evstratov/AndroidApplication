package com.example.fitnesappv;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class DeleteEditExercise extends Activity {
    Button btn_edit;
    Button btn_back;

    TableLayout table_exercise;

    DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_exercise);

        dbHelper = new DBHelper(this);

        table_exercise = (TableLayout) findViewById(R.id.table_delete_exercise);
        PrintTable();

        btn_back = (Button) findViewById(R.id.btn_back_delete);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void PrintTable(){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        final Cursor cursor = database.query(DBHelper.TABLE_EXERCISE, null, null,
                null, null, null, null);

        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            final int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);

            int number = 1;

            do {
                final String delOrEditNumber = cursor.getString(idIndex);

                final TableRow tableRow = getTableRow();

                final TextView numberText = getTextView(Integer.toString(number));
                final TextView nameText = getTextView(cursor.getString(nameIndex));

                final ImageButton btn_del = getImageButton();
                btn_del.setImageResource(android.R.drawable.ic_menu_delete);
                btn_del.setMinimumWidth(btn_del.getHeight());
                btn_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delRecordById(delOrEditNumber);
                    }
                });

                final ImageButton btn_edit = getImageButton();
                btn_edit.setImageResource(android.R.drawable.ic_menu_edit);
                btn_edit.setMinimumWidth(btn_del.getHeight());
                btn_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DeleteEditExercise.this, EditExercise.class);
                        intent.putExtra("editIdExercise", delOrEditNumber);
                        startActivity(intent);
                    }
                });

                tableRow.addView(numberText);
                tableRow.addView(nameText);
                tableRow.addView(btn_edit);
                tableRow.addView(btn_del);

                table_exercise.addView(tableRow);
                number++;
            } while (cursor.moveToNext());
        }
        database.close();
        cursor.close();
    }

    private void delRecordById(String id){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        if (database.delete(DBHelper.TABLE_EXERCISE, DBHelper.KEY_ID + "=?", new String[]{id}) > 0){
         Toast toast = Toast.makeText(DeleteEditExercise.this, "Удалено", Toast.LENGTH_SHORT);
         toast.show();
         database.close();
         clearPreviousTable();
         PrintTable();
        } else {
            database.close();
        }
    }
    @SuppressLint("ResourceAsColor")
    private TextView getTextView(String text){
        final TextView textView = new TextView(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 0, 20, 0);
        lp.weight = 1.0f;
        lp.gravity = Gravity.TOP;
        textView.setLayoutParams(lp);
        textView.setTextAppearance(R.style.SimpleText);
        textView.setTextColor(ContextCompat.getColor(this.getApplicationContext(), R.color.colorText));
        textView.setText(text);

        return textView;
    }
    private TableRow getTableRow(){
        final TableRow tableRow = new TableRow(this);
        TableLayout.LayoutParams lp = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,0);
        lp.setMargins(0, 10, 0, 0);
        tableRow.setBackgroundResource(R.drawable.tables);
        //tableRow.
        tableRow.setLayoutParams(lp);

        return tableRow;
    }

    private ImageButton getImageButton(){
        final ImageButton button = new ImageButton(DeleteEditExercise.this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0,10,10,10);
        button.setLayoutParams(lp);
        button.setBackgroundResource(R.drawable.buttons);
        return button;
    }
    private final void clearPreviousTable() {
        table_exercise.removeAllViews();
    }
}
