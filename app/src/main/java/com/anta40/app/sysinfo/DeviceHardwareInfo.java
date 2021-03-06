package com.anta40.app.sysinfo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

import com.jaredrummler.android.device.DeviceName;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import github.nisrulz.easydeviceinfo.base.DeviceType;
import github.nisrulz.easydeviceinfo.base.EasyDeviceMod;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;

public class DeviceHardwareInfo {

    private Context ctxt;
    private Activity activity;
    private EasyDeviceMod easyDeviceMod;
    private String deviceUniqueIdentifier;

    public DeviceHardwareInfo (Context ctxt, Activity activity){

        this.ctxt = ctxt;
        this.activity = activity;
        easyDeviceMod = new EasyDeviceMod(ctxt);
    }

    public String getDeviceInfo(){
        String str = "";

        str += "\nDEVICE HARDWARE INFO";

        // disk space
        File external = Environment.getExternalStorageDirectory();
        str += "\nDisk space: "+Util.humanReadableByteCount(external.getTotalSpace(), true);
        str += "\nFree disk space: "+Util.humanReadableByteCount(external.getFreeSpace(), true);

        // screen resolution
        WindowManager wm = (WindowManager) ctxt.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        str += "\nScreenWidth: "+size.x+", ScreenHeight:"+size.y;

        str += "\nDevice brand name: " + android.os.Build.BRAND;

        int devType = easyDeviceMod.getDeviceType(activity);
        String deviceType = "";
        switch (devType) {
            case DeviceType.WATCH:
                deviceType= "watch";
                break;
            case DeviceType.PHONE:
                deviceType = "phone";
                break;
            case DeviceType.PHABLET:
                deviceType = "phablet";
                break;
            case DeviceType.TABLET:
                deviceType = "tablet";
                break;
            case DeviceType.TV:
                deviceType = "TV";
                break;
        }

        str += "\nDevice type: "+deviceType;
        str += "\nDevice model: ";
        str += "\nDevice name: "+ DeviceName.getDeviceName();


        str += "\nCPU usage: ";

        // memory
        ActivityManager actManager = (ActivityManager) ctxt.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        str += "\nTotal memory: "+Util.humanReadableByteCount(memInfo.totalMem, true);
        str += "\nUsed memory: "+Util.humanReadableByteCount(memInfo.totalMem-memInfo.availMem, true);

        // battery
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = ctxt.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        str += "\nBattery level: "+level+"%";
        str += "\nCharging: "+ (status == BatteryManager.BATTERY_STATUS_CHARGING);
        str += "\nFully charged: "+(status == BatteryManager.BATTERY_STATUS_FULL);

        // earphone
        AudioManager am = (AudioManager) ctxt.getSystemService(Context.AUDIO_SERVICE);
        str += "\nHeadphones attached: "+am.isWiredHeadsetOn();

        str += "\nUUID: "+getUUID();

        WifiManager wifiMan = (WifiManager) ctxt.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        String macAddr = wifiInf.getMacAddress();
        str += "\nWiFi MAC address: "+macAddr;

        str += "\nAndroid ID: ";

        str += "\nIs simulator: "+isRunningOnEmulator();


        str += "\nIMEI: "+getDeviceIMEI();

        String wifiStatus = "";
        if(ctxt.getSystemService(Context.WIFI_SERVICE) == null) {
            wifiStatus = "Not supported";
        }
        else {
            wifiStatus = "Supported";
        }
        str += "\nWifi component: "+wifiStatus;

        ConnectivityManager connManager = (ConnectivityManager) ctxt.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        String dataStatus = "Not connected";

        if( mobile.isAvailable() && mobile.getDetailedState() == NetworkInfo.DetailedState.CONNECTED ){
            dataStatus = "Connected";
        }
        str += "\nData component: "+dataStatus;

        // GPS
        PackageManager packageManager = ctxt.getPackageManager();
        boolean hasGPS = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        str += "\nGPS component: "+hasGPS;

        // Phone
        PackageManager pm = ctxt.getPackageManager();
        String phoneStatus = "Not supported";
        if (pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)){
            phoneStatus = "Supported";
        }
        str += "\nPhone component: "+phoneStatus;

        // Bluetooth
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String bluetoothStatus = "";
        if (bluetoothAdapter == null){
            bluetoothStatus = "Not supported";
        }
        else {
            bluetoothStatus = "Supported";
        }
        str += "\nBluetooth component: "+bluetoothStatus;

        // Earphone
        str += "\nEarphone component: "+am.isWiredHeadsetOn();

        // NFC
        NfcAdapter mNfcAdapter;
        String nfcStatus = "";

        try{
            mNfcAdapter = NfcAdapter.getDefaultAdapter(ctxt.getApplicationContext());

            if (mNfcAdapter == null) {
                nfcStatus = "Not supported.";
            }

            if (!mNfcAdapter.isEnabled()) {
                nfcStatus = "Supported. NFC is disabled.";
            } else {
                nfcStatus = "Supported. NFC is enabled.";
            }

        }catch (Exception e) {
            nfcStatus = "No properties found";
        }

        str += "\nNFC component: "+nfcStatus;

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

    public String getDeviceIMEI() {
        String[] permissions = {Manifest.permission.READ_PHONE_STATE};
        String rationale = "Please provide permission to detect your IMEI";
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle("Info")
                .setSettingsDialogTitle("Warning");

        /*
        String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) ctxt.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            deviceUniqueIdentifier = tm.getDeviceId();
        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Settings.Secure.getString(ctxt.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;
        */

       // final String deviceUniqueIdentifier;

        Permissions.check(activity, permissions, rationale, options, new PermissionHandler() {
            @SuppressLint("MissingPermission")
            @Override
            public void onGranted() {
                TelephonyManager tm = (TelephonyManager) ctxt.getSystemService(TELEPHONY_SERVICE);
                if (null != tm) {
                    deviceUniqueIdentifier = tm.getDeviceId();
                }
                if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
                    deviceUniqueIdentifier = Settings.Secure.getString(ctxt.getContentResolver(), Settings.Secure.ANDROID_ID);
                }
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                deviceUniqueIdentifier = "NULL";
            }
        });

        return deviceUniqueIdentifier;
    }

    public String getUUID(){
        TelephonyManager tm = (TelephonyManager) ctxt.getSystemService(TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String tmSerial = tm.getSimSerialNumber();
        @SuppressLint("MissingPermission") String tmDeviceId = tm.getDeviceId();
        String androidId = android.provider.Settings.Secure.getString(ctxt.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        if (tmSerial  == null) tmSerial   = "1";
        if (tmDeviceId== null) tmDeviceId = "1";
        if (androidId == null) androidId  = "1";
        UUID deviceUuid = new UUID(androidId.hashCode(),
                ((long)tmDeviceId.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }
}
