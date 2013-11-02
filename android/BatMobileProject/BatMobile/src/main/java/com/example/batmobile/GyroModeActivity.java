package com.example.batmobile;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.TextView;

public class GyroModeActivity extends Activity implements RotationController.IRotaionListener {
    TextView xAxis, yAxis, zAxis;
    Button power;

    RotationController mRotationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gyro_activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        xAxis = (TextView)findViewById(R.id.textViewXAxis);
        yAxis = (TextView)findViewById(R.id.textViewYAxis);
        zAxis = (TextView)findViewById(R.id.textViewZAxis);
        power = (Button)findViewById(R.id.textViewPowerState);

        mRotationController = new RotationController(this);

        mRotationController.addListener(this);

        mRotationController.regiseter();

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

    @Override
    public void RotationChanged(float x, float y, float z) {
        xAxis.setText(Float.toString(x));
        yAxis.setText(Float.toString(y));
        zAxis.setText(Float.toString(z));
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.gyro_fragment_main, container, false);
            return rootView;
        }
    }
}
