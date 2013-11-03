package com.example.batmobile.menu;

import android.content.Context;
import android.view.MenuItem;
import android.widget.Toast;

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

    public void HandleMenuItemClick(Context context, MenuItem item){
        switch (item.getItemId()) {
            case R.id.togggleConnect:
                if (mBluetoothController.isConnected()){
                    mBluetoothController.disconnect();
                    item.setTitle("Connect");
                }else {
                    if(!mBluetoothController.connectToFirstBoundedDevice())
                        Toast.makeText(context, "Couldn't find device", 1000);
                    item.setTitle("Disconnect");
                }
        }
    }
}
