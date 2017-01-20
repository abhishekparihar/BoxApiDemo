package com.abhi.boxapidemo.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.abhi.boxapidemo.R;
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
        final BoxAuthentication.BoxAuthenticationInfo tempInfo = info;
        Log.d("", "onAuthCreated: user = " + info.getUser().getName());
        onSuccessfulLogin();
        runOnUiThread(new Runnable() {
            public void run() {
                // runs on UI thread
                Toast.makeText(LoginActivity.this, "Welcome, " + tempInfo.getUser().getName()+"!" , Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void onSuccessfulLogin() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
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

        mSession = new BoxSession(this);
        mSession.setSessionAuthListener(this);
    }
}
