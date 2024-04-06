package com.example.elevent;

import android.app.Application;
import android.content.Intent;

public class ExceptionHandler extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                Intent intent = new Intent("YOUR_PACKAGE_NAME.ACTION_SHOW_ERROR_FRAGMENT");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                System.exit(1); // Optional - Kill the app after showing the error fragment
            }
        });
    }
}


