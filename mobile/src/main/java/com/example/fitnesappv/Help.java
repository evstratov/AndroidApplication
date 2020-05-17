package com.example.fitnesappv;

import android.app.Activity;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class Help extends Activity {
    Button btn_back;
    TextView header;
    TextView content;
    DBHelper dbHelper;

    TableLayout tableLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Bundle arguments = getIntent().getExtras();
        String currentExercise = arguments.get("currentExercise").toString();

        header = (TextView) findViewById(R.id.txt_header);
        content = (TextView) findViewById(R.id.txt_approaches);
        tableLayout = (TableLayout) findViewById(R.id.tablelayout_help_id);

        dbHelper = new DBHelper(this);
        String contentStr = null;
        try {
            contentStr = GetContentByName(currentExercise);
        } catch (IOException e) {
            e.printStackTrace();
        }
        header.setTextAppearance(R.style.TitleText);
        header.setText(currentExercise);

        content.setText(contentStr);
        content.setTextAppearance(R.style.SimpleText);
        dbHelper.close();

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    String GetContentByName(String name) throws IOException {
        String content = "";
        String folderPath = "";

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String selection = DBHelper.KEY_NAME + " = ?";
        String[] selectionArgs = new String[]{name};
        Cursor cursor = database.query(DBHelper.TABLE_EXERCISE, null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            int contentIndex = cursor.getColumnIndex(DBHelper.KEY_CONTENT);
            int imageIndex = cursor.getColumnIndex(DBHelper.KEY_IMAGES);
            do {
                content = cursor.getString(contentIndex);
                folderPath = cursor.getString(imageIndex);

                String[] fileNames = getAssets().list(folderPath);
                for(String imageName:fileNames){
                    final TableRow tableRow = getTableRow();

                    final ImageView image = new ImageView(Help.this);

                    String imagePath = folderPath + "/" + imageName;
                    InputStream istr = getApplicationContext().getAssets().open(imagePath);
                    image.setImageDrawable(Drawable.createFromStream(istr, null));

                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.MATCH_PARENT);
                    lp.setMargins(0, 10, 0, 10);

                    image.setLayoutParams(lp);

                    tableRow.addView(image);
                    tableLayout.addView(tableRow);
                }
            } while (cursor.moveToNext());
        }
        return content;
    }

    private TableRow getTableRow(){
        final TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        return tableRow;
    }
}
