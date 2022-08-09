package com.example.smartcane;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView devicesContainer;
    private RecyclerView.LayoutManager layoutManager;
    private DevicesAdapter adapter;
    private Button searchButton;
    private Button gotoSensorsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        devicesContainer = findViewById(R.id.devices_container);
        layoutManager = new LinearLayoutManager(this);
        devicesContainer.setLayoutManager(layoutManager);
        gotoSensorsButton = findViewById(R.id.goto_sensors);
        gotoSensorsButton.setEnabled(false);
    }

    private ArrayList<Pair<String, String>> searchForDevices(){
        ArrayList<Pair<String, String>> res = new ArrayList<>();
        res.add(new Pair<>("Kasra's headset", "AA:BB:CC:DD:EE:FF"));
        res.add(new Pair<>("My hands free", "00:11:22:33:44:55"));
        res.add(new Pair<>("Blind assistant", "FF:FF:FF:FF:FF:FF"));
        return res;
    }

    public void handleClick(View target){
        switch (target.getId()){
            case R.id.search_button:
                Toast.makeText(this, "About to search for devices", Toast.LENGTH_SHORT).show();
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ArrayList<Pair<String, String>> devicesList = this.searchForDevices();
                adapter = new DevicesAdapter(devicesList);
                devicesContainer.setAdapter(adapter);
                gotoSensorsButton.setEnabled(true);
                break;
            case R.id.goto_sensors:
                Toast.makeText(this, "About to switch to sensors activity", Toast.LENGTH_SHORT).show();
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(this, SensorsInfoActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


}