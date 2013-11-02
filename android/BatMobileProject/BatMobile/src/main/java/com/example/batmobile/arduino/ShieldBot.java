package com.example.batmobile.arduino;

import com.example.batmobile.BluetoothSerialService;

/**
 * Created by IZubkov on 11/2/13.
 */
public class ShieldBot {
    public static final int MAX_FORWARD_SPIN_VALUE = 127;
    public static final int MAX_REVERS_SPIN_VALUE = -128;
    public static final int STOP_VALUE = 0;

    public static final byte COMMAND_DRIVE = 'd';

    public int mLeftRightSpinLag = 1;

    private static BluetoothSerialService mSerialService = null;

    public void Drive(int leftSpin, int rightSpin){

    }

    public void Stop(){
        Drive(STOP_VALUE, STOP_VALUE);
    }

    public void SendSignal(ControlSignal signal){
    }


}
