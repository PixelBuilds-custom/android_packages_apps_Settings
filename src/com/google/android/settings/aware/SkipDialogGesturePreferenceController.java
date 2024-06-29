package com.google.android.settings.aware;

import android.content.Context;
import android.provider.Settings;
import com.android.settings.R;

public class SkipDialogGesturePreferenceController extends AwareGesturePreferenceController {

    public SkipDialogGesturePreferenceController(Context context, String str) {
        super(context, str);
    }

    private boolean isSkipGestureEnabled() {
        return this.mHelper.isEnabled() && Settings.Secure.getInt(this.mContext.getContentResolver(), "skip_gesture", 1) == 1;
    }

    /* access modifiers changed from: protected */
    public CharSequence getGestureSummary() {
        return this.mContext.getText(isSkipGestureEnabled() ? R.string.gesture_skip_on_summary : R.string.gesture_setting_off);
    }
}
