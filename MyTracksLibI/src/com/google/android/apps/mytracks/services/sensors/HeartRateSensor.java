package com.google.android.apps.mytracks.services.sensors;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.apps.mytracks.content.Sensor;
import com.google.android.apps.mytracks.content.Sensor.SensorDataSet;
import com.google.android.apps.mytracks.content.Sensor.SensorState;
import com.google.protobuf.InvalidProtocolBufferException;

public class HeartRateSensor {
	private static final String TAG = "HeartRateSensor";
	private static HeartRateSensor instance = null;

	public static boolean isBtEnabled() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		return (mBluetoothAdapter != null) && mBluetoothAdapter.isEnabled();
	}

	public static void requestEnableBt(Activity activity, int requestCode) {
		if (!isBtEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    activity.startActivityForResult(enableBtIntent, requestCode);
		}
	}

	private boolean enabled;
    private SensorManager sensorManager;

    protected HeartRateSensor() {
    }

    public static HeartRateSensor getInstance() {
	if (instance == null) {
	   instance = new HeartRateSensor();
	}
	return instance;
     }

    public void onStart(){
	Log.d(TAG, "onStart()");
	enabled = true;
    }
    public void onStop() {
		Log.d(TAG, "onStop()");
	enabled = false;
		sensorManager = null;
	    SensorManagerFactory.releaseSensorManager();
	}

    private String getHeartRate(Context context, Sensor.SensorDataSet sensorDataSet) {
	String heartRate;

		if (sensorDataSet.hasHeartRate() &&
			sensorDataSet.getHeartRate().getState() == Sensor.SensorState.SENDING &&
			sensorDataSet.getHeartRate().hasValue()) {
			heartRate = Integer.toString(sensorDataSet.getHeartRate().getValue());
		} else {
		    heartRate = SensorUtils.getStateAsString(sensorDataSet.hasHeartRate() ?
		sensorDataSet.getHeartRate().getState() : Sensor.SensorState.NONE, context);
	    }
	    return heartRate;
    }

    public String getHeartRate(Context context) {
	final String def = "--";

	if (!enabled)
		return def;

	if (sensorManager == null) {
	    sensorManager = SensorManagerFactory.getSensorManager(context);
	}

	if (sensorManager == null) {
		Log.d(TAG, "getHeartRate: sensorManager is null");
			return def;
	}

		SensorDataSet sensorDataSet = sensorManager.getSensorDataSet();
		if (sensorDataSet == null) {
		    Log.d(TAG, "getHeartRate: Sensor data set is null.");
		    return def;
		}

		byte[] buff = sensorDataSet.toByteArray();

		try {
			sensorDataSet = Sensor.SensorDataSet.parseFrom(buff);
			String heartRate = getHeartRate(context, sensorDataSet);
			Log.d(TAG, "getHeartRate: " + heartRate);
			if (heartRate.equals("0"))
				return def;
			return heartRate;
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return def;
	}
}
