package com.master.killercode.runtime_exception;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Exception {

    private String strCurrentErrorLog;
    private File txtFile;
    private static final String EXTRA_STACK_TRACE = "EXTRA_STACK_TRACE";
    private static final String EXTRA_ACTIVITY_LOG = "EXTRA_ACTIVITY_LOG";

    //============================================================================================================================
    //
    // Class Builder Create
    //
    //============================================================================================================================

    public static class Make {

        private Activity activity;
        private boolean showAlertDialog = false;
        private boolean saveInSD = false;

        public Make(Activity activity) {
            this.activity = activity;
        }

//        public Make activity(Activity activity) {
//            this.activity = activity;
//            return this;
//        }

        public Make showDialogException(boolean b) {
            this.showAlertDialog = b;
            return this;
        }

        public Make saveLogInSD(boolean b) {
            this.saveInSD = b;
            return this;
        }

        public void build() {
            new Exception(this);
        }
    }

    //============================================================================================================================
    //
    // init Exception
    //
    //============================================================================================================================

    Exception(Make builder) {
        init(builder);
    }

    private Thread.UncaughtExceptionHandler defaltHandler;

    private void init(final Make builder) {

//        Thread.setDefaultUncaughtExceptionHandler(new ExeptionHelper(activity));
        defaltHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {

                if (builder.saveInSD) {
                    writeToFileComplet(builder.activity, getAllErrorDetailsFromIntent(builder.activity, e));
                }

                if (builder.showAlertDialog) {

                    initActivity(builder, e.toString());
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(10);

                } else {
                    defaltHandler.uncaughtException(t, e);
                }


            }
        });
    }

    //============================================================================================================================
    //
    // String Create And Save
    //
    //============================================================================================================================

    private String getAllErrorDetailsFromIntent(Activity activity, Throwable e) {
        if (TextUtils.isEmpty(strCurrentErrorLog)) {
            String LINE_SEPARATOR = "\n";
            StringBuilder errorReport = new StringBuilder();

            errorReport.append("************ DEVICE INFORMATION ***********" + LINE_SEPARATOR);
            errorReport.append("Brand: " + Build.BRAND);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Device: " + Build.DEVICE);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Model: " + Build.MODEL);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Id: " + Build.ID);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Product: " + Build.PRODUCT);
            errorReport.append(LINE_SEPARATOR);

            errorReport.append(LINE_SEPARATOR + "\n************ BUILD INFO ************\n");
            errorReport.append("SDK: " + Build.VERSION.SDK);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Release: " + Build.VERSION.RELEASE);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Incremental: " + Build.VERSION.INCREMENTAL);
            errorReport.append(LINE_SEPARATOR);

            errorReport.append(LINE_SEPARATOR + "\n***** APP INFO \n");
            String versionName = getVersionName(activity);
            errorReport.append("Version: ");
            errorReport.append(versionName);
            errorReport.append(LINE_SEPARATOR);
            Date currentDate = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String firstInstallTime = getFirstInstallTimeAsString(activity, dateFormat);
            if (!TextUtils.isEmpty(firstInstallTime)) {
                errorReport.append("Installed On: ");
                errorReport.append(firstInstallTime);
                errorReport.append(LINE_SEPARATOR);
            }
            String lastUpdateTime = getLastUpdateTimeAsString(activity, dateFormat);
            if (!TextUtils.isEmpty(lastUpdateTime)) {
                errorReport.append("Updated On: ");
                errorReport.append(lastUpdateTime);
                errorReport.append(LINE_SEPARATOR);
            }
            errorReport.append("Current Date: ");
            errorReport.append(dateFormat.format(currentDate));
            errorReport.append(LINE_SEPARATOR);

            String activityStack = getStackTraceFromIntent(activity.getIntent());
            errorReport.append(LINE_SEPARATOR);
            if (activityStack != null) {
                errorReport.append("\n***** ERROR LOG \n");
                errorReport.append(activityStack);
                errorReport.append(LINE_SEPARATOR);
            }

            String activityLog = getActivityLogFromIntent(activity.getIntent());
            errorReport.append(LINE_SEPARATOR);
            if (activityLog != null) {
                errorReport.append("\n***** USER ACTIVITIES \n");
                errorReport.append("User Activities: ");
                errorReport.append(activityLog);
                errorReport.append(LINE_SEPARATOR);
            }

            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            errorReport.append("\n************ CAUSE OF ERROR ************\n").append(LINE_SEPARATOR);
            errorReport.append(stackTrace.toString());

            strCurrentErrorLog = errorReport.toString();
            return strCurrentErrorLog;
        } else {
            return strCurrentErrorLog;
        }
    }

    //============================================================================================================================

    private String getVersionName(Activity activity) {
        try {
            PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (java.lang.Exception e) {
            return "Unknown";
        }
    }

    private String getFirstInstallTimeAsString(Activity activity, DateFormat dateFormat) {
        long firstInstallTime;
        try {
            firstInstallTime = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).firstInstallTime;
            return dateFormat.format(new Date(firstInstallTime));
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    private String getLastUpdateTimeAsString(Activity activity, DateFormat dateFormat) {
        long lastUpdateTime;
        try {
            lastUpdateTime = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).lastUpdateTime;
            return dateFormat.format(new Date(lastUpdateTime));
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    private String getActivityLogFromIntent(Intent intent) {
        return intent.getStringExtra(Exception.EXTRA_ACTIVITY_LOG);
    }

    private String getStackTraceFromIntent(Intent intent) {
        return intent.getStringExtra(Exception.EXTRA_STACK_TRACE);
    }

    private String replaceString(String text) {
        return text.replace(" ", "_").replace("-", "_").toLowerCase();
    }

    //============================================================================================================================

    private void writeToFile(Activity activity, String text) {

        try {

            String nome_projeto_local_das_pastas = String.valueOf(Environment.getExternalStorageDirectory() + "/Crash_Reports_" + activity.getResources().getString(R.string.app_name));

            //Gets the Android external storage directory & Create new folder Crash_Reports
            File dir = new File(nome_projeto_local_das_pastas);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            Date date = new Date();
            String filename = dateFormat.format(date) + ".txt";

            // Write the file into the folder
            File reportFile = new File(dir, filename);
            FileWriter fileWriter = new FileWriter(reportFile);
            fileWriter.append(text);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToFileComplet(Activity activity, String text) {
        Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        String localPath = "";
        String pasta = "/Crash_Reports_" + activity.getResources().getString(R.string.app_name);

//        if (isSDPresent && isExternalStorageWritable()) {
//            localPath = String.valueOf(Environment.getExternalStorageState() + pasta);
//        } else {
        localPath = replaceString(String.valueOf(Environment.getExternalStorageDirectory() + pasta));
//        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date currentDate = new Date();
        String filename = replaceString(dateFormat.format(currentDate).replace(" ", "_").replace("-", "_") + ".txt");

        FileOutputStream outputStream;
        try {
            File file = new File(localPath);
            file.mkdir();
            txtFile = new File(localPath + "/" + filename);
            txtFile.createNewFile();

            outputStream = new FileOutputStream(txtFile);
            outputStream.write(text.getBytes());
            outputStream.close();

            Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri fileContentUri = Uri.fromFile(txtFile);
            mediaScannerIntent.setData(fileContentUri);
            activity.sendBroadcast(mediaScannerIntent);
        } catch (IOException e) {
            Log.e("REQUIRED", "This app does not have write storage permission to save log file.");
//            Toast.makeText(activity, "Storage Permission Not Found", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    //============================================================================================================================
    //
    // ActivityError Start
    //
    //============================================================================================================================

    private void initActivity(Make builder, String errorLogs) {
//            Logger.LogError("custom error",errorLogs.toString());
        //Open Send log activity
        Intent intent = new Intent(builder.activity, ActivityError.class);
//        intent.setAction(".ActivityError"); // see step 5.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
        intent.putExtra("logs", errorLogs);
        intent.putExtra("path", txtFile.toString());
        builder.activity.startActivity(intent);
    }
}
