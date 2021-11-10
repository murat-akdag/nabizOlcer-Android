package com.example.murat.myapplication;

import android.content.Intent;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button btnOn, btnOff;
    TextView sensorView0;
    Handler bluetoothIn;
    String sensor0;



    final int handlerState = 0;        				
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static String address;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btnOn = (Button) findViewById(R.id.buttonOn);
        btnOff = (Button) findViewById(R.id.buttonOff);
        sensorView0 = (TextView) findViewById(R.id.sensorView0);


        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {										
                    String readMessage = (String) msg.obj;                                                               
                    recDataString.append(readMessage);      								
                    int endOfLineIndex = recDataString.indexOf("#");                    
                    if (endOfLineIndex > 0) {                                           
                         if (recDataString.charAt(endOfLineIndex) == '#')								
                        {
                            sensor0 = recDataString.substring(0, endOfLineIndex);             


                            sensorView0.setText(" Nabız = " + sensor0);	

                        }
                        recDataString.delete(0, recDataString.length()); 					
                       

                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       
        checkBTState();


        
        btnOff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("0");    
                Toast.makeText(getBaseContext(), "Ölçüm Başladı", Toast.LENGTH_SHORT).show();
            }
        });

        btnOn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("1");    
                Toast.makeText(getBaseContext(), "Kişi Ekle", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), secondActivity.class);
                intent.putExtra("nabiz",sensor0);
                startActivity(intent);
            }
        });
    }

    public void gonder(){
        Intent intent=new Intent(getApplicationContext(),secondActivity.class);

        startActivity(intent);
    }
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();

        address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        mConnectedThread.write("x");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            btSocket.close();
        } catch (IOException e2) {
        }
    }

    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);        	
                    String readMessage = new String(buffer, 0, bytes);
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           
            try {
                mmOutStream.write(msgBuffer);                
            } catch (IOException e) {
                
                Toast.makeText(getBaseContext(), "Bağlantı Başarısız", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
}

