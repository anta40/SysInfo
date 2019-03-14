package com.anta40.app.sysinfo;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import java.util.Locale;
import java.util.TimeZone;

import github.nisrulz.easydeviceinfo.base.EasyDeviceMod;

/**
 * Created by Andre Tampubolon
 */
public class DeviceOSInfo {

    private Context ctxt;
    private EasyDeviceMod easyDeviceMod;

    public DeviceOSInfo(Context ctxt){
        this.ctxt = ctxt;
        easyDeviceMod = new EasyDeviceMod(ctxt);

    }

    public String getDeviceOSInfo(){
        String str = "";
        str += "\n\nDEVICE OS INFO";

        str += "\nSystem name: "+ Build.BRAND;
        str += "\nSystem device type: "+android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")";
        str += "\nSystem version: " + Build.VERSION.RELEASE;
        str += "\nLanguage: "+ Locale.getDefault().getDisplayLanguage();
        str += "\nCountry: "+Locale.getDefault().getCountry();

        TimeZone tz = TimeZone.getDefault();
        str += "\nTime zone: "+tz.getID();
        str += "\nRoot: "+easyDeviceMod.isDeviceRooted();
        return str;
    }
}
