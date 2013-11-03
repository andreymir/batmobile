package com.example.batmobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.batmobile.arduino.BluetoothController;
import com.example.batmobile.arduino.Mode;
import com.example.batmobile.arduino.Options;
import com.example.batmobile.arduino.ShieldBotManager;
import com.example.batmobile.menu.MenuController;

<<<<<<< HEAD
=======
import java.util.List;

import static com.example.batmobile.arduino.Mode.Manual;

>>>>>>> d5d5ccb47ba0e413c4bb14f655f68ab2517eccd1
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
        menuController.HandleMenuItemClick(this, item);

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

            mRotationController.addListener(this);
            mRotationController.addListener(manager);

            if (!mBluetoothController.init())
                finishDialogNoBluetooth();

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

                Mode mode = Options.getInstance().mode;

                switch(mode)
                {
                    case Manual:
                        mBluetoothController.setManualMode();
                        break;
                    case Protected:
                        mBluetoothController.setProtectedMode();
                        break;
                    case FollowLine:
                        mBluetoothController.setFollowLineMode();
                        break;
                    default:
                        mBluetoothController.setIdleMode();
                }

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
