package com.example.batmobile.arduino;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.batmobile.BluetoothSerialService;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * Created by IZubkov on 11/2/13.
 */
public class BluetoothController {
    private Context mContext;

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


    public BluetoothController(Context context){
        mContext = context;
    }

    public boolean init(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            return false;
        }

        mSerialService = new BluetoothSerialService(mContext, mHandlerBT);
        return true;
    }

    public boolean isConnected(){
        return mConnectedDeviceName != null;
    }

    public boolean connectToFirstBoundedDevice(){
        Iterator<BluetoothDevice> iterator = mBluetoothAdapter.getBondedDevices().iterator();
        BluetoothDevice device = null;
        if (iterator.hasNext())
            device =  mBluetoothAdapter.getBondedDevices().iterator().next();

        //todo: add null handling

        if (device == null)
            return false;

        mSerialService.connect(device);
        return true;
    }

    public void setProtectedMode()
    {
        char[] chars = {'\u0002', 'm', 's'};
        byte[] command = Charset.forName("ISO-8859-1").encode(CharBuffer.wrap(chars)).array();
        mSerialService.write(command);
    }

    public void setFollowLineMode()
    {
        char[] chars = {'\u0002', 'm', 'f'};
        byte[] command = Charset.forName("ISO-8859-1").encode(CharBuffer.wrap(chars)).array();
        mSerialService.write(command);
    }

    public void setManualMode()
    {
        char[] chars = {'\u0002', 'm', 'm'};
        byte[] command = Charset.forName("ISO-8859-1").encode(CharBuffer.wrap(chars)).array();
        mSerialService.write(command);
    }

    public void setIdleMode()
    {
        char[] chars = {'\u0002', 'm', 'i'};
        byte[] command = Charset.forName("ISO-8859-1").encode(CharBuffer.wrap(chars)).array();
        mSerialService.write(command);
    }

    public void sendMessage(byte[] message){
        mSerialService.write(message);
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
                    Toast.makeText(mContext.getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(mContext.getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void disconnect() {
        mSerialService.stop();
    }
}
