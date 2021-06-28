package com.phone.info;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

public class BugReportDeviceInfoBuilder extends BugReportPropertiesBuilder {

    static private Map<String, String> staticProperties = new HashMap<>();

    static {
        staticProperties.put("Client", "android-google");
        staticProperties.put("Roaming", "mobile-notroaming");
        staticProperties.put("TimeZone", TimeZone.getDefault().getID());
        staticProperties.put("GL.Extensions", TextUtils.join(",", EglExtensionRetriever.getEglExtensions()));
    }

    public BugReportDeviceInfoBuilder(Context context) {
        super(context);
    }

/*    @Override
    public BugReportDeviceInfoBuilder setFileName(String fname){
        File sPath = context.getDir(Constants.mDir, Context.MODE_PRIVATE);
        file = new File(sPath,fname);
        return this;
    }*/

    @Override
    public BugReportDeviceInfoBuilder build() {
        setContent(buildProperties(getDeviceInfo()));
        super.build();
        return this;
    }

    private Map<String, String> getDeviceInfo() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("UserReadableName", getUserReadableName());
        values.putAll(getBuildValues());
        values.putAll(getConfigurationValues());
        values.putAll(getDisplayMetricsValues());
        values.putAll(getPackageManagerValues());
        values.putAll(getOperatorValues());
        values.putAll(staticProperties);
        return values;
    }

    private String getUserReadableName() {
        String fingerprint = TextUtils.isEmpty(Build.FINGERPRINT) ? "" : Build.FINGERPRINT;
        String manufacturer = TextUtils.isEmpty(Build.MANUFACTURER) ? "" : Build.MANUFACTURER;
        String product = TextUtils.isEmpty(Build.PRODUCT) ? "" : Build.PRODUCT.replace("aokp_", "").replace("aosp_", "").replace("cm_", "").replace("lineage_", "");
        String model = TextUtils.isEmpty(Build.MODEL) ? "" : Build.MODEL;
        String device = TextUtils.isEmpty(Build.DEVICE) ? "" : Build.DEVICE;
        String result = (fingerprint.toLowerCase().contains(product.toLowerCase()) || product.toLowerCase().contains(device.toLowerCase()) || device.toLowerCase().contains(product.toLowerCase())) ? model : product;
        if (!result.toLowerCase().contains(manufacturer.toLowerCase())) {
            result = manufacturer + " " + result;
        }
        if (TextUtils.isEmpty(result)) {
            return "";
        }
        return (result.substring(0, 1).toUpperCase() + result.substring(1))
                .replace("\n", " ")
                .replace("\r", " ")
                .replace(",", " ")
                .trim();
    }

    private Map<String, String> getBuildValues() {
        Map<String, String> values = new LinkedHashMap<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            values.put("Build.HARDWARE", Build.HARDWARE);
            values.put("Build.RADIO", Build.RADIO);
            values.put("Build.BOOTLOADER", Build.BOOTLOADER);
        }
        values.put("Build.FINGERPRINT", Build.FINGERPRINT);
        values.put("Build.BRAND", Build.BRAND);
        values.put("Build.DEVICE", Build.DEVICE);
        values.put("Build.VERSION.SDK_INT", Integer.toString(Build.VERSION.SDK_INT));
        values.put("Build.MODEL", Build.MODEL);
        values.put("Build.MANUFACTURER", Build.MANUFACTURER);
        values.put("Build.PRODUCT", Build.PRODUCT);
        values.put("Build.ID", Build.ID);
        values.put("Build.VERSION.RELEASE", Build.VERSION.RELEASE);
        return values;
    }

    private Map<String, String> getConfigurationValues() {
        Map<String, String> values = new LinkedHashMap<>();
        Configuration config = context.getResources().getConfiguration();
        values.put("TouchScreen", Integer.toString(config.touchscreen));
        values.put("Keyboard", Integer.toString(config.keyboard));
        values.put("Navigation", Integer.toString(config.navigation));
        values.put("ScreenLayout", Integer.toString(config.screenLayout & 15));
        values.put("HasHardKeyboard", Boolean.toString(config.keyboard == Configuration.KEYBOARD_QWERTY));
        values.put("HasFiveWayNavigation", Boolean.toString(config.navigation == Configuration.NAVIGATIONHIDDEN_YES));
        values.put("GL.Version", Integer.toString(((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getDeviceConfigurationInfo().reqGlEsVersion));
        
        return values;
    }

    //屏幕尺寸
    private Map<String, String> getDisplayMetricsValues() {
        Map<String, String> values = new LinkedHashMap<>();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        values.put("Screen.Density", Integer.toString(metrics.densityDpi));
        values.put("Screen.Width", Integer.toString(metrics.widthPixels));
        values.put("Screen.Height", Integer.toString(metrics.heightPixels));
        return values;
    }

    private Map<String, String> getPackageManagerValues() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("Platforms", TextUtils.join(",", NativeDeviceInfoProvider.getPlatforms()));
        values.put("SharedLibraries", TextUtils.join(",", NativeDeviceInfoProvider.getSharedLibraries(context)));
        values.put("Features", TextUtils.join(",", NativeDeviceInfoProvider.getFeatures(context)));
        values.put("Locales", TextUtils.join(",", NativeDeviceInfoProvider.getLocales(context)));
        NativeGsfVersionProvider gsfVersionProvider = new NativeGsfVersionProvider(context);
        values.put("GSF.version", Integer.toString(gsfVersionProvider.getGsfVersionCode(false)));
        values.put("Vending.version", Integer.toString(gsfVersionProvider.getVendingVersionCode(false)));
        values.put("Vending.versionString", gsfVersionProvider.getVendingVersionString(false));
        return values;
    }

    private Map<String, String> getOperatorValues() {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Map<String, String> values = new LinkedHashMap<>();
        values.put("CellOperator", tm.getNetworkOperator());
        values.put("SimOperator", tm.getSimOperator());
        return values;
    }
}
