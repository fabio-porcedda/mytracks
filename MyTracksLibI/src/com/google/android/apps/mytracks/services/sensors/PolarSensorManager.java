/*
 * Copyright 2011 Google Inc.
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

import android.bluetooth.BluetoothDevice;
import android.content.Context;

/**
 * A Polar heart rate sensor manager.
 * 
 * @author Jimmy Shih
 */
public class PolarSensorManager extends BluetoothSensorManager {

  protected static boolean supports(BluetoothDevice device) {
	  String name = device.getName();

	  if (name == null)
		  return false;
      return name.startsWith("Polar iWL");
  }

  public PolarSensorManager(Context context, BluetoothDevice device) {
    super(context, new PolarMessageParser(), device);
  }
}
