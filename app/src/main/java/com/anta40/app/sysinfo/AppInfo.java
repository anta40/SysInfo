package com.anta40.app.sysinfo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import github.nisrulz.easydeviceinfo.base.EasyDeviceMod;

public class AppInfo {

    private Context ctxt;
    private final String PACKAGE_NAME = "cn.com.paic.smartagent2";

    public AppInfo(Context ctxt){
        this.ctxt = ctxt;
    }

    public String getAppInfo(){
        String str = "";
        str += "\n\nAPP INFO";

        PackageInfo pinfo = null;
        try {
            pinfo = ctxt.getPackageManager().getPackageInfo(PACKAGE_NAME, 0);

            PackageManager packageManager= ctxt.getApplicationContext().getPackageManager();
            String appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(PACKAGE_NAME,
                    PackageManager.GET_META_DATA));

            str += "\nApplication version: "+ pinfo.versionName;
            str += "\nApplication build version: "+pinfo.versionCode;
            str += "\nApp name: "+appName;
            str += "\nDebugger attached: "+isRunningOnEmulator();
            str += "\nApp status: ";
            str += "\nLogin time: ";

        }
        catch (PackageManager.NameNotFoundException nfe){

        }

        return str;
    }

    private boolean isRunningOnEmulator() {
        boolean result =
                Build.FINGERPRINT.startsWith("generic")//
                        ||Build.FINGERPRINT.startsWith("unknown")//
                        ||Build.MODEL.contains("google_sdk")//
                        ||Build.MODEL.contains("Emulator")//
                        ||Build.MODEL.contains("Android SDK built for x86");
        if (result)
            return true;
        result |= Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic");

        if(result)
            return true;
        result |= "google_sdk".equals(Build.PRODUCT);

        return result;
    }
}
