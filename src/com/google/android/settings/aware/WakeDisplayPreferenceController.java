package com.google.android.settings.aware;

import android.content.Context;
import android.hardware.display.AmbientDisplayConfiguration;
import android.os.UserHandle;
import android.provider.Settings;
import com.android.settings.R;

public class WakeDisplayPreferenceController extends AwareTogglePreferenceController {
    private AmbientDisplayConfiguration mAmbientConfig;
    private final int mUserId = UserHandle.myUserId();

    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_display;
    }

    public WakeDisplayPreferenceController(Context context, String str) {
        super(context, str);
    }

    public int getAvailabilityStatus() {
        return (!this.mHelper.isGestureConfigurable() || !getAmbientConfig().alwaysOnEnabled(this.mUserId)) ? 5 : 0;
    }

    public boolean isChecked() {
        return getAmbientConfig().wakeDisplayGestureEnabled(this.mUserId);
    }

    public boolean setChecked(boolean z) {
        this.mHelper.writeFeatureEnabled("doze_wake_display_gesture", z);
        Settings.Secure.putInt(this.mContext.getContentResolver(), "doze_wake_display_gesture", z ? 1 : 0);
        return true;
    }

    public CharSequence getSummary() {
        return this.mContext.getText(getAmbientConfig().alwaysOnEnabled(this.mUserId) ? R.string.aware_wake_display_summary : R.string.aware_wake_display_summary_aod_off);
    }

    public void setConfig(AmbientDisplayConfiguration ambientDisplayConfiguration) {
        this.mAmbientConfig = ambientDisplayConfiguration;
    }

    private AmbientDisplayConfiguration getAmbientConfig() {
        if (this.mAmbientConfig == null) {
            this.mAmbientConfig = new AmbientDisplayConfiguration(this.mContext);
        }
        return this.mAmbientConfig;
    }
}
