package com.google.android.apps.mytracks.services.sensors;

import android.content.Context;
import android.util.Log;

import com.google.android.apps.mytracks.content.Sensor;
import com.google.android.apps.mytracks.content.Sensor.SensorDataSet;
import com.google.protobuf.InvalidProtocolBufferException;

public class HeartRate {
	private static final String TAG = "HeartRate";
	
	private SensorManager sensorManager;
	   
	 private String getHeartRate(Context context, Sensor.SensorDataSet sensorDataSet) {
		    String heartRate;
		    
		    if (sensorDataSet.hasHeartRate() &&		
			sensorDataSet.getHeartRate().getState() == Sensor.SensorState.SENDING &&
			sensorDataSet.getHeartRate().hasValue()) {
			heartRate = Integer.toString(sensorDataSet.getHeartRate().getValue());
		    } else {
		      heartRate = SensorUtils.getStateAsString(
		          sensorDataSet.hasHeartRate() ? sensorDataSet.getHeartRate().getState()
		              : Sensor.SensorState.NONE, context);
		    }
		    return heartRate;
	  }
	   
	   public String getHeartRate(Context context)
	   {
		   if (sensorManager == null)
			   sensorManager = SensorManagerFactory.getSystemSensorManager(context);
		   
		   if (sensorManager == null) {
		        Log.d(TAG, "getHeartRate: sensorManager is null.");
		        return "--";
		   }
		   
		   SensorDataSet sensorDataSet = sensorManager.getSensorDataSet();
		   if (sensorDataSet == null) {
		        Log.d(TAG, "getHeartRate: Sensor data set is null.");
		        return "--";
		   }
		   
		   byte[] buff = sensorManager.getSensorDataSet().toByteArray();
		 
		   try {
			   sensorDataSet = Sensor.SensorDataSet.parseFrom(buff);
			   String heartRate = getHeartRate(context, sensorDataSet);
			   Log.d(TAG, "getHeartRate: " + heartRate);
			   return heartRate;
		   } catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		   }
		   return "--";
	   }
	   
}
