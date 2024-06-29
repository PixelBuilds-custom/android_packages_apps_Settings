package com.google.android.settings.aware;

import android.content.Context;
import android.provider.Settings;
import com.android.settings.R;

public class AwareSkipPreferenceController extends AwareBasePreferenceController {

    public AwareSkipPreferenceController(Context context, String str) {
        super(context, str);
    }

    public CharSequence getSummary() {
        return this.mContext.getText(isSkipGestureEnabled() ? R.string.gesture_skip_on_summary : R.string.gesture_setting_off);
    }

    private boolean isSkipGestureEnabled() {
        return this.mFeatureProvider.isEnabled(this.mContext) && Settings.Secure.getInt(this.mContext.getContentResolver(), "skip_gesture", 1) == 1;
    }
}
