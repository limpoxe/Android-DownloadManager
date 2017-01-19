/*
 * Copyright (C) 2012 The Android Open Source Project
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
import android.util.Log;

import java.util.Collection;

public class DownloadNotifier {

    private final Object mLock1 = new Object();
    private final Object mLock2 = new Object();
    private final Object mLock3 = new Object();

    public DownloadNotifier(Context context) {
    }

    public void cancelAll() {
        Log.v("DownloadNotifier", "cancelAll Notification");
    }

    public void notifyDownloadSpeed(long id, long bytesPerSecond) {
        synchronized (mLock1) {
            if (bytesPerSecond != 0) {
                Log.v("DownloadNotifier", "notifyDownloadSpeed " + id + " " + bytesPerSecond);
            } else {
                Log.v("DownloadNotifier", "notifyDownloadSpeed " + id + " " + bytesPerSecond);
            }
        }
    }

    public void updateWith(Collection<DownloadInfo> downloads) {
        synchronized (mLock2) {
            Log.v("DownloadNotifier", "updateWith ");
        }
    }

    public void dumpSpeeds() {
        synchronized (mLock3) {
            Log.v("DownloadNotifier", "dumpSpeed");
        }
    }

}
