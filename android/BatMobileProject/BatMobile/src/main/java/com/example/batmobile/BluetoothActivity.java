package com.example.batmobile;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class BluetoothActivity extends Activity {

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private static TextView mTitle;

    // Name of the connected device
    private String mConnectedDeviceName = null;

    /**
     * Set to true to add debugging code and logging.
     */
    public static final boolean DEBUG = true;

    /**
     * Set to true to log each character received from the remote process to the
     * android log, which makes it easier to debug some kinds of problems with
     * emulating escape sequences and control codes.
     */
    public static final boolean LOG_CHARACTERS_FLAG = DEBUG && false;

    /**
     * Set to true to log unknown escape sequences.
     */
    public static final boolean LOG_UNKNOWN_ESCAPE_SEQUENCES = DEBUG && false;

    /**
     * The tag we use when logging, so that our messages can be distinguished
     * from other messages in the log. Public because it's used by several
     * classes.
     */
    public static final String LOG_TAG = "BluetoothActivity";

    // Message types sent from the BluetoothReadService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    private static BluetoothSerialService mSerialService = null;
    private BluetoothAdapter mBluetoothAdapter = null;

    private boolean mEnablingBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            finishDialogNoBluetooth();
            return;
        }

        mSerialService = new BluetoothSerialService(this, mHandlerBT);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bluetooth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onConnectButtonClick(View v) {
        Log.e(LOG_TAG, "+ onConnectButtonClick +");

        BluetoothDevice device =  mBluetoothAdapter.getBondedDevices().iterator().next();
        mSerialService.connect(device);
    }

    public void onDriveButtonClick(View v) {
        Log.e(LOG_TAG, "+ onDriveButtonClick +");

        char[] chars = {'\u0003', 'd', '\u0000', '\u0000'};
        byte[] command = Charset.forName("ISO-8859-1").encode(CharBuffer.wrap(chars)).array();
        command[2] = (byte)(((SeekBar)findViewById(R.id.leftWheelBar)).getProgress() - 127);
        command[3] = (byte)(((SeekBar)findViewById(R.id.rightWheelBar)).getProgress() - 127);
        mSerialService.write(command);
    }

    public void onStopButtonClick(View v) {
        Log.e(LOG_TAG, "+ onStopButtonClick +");

        char[] chars = {'\u0003', 'd', '\u0000', '\u0000'};
        byte[] command = Charset.forName("ISO-8859-1").encode(CharBuffer.wrap(chars)).array();
        mSerialService.write(command);
    }

    public void onIdleModeButtonClick(View v) {
        Log.e(LOG_TAG, "+ onIdleModeButtonClick +");

        char[] chars = {'\u0002', 'm', 'i'};
        byte[] command = Charset.forName("ISO-8859-1").encode(CharBuffer.wrap(chars)).array();
        mSerialService.write(command);
    }

    public void onManualModeButtonClick(View v) {
        Log.e(LOG_TAG, "+ onManualModeButtonClick +");

        char[] chars = {'\u0002', 'm', 'm'};
        byte[] command = Charset.forName("ISO-8859-1").encode(CharBuffer.wrap(chars)).array();
        mSerialService.write(command);
    }

    public void onFollowLineModeButtonClick(View v) {
        Log.e(LOG_TAG, "+ onFollowLineModeButtonClick +");

        char[] chars = {'\u0002', 'm', 'f'};
        byte[] command = Charset.forName("ISO-8859-1").encode(CharBuffer.wrap(chars)).array();
        mSerialService.write(command);
    }

    public void onParktronicModeButtonClick(View v) {
        Log.e(LOG_TAG, "+ onParktronicModeButtonClick +");

        char[] chars = {'\u0002', 'm', 'p'};
        byte[] command = Charset.forName("ISO-8859-1").encode(CharBuffer.wrap(chars)).array();
        mSerialService.write(command);
    }

    public void onBeepButtonClick(View v) {
        Log.e(LOG_TAG, "+ onBeepButtonClick +");

        char[] chars = {'\u0001', 'b'};
        byte[] command = Charset.forName("ISO-8859-1").encode(CharBuffer.wrap(chars)).array();
        mSerialService.write(command);
    }

    @Override
    public synchronized void onResume() {
        super.onResume();

        if (DEBUG) {
            Log.e(LOG_TAG, "+ ON RESUME +");
        }

        if (!mEnablingBT) { // If we are turning on the BT we cannot check if it's enable
            if ( (mBluetoothAdapter != null)  && (!mBluetoothAdapter.isEnabled()) ) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.alert_dialog_turn_on_bt)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.alert_dialog_warning_title)
                        .setCancelable( false )
                        .setPositiveButton(R.string.alert_dialog_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mEnablingBT = true;
                                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                            }
                        })
                        .setNegativeButton(R.string.alert_dialog_no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finishDialogNoBluetooth();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }

            if (mSerialService != null) {
                // Only if the state is STATE_NONE, do we know that we haven't started already
                if (mSerialService.getState() == BluetoothSerialService.STATE_NONE) {
                    // Start the Bluetooth chat services
                    mSerialService.start();
                }
            }
        }
    }

    public void finishDialogNoBluetooth() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_dialog_no_bt)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(R.string.app_name)
                .setCancelable( false )
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // The Handler that gets information back from the BluetoothService
    private final Handler mHandlerBT = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if(DEBUG) Log.i(LOG_TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothSerialService.STATE_CONNECTED:

                            break;

                        case BluetoothSerialService.STATE_CONNECTING:

                            break;

                        case BluetoothSerialService.STATE_LISTEN:
                        case BluetoothSerialService.STATE_NONE:

                            break;
                    }
                    break;
                case MESSAGE_WRITE:

                    break;
/*
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                mEmulatorView.write(readBuf, msg.arg1);

                break;
*/
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_bluetooth, container, false);
            return rootView;
        }

    }

}
