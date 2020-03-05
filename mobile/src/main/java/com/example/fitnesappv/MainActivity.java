package com.example.fitnesappv;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Button btn_stop;
    TextView txt_timer;
    TextView txt_common_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        btn_stop = (Button) findViewById(R.id.btn_stop);
        txt_timer = (TextView) findViewById(R.id.txt_timer);
        txt_common_time = (TextView) findViewById(R.id.txt_common_time);

        final MediaPlayer bellSound = MediaPlayer.create(this, R.raw.bell);

        CommonTime();

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int seconds = 5;
                txt_timer.setTextSize(70);
                CountDownTimer countDownTimer = new CountDownTimer(seconds * 1000, 1000) {
                    @Override
                    public void onTick(long millis) {
                        txt_timer.setText(Integer.toString((int) millis/1000));
                    }

                    @Override
                    public void onFinish() {
                        bellSound.start();
                        txt_timer.setTextSize(20);
                        txt_timer.setText("Подход");
                    }
                }.start();
            }
        });
    }

    private void CommonTime()
    {
        Timer timerCommon = new Timer();


        class CommonTimerTask extends TimerTask
        {
            long passedTime = 0;
            SimpleDateFormat formating = new SimpleDateFormat("HH:mm:ss");
            @Override
            public void run() {
                passedTime += 1000;
                txt_common_time.setText(formating.format(passedTime));
            }
        }
    }
}

