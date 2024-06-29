package com.google.android.settings.aware;

import android.content.Context;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.R;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.security.SecurityFeatureProvider;
import com.android.settings.security.trustagent.TrustAgentManager;

public class AwareLockPreferenceController extends AwareTogglePreferenceController {
    private final LockPatternUtils mLockPatternUtils;
    private final TrustAgentManager mTrustAgentManager;


    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_display;
    }

    public AwareLockPreferenceController(Context context, String str) {
        super(context, str);
        SecurityFeatureProvider securityFeatureProvider = FeatureFactory.getFactory(context).getSecurityFeatureProvider();
        this.mLockPatternUtils = securityFeatureProvider.getLockPatternUtils(context);
        this.mTrustAgentManager = securityFeatureProvider.getTrustAgentManager();
    }

    public int getAvailabilityStatus() {
        return (!this.mLockPatternUtils.isSecure(UserHandle.myUserId()) || !this.mHelper.isGestureConfigurable()) ? 5 : 0;
    }

    public boolean isChecked() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "aware_lock_enabled", 1) == 1;
    }

    public boolean setChecked(boolean z) {
        this.mHelper.writeFeatureEnabled("aware_lock_enabled", z);
        Settings.Secure.putInt(this.mContext.getContentResolver(), "aware_lock_enabled", z ? 1 : 0);
        return true;
    }

    public CharSequence getSummary() {
        CharSequence activeTrustAgentLabel = this.mTrustAgentManager.getActiveTrustAgentLabel(this.mContext, this.mLockPatternUtils);
        if (!TextUtils.isEmpty(activeTrustAgentLabel)) {
            return this.mContext.getString(R.string.lockpattern_settings_power_button_instantly_locks_summary, new Object[]{activeTrustAgentLabel});
        }
        return this.mContext.getString(R.string.summary_placeholder);
    }
}
