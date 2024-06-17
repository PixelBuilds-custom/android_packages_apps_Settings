/*
 * Copyright (C) 2017 The Android Open Source Project
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
package com.android.settings.development;

import android.content.Context;
import android.provider.Settings;

import androidx.annotation.VisibleForTesting;
import androidx.preference.Preference;
import androidx.preference.SwitchPreferenceCompat;

import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

public class AppsNotRespondingPreferenceController extends DeveloperOptionsPreferenceController
        implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {

    private static final String SHOW_ALL_ANRS_KEY = "show_all_anrs";

    @VisibleForTesting
    static final int SETTING_VALUE_ON = 1;
    @VisibleForTesting
    static final int SETTING_VALUE_OFF = 0;

    public AppsNotRespondingPreferenceController(Context context) {
        super(context);
    }

    @Override
    public String getPreferenceKey() {
        return SHOW_ALL_ANRS_KEY;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final boolean isEnabled = (Boolean) newValue;
        Settings.Secure.putInt(mContext.getContentResolver(),
                Settings.Secure.ANR_SHOW_BACKGROUND,
                isEnabled ? SETTING_VALUE_ON : SETTING_VALUE_OFF);
        return true;
    }

    @Override
    public void updateState(Preference preference) {
        final int mode = Settings.Secure.getInt(mContext.getContentResolver(),
                Settings.Secure.ANR_SHOW_BACKGROUND, SETTING_VALUE_OFF);
        (((SwitchPreferenceCompat) mPreference)).setChecked(mode != SETTING_VALUE_OFF);
    }

    @Override
    protected void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        Settings.Secure.putInt(mContext.getContentResolver(),
                Settings.Secure.ANR_SHOW_BACKGROUND, SETTING_VALUE_OFF);
        (((SwitchPreferenceCompat) mPreference)).setChecked(false);
    }
}
