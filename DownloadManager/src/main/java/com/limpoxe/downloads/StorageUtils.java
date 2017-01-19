/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.limpoxe.downloads;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import static android.text.format.DateUtils.DAY_IN_MILLIS;

/**
 * Utility methods for managing storage space related to
 * {@link DownloadManager}.
 */
public class StorageUtils {

    /**
     * Minimum age for a file to be considered for deletion.
     */
    static final long MIN_DELETE_AGE = DAY_IN_MILLIS;

    /**
     * Reserved disk space to avoid filling disk.
     */
    static final long RESERVED_BYTES = 32 * 1024 * 1024;//32MB

    static boolean sForceFullEviction = false;

    /**
     * Ensure that requested free space exists on the partition backing the
     * given {@link FileDescriptor}. If not enough space is available, it tries
     * freeing up space as follows:
     * <ul>
     * <li>If backed by the data partition (including emulated external
     * storage), then ask {@link PackageManager} to free space from cache
     * directories.
     * <li>If backed by the cache partition, then try deleting older downloads
     * to free space.
     * </ul>
     */
    public static void ensureAvailableSpace(Context context, FileDescriptor fd)
            throws IOException, StopRequestException {
        //TODO
        Log.v("StorageUtil", "should check space here");
    }

    /**
     * Return number of available bytes on the filesystem backing the given
     * {@link FileDescriptor}, minus any {@link #RESERVED_BYTES} buffer.
     */
    private static long getAvailableBytes(FileDescriptor fd) throws IOException {
        try {
            //TODO only Sdcard check??
            String sdcardDir = Environment.getExternalStorageDirectory().getPath();
            StatFs stat = new StatFs(sdcardDir);
            long bytesAvailable = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                bytesAvailable = (long)stat.getBlockSizeLong() * (long)stat.getAvailableBlocksLong();
            } else {
                bytesAvailable = (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
            }
            return bytesAvailable - RESERVED_BYTES;
        } catch (Exception e) {
            throw new  IOException("getAvailableBytes IOException");
        }
    }

    public static String normalizeMimeType(String type) {
        if (type == null) {
            return null;
        }

        type = type.trim().toLowerCase(Locale.ROOT);

        final int semicolonIndex = type.indexOf(';');
        if (semicolonIndex != -1) {
            type = type.substring(0, semicolonIndex);
        }
        return type;
    }

    public static String buildValidExtFilename(String name) {
        if (TextUtils.isEmpty(name) || ".".equals(name) || "..".equals(name)) {
            return "(invalid)";
        }
        final StringBuilder res = new StringBuilder(name.length());
        for (int i = 0; i < name.length(); i++) {
            final char c = name.charAt(i);
            if (isValidExtFilenameChar(c)) {
                res.append(c);
            } else {
                res.append('_');
            }
        }
        return res.toString();
    }

    private static boolean isValidExtFilenameChar(char c) {
        switch (c) {
            case '\0':
            case '/':
                return false;
            default:
                return true;
        }
    }

    public static String buildValidFatFilename(String name) {
        if (TextUtils.isEmpty(name) || ".".equals(name) || "..".equals(name)) {
            return "(invalid)";
        }
        final StringBuilder res = new StringBuilder(name.length());
        for (int i = 0; i < name.length(); i++) {
            final char c = name.charAt(i);
            if (isValidFatFilenameChar(c)) {
                res.append(c);
            } else {
                res.append('_');
            }
        }
        return res.toString();
    }

    private static boolean isValidFatFilenameChar(char c) {
        if ((0x00 <= c && c <= 0x1f)) {
            return false;
        }
        switch (c) {
            case '"':
            case '*':
            case '/':
            case ':':
            case '<':
            case '>':
            case '?':
            case '\\':
            case '|':
            case 0x7F:
                return false;
            default:
                return true;
        }
    }

    /**
     * Check if given filename is valid for a FAT filesystem.
     */
    public static boolean isValidFatFilename(String name) {
        return (name != null) && name.equals(buildValidFatFilename(name));
    }

    public static String trimFilename(String str, int maxBytes) throws UnsupportedEncodingException {
        final StringBuilder res = new StringBuilder(str);
        trimFilename(res, maxBytes);
        return res.toString();
    }

    private static void trimFilename(StringBuilder res, int maxBytes) throws UnsupportedEncodingException {
        byte[] raw = res.toString().getBytes("UTF-8");
        if (raw.length > maxBytes) {
            maxBytes -= 3;
            while (raw.length > maxBytes) {
                res.deleteCharAt(res.length() / 2);
                raw = res.toString().getBytes("UTF-8");
            }
            res.insert(res.length() / 2, "...");
        }
    }
}
