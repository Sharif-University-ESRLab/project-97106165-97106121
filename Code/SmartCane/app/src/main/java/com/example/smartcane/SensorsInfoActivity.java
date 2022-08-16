package com.example.smartcane;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;

public class SensorsInfoActivity extends AppCompatActivity {
    static long updateRate = 100;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    Handler handler = new Handler();
    Thread updaterThread;
    String TAG = "Sensor Info Activity";

    private void updateSensorsInfo(double top, double bottom, double left, double right) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                TextView tvTop = findViewById(R.id.textview_top);
                TextView tvBottom = findViewById(R.id.textview_bottom);
                TextView tvLeft = findViewById(R.id.textview_left);
                TextView tvRight = findViewById(R.id.textview_right);
                tvTop.setText(df.format(top));
                tvBottom.setText(df.format(bottom));
                tvLeft.setText(df.format(left));
                tvRight.setText(df.format(right));
            }
        };
        handler.post(r);
    }

    private double getRandom() {
        return Math.random() * 4;
    }
    private void playBeep(float leftVolume, float rightVolume){
        MediaPlayer mp = MediaPlayer.create(SensorsInfoActivity.this, R.raw.beep1);
        mp.setVolume(leftVolume, rightVolume);
        mp.start();
    }
    public void playAudio(View target){
        int id = target.getId();
        if(id == R.id.left_button){
            playBeep(1, 0);
        }
        else if(id == R.id.middle_button){
            playBeep(1, 1);
        }
        else if(id == R.id.right_button){
            playBeep(0, 1);
        }
    }

    private final Runnable sensorInfoUpdater = new Runnable() {
        @Override
        public void run() {
            while (true) {
                double top = getRandom();
                double bottom = getRandom();
                double right = getRandom();
                double left = getRandom();
                Log.i(TAG,
                        "top = " + df.format(top) +
                                ", bottom = " + df.format(bottom) +
                                ", left = " + df.format(left) +
                                ", right = " + df.format(right));
                updateSensorsInfo(top, bottom, left, right);
                synchronized (this) {
                    try {
                        wait(updateRate);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors_info);
        if (updaterThread == null) {
            updaterThread = new Thread(sensorInfoUpdater);
            Log.i(TAG, "created updater thread");
        }
        updaterThread.start();
        Log.i(TAG, "started updater thread");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (updaterThread != null) {
            updaterThread.interrupt();
            Log.i(TAG, "killed updater thread");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}