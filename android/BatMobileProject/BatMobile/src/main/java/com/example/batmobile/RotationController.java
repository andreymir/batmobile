package com.example.batmobile;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.Context;

/**
 * Created by IZubkov on 11/2/13.
 */
public class RotationController implements SensorEventListener {
    private final SensorManager mSensorManager;
    private final Sensor mRotationVectorSensor;
    private final Activity mActivity;
    private IRotaionListener mRotaionListener;

    public RotationController(Activity activity){
        mActivity = activity;
        mSensorManager = (SensorManager)mActivity.getSystemService(Context.SENSOR_SERVICE);
        mRotationVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);


    }

    public void regiseter(){
        mSensorManager.registerListener(this, mRotationVectorSensor, 10000);
    }

    public void unregister(){
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // we received a sensor event. it is a good practice to check
        // that we received the proper event
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // convert the rotation-vector to a 4x4 matrix. the matrix
            // is interpreted by Open GL as the inverse of the
            // rotation-vector, which is what we want.
            mRotaionListener.RotationChanged(event.values[0],event.values[1],event.values[2]);
        }
    }

    public void addListener(IRotaionListener listener){
        mRotaionListener = listener;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    interface IRotaionListener
    {
        public void RotationChanged(float x, float y, float z);
    }

}
