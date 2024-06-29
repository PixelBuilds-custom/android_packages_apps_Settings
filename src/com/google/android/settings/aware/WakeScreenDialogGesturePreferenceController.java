package com.google.android.settings.aware;

import android.content.Context;
import android.provider.Settings;
import com.android.settings.R;

public class WakeScreenDialogGesturePreferenceController extends AwareGesturePreferenceController {

    public WakeScreenDialogGesturePreferenceController(Context context, String str) {
        super(context, str);
    }

    /* access modifiers changed from: protected */
    public CharSequence getGestureSummary() {
        return this.mContext.getText(isGestureEnabled() ? R.string.gesture_setting_on : R.string.gesture_setting_off);
    }

    private boolean isGestureEnabled() {
        return this.mFeatureProvider.isEnabled(this.mContext) && Settings.Secure.getInt(this.mContext.getContentResolver(), "doze_wake_screen_gesture", 1) == 1;
    }
}
