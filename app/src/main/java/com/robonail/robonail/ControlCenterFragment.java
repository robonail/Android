package com.robonail.robonail;


import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.StringTokenizer;
import java.util.UUID;

import static android.R.drawable.stat_notify_error;
import static android.R.drawable.stat_sys_data_bluetooth;
import static com.robonail.robonail.RobonailApplication.response;


/**
 * Created by Michael on 7/9/2017.
 */

public class ControlCenterFragment extends Fragment {
    View myView;


    private static final String TAG = "ControlCenterFragment";

    Button btnOn, btnOff;
    ImageButton imgBtnBluetooth, imgBtnUp, imgBtnDown, imgBtnLeft, imgBtnRight,
            imgBtnSend1, imgBtnSend2, imgBtnSend3, imgBtnSend4, imgBtnSend5;
    EditText txtUp,txtDown,txtLeft, txtRight, txtCommand1, txtCommand2, txtCommand3, txtCommand4,
            txtCommand5, txtValue1, txtValue2, txtValue3, txtValue4, txtValue5;
    Switch switch1, switch2, switch3, switch4;
    SeekBar brightness;
    static TextView txtArduino;
    Handler h;

    final int RECIEVE_MESSAGE = 1;		// Status  for Handler

    final int handlerState = 0;                        //used to identify handler message

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder(256);

    //private ConnectedThread mConnectedThread;

    public  boolean processingPreviousMessage=false;
    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address;

