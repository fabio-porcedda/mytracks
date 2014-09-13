/*
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.android.apps.mytracks.services.sensors;

/*import com.google.android.apps.mytracks.services.sensors.ant.AntSensorManager;*/
import java.util.HashSet;
import java.util.Set;

import com.google.android.apps.mytracks.content.Sensor.SensorState;
import com.google.android.apps.mytracks.util.PreferencesUtils;
import com.google.android.maps.mytracks.R;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

/**
 * A factory of {@link SensorManager}.
 *
 * @author Sandor Dornbush
 */
public class SensorManagerFactory {

  private static String TAG = "SensorManagerFactory";

  private static Set<SensorManager> sensorManagers;
  private static SensorManager sensorManager;

  private static Set<SensorManager> getSensorManagers(Context context) {
	if (sensorManagers != null)
		return sensorManagers;

	final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
	if (adapter == null)
		return null;

    final Set<BluetoothDevice> devices = adapter.getBondedDevices();
	SensorManager sm = null;
	sensorManagers = new HashSet<SensorManager>();
	for (BluetoothDevice dev: devices) {
			  if (PolarSensorManager.supports(dev)) {
				  sm = new PolarSensorManager(context, dev);
			  } else if (ZephyrSensorManager.supports(dev)) {
				  sm = new ZephyrSensorManager(context, dev);
			  }
			  if (sm != null) {
				  sensorManagers.add(sm);
			      sm.startSensor();
			      sm = null;
			  }
	}
    return sensorManagers;
  }

  private static void releaseSensorManagers() {
	  if (sensorManagers != null) {
		  for (SensorManager sm: sensorManagers) {
			sm.stopSensor();
		    Log.d(TAG, "releaseSensorManagers: stopSensor()");

		  }
		  sensorManagers = null;
	  }
  }

  public static boolean isEmpty(Context context){
	  return (getSensorManagers(context).isEmpty());
  }

  public static SensorManager getSensorManager(Context context) {
	  if (sensorManager != null)
		  return sensorManager;

	  if (getSensorManagers(context).isEmpty()) {
		   Log.d(TAG, "getSensorManager: sensorManagers is empty.");
		   return null;
      }

	  if ((sensorManagers.size() == 1)) {
		   sensorManager = sensorManagers.iterator().next();
		   return sensorManager;
	  }

	  for (SensorManager sm: sensorManagers) {
		  if (sm.getSensorState() == SensorState.CONNECTED) {
			  sensorManager = sm;
			  for (SensorManager osm: sensorManagers) {
				  if (osm != sm) {
					  osm.stopSensor();
					  Log.d(TAG, "getSensorManager: stopSensor()");
				  }
			  }
			} else {
					Log.d(TAG, "getSensorManager: " + sm.toString() + ":" + sm.getSensorState());
			}
	  }
	  return sensorManager;
  }

  public static void releaseSensorManager() {
		releaseSensorManagers();
		sensorManager = null;
  }
}
