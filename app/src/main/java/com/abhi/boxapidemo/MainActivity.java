package com.abhi.boxapidemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.box.androidsdk.content.BoxApiFile;
import com.box.androidsdk.content.BoxApiFolder;
import com.box.androidsdk.content.BoxConfig;
import com.box.androidsdk.content.auth.BoxAuthentication;
import com.box.androidsdk.content.models.BoxItem;
import com.box.androidsdk.content.models.BoxSession;

public class MainActivity extends AppCompatActivity implements BoxAuthentication.AuthListener{

    BoxSession mSession = null;
    BoxSession mOldSession = null;

    private ArrayAdapter<BoxItem> mAdapter;

    private BoxApiFolder mFolderApi;
    private BoxApiFile mFileApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    /**
     * Set required config parameters. Use values from your application settings in the box developer console.
     */
    private void configureClient() {
        BoxConfig.CLIENT_ID = "qu8vkgnlj4s9p0waapm03qeg49q7yasi";
        BoxConfig.CLIENT_SECRET = "yRagLa9avaJb7h2xzvtI09ipOm1HpXiG";




        // needs to match redirect uri in developer settings if set.
        //   BoxConfig.REDIRECT_URL = "<YOUR_REDIRECT_URI>";
    }

    /**
     * Create a BoxSession and authenticate.
     */
    private void initSession() {
        mAdapter.clear();
        mSession = new BoxSession(this);
        mSession.setSessionAuthListener(this);
        mSession.authenticate(this);
    }

    @Override
    public void onRefreshed(BoxAuthentication.BoxAuthenticationInfo info) {

    }

    @Override
    public void onAuthCreated(BoxAuthentication.BoxAuthenticationInfo info) {

    }

    @Override
    public void onAuthFailure(BoxAuthentication.BoxAuthenticationInfo info, Exception ex) {

    }

    @Override
    public void onLoggedOut(BoxAuthentication.BoxAuthenticationInfo info, Exception ex) {

    }
}
