package com.example.batmobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.batmobile.arduino.BluetoothController;
import com.example.batmobile.arduino.ShieldBotManager;
import com.example.batmobile.menu.MenuController;

import java.util.List;

public class GyroModeActivity extends Activity {

    private BluetoothController mBluetoothController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gyro_activity_main);

        mBluetoothController = new BluetoothController(this);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment(mBluetoothController))
                    .commit();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bat_mobile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        MenuController menuController = new MenuController(mBluetoothController);
        menuController.HandleMenuItemClick(item);

        return super.onOptionsItemSelected(item);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements IRotaionListener  {
        private TextView xAxis, yAxis, zAxis;
        private TextView power;
        private boolean isLestining = false;

        private BluetoothController mBluetoothController;

        private RotationController mRotationController;


        public PlaceholderFragment(BluetoothController bluetoothController) {
            mBluetoothController = bluetoothController;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.gyro_fragment_main, container, false);


            xAxis = (TextView)rootView.findViewById(R.id.textViewXAxis);
            yAxis = (TextView)rootView.findViewById(R.id.textViewYAxis);
            zAxis = (TextView)rootView.findViewById(R.id.textViewZAxis);
            power = (TextView)rootView.findViewById(R.id.textViewPowerState);

            mRotationController = new RotationController(getActivity());

            ShieldBotManager manager = new ShieldBotManager(mBluetoothController);

            mRotationController.addListener(manager);
            //mRotationController.addListener(this);

            if (!mBluetoothController.init())
                finishDialogNoBluetooth();

            if(!mBluetoothController.connectToFirstBoundedDevice())
                Toast.makeText(getActivity(), "Couldn't find device", 1000);

            return rootView;
        }



        @Override
        public void RotationChanged(float x, float y, float z) {
            xAxis.setText(Float.toString(x));
            yAxis.setText(Float.toString(y));
            zAxis.setText(Float.toString(z));
        }

        public void powerButtonClick(){
            power.setText("POWER!");

            if (!isLestining){
                mRotationController.regiseter();

                mBluetoothController.setManualMode();

                isLestining = true;
            }
            else{
                mRotationController.unregister();

                mBluetoothController.setIdleMode();

                isLestining = false;
            }
        }

        public void finishDialogNoBluetooth() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.alert_dialog_no_bt)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(R.string.app_name)
                    .setCancelable( false )
                    .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getActivity().finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    public void onPowerButtonClick(View v){

        PlaceholderFragment fragment = (PlaceholderFragment)getFragmentManager().findFragmentById(R.id.container);

        fragment.powerButtonClick();
    }



}
