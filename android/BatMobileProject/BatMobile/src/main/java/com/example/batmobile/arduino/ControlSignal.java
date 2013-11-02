package com.example.batmobile.arduino;

/**
 * Created by IZubkov on 11/2/13.
 */
public class ControlSignal {
    public byte mCommand;
    public byte mLeftSpin;
    public byte mRrightSpin;

    public ControlSignal(byte cmd, byte leftSpin, byte rightSpin){
        mCommand = cmd;
        mLeftSpin = leftSpin;
        mRrightSpin = rightSpin;
    }

    public byte[] toBytes(){
        return new byte[]{3, mCommand, mLeftSpin, mRrightSpin};
    }


}
