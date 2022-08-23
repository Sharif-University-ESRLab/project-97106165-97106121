package com.example.smartcane;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private RecyclerView devicesContainer;
    private RecyclerView.LayoutManager layoutManager;
    private DevicesAdapter adapter;
    private Button searchButton;
    private Button gotoSensorsButton;
    private String selectedAddress;

    private ArrayList<Pair<String, String>> devicesList;

    private static String TAG = "Main Activity";


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

    private ArrayList<Pair<String, String>> searchForDevices() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        ArrayList<Pair<String, String>> res = new ArrayList<>();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                res.add(new Pair<>(device.getName(), device.getAddress()));
            }
        }
//        res.add(new Pair<>("Kasra headset", "AA:BB:CC:DD:EE:FF"));
//        res.add(new Pair<>("My hands free", "00:11:22:33:44:55"));
//        res.add(new Pair<>("Blind assistant", "FF:FF:FF:FF:FF:FF"));
        return res;
    }

    public void deviceClick(View target, int position) {
        final Pair<String, String> deviceInfo = devicesList.get(position);
        Toast.makeText(getBaseContext(), "Selected device with name: " + deviceInfo.first + " and address: " + deviceInfo.second, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "Selected device with name: " + deviceInfo.first + " and address: " + deviceInfo.second);
        selectedAddress = deviceInfo.second;
    }

    public void handleClick(View target) {
        int id = target.getId();
        if (id == R.id.search_button) {
            Toast.makeText(getBaseContext(), "About to search for devices", Toast.LENGTH_SHORT).show();
//            try {
//                sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            this.devicesList = this.searchForDevices();
            adapter = new DevicesAdapter(this.devicesList);
            devicesContainer.setAdapter(adapter);
            adapter.setClickListener(this::deviceClick);
            gotoSensorsButton.setEnabled(true);
        } else if (id == R.id.goto_sensors) {
            Toast.makeText(getBaseContext(), "About to switch to sensors activity", Toast.LENGTH_SHORT).show();
//            try {
//                sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            Intent intent = new Intent(this, SensorsInfoActivity.class);
            startActivity(intent);
        } else if (id == R.id.map_select) {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("selected_address", selectedAddress);
            startActivity(intent);
        }

    }

}