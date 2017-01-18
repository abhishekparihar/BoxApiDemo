package com.abhi.boxapidemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.box.androidsdk.content.BoxApiFile;
import com.box.androidsdk.content.BoxApiFolder;
import com.box.androidsdk.content.auth.BoxAuthentication;
import com.box.androidsdk.content.models.BoxSession;

public class LoginActivity extends AppCompatActivity implements BoxAuthentication.AuthListener{

    BoxSession mSession = null;
    BoxSession mOldSession = null;
    private ProgressDialog mDialog;
    private BoxApiFolder mFolderApi;
    private BoxApiFile mFileApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSession = new BoxSession(this);
        mSession.setSessionAuthListener(this);
    }

    public void login(View v) {
        mSession.authenticate(this);
    }


    @Override
    public void onRefreshed(BoxAuthentication.BoxAuthenticationInfo info) {

    }

    @Override
    public void onAuthCreated(BoxAuthentication.BoxAuthenticationInfo info) {
        mFolderApi = new BoxApiFolder(mSession);
        mFileApi = new BoxApiFile(mSession);
        Log.d("", "onAuthCreated: user = " + info.getUser().getName());
        runOnUiThread(new Runnable() {
            public void run() {
                // runs on UI thread
                Toast.makeText(LoginActivity.this, "Welcome User" , Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onAuthFailure(BoxAuthentication.BoxAuthenticationInfo info, Exception ex) {
        if (ex != null) {
//            clearAdapter();
        } else if (info == null && mOldSession != null) {
            mSession = mOldSession;
            mSession.setSessionAuthListener(this);
            mOldSession = null;
            onAuthCreated(mSession.getAuthInfo());
        }
    }

    @Override
    public void onLoggedOut(BoxAuthentication.BoxAuthenticationInfo info, Exception ex) {

    }
}
