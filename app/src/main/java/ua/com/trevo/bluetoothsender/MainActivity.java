package ua.com.trevo.bluetoothsender;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TabHost;
import java.util.UUID;
import java.io.IOException;
import java.io.OutputStream;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity implements OnClickListener{

    private ToggleButton PWM1;
    private ToggleButton PWM2;
    private ToggleButton PWM3;
    private Switch SW1;
    private Switch SW2;
    private Switch SW3;
    private Button SendFromTerminal;
    private EditText TextToSend;

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address = "20:16:11:23:99:51";

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("TabHost");

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");

        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator(getString(R.string.tabLabel1));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator(getString(R.string.tabLabel2));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag3");
        tabSpec.setContent(R.id.tab3);
        tabSpec.setIndicator(getString(R.string.tabLabel3));
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);

        PWM1 = (ToggleButton) findViewById(R.id.toggleButton1);
        PWM2 = (ToggleButton) findViewById(R.id.toggleButton2);
        PWM3 = (ToggleButton) findViewById(R.id.toggleButton3);

        SW1 = (Switch) findViewById(R.id.switch1);
        SW2 = (Switch) findViewById(R.id.switch2);
        SW3 = (Switch) findViewById(R.id.switch3);

        TextToSend = (EditText) findViewById(R.id.editSend);

        SendFromTerminal = (Button) findViewById(R.id.sendTerminal);

        PWM1.setOnClickListener(this); SW1.setOnClickListener(this);
        PWM2.setOnClickListener(this); SW2.setOnClickListener(this);
        PWM3.setOnClickListener(this); SW3.setOnClickListener(this);
        SendFromTerminal.setOnClickListener(this);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!btAdapter.isEnabled()) {
            String enableBT = BluetoothAdapter.ACTION_REQUEST_ENABLE;
            startActivityForResult(new Intent(enableBT), 0);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException ignored) {
        }

        btAdapter.cancelDiscovery();

        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException ignored) {
            }
        }

        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException ignored) {

        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException ignored) {

            }
        }

        try     {
            btSocket.close();
        } catch (IOException ignored) {

        }
    }

    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        try {
            outStream.write(msgBuffer);
        } catch (IOException ignored) {

        }
    }

    @Override
    public void onClick(View v) {

        String value = "#";

        if(v == SW1) {
            if(SW1.isChecked()){value += "S1+";}
            else {value += "S1-";}
        }
        else if(v == SW2) {
            if(SW2.isChecked()){value += "S2+";}
            else {value += "S2-";}
        }
        else if(v == SW3) {
            if(SW3.isChecked()){value += "S3+";}
            else {value += "S3-";}
        }
        else if(v == PWM1) {
            if (PWM1.isChecked()) {value += "P1+";}
            else {value += "P1-";}
        }
        else if(v == PWM2) {
            if (PWM2.isChecked()) {value += "P2+";}
            else {value += "P2-";}
        }
        else if(v == PWM3) {
            if (PWM3.isChecked()) {value += "P3+";}
            else {value += "P3-";}
        }
        value += "^";
        if(v == SendFromTerminal) {
            value = TextToSend.getText().toString();
        }
        sendData(value);
    }
}