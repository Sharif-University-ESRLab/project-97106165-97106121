package com.example.smartcane;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
        double res = Math.random() * 4;
        return res;
    }

    private Runnable sensorInfoUpdater = new Runnable() {
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