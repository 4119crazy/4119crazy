package com.phone.info;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.os.Build;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class NativeDeviceInfoProvider  {

    private Context context;
    private String localeString;
    private String networkOperator = "";
    private String simOperator = "";


    public int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }

    static public List<String> getPlatforms() {
        List<String> platforms = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            platforms = Arrays.asList(Build.SUPPORTED_ABIS);
        } else {
            if (!TextUtils.isEmpty(Build.CPU_ABI)) {
                platforms.add(Build.CPU_ABI);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO && !TextUtils.isEmpty(Build.CPU_ABI2)) {
                platforms.add(Build.CPU_ABI2);
            }
        }
        return platforms;
    }

    static public List<String> getFeatures(Context context) {
        List<String> featureStringList = new ArrayList<>();
        for (FeatureInfo feature: context.getPackageManager().getSystemAvailableFeatures()) {
            if (!TextUtils.isEmpty(feature.name)) {
                featureStringList.add(feature.name);
            }
        }
        Collections.sort(featureStringList);
        return featureStringList;
    }

    static public List<String> getLocales(Context context) {
        List<String> rawLocales = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rawLocales.addAll(Arrays.asList(context.getAssets().getLocales()));
        } else {
            for (Locale locale: Locale.getAvailableLocales()) {
                rawLocales.add(locale.toString());
            }
        }
        List<String> locales = new ArrayList<>();
        for (String locale: rawLocales) {
            if (TextUtils.isEmpty(locale)) {
                continue;
            }
            locales.add(locale.replace("-", "_"));
        }
        Collections.sort(locales);
        return locales;
    }

    static public List<String> getSharedLibraries(Context context) {
        List<String> libraries = new ArrayList<>();
        libraries.addAll(Arrays.asList(context.getPackageManager().getSystemSharedLibraryNames()));
        Collections.sort(libraries);
        return libraries;
    }
}
