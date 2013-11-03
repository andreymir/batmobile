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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.batmobile.arduino.Mode;
import com.example.batmobile.arduino.Options;

public class OptionsActivity extends Activity {

    RadioButton manualRadioButton;
    RadioButton protectedRadioButton;
    RadioButton followLineRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_main);

        manualRadioButton = (RadioButton)findViewById(R.id.radioButtonManual);
        protectedRadioButton = (RadioButton)findViewById(R.id.radioButtonProtected);
        followLineRadioButton = (RadioButton)findViewById(R.id.radioButtonFollowLine);
        RadioGroup rg = (RadioGroup)findViewById(R.id.rg);

        Mode mode = Options.getInstance().mode;

        switch(mode)
        {
            case Manual:
                rg.check(manualRadioButton.getId());
                break;
            case Protected:
                rg.check(protectedRadioButton.getId());
                break;
            case FollowLine:
                rg.check(followLineRadioButton.getId());
                break;
        }

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options, menu);
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
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.options_fragment_main, container, false);
            return rootView;
        }
    }



    public void saveButtonClick(View v){

        RadioGroup rg = (RadioGroup)findViewById(R.id.rg);
        int rbid = rg.getCheckedRadioButtonId();

        switch(rbid)
        {
            case R.id.radioButtonManual:
                Options.getInstance().mode = Mode.Manual;
                break;
            case R.id.radioButtonProtected:
                Options.getInstance().mode = Mode.Protected;
                break;
            case R.id.radioButtonFollowLine:
                Options.getInstance().mode = Mode.FollowLine;
                break;
        }

        finish();
    }


    public void cancelButtonClick(View v){
        finish();
    }
}
