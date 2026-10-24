package com.example.rccarcontroller;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button btnConnect;
    Button btnForward;
    Button btnBackward;
    Button btnLeft;
    Button btnRight;

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    OutputStream outputStream;

    DatabaseHelper databaseHelper;

    String deviceAddress = "98:DA:60:0D:24:12";

    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConnect = findViewById(R.id.btnConnect);
        btnForward = findViewById(R.id.btnForward);
        btnBackward = findViewById(R.id.btnBackward);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        databaseHelper = new DatabaseHelper(this);

        requestBluetoothPermission();

        btnConnect.setOnClickListener(v -> connectBluetooth());

        setupButton(btnForward, "F", "D");
        setupButton(btnBackward, "B", "D");
        setupButton(btnLeft, "L", "T");
        setupButton(btnRight, "R", "T");
    }

    private void requestBluetoothPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                        1
                );
            }
        }
    }

    private void connectBluetooth() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Bluetooth permission missing", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);

        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();

            outputStream = bluetoothSocket.getOutputStream();

            Toast.makeText(this, "Bluetooth Connected", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendCommand(String command) {

        if (outputStream != null) {
            try {
                outputStream.write(command.getBytes());

                String currentTime = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()
                ).format(new Date());

                databaseHelper.insertCommand(command, currentTime);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupButton(Button button, String commandDown, String commandUp) {

        button.setOnTouchListener((v, event) -> {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sendCommand(commandDown);
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                sendCommand(commandUp);
            }

            return true;
        });
    }
}