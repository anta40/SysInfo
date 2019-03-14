package com.anta40.app.sysinfo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by Andre Tampubolon
 */
public class NetworkInfo {

    private Context ctxt;

    public NetworkInfo(Context ctxt){
        this.ctxt = ctxt;
    }

    public String getNetworkInfo(){
        String str = "";
        str += "\n\nNETWORK INFO";

        WifiManager wm = (WifiManager) ctxt.getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        str += "\nIP address: "+ip;
        str += "\nNetmask address: ";
        str += "\nCell/wifi: "+checkNetworkStatus(ctxt);
        str += "\n2G/3G/4G: "+getCellMode(ctxt);

        TelephonyManager  tm=(TelephonyManager) ctxt.getSystemService(Context.TELEPHONY_SERVICE);
        String networkType = "";

        switch(tm.getPhoneType()){
            case TelephonyManager.PHONE_TYPE_CDMA:
                networkType = "CDMA";
                break;

            case TelephonyManager.PHONE_TYPE_GSM:
                networkType = "GSM";
                break;
        }

        str += "\nGSM/CDMA: "+networkType;
        str += "\nBase station number: ";
        str += "\nBase station single status: ";
        return str;
    }

    public static String checkNetworkStatus(final Context context) {

        String networkStatus = "";

        // Get connect mangaer
        final ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // check for wifi
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        // check for mobile data
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if( wifi.isAvailable() ) {
            networkStatus = "wifi";
        } else if( mobile.isAvailable() ) {
            networkStatus = "cell";
        } else {
            networkStatus = "no network";
        }

        return networkStatus;

    }

    public static String getCellMode(Context ctxt){
        TelephonyManager tm = (TelephonyManager) ctxt.getSystemService(Context.TELEPHONY_SERVICE);

        String res = "Not available";

        if ((tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSDPA)) {
            res="3G";
        } else if ((tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSPAP)) {
            res="4G";
        }else if ((tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_EDGE)) {
            res="2G";
        }

        return res;
    }
}
