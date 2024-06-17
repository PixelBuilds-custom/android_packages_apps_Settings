/*
 * Copyright (C) 2020 The Android Open Source Project
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

package com.android.settings.accessibility;

import static com.android.settings.accessibility.DisableAnimationsPreferenceController.ANIMATION_OFF_VALUE;
import static com.android.settings.accessibility.DisableAnimationsPreferenceController.ANIMATION_ON_VALUE;
import static com.android.settings.accessibility.DisableAnimationsPreferenceController.TOGGLE_ANIMATION_TARGETS;

import static com.google.common.truth.Truth.assertThat;

import android.content.Context;
import android.os.Looper;
import android.provider.Settings;

import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.android.settings.core.BasePreferenceController;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DisableAnimationsPreferenceControllerTest {

    private static final String TEST_PREFERENCE_KEY = "disable_animation";
    private final Context mContext = ApplicationProvider.getApplicationContext();

    private PreferenceScreen mScreen;
    private SwitchPreferenceCompat mPreference;
    private DisableAnimationsPreferenceController mController;

    @Before
    public void setUp() {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
        PreferenceManager preferenceManager = new PreferenceManager(mContext);
        mScreen = preferenceManager.createPreferenceScreen(mContext);
        final SwitchPreferenceCompat preference = new SwitchPreferenceCompat(mContext);
        preference.setKey(TEST_PREFERENCE_KEY);
        mScreen.addPreference(preference);

        mController = new DisableAnimationsPreferenceController(mContext, TEST_PREFERENCE_KEY);
        mController.displayPreference(mScreen);
        mPreference = mScreen.findPreference(TEST_PREFERENCE_KEY);
    }

    @Test
    public void getAvailabilityStatus_shouldReturnAvailable() {
        assertThat(mController.getAvailabilityStatus()).isEqualTo(
                BasePreferenceController.AVAILABLE);
    }

    @Test
    public void isChecked_enabledAnimation_shouldReturnFalse() {
        setAnimationScale(ANIMATION_ON_VALUE);

        mController.updateState(mPreference);

        assertThat(mController.isChecked()).isFalse();
        assertThat(mPreference.isChecked()).isFalse();
    }

    @Test
    public void isChecked_disabledAnimation_shouldReturnTrue() {
        setAnimationScale(ANIMATION_OFF_VALUE);

        mController.updateState(mPreference);

        assertThat(mController.isChecked()).isTrue();
        assertThat(mPreference.isChecked()).isTrue();
    }

    @Test
    public void setChecked_disabledAnimation_shouldDisableAnimationTargets() {
        mController.setChecked(true);

        for (String animationSetting : TOGGLE_ANIMATION_TARGETS) {
            final float value = Settings.Global.getFloat(mContext.getContentResolver(),
                    animationSetting, /* def= */ -1.0f);
            assertThat(Float.compare(value, ANIMATION_OFF_VALUE)).isEqualTo(0);
        }
    }

    @Test
    public void setChecked_enabledAnimation_shouldEnableAnimationTargets() {
        mController.setChecked(false);

        for (String animationSetting : TOGGLE_ANIMATION_TARGETS) {
            final float value = Settings.Global.getFloat(mContext.getContentResolver(),
                    animationSetting, /* def= */ -1.0f);
            assertThat(Float.compare(value, ANIMATION_ON_VALUE)).isEqualTo(0);
        }
    }

    @Test
    public void onStart_enabledAnimation_shouldReturnFalse() {
        mController.onStart();

        setAnimationScale(ANIMATION_ON_VALUE);

        assertThat(mController.isChecked()).isFalse();
        assertThat(mPreference.isChecked()).isFalse();
    }

    @Test
    public void onStart_disabledAnimation_shouldReturnTrue() {
        mController.onStart();

        setAnimationScale(ANIMATION_OFF_VALUE);

        assertThat(mController.isChecked()).isTrue();
        assertThat(mPreference.isChecked()).isTrue();
    }

    @Test
    public void onStop_shouldNotUpdateTargets() {
        mPreference.setChecked(true);
        mController.onStart();
        mController.onStop();

        setAnimationScale(ANIMATION_ON_VALUE);

        assertThat(mPreference.isChecked()).isTrue();
    }

    private void setAnimationScale(float newValue) {
        for (String animationPreference : TOGGLE_ANIMATION_TARGETS) {
            Settings.Global.putFloat(mContext.getContentResolver(), animationPreference,
                    newValue);
        }
    }
}