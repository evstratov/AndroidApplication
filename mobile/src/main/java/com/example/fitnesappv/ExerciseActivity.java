package com.example.fitnesappv;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    Button btn_stop;
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
        txt_timer = (TextView) findViewById(R.id.txt_timer);
        txt_curExercise = (TextView) findViewById(R.id.txt_curExercise);
        txt_common_time = (TextView) findViewById(R.id.txt_common_time);
        exercise_table = (TableLayout) findViewById(R.id.table_exercise);

        ReadExerciseFromDB(typeOfExercise, valueOfType);
        ShowCurExercise();

        minFormating = new SimpleDateFormat("mm:ss");
        minFormating.setTimeZone(TimeZone.getTimeZone("UTC"));

        final MediaPlayer bellSound = MediaPlayer.create(this, R.raw.bell);

        // общее время тренировки
        CommonTime();

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

            do {
                String name = cursor.getString(nameIndex);
                String approaches = cursor.getString(approachIndex);

                exerciseList.add(new ExerciseInfo(name, Integer.parseInt(approaches)));

                final TableRow tableRow = new TableRow(this);
                tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));

                // Creation textView with name
                final TextView nameText = new TextView(this);
                nameText.setText(name);
                nameText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));

                // Creation textView with approach count
                final TextView approachText = new TextView(this);
                approachText.setText(approaches);
                approachText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));

                tableRow.addView(nameText);
                tableRow.addView(approachText);

                exercise_table.addView(tableRow);
            } while (cursor.moveToNext());

        }
        cursor.close();
        dbHelper.close();
    }

    private void ShowCurExercise(){
        String text = String.format("Упражнение: %s, подход: %o.", exerciseList.get(curExerciseIndex).name, approach);
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
        Timer timerCommon = new Timer();
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
    }
}

