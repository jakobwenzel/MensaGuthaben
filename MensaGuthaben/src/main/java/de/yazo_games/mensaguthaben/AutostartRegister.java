package de.yazo_games.mensaguthaben;

import android.content.ComponentName;
import android.content.pm.PackageManager;

/**
 * register or unregister the app to be autostarted on nfc discovery
 */
public class AutostartRegister {
    static void register(PackageManager pm, boolean autostart) {
        System.out.println("Autostart is "+autostart);
        int enabled = autostart ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

        System.out.println("Setting to "+enabled);
        pm.setComponentEnabledSetting(
                new ComponentName("de.yazo_games.mensaguthaben", "de.yazo_games.mensaguthaben.ActivityAlias"),
                enabled,
                PackageManager.DONT_KILL_APP);
    }
}
