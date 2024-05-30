package org.openobservatory.ooniprobe.common;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.view.ContextThemeWrapper;

import java.util.Locale;

public class LocaleUtils {


    private static Locale sLocale;

    /**
     * Set the app's locale to the one specified by the given string.
     *
     * @param localeString The locale in the format: language, language_country, language_country_variant.
     *                     e.g., en, fr, pt_BR, zh_CN, zh_TW, etc.
     */
    public static void setLocale(String localeString) {
        String[] parts = localeString.split("_");
        Locale locale = new Locale(parts[0]);
        if (parts.length == 2) {
            locale = new Locale(parts[0], parts[1]);
        } else if (parts.length == 3) {
            locale = new Locale(parts[0], parts[1], parts[2]);
        }
        sLocale = locale;
        if (sLocale != null) {
            Locale.setDefault(sLocale);
        }
    }

    public static void updateConfig(ContextThemeWrapper wrapper) {
        if (sLocale != null) {
            Configuration configuration = new Configuration();
            configuration.setLocale(sLocale);
            wrapper.applyOverrideConfiguration(configuration);
        }
    }


    public static void updateConfig(Application app, Configuration configuration) {
        if (sLocale != null) {
            //Wrapping the configuration to avoid Activity endless loop
            Configuration config = new Configuration(configuration);
            config.locale = sLocale;
            Resources res = app.getBaseContext().getResources();
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
    }
}