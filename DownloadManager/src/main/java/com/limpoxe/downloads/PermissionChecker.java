package com.limpoxe.downloads;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;

/**
 * Created by cailiming on 16/12/14.
 */

public class PermissionChecker {
    public static boolean writeExternalStoragePermission(Context context) {
        Log.w("PermissionChecker", "check WRITE_EXTERNAL_STORAGE");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int permissionState = context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionState == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFileCanDelate(Context context, File file) {
        return true;
    }
}
