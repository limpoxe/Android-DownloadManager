package com.limpoxe.example;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.limpoxe.downloads.DownloadManager;

import java.io.File;

/**
 * 没用通知栏和下载列表页面、权限检查
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Handler handler;

    DownloadManager dw;

    IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

    BroadcastReceiver downloadComplte = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("MainActivity", "downloadComplte");

            long did = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            //如果有多个任务在下载，只关心需要关心的任务
            if (did == downloadId) {
                //下载完成后打开图片
                Intent install = new Intent(Intent.ACTION_VIEW);
                Uri downloadFileUri = dw.getUriForDownloadedFile(downloadId);
                if (downloadFileUri != null) {
                    DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
                    Cursor c = null;
                    try {
                        c = dw.query(query);
                        if (c != null && c.moveToFirst()) {
                            String localUri = c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
                            Log.d("MainActivity", downloadFileUri.toString());
                            //7.0系统不能在intent中包含file:///协议
                            File file = new File(localUri.replace("file://", ""));
                            Intent openIntent = new Intent(Intent.ACTION_VIEW,
                                    FileProvider.getUriForFile(MainActivity.this,
                                            context.getApplicationContext().getPackageName() + ".provider", file));
                            openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(openIntent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (c != null) {
                            c.close();
                        }
                    }
                } else {
                    Log.e("MainActivity", "download error");
                }
            }

        }
    };

    long downloadId = 0;
    DownloadStatusObserver observer;

    private boolean isDownloading = false;

    class DownloadStatusObserver extends ContentObserver {
        public DownloadStatusObserver() {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {

            Log.w("MainActivity", "onChange " + downloadId);

            if (downloadId != 0) {
                int[] bytesAndStatus = getBytesAndStatus(downloadId);
                int currentSize = bytesAndStatus[0];//当前大小
                int totalSize = bytesAndStatus[1];//总大小
                int status = bytesAndStatus[2];//下载状态

                progressBar.setMax(totalSize);
                progressBar.setProgress(currentSize);

                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    isDownloading = false;
                    Toast.makeText(MainActivity.this, "id=" + downloadId + "下载完成", Toast.LENGTH_SHORT).show();
                }
                textView.setText("id=" + downloadId + "下载进度：" + currentSize + "/" + totalSize);
            }
        }

    }

    private ProgressBar progressBar;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermiss();

        handler = new Handler();
        dw = DownloadManager.getInstance(this);

        progressBar = (ProgressBar)findViewById(R.id.firstBar);
        textView = (TextView) findViewById(R.id.text);

        findViewById(R.id.download).setOnClickListener(this);
        findViewById(R.id.pause).setOnClickListener(this);
        findViewById(R.id.resume).setOnClickListener(this);

        registerReceiver(downloadComplte, filter);
    }

    private void checkPermiss() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int permissionState = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionState != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10086);
                } else {
                    Toast.makeText(MainActivity.this, "6.+系统, 请在设置中授权存储卡读写权限, 才能使用下载功能", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 10086) {
            if (permissions != null && permissions.length > 0
                    && grantResults != null && grantResults.length > 0) {
                if(permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "ok, let's go", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.download) {

            checkPermiss();

            if (isDownloading) {
                Toast.makeText(MainActivity.this, "心急吃不了豆腐", Toast.LENGTH_SHORT).show();
                return;
            }

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://download.taobaocdn.com/wireless/taobao4android/latest/701483.apk"));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            request.setTitle("下载jpg");
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            request.setMimeType("image/jpeg");

            //实际下载后存放的路径并不一定是这个名字，如果有重名的，自动向名字中追加数字编号
            File apkFile = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS + "/701483.apk");
            request.setDestinationUri(Uri.fromFile(apkFile));

            downloadId = dw.enqueue(request);
            isDownloading = true;

            Uri uri = dw.getDownloadUri(downloadId);

            if (uri != null) {
                progressBar.setMax(1000);
                progressBar.setProgress(0);

                if (observer != null) {
                    getContentResolver().unregisterContentObserver(observer);
                    observer = null;
                }
                observer = new DownloadStatusObserver();
                getContentResolver().registerContentObserver(uri, true, observer);
            }
        } else if (v.getId() == R.id.pause) {
            dw.pauseDownload(downloadId);
        } else if (v.getId() == R.id.resume) {
            dw.resumeDownload(downloadId);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(downloadComplte);
        if (observer != null) {
            getContentResolver().unregisterContentObserver(observer);
            observer = null;
        }

    }

    private int[] getBytesAndStatus(long downloadId) {
        int[] bytesAndStatus = new int[] { -1, -1, 0 };
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = null;
        try {
            c = dw.query(query);
            if (c != null && c.moveToFirst()) {
                bytesAndStatus[0] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                bytesAndStatus[1] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                bytesAndStatus[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return bytesAndStatus;
    }

}
