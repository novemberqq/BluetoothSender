package ua.com.trevo.bluetoothsender;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TabHost;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity implements OnClickListener{

    ToggleButton PWM1;
    ToggleButton PWM2;
    ToggleButton PWM3;
    Switch SW1;
    Switch SW2;
    Switch SW3;
    Button SendFromTerminal;
    Button ConnectToDevice;
    EditText DeviceIdEdit;

    BluetoothSocket clientSocket;

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
        DeviceIdEdit = (EditText) findViewById(R.id.deviceID);

        SendFromTerminal = (Button) findViewById(R.id.sendTerminal);
        ConnectToDevice = (Button) findViewById(R.id.connectDevice);


        PWM1.setOnClickListener(this); SW1.setOnClickListener(this);
        PWM2.setOnClickListener(this); SW2.setOnClickListener(this);
        PWM3.setOnClickListener(this); SW3.setOnClickListener(this);
        SendFromTerminal.setOnClickListener(this);
        ConnectToDevice.setOnClickListener(this);

        String enableBT = BluetoothAdapter.ACTION_REQUEST_ENABLE;
        startActivityForResult(new Intent(enableBT), 0);
    }

    @Override
    public void onClick(View v) {

            try {
                OutputStream outStream = clientSocket.getOutputStream();

                int value = 0;

                if(v == SW1){
                    value = (SW1.isChecked() ? 1 : 0) + 2;
                }else if(v == SW2){
                    value = (SW2.isChecked() ? 1 : 0) + 4;
                }else if(v == SW3){
                    value = (SW3.isChecked() ? 1 : 0) + 8;
                }else if (v == PWM1) {
                    value = (PWM1.isChecked() ? 1 : 0) + 16;
                } else if (v == PWM2) {
                    value = (PWM2.isChecked() ? 1 : 0) + 32;
                } else if (v == PWM3) {
                    value = (PWM3.isChecked() ? 1 : 0) + 64;
                } else if(v == SendFromTerminal){

                } else if(v == ConnectToDevice) {

                    BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

                    Editable str  = DeviceIdEdit.getText();

                    try{
                        BluetoothDevice device = bluetooth.getRemoteDevice(str.toString());

                        Method m = device.getClass().getMethod(
                                "createRfcommSocket", int.class);

                        clientSocket = (BluetoothSocket) m.invoke(device, 1);
                        clientSocket.connect();

                        //В случае появления любых ошибок, выводим в лог сообщение
                    } catch (IOException e) {
                        Log.d("BLUETOOTH", e.getMessage());
                    } catch (SecurityException e) {
                        Log.d("BLUETOOTH", e.getMessage());
                    } catch (NoSuchMethodException e) {
                        Log.d("BLUETOOTH", e.getMessage());
                    } catch (IllegalArgumentException e) {
                        Log.d("BLUETOOTH", e.getMessage());
                    } catch (IllegalAccessException e) {
                        Log.d("BLUETOOTH", e.getMessage());
                    } catch (InvocationTargetException e) {
                        Log.d("BLUETOOTH", e.getMessage());
                    }

                    //Выводим сообщение об успешном подключении
                    Toast.makeText(getApplicationContext(), "CONNECTED", Toast.LENGTH_LONG).show();
                }

                outStream.write(value);

            } catch (IOException e) {
                Log.d("BLUETOOTH", e.getMessage());
            }
        }

}