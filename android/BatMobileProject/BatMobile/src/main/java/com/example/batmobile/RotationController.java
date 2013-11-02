package com.example.batmobile;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.Context;

import java.util.List;

/**
 * Created by IZubkov on 11/2/13.
 */
public class RotationController implements SensorEventListener {
    private final SensorManager mSensorManager;
    private final Sensor mAccelerometerSensor;
    private final Sensor mMagneticSensor;
    private final Activity mActivity;
    private IRotaionListener mRotaionListener;
    private float[] rMatrix;

    public static final float MAX_PITCH_VALUE = 0;
    public static float MIN_PITCH_VALUE = -180;
    public static float AVG_PITCH_VALUE = (MAX_PITCH_VALUE - MIN_PITCH_VALUE) / 2 + MIN_PITCH_VALUE;

    public static final float MAX_ROLL_VALUE = 90;
    public static float MIN_ROLL_VALUE = -90;
    public static float AVG_ROLL_VALUE = 0;

    public RotationController(Activity activity){
        mActivity = activity;
        mSensorManager = (SensorManager)mActivity.getSystemService(Context.SENSOR_SERVICE);

        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void regiseter(){
        mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
        rMatrix = new float[16];
    }

    public void unregister(){
        mSensorManager.unregisterListener(this);
    }
/*
    @Override
    public void onSensorChanged(SensorEvent event) {
        // we received a sensor event. it is a good practice to check

        // that we received the proper event
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            // convert the rotation-vector to a 4x4 matrix. the matrix
            // is interpreted by Open GL as the inverse of the
            // rotation-vector, which is what we want.
            mRotaionListener.RotationChanged(event.values[0],event.values[1],event.values[2]);
        }
    }
 */

    private float[] gravityMatrix, geomagneticMatrix;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            gravityMatrix = event.values.clone();// Fill gravityMatrix with accelerometer values
        else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            geomagneticMatrix = event.values.clone();// Fill geomagneticMatrix with magnetic-field sensor values

        if(gravityMatrix != null && geomagneticMatrix != null){
            float[] orientation = new float[3];

            SensorManager.getRotationMatrix(rMatrix, null, gravityMatrix, geomagneticMatrix);// Retrieve RMatrix, necessary for the getOrientation method
            SensorManager.getOrientation(rMatrix, orientation);// Get the current orientation of the device
            mRotaionListener.RotationChanged((float)Math.toDegrees(orientation[0]), (float)Math.toDegrees(orientation[1]), (float)Math.toDegrees(orientation[2]));
        }
    }

    public void addListener(IRotaionListener listener){
        mRotaionListener = listener;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}