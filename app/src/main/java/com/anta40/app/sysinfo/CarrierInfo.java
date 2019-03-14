package com.anta40.app.sysinfo;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by Andre Tampubolon
 */
public class CarrierInfo {

    private Context ctxt;

    public CarrierInfo(Context ctxt){
        this.ctxt = ctxt;
    }

    public String getCarrierInfo(){
        String str = "";
        TelephonyManager tm = (TelephonyManager) ctxt.getSystemService(Context.TELEPHONY_SERVICE);

        str += "\n\nCARRIER INFO";

        str += "\nCarrier name: "+tm.getSimOperatorName()+" "+tm.getNetworkOperatorName();
        str += "\nAllows VOIP: ";
        str += "\nCarrier ISO country code: "+tm.getNetworkCountryIso();

        String networkOperator = tm.getNetworkOperator();
        int mnc = 0, mcc = 0;

        if (!TextUtils.isEmpty(networkOperator)){
            mcc = Integer.parseInt(networkOperator.substring(0, 3));
            mnc = Integer.parseInt(networkOperator.substring(3));
        }

        str += "\nCarrier mobile country code: "+mcc;
        str += "\nCarrier mobile network code: "+mnc;

        return str;
    }
}