    public static final String RobonailPreferences = "RobonailPreferences" ;
    SharedPreferences sharedpreferences;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.control_center,container,false);
        //return super.onCreateView(inflater, container, savedInstanceState);




        super.onCreate(savedInstanceState);

        // MAC-address of Bluetooth module (you must edit this line)
        Intent newint = getActivity().getIntent();

        sharedpreferences = getActivity().getSharedPreferences(RobonailPreferences, Context.MODE_PRIVATE);
        address = sharedpreferences.getString("bluetoothAddress","");

        //setContentView(R.layout.activity_main);



        //view of the ledControl
        //getActivity().setContentView(R.layout.control_center);

        //btnOn = (Button)  myView.findViewById(R.id.btnOn);					// button LED ON
       // btnOff = (Button) myView.findViewById(R.id.btnOff);				// button LED OFF
        //imgBtnBluetooth = (ImageButton)  myView.findViewById(R.id.imgBtnBluetooth);
        txtArduino = (TextView)  myView.findViewById(R.id.txtArduino);		// for display the received data from the Arduino
        txtArduino.setMovementMethod(new ScrollingMovementMethod());
        txtUp=(EditText) myView.findViewById(R.id.txtUp);
        txtUp.setText(sharedpreferences.getString("txtUp","12.75"));
        txtDown=(EditText) myView.findViewById(R.id.txtDown);
        txtDown.setText(sharedpreferences.getString("txtDown", "12.75"));
        txtLeft=(EditText) myView.findViewById(R.id.txtLeft);
        txtLeft.setText(sharedpreferences.getString("txtLeft", "12.75"));
        txtRight=(EditText) myView.findViewById(R.id.txtRight);
        txtRight.setText(sharedpreferences.getString("txtRight", "12.75"));

        imgBtnUp = (ImageButton)  myView.findViewById(R.id.imgBtnUp);
        imgBtnDown = (ImageButton)  myView.findViewById(R.id.imgBtnDown);
        imgBtnLeft = (ImageButton)  myView.findViewById(R.id.imgBtnLeft);
        imgBtnRight = (ImageButton)  myView.findViewById(R.id.imgBtnRight);

        //Set up our 5 command lines
        imgBtnSend1 = (ImageButton)  myView.findViewById(R.id.imgBtnSend1);
        imgBtnSend2 = (ImageButton)  myView.findViewById(R.id.imgBtnSend2);
        imgBtnSend3 = (ImageButton)  myView.findViewById(R.id.imgBtnSend3);
        imgBtnSend4 = (ImageButton)  myView.findViewById(R.id.imgBtnSend4);
        imgBtnSend5 = (ImageButton)  myView.findViewById(R.id.imgBtnSend5);


        switch1 = (Switch)  myView.findViewById(R.id.switch1);
        switch1.setChecked(sharedpreferences.getBoolean("switch1", false));
        switch2 = (Switch)  myView.findViewById(R.id.switch2);
        switch2.setChecked(sharedpreferences.getBoolean("switch2", false));
        switch3 = (Switch)  myView.findViewById(R.id.switch3);
        switch3.setChecked(sharedpreferences.getBoolean("switch3", false));
        switch4 = (Switch)  myView.findViewById(R.id.switch4);
        switch4.setChecked(sharedpreferences.getBoolean("switch4", false));

        txtCommand1=(EditText) myView.findViewById(R.id.txtCommand1);
        txtCommand1.setText(sharedpreferences.getString("txtCommand1", "command..."));
        txtCommand2=(EditText) myView.findViewById(R.id.txtCommand2);
        txtCommand2.setText(sharedpreferences.getString("txtCommand2", "command..."));
        txtCommand3=(EditText) myView.findViewById(R.id.txtCommand3);
        txtCommand3.setText(sharedpreferences.getString("txtCommand3", "command..."));
        txtCommand4=(EditText) myView.findViewById(R.id.txtCommand4);
        txtCommand4.setText(sharedpreferences.getString("txtCommand4", "command..."));
        txtCommand5=(EditText) myView.findViewById(R.id.txtCommand5);
        txtCommand5.setText(sharedpreferences.getString("txtCommand5", "command..."));

        txtValue1=(EditText) myView.findViewById(R.id.txtValue1);
        txtValue1.setText(sharedpreferences.getString("txtValue1", "value..."));
        txtValue2=(EditText) myView.findViewById(R.id.txtValue2);
        txtValue2.setText(sharedpreferences.getString("txtValue2", "value..."));
        txtValue3=(EditText) myView.findViewById(R.id.txtValue3);
        txtValue3.setText(sharedpreferences.getString("txtValue3", "value..."));
        txtValue4=(EditText) myView.findViewById(R.id.txtValue4);
        txtValue4.setText(sharedpreferences.getString("txtValue4", "value..."));
        txtValue5=(EditText) myView.findViewById(R.id.txtValue5);
        txtValue5.setText(sharedpreferences.getString("txtValue5", "value..."));
        //btnConnect = (Button) myView.findViewById(R.id.btnConnect);
       // brightness = (SeekBar) myView.findViewById(R.id.seekBar);
       // lumn = (TextView) myView.findViewById(R.id.lumn);

        /*h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if(processingPreviousMessage)
                    txtArduino.setText(txtArduino.getText().toString()+"\r\nSTILL PROCESSING LAST MESSAGE\r\nSTILL PROCESSING LAST MESSAGE"); 	        // update TextView

                processingPreviousMessage=true;
                if (msg.what == handlerState) {                                     //if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    sb.append(readMessage);                                      //keep appending to string until ~
                    if(sb.indexOf("\r\n")>-1) {
                        StringTokenizer st2 = new StringTokenizer(sb.toString(), "\r\n");
                        for (int i=0; i<st2.countTokens(); i++) //we skip the last token because it may be an incomplete line
                        {
                            if(i==st2.countTokens()-1 && sb.indexOf("\r\n")!=sb.length()-2) //we are on the last token but it does NOT end is a \r\n. it is a partial message
                                break;
                            String receivedMessage = st2.nextElement().toString();
                            txtArduino_addline("Received : "+receivedMessage); //st2.nextElement();
                            sb.delete(0, sb.indexOf("\r\n")+2);                    //clear all string data. we run this for each token in the stringtokenizer
                            processMessage(receivedMessage);
                        }
                    }
                }
                processingPreviousMessage=false;
            }
        };
        btAdapter = BluetoothAdapter.getDefaultAdapter();		// get Bluetooth adapter
        checkBTState();*/

        imgBtnUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendCommand("U",txtUp.getText().toString());
            }
        });
        imgBtnDown.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendCommand("D",txtDown.getText().toString());
            }
        });
        imgBtnLeft.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendCommand("L",txtLeft.getText().toString());
            }
        });
        imgBtnRight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendCommand("R",txtRight.getText().toString());
            }
        });

        imgBtnSend1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendCommand(txtCommand1.getText().toString(),txtValue1.getText().toString());
            }
        });
        imgBtnSend2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendCommand(txtCommand2.getText().toString(),txtValue2.getText().toString());
            }
        });
        imgBtnSend3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendCommand(txtCommand3.getText().toString(),txtValue3.getText().toString());
            }
        });
        imgBtnSend4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendCommand(txtCommand4.getText().toString(),txtValue4.getText().toString());
            }
        });
        imgBtnSend5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendCommand(txtCommand5.getText().toString(),txtValue5.getText().toString());
            }
        });

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { sendCommand("D1","1");
                } else { sendCommand("D1","0"); } }
        });
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { sendCommand("D2","1");
                } else { sendCommand("D2","0"); } }
        });
        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { sendCommand("D3","1");
                } else { sendCommand("D3","0"); } }
        });
        switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { sendCommand("D4","1");
                } else { sendCommand("D4","0"); } }
        });
