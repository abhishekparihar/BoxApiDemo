package com.abhi.boxapidemo.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.abhi.boxapidemo.R;
import com.box.androidsdk.content.BoxConfig;

public class SplashActivity extends Activity {

    private static final long SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        configureClient();
        new Handler().postDelayed(new Runnable() {
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                goToLogin();
            }
        }, SPLASH_TIME_OUT);
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void configureClient() {
        BoxConfig.CLIENT_ID = "bn8dgwte67427ycuy7ell6nzth2snv73";
        BoxConfig.CLIENT_SECRET = "5udXoQ5eNyzh7f14KQxwFrQ7sK81sxQH";

        // needs to match redirect uri in developer settings if set.
        //   BoxConfig.REDIRECT_URL = "<YOUR_REDIRECT_URI>";
    }
}
