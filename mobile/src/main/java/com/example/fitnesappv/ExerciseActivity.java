package com.example.fitnesappv;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

public class ExerciseActivity extends Activity {

    Button btn_stop;
    TextView txt_timer;
    TextView txt_common_time;
    boolean isApproach;

    SimpleDateFormat minFormating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        isApproach = false;
        btn_stop = (Button) findViewById(R.id.btn_stop);
        txt_timer = (TextView) findViewById(R.id.txt_timer);
        txt_common_time = (TextView) findViewById(R.id.txt_common_time);

        minFormating = new SimpleDateFormat("mm:ss");
        minFormating.setTimeZone(TimeZone.getTimeZone("UTC"));

        final MediaPlayer bellSound = MediaPlayer.create(this, R.raw.bell);

        // общее время тренировки
        CommonTime();

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int seconds = 5;

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

                        long[] pattern = { 300, 300, 300, 300 };
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                        if (vibrator.hasVibrator()) {
                            vibrator.vibrate(pattern, -1);
                        }
                    }
                }.start();
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
}

