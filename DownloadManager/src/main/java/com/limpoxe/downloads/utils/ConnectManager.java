package com.limpoxe.downloads.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.limpoxe.downloads.Constants;

/**
 * Created by cailiming on 16/12/14.
 */

public class ConnectManager {
    public static final int TYPE_NONE        = -1;
    public static final int TYPE_MOBILE      = 0;
    public static final int TYPE_WIFI        = 1;
    public static final int TYPE_MOBILE_MMS  = 2;
    public static final int TYPE_MOBILE_SUPL = 3;
    public static final int TYPE_MOBILE_DUN  = 4;
    public static final int TYPE_MOBILE_HIPRI = 5;
    public static final int TYPE_WIMAX       = 6;
    public static final int TYPE_BLUETOOTH   = 7;
    public static final int TYPE_DUMMY       = 8;
    public static final int TYPE_ETHERNET    = 9;
    public static final int TYPE_MOBILE_FOTA = 10;
    public static final int TYPE_MOBILE_IMS  = 11;
    public static final int TYPE_MOBILE_CBS  = 12;
    public static final int TYPE_WIFI_P2P    = 13;
    public static final int TYPE_MOBILE_IA = 14;
    public static final int TYPE_MOBILE_EMERGENCY = 15;
    public static final int TYPE_PROXY = 16;
    public static final int TYPE_VPN = 17;

    public static final int MAX_RADIO_TYPE   = TYPE_VPN;
    public static final int MAX_NETWORK_TYPE = TYPE_VPN;

    public static boolean isNetworkTypeValid(int networkType) {
        return networkType >= 0 && networkType <= MAX_NETWORK_TYPE;
    }

    public static String getNetworkTypeName(int type) {
        switch (type) {
            case TYPE_MOBILE:
                return "MOBILE";
            case TYPE_WIFI:
                return "WIFI";
            case TYPE_MOBILE_MMS:
                return "MOBILE_MMS";
            case TYPE_MOBILE_SUPL:
                return "MOBILE_SUPL";
            case TYPE_MOBILE_DUN:
                return "MOBILE_DUN";
            case TYPE_MOBILE_HIPRI:
                return "MOBILE_HIPRI";
            case TYPE_WIMAX:
                return "WIMAX";
            case TYPE_BLUETOOTH:
                return "BLUETOOTH";
            case TYPE_DUMMY:
                return "DUMMY";
            case TYPE_ETHERNET:
                return "ETHERNET";
            case TYPE_MOBILE_FOTA:
                return "MOBILE_FOTA";
            case TYPE_MOBILE_IMS:
                return "MOBILE_IMS";
            case TYPE_MOBILE_CBS:
                return "MOBILE_CBS";
            case TYPE_WIFI_P2P:
                return "WIFI_P2P";
            case TYPE_MOBILE_IA:
                return "MOBILE_IA";
            case TYPE_MOBILE_EMERGENCY:
                return "MOBILE_EMERGENCY";
            case TYPE_PROXY:
                return "PROXY";
            case TYPE_VPN:
                return "VPN";
            default:
                return Integer.toString(type);
        }
    }

    public static boolean isNetworkTypeMobile(int networkType) {
        switch (networkType) {
            case TYPE_MOBILE:
            case TYPE_MOBILE_MMS:
            case TYPE_MOBILE_SUPL:
            case TYPE_MOBILE_DUN:
            case TYPE_MOBILE_HIPRI:
            case TYPE_MOBILE_FOTA:
            case TYPE_MOBILE_IMS:
            case TYPE_MOBILE_CBS:
            case TYPE_MOBILE_IA:
            case TYPE_MOBILE_EMERGENCY:
                return true;
            default:
                return false;
        }
    }

    public static boolean isNetworkTypeWifi(int networkType) {
        switch (networkType) {
            case TYPE_WIFI:
            case TYPE_WIFI_P2P:
                return true;
            default:
                return false;
        }
    }

    public static NetworkInfo getActiveNetworkInfo(Context context, int uid) {
        ConnectivityManager connectivity =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            Log.w(Constants.TAG, "couldn't get connectivity manager");
            return null;
        }

        final NetworkInfo activeInfo = connectivity.getActiveNetworkInfo();
        if (activeInfo == null && Constants.LOGVV) {
            Log.v(Constants.TAG, "network is not available");
        }
        return activeInfo;
    }
}