/*
        btnOn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //btnOn.setEnabled(false);
                //btnOff.setEnabled(true);
                mConnectedThread.write("<HelloWorld, 12, 24.7>"); //"Turn on");	// Send "1" via Bluetooth
                //Toast.makeText(getBaseContext(), "Turn on LED", Toast.LENGTH_SHORT).show();
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               // btnOff.setEnabled(false);
              //  btnOn.setEnabled(true);
                mConnectedThread.write("<Nope,12,24.7>"); //"Turn off");	// Send "0" via Bluetooth
                //Toast.makeText(getBaseContext(), "Turn off LED", Toast.LENGTH_SHORT).show();
            }
        });
        */

        /*imgBtnBluetooth.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (btSocket!=null) //If the btSocket is busy
                {
                    Disconnect();
                }else{
                    connectToBluetooth();
                }
            }
        });*/
        return myView;
    }


    public void processMessage(String receivedMessage){
        //the format of received messages is <command, value>
        int startPosition = receivedMessage.indexOf("<")+1;
        int commaPosition = receivedMessage.indexOf(",");
        int endPosition = receivedMessage.indexOf(">");
        if(startPosition==1 && commaPosition>startPosition && endPosition>commaPosition) { //the message is valid because it is in format <cmd,val>
            String command = receivedMessage.substring(startPosition, commaPosition);
            String commandValue = receivedMessage.substring(commaPosition+1,endPosition);
            //command=command+commandValue;
            if(command.equals("i")){
                int val = Integer.parseInt(commandValue);
                val++;
                sendCommand("i",String.valueOf(val));
            }

        }
    }
    public void stop(){
        sendCommand("X","");
    }

    public void sendCommand(String command, String value) {


        Handler handler = new Handler();
        String myURL = RobonailApplication.cmdURL //"http://192.168.4.100:80/cmd"
                + "?c="+command+"&v="+value+"&f=mobile;";

        new RetrieveHttp().execute(myURL);
        //Log.i(TAG, "received:"+RobonailApplication.response);
    }

    public static void txtArduino_addline(String s) {
        while(s.endsWith("\n"))
            s=s.substring(0,s.length()-1);
        txtArduino.setText(txtArduino.getText().toString() + "\r\n"+s);            // update TextView
        while(txtArduino.getLineCount()>10) {
            String origText = txtArduino.getText().toString();
            int start = origText.indexOf("\r\n");
            txtArduino.setText(origText.substring(start + 2));
        }
    }
    /*

    @Override
    public void onCreate(Bundle savedInstanceState) {
    }

    */

    /*
    private void Disconnect() {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
                imgBtnBluetooth.setImageResource(stat_notify_error);
                btSocket=null;
            }
            catch (IOException e)
            { Log.e(TAG, "Error closing bluetooth socket",e);}
        }
        //finish(); //return to the first layout
        Toast.makeText(getActivity(), "Bluetooth disconnected", Toast.LENGTH_LONG).show();
        txtArduino.setText("Bluetooth disconnected");

    }
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Bluetooth Error: Could not create Insecure RFComm Connection",e);
                Toast.makeText(getActivity(), "Bluetooth Error: Could not create Insecure RFComm Connection", Toast.LENGTH_LONG).show();
                txtArduino.setText("Bluetooth Error: Could not create Insecure RFComm Connection");
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }
*/
    @Override
    public void onResume() {
        super.onResume();
        //connectToBluetooth();

    }

    /*
    private void connectToBluetooth(){

        Log.d(TAG, "...onResume - try connect...");
        BluetoothDevice device;
        //btAdapter = BluetoothAdapter.getDefaultAdapter();		// get Bluetooth adapter
        //checkBTState();
        try {
            // Set up a pointer to the remote node using it's address.
            device = btAdapter.getRemoteDevice(address);

            // Two things are needed to make a connection:
            //   A MAC address, which we got above.
            //   A Service ID or UUID.  In this case we are using the
            //     UUID for SPP.
            try {
                if(btSocket!=null)
                    Disconnect();
                btSocket = createBluetoothSocket(device);
            } catch (IOException e) {
                errorExit("Bluetooth Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
                txtArduino.setText("Bluetooth Fatal Error In onResume() and socket create failed");
                imgBtnBluetooth.setImageResource(stat_notify_error);
                return;
            }

        } catch (Exception e) {
            errorExit("Bluetooth Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
            Toast.makeText(getActivity(), "Bluetooth failed with address:"+address, Toast.LENGTH_LONG).show();
            txtArduino.setText("Bluetooth failed with address:"+address);
            imgBtnBluetooth.setImageResource(stat_notify_error);
            return;
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
            Toast.makeText(getActivity(), "....Bluetooth Connected...", Toast.LENGTH_LONG).show();
            txtArduino.setText( "....Bluetooth Connected...");
            Log.d(TAG, "....Connection ok...");
            imgBtnBluetooth.setImageResource(stat_sys_data_bluetooth);
        } catch (IOException e) {
            try {
                Toast.makeText(getActivity(), "....ERROR Bluetooth Connection btSocket.connect();...", Toast.LENGTH_LONG).show();
                Log.e(TAG, "....ERROR Bluetooth Connection btSocket.connect();...",e);
                txtArduino.setText("....ERROR Bluetooth Connection btSocket.connect();...");
                btSocket.close();
                imgBtnBluetooth.setImageResource(stat_notify_error);
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                txtArduino.setText("Fatal Error In onResume() and unable to close socket during connection failure");
                imgBtnBluetooth.setImageResource(stat_notify_error);
                return;
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Create Socket...");
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
    }*/
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "...In onPause()...");
        /*
        try     {
            btSocket.close();
        } catch (Exception e2) { //IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
            txtArduino.setText("Fatal Error In onPause() and failed to close socket.");
        }*/
    }
    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences.Editor editor =sharedpreferences.edit();
        editor.putString("txtUp", txtUp.getText().toString());
        editor.putString("txtDown", txtDown.getText().toString());
        editor.putString("txtLeft", txtLeft.getText().toString());
        editor.putString("txtRight", txtRight.getText().toString());
        editor.putString("txtCommand1", txtCommand1.getText().toString());
        editor.putString("txtCommand2", txtCommand2.getText().toString());
        editor.putString("txtCommand3", txtCommand3.getText().toString());
        editor.putString("txtCommand4", txtCommand4.getText().toString());
        editor.putString("txtCommand5", txtCommand5.getText().toString());
        editor.putString("txtValue1", txtValue1.getText().toString());
        editor.putString("txtValue2", txtValue2.getText().toString());
        editor.putString("txtValue3", txtValue3.getText().toString());
        editor.putString("txtValue4", txtValue4.getText().toString());
        editor.putString("txtValue5", txtValue5.getText().toString());
        editor.putBoolean("switch1", switch1.isChecked());
        editor.putBoolean("switch2", switch2.isChecked());
        editor.putBoolean("switch3", switch3.isChecked());
        editor.putBoolean("switch4", switch4.isChecked());
        editor.apply();

        /*try     {
            btSocket.close();
        } catch (Exception e2) { //IOException e2) {
            errorExit("Fatal Error", "In onStop() and failed to close socket." + e2.getMessage() + ".");
            txtArduino.setText("Fatal Error In onStop() and failed to close socket.");
        }*/
        Log.d(TAG, "...In onStop()...");
    }

    /*
    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            Toast.makeText(getActivity(), "Bluetooth not supported", Toast.LENGTH_LONG).show();
            errorExit("Fatal Error", "Bluetooth not supported");
            txtArduino.setText("Fatal Error Bluetooth not supported");
            imgBtnBluetooth.setImageResource(stat_notify_error);
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
                Toast.makeText(getActivity(), "Bluetooth on", Toast.LENGTH_LONG).show();
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }*/

    private void errorExit(String title, String message){
        Toast.makeText(getActivity(), title + " - " + message, Toast.LENGTH_LONG).show();
        //finish();
    }

   /* private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        private volatile boolean exit = false;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(getActivity(), "...Bluetooth Error ConnectedThread socket input/output streams...", Toast.LENGTH_LONG).show();
                txtArduino.setText("...Bluetooth Error ConnectedThread socket input/output streams..." + e.getMessage() + "...");
                imgBtnBluetooth.setImageResource(stat_notify_error);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void end(){
            exit = true;
        }
        public void run() {
            byte[] buffer = new byte[128];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true && !exit) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);		// Get number of bytes and message in "buffer"
                    //
                    String readMessage=new String(buffer,0,bytes);
                    h.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                    //h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();		// Send to message queue Handler
                } catch (IOException e) {
                    Log.e(TAG, "....Bluetooth Error ConnectedThread mmInStream.read(buffer: " + e.getMessage() + "...");
                    //txtArduino.setText("...Bluetooth Error ConnectedThread mmInStream.read(buffer) " + e.getMessage() + "...");
                    //imgBtnBluetooth.setImageResource(stat_notify_error);
                    break;
                }
            }
        }

        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
                txtArduino.setText("...Error data send: " + e.getMessage() + "...");
                imgBtnBluetooth.setImageResource(stat_notify_error);
            }
        }
        }*/
}
