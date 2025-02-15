package com.google.android.settings.aware;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.aware.AwareFeatureProvider;
import com.android.settings.gestures.GesturePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.google.android.settings.aware.AwareHelper;

public class TapGesturePreferenceController extends GesturePreferenceController implements AwareHelper.Callback {
    private static final int OFF = 0;
    private static final int ON = 1;
    private static final String PREF_KEY_VIDEO = "gesture_tap_video";
    private final AwareFeatureProvider mFeatureProvider;
    private AwareHelper mHelper;
    private Preference mPreference;

    /* access modifiers changed from: protected */
    public String getVideoPrefKey() {
        return PREF_KEY_VIDEO;
    }

    public boolean isPublicSlice() {
        return true;
    }

    public TapGesturePreferenceController(Context context, String str) {
        super(context, str);
        this.mFeatureProvider = FeatureFactory.getFactory(context).getAwareFeatureProvider();
        this.mHelper = new AwareHelper(context);
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    public int getAvailabilityStatus() {
        if (!this.mFeatureProvider.isSupported(this.mContext)) {
            return 3;
        }
        return !this.mHelper.isGestureConfigurable() ? 5 : 0;
    }

    /* access modifiers changed from: protected */
    public boolean canHandleClicks() {
        return this.mHelper.isGestureConfigurable();
    }

    public boolean isChecked() {
        return this.mFeatureProvider.isEnabled(this.mContext) && Settings.Secure.getInt(this.mContext.getContentResolver(), "tap_gesture", 0) == 1;
    }

    public boolean setChecked(boolean z) {
        this.mHelper.writeFeatureEnabled("tap_gesture", z);
        return Settings.Secure.putInt(this.mContext.getContentResolver(), "tap_gesture", z ? 1 : 0);
    }

    public void onStart() {
        this.mHelper.register(this);
    }

    public void onStop() {
        this.mHelper.unregister();
    }

    public void onChange(Uri uri) {
        updateState(this.mPreference);
    }
}
