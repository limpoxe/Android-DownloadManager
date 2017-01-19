package com.limpoxe.downloads;

import android.content.Context;
import android.util.Log;

import java.io.File;

/**
 * Created by cailiming on 16/12/14.
 */

public class PermissionChecker {
    public static boolean writeExternalStoragePermission() {
        Log.e("PermissionChecker", "check WRITE_EXTERNAL_STORAGE");
        return true;
    }

    public static boolean isFileCanDelate(Context context, File file) {
        return true;
    }
}
