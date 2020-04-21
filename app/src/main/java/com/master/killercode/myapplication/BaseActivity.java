package com.master.killercode.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.master.killercode.runtime_exception.Exception;

public class BaseActivity extends AppCompatActivity {

    public Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = BaseActivity.this;

        new Exception.Make(this)
                .showDialogException(true)
                .saveLogInSD(true)
                .build();
    }
}
