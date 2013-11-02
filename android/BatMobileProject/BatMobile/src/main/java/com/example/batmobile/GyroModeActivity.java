package com.example.batmobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
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

import java.util.List;

public class GyroModeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gyro_activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gyro_mode, menu);
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


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements RotationController.IRotaionListener  {
        private TextView xAxis, yAxis, zAxis;
        private TextView power;
        private boolean isLestining = false;

        private RotationController mRotationController;

        public PlaceholderFragment() {

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

            mRotationController.addListener(this);

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
                isLestining = true;
            }
            else{
                mRotationController.unregister();
                isLestining = false;
            }
        }

    }

    public void onPowerButtonClick(View v){

        PlaceholderFragment fragment = (PlaceholderFragment)getFragmentManager().findFragmentById(R.id.container);

        fragment.powerButtonClick();


    }

}
