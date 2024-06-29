package com.google.android.settings.aware;

import android.content.Context;

public class AwareSettingsPreferenceController extends AwareGesturePreferenceController {

    /* access modifiers changed from: protected */
    public CharSequence getGestureSummary() {
        return null;
    }

    public AwareSettingsPreferenceController(Context context, String str) {
        super(context, str);
    }
}
