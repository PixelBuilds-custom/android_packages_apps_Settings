/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.deviceinfo.firmwareversion;

import android.content.Context;
import android.os.SystemProperties;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;

public class CustomFirmwareVersionPreferenceController extends BasePreferenceController {

    private static final String VERSION_PROPERTY = "ro.build.version.custom";
    private static final String BUILD_TYPE_PROPERTY = "ro.pb.buildtype";
    private static final String DEVICE_CODENAME_PROPERTY = "ro.build.version.device";

    public CustomFirmwareVersionPreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public CharSequence getSummary() {
        String internalVer = SystemProperties.get(VERSION_PROPERTY,
                mContext.getString(R.string.device_info_default));
        String deviceCodename = SystemProperties.get(DEVICE_CODENAME_PROPERTY, 
                mContext.getString(R.string.device_info_default));
        String buildType = SystemProperties.get(BUILD_TYPE_PROPERTY,
                mContext.getString(R.string.device_info_default));
        // Only append build type when this is a release build
        if (buildType != null && "release".equals(buildType)) {
            return internalVer + " | " + deviceCodename + " | " + buildType;
        } else {
            return internalVer + " | " + deviceCodename;
        }
    }
}
