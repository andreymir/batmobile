package com.example.batmobile.arduino;

import com.example.batmobile.IRotaionListener;

/**
 * Created by IZubkov on 11/2/13.
 */
public class ShieldBotManager implements IRotaionListener {
    private BluetoothController mBluetoothController;

    public ShieldBotManager(BluetoothController bluetoothController){
        mBluetoothController = bluetoothController;
    }

    @Override
    public void RotationChanged(float x, float y, float z) {
        ControlSignal signal = ShieldBotUtility.CreateControlSignalFromVector(y, z);
        mBluetoothController.sendMessage(signal.toBytes());
    }
}
