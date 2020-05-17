package com.example.fitnesappv;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

class ExerciseInfo{
    public String name;
    public int approaches;

    public ExerciseInfo(String name, int approaches){
        this.name = name;
        this.approaches = approaches;
    }
}

public class ExerciseActivity extends Activity {
    TableLayout exercise_table;
    DBHelper dbHelper;
    Timer timerCommon;

    Button btn_stop;
    Button btn_help;
    Button btn_back;
    TextView txt_timer;
    TextView txt_curExercise;
    TextView txt_common_time;

    boolean isApproach;
    boolean isWait;
    int curExerciseIndex;
    int approach;

    List<ExerciseInfo> exerciseList;
    SimpleDateFormat minFormating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        Bundle arguments = getIntent().getExtras();
        String typeOfExercise = arguments.get("type").toString();
        String valueOfType = arguments.get("key_value").toString();

        isApproach = false;
        isWait = false;
        curExerciseIndex = 0;
        approach = 1;

        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_help = (Button) findViewById(R.id.btn_help);
        txt_timer = (TextView) findViewById(R.id.txt_timer);
        txt_curExercise = (TextView) findViewById(R.id.txt_curExercise);
        txt_common_time = (TextView) findViewById(R.id.txt_common_time);
        exercise_table = (TableLayout) findViewById(R.id.table_exercise_id);

        ReadExerciseFromDB(typeOfExercise, valueOfType);
        ShowCurExercise();

        minFormating = new SimpleDateFormat("mm:ss");
        minFormating.setTimeZone(TimeZone.getTimeZone("UTC"));

        final MediaPlayer bellSound = MediaPlayer.create(this, R.raw.bell);

        // общее время тренировки
        CommonTime();

        btn_back = (Button) findViewById(R.id.btn_back_exercise);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isWait) {
                    int seconds = 5;
                    isWait = true;
                    CountDownTimer countDownTimer = new CountDownTimer(seconds * 1000, 1000) {
                        @Override
                        public void onTick(long millis) {
                            txt_timer.setText(minFormating.format(millis));
                        }

                        @Override
                        public void onFinish() {
                            bellSound.start();
                            txt_timer.setText("Подход");
                            isApproach = true;
                            isWait = false;

                            NextApproach();

                            long[] pattern = {300, 300, 300, 300};
                            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                            if (vibrator.hasVibrator()) {
                                vibrator.vibrate(pattern, -1);
                            }
                        }
                    }.start();
                }
            }
        });

        btn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExerciseActivity.this, Help.class);
                intent.putExtra("currentExercise", exerciseList.get(curExerciseIndex).name);
                startActivity(intent);
            }
        });

        txt_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isApproach){
                    isApproach = false;
                    txt_timer.setText("");
                }
            }
        });
    }

    private void ReadExerciseFromDB(String type, String value){
        dbHelper = new DBHelper(this);
        exerciseList = new LinkedList<ExerciseInfo>();
        String selection = "";
        switch (type){
            case "GROUP":
                selection = DBHelper.KEY_GROUP + " = ?";
                break;
            case "COMPLEX":
                selection = DBHelper.KEY_COMPLEX + " = ?";
                break;
        }

        String[] selectionArgs = new String[] { value };

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_EXERCISE, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            int approachIndex = cursor.getColumnIndex(DBHelper.KEY_APPROACH);

            TableRow titleTableRow = getTableRow();
            // заголовки таблицы со списком упражнений
            TextView nameTitle = getTextView("Название");
            nameTitle.setTextColor(ContextCompat.getColor(this.getApplicationContext(), R.color.colorTitleText));
            nameTitle.setTextAppearance(R.style.TitleText);

            TextView approachTitle = getTextView("Подходы");
            approachTitle.setTextColor(ContextCompat.getColor(this.getApplicationContext(), R.color.colorTitleText));
            approachTitle.setTextAppearance(R.style.TitleText);

            titleTableRow.addView(nameTitle);
            titleTableRow.addView(approachTitle);

            exercise_table.addView(titleTableRow);

            do {
                String name = cursor.getString(nameIndex);
                String approaches = cursor.getString(approachIndex);

                exerciseList.add(new ExerciseInfo(name, Integer.parseInt(approaches)));

                final TableRow tableRow = getTableRow();

                final TextView nameText = getTextView(name);
                final TextView approachText = getTextView(approaches);

                tableRow.addView(nameText);
                tableRow.addView(approachText);

                exercise_table.addView(tableRow);
            } while (cursor.moveToNext());

        }
        cursor.close();
        dbHelper.close();
    }

    @SuppressLint("ResourceAsColor")
    private TextView getTextView(String text){
        final TextView textView = new TextView(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 0, 20, 0);
        textView.setLayoutParams(lp);

        textView.setText(text);
        textView.setTextColor(ContextCompat.getColor(this.getApplicationContext(), R.color.colorText));
        textView.setTextAppearance(R.style.SimpleText);

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
    private void ShowCurExercise(){
        String text = String.format("%s, подход: %o.", exerciseList.get(curExerciseIndex).name, approach);
        txt_curExercise.setText(text);
    }
    private void NextApproach(){
        approach++;
        if(exerciseList.get(curExerciseIndex).approaches < approach)
        {
            approach = 1;
            if(curExerciseIndex < exerciseList.size() - 1)
                curExerciseIndex++;
                // ИНАЧЕ конец тренировки
            else
                StopExercises();
        }
        ShowCurExercise();
    }

    private void CommonTime()
    {
        timerCommon = new Timer();
        final SimpleDateFormat hourFormating = new SimpleDateFormat("H:mm:ss");
        hourFormating.setTimeZone(TimeZone.getTimeZone("UTC"));
        class CommonTimerTask extends TimerTask
        {
            long passedTime = 0;

            @Override
            public void run() {
                passedTime += 1000;
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        txt_common_time.setText(hourFormating.format(passedTime));
                    }
                });
            }
        }

        timerCommon.schedule(new CommonTimerTask(), 1000, 1000);
    }
    private void StopExercises(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Конец тренировки")
                .setMessage("Время тренировки: " + txt_common_time.getText())
                .setPositiveButton("Закончить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Закрываем окно
                        Intent intent = new Intent(ExerciseActivity.this, MainActivity.class);
                        startActivity(intent);
                        dialog.cancel();
                    }
                });
        builder.create();
        builder.show();
        timerCommon.cancel();
    }
}

