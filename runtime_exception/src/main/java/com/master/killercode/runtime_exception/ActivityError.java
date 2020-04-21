package com.master.killercode.runtime_exception;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Jonh on 18/08/18.
 */
public class ActivityError extends android.app.Activity implements View.OnClickListener {
    private AlertDialog alertDialog;
    private String logs;
    private String txtFile;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // make a dialog without a titlebar
        setFinishOnTouchOutside(false); // prevent users from dismissing the dialog by tapping outside
        setContentView(R.layout.log_activity);
        logs = getIntent().getStringExtra("logs");
        txtFile = getIntent().getStringExtra("path");
        showConfirmation();
    }

    @Override
    public void onClick(View v) {
        // respond to button clicks in your UI

    }

    private void sendLogFile() {
        if (logs == null)
            return;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ajit.jati@oodlestechnologies.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Error reported from MyAPP");
        intent.putExtra(Intent.EXTRA_TEXT, "Log file attached." + logs); // do this so some email clients don't complain about empty body.
        startActivity(intent);
    }

    private void emailErrorLog() {

        String errorLog = logs;
        String[] emailAddressArray = "jonn255d@gmail.com".split("\\s*,\\s*");
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailAddressArray);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getApplicationName(ActivityError.this) + " Application Crash Error Log");
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) + errorLog);
        if (!txtFile.equals("")) {
            Uri filePath = Uri.fromFile(new File(txtFile));
            emailIntent.putExtra(Intent.EXTRA_STREAM, filePath);
        }
        startActivity(Intent.createChooser(emailIntent, "Email Error Log"));
    }

    private void shareErrorLog() {
        String errorLog = logs;
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "Application Crash Error Log");
        share.putExtra(Intent.EXTRA_TEXT, errorLog);
        startActivity(Intent.createChooser(share, "Share Error Log"));
    }

    private void copyErrorToClipboard() {
        String errorInformation = logs;
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("View Error Log", errorInformation);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(ActivityError.this, "Error Log Copied", Toast.LENGTH_SHORT).show();
        }
    }

    private void showConfirmation() {
        // method as shown above

        alertDialog = new AlertDialog.Builder(ActivityError.this).create();
        alertDialog.setTitle("App ta Bunitinho!");
        alertDialog.setMessage("Click em ok para confirmar que está tudo certo, do contrário, click em cancelar.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
//                sendLogFile();
                finish();

            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                emailErrorLog();
                alertDialog.dismiss();
                finish();
            }
        });

        alertDialog.show();

    }

    public String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }
}