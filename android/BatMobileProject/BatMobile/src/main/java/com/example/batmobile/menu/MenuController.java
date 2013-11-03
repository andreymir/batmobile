package com.example.batmobile.menu;

import android.view.MenuItem;

import com.example.batmobile.R;
import com.example.batmobile.arduino.BluetoothController;

/**
 * Created by IZubkov on 11/3/13.
 */
public class MenuController {
    private final BluetoothController mBluetoothController;

    public MenuController(BluetoothController bluetoothController){
        mBluetoothController = bluetoothController;
    }

    public void HandleMenuItemClick(MenuItem item){
        switch (item.getItemId()) {
            case R.id.togggleConnect:
                if (mBluetoothController.isConnected()){
                    mBluetoothController.disconnect();
                    item.setTitle("Connect");
                }else {
                    mBluetoothController.connectToFirstBoundedDevice();
                    item.setTitle("Disconnect");
                }
        }
    }
}
