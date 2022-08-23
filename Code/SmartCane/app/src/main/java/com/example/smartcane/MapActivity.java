package com.example.smartcane;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private Marker marker;
    private GoogleMap map;
    private String selectedAddress;
    private boolean zoomOnce = false;
    private static String TAG = "Map Activity";
    private double distance, prevDistance = 0;
    private boolean reached = false;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean reactToChanges = false;
    private boolean locationPickingEnabled = true;
    private boolean started = false;
    public static BluetoothSocket mmSocket;
    public static ConnectedThread connectedThread;
    public static CreateConnectThread createConnectThread;
    public static Handler handler;

    private static final double DISTANCE_TRIGGER = 5;

    private final static int CONNECTING_STATUS = 1; // used in bluetooth handler to identify message status
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update

    private final static int BEEP = 0;
    private final static int CORRECT = 1;
    private final static int WRONG = 2;
    private final static int DESTINATION = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.selectedAddress = getIntent().getStringExtra("selected_address");
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_map);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        Button startButton = findViewById(R.id.start_button);
        startButton.setEnabled(false);
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case CONNECTING_STATUS:
                        switch(msg.arg1){
                            case 1:
                                Log.e(TAG, "Successfully connected to device: " + selectedAddress);
                                break;
                            case -1:
                                Log.e(TAG, "Failed to connect to device: " + selectedAddress);
                                break;
                        }
                        break;

                    case MESSAGE_READ:
                        String arduinoMsg = msg.obj.toString(); // Read message from Arduino
                        Log.e(TAG, "Read new message from arduino: " + arduinoMsg);
                        handleArduinoMessage(arduinoMsg);
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

    @Override
    protected void onStop() {
        super.onStop();
        if (createConnectThread != null){
            createConnectThread.cancel();
        }
    }

    private void handleArduinoMessage(String msg){
        if(msg.equals("L")){
            playSound(BEEP, 1, 0);
        }
        else if(msg.equals("R")){
            playSound(BEEP, 0, 1);
        }
        else if(msg.equals("C")){
            playSound(BEEP, 1, 1);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
//        LatLng sharif = new LatLng(35.70476135, 51.35157206);
//        marker = googleMap.addMarker(new MarkerOptions()
//                .position(sharif)
//                .title("Marker in Sharif University of Technology"));
        this.map = googleMap;
        this.map.setMyLocationEnabled(true);
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationManager.getLoca, 16.0f));
        map.setOnMapLongClickListener(latLng -> handleLocationPicked(latLng));
    }

    private void handleLocationPicked(LatLng latLng){
        if(!locationPickingEnabled) return;
        resetParams();
        findViewById(R.id.start_button).setEnabled(true);
        marker = this.map.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Destination"));
    }

    private void resetParams(){
        if(marker != null) {
            marker.remove();
        }
        prevDistance = 0;
        distance = 10000000000d;
        reached = false;
    }

    private static double calcDistance(double lat1, double lon1, double lat2, double lon2){
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double r = 6371000;
        return(c * r);
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        //mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
//        MarkerOptions options = new MarkerOptions()
//                .position(latLng)
//                .title("I am here!");
//        map.addMarker(options);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
    }

    private void reactToNewDistance(double distance){
        if(distance < prevDistance - DISTANCE_TRIGGER){
            Log.e(TAG, "You got closer to destination");
            playSound(CORRECT, 1, 1);
            prevDistance = distance;
        }
        else if(distance - DISTANCE_TRIGGER > prevDistance){
            Log.e(TAG, "You got farther from destination");
            playSound(WRONG, 1, 1);
            prevDistance = distance;
        }
        if(!reached && distance < 20){
            Log.e(TAG, "You reached destination");
            playSound(DESTINATION, 1, 1);
            reached = true;
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.i(TAG, "lat: " + location.getLatitude() + ", lon: " + location.getLongitude());
        if(!reactToChanges) return;
        if(marker == null) return;
        double latSrc = location.getLatitude();
        double lonSrc = location.getLongitude();
        double latDst = marker.getPosition().latitude;
        double lonDst = marker.getPosition().longitude;
        distance = calcDistance(latSrc, lonSrc, latDst, lonDst);
        reactToNewDistance(distance);
    }

    public void handleStart(View target){
        Button button = (Button) target;
        if(!started){
            locationPickingEnabled = false;
            reactToChanges = true;
            started = true;
            button.setText("End");
            handleConnect();
            Log.e(TAG, "Routing Started");
        }
        else {
            locationPickingEnabled = true;
            reactToChanges = false;
            started = false;
            button.setText("Start");
            resetParams();
            button.setEnabled(false);
            if (createConnectThread != null){
                createConnectThread.cancel();
            }
            Log.e(TAG, "Routing Ended");
        }
    }


    public void handleConnect(){
        if(this.selectedAddress == null){
            return;
        }
//        findViewById(R.id.start_button).setEnabled(false);
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

//    @Override
//    public void onBackPressed() {
//        // Terminate Bluetooth Connection and close app
//        if (createConnectThread != null){
//            createConnectThread.cancel();
//        }
//        Intent a = new Intent(Intent.ACTION_MAIN);
//        a.addCategory(Intent.CATEGORY_HOME);
//        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(a);
//    }

    private void playSound(int sound, float leftVolume, float rightVolume){
        MediaPlayer mp;
        if(sound == BEEP) {
            mp = MediaPlayer.create(MapActivity.this, R.raw.beep1);
        }
        else if(sound == CORRECT){
            mp = MediaPlayer.create(MapActivity.this, R.raw.correct);
        }
        else if(sound == WRONG){
            mp = MediaPlayer.create(MapActivity.this, R.raw.wrong);
        }
        else if(sound == DESTINATION){
            mp = MediaPlayer.create(MapActivity.this, R.raw.destination);
        }
        else {
            return;
        }
        mp.setVolume(leftVolume, rightVolume);
        mp.start();
    }

}

    