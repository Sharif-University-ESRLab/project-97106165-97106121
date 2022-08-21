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
    public static Handler handler;
    private ArrayList<Pair<String, String>> devicesList;
    public static BluetoothSocket mmSocket;
    public static ConnectedThread connectedThread;
    public static CreateConnectThread createConnectThread;
    private static String TAG = "Main Activity";

    private final static int CONNECTING_STATUS = 1; // used in bluetooth handler to identify message status
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        devicesContainer = findViewById(R.id.devices_container);
        layoutManager = new LinearLayoutManager(this);
        devicesContainer.setLayoutManager(layoutManager);
        gotoSensorsButton = findViewById(R.id.goto_sensors);
        gotoSensorsButton.setEnabled(false);

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case CONNECTING_STATUS:
                        switch(msg.arg1){
                            case 1:
                                Log.w(TAG, "Successfully connected to device: " + selectedAddress);
                                break;
                            case -1:
                                Log.w(TAG, "Failed to connect to device: " + selectedAddress);
                                break;
                        }
                        break;

                    case MESSAGE_READ:
                        String arduinoMsg = msg.obj.toString(); // Read message from Arduino
                        Log.w(TAG, "Read new message from arduino: " + arduinoMsg);
//                        switch (arduinoMsg.toLowerCase()){
//                            case "led is turned on":
//                                imageView.setBackgroundColor(getResources().getColor(R.color.colorOn));
//                                textViewInfo.setText("Arduino Message : " + arduinoMsg);
//                                break;
//                            case "led is turned off":
//                                imageView.setBackgroundColor(getResources().getColor(R.color.colorOff));
//                                textViewInfo.setText("Arduino Message : " + arduinoMsg);
//                                break;
//                        }
                        break;
                }
            }
        };
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
            startActivity(intent);
        }

    }

    public void handleConnect(View target){
        if(this.selectedAddress == null){
            return;
        }
        findViewById(R.id.button_connect).setEnabled(false);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        createConnectThread = new CreateConnectThread(bluetoothAdapter, this.selectedAddress);
        createConnectThread.start();
    }

    public static class CreateConnectThread extends Thread {

        public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address) {
            /*
            Use a temporary object that is later assigned to mmSocket
            because mmSocket is final.
             */
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket tmp = null;
            UUID uuid = bluetoothDevice.getUuids()[0].getUuid();

            try {
                /*
                Get a BluetoothSocket to connect with the given BluetoothDevice.
                Due to Android device varieties,the method below may not work fo different devices.
                You should try using other methods i.e. :
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                 */
                tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);

            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
                Log.e("Status", "Device connected");
                handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                    Log.e("Status", "Cannot connect to device");
                    handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.run();
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    public static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes = 0; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    /*
                    Read from the InputStream from Arduino until termination character is reached.
                    Then send the whole String message to GUI Handler.
                     */
                    buffer[bytes] = (byte) mmInStream.read();
                    String readMessage;
                    if (buffer[bytes] == '\n'){
                        readMessage = new String(buffer,0,bytes);
                        Log.e("Arduino Message",readMessage);
                        handler.obtainMessage(MESSAGE_READ,readMessage).sendToTarget();
                        bytes = 0;
                    } else {
                        bytes++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes(); //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e("Send Error","Unable to send message",e);
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    @Override
    public void onBackPressed() {
        // Terminate Bluetooth Connection and close app
        if (createConnectThread != null){
            createConnectThread.cancel();
        }
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

}