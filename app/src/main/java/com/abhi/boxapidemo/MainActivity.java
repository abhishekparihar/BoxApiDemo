package com.abhi.boxapidemo;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.box.androidsdk.content.BoxApiFile;
import com.box.androidsdk.content.BoxApiFolder;
import com.box.androidsdk.content.BoxConfig;
import com.box.androidsdk.content.BoxConstants;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.auth.BoxAuthentication;
import com.box.androidsdk.content.models.BoxError;
import com.box.androidsdk.content.models.BoxFile;
import com.box.androidsdk.content.models.BoxItem;
import com.box.androidsdk.content.models.BoxIteratorItems;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.requests.BoxRequestsFile;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements BoxAuthentication.AuthListener{

    BoxSession mSession = null;
    BoxSession mOldSession = null;
    private ProgressDialog mDialog;
    private ArrayAdapter<BoxItem> mAdapter;

    private BoxApiFolder mFolderApi;
    private BoxApiFile mFileApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configureClient();
        initSession();
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
        BoxConfig.CLIENT_ID = "bn8dgwte67427ycuy7ell6nzth2snv73";
        BoxConfig.CLIENT_SECRET = "5udXoQ5eNyzh7f14KQxwFrQ7sK81sxQH";




        // needs to match redirect uri in developer settings if set.
        //   BoxConfig.REDIRECT_URL = "<YOUR_REDIRECT_URI>";
    }

    /**
     * Create a BoxSession and authenticate.
     */
    private void initSession() {
        //mAdapter.clear();
        mSession = new BoxSession(this);
        mSession.setSessionAuthListener(this);
        mSession.authenticate(this);
    }

    @Override
    public void onRefreshed(BoxAuthentication.BoxAuthenticationInfo info) {

    }

    @Override
    public void onAuthCreated(BoxAuthentication.BoxAuthenticationInfo info) {
        mFolderApi = new BoxApiFolder(mSession);
        mFileApi = new BoxApiFile(mSession);
        loadRootFolder();
    }

    @Override
    public void onAuthFailure(BoxAuthentication.BoxAuthenticationInfo info, Exception ex) {
        if (ex != null) {
            clearAdapter();
        } else if (info == null && mOldSession != null) {
            mSession = mOldSession;
            mSession.setSessionAuthListener(this);
            mOldSession = null;
            onAuthCreated(mSession.getAuthInfo());
        }
    }
    private void clearAdapter() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.clear();
            }
        });

    }

    @Override
    public void onLoggedOut(BoxAuthentication.BoxAuthenticationInfo info, Exception ex) {
        clearAdapter();
        initSession();
    }
    private void loadRootFolder() {
        new Thread() {
            @Override
            public void run() {
                try {
                    //Api to fetch root folder
                    final BoxIteratorItems folderItems = mFolderApi.getItemsRequest(BoxConstants.ROOT_FOLDER_ID).send();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (BoxItem boxItem: folderItems) {
                                mAdapter.add(boxItem);
                            }
                        }
                    });
                } catch (BoxException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }
    private void uploadSampleFile() {
        mDialog = ProgressDialog.show(MainActivity.this, getText(R.string.boxsdk_Please_wait), getText(R.string.boxsdk_Please_wait));
        new Thread() {
            @Override
            public void run() {
                try {
                    String uploadFileName = "box_logo.png";
                    InputStream uploadStream = getResources().getAssets().open(uploadFileName);
                    String destinationFolderId = "0";
                    String uploadName = "BoxSDKUpload.png";
                    BoxRequestsFile.UploadFile request = mFileApi.getUploadRequest(uploadStream, uploadName, destinationFolderId);
                    final BoxFile uploadFileInfo = request.send();
                    showToast("Uploaded " + uploadFileInfo.getName());
                    loadRootFolder();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (BoxException e) {
                    e.printStackTrace();
                    BoxError error = e.getAsBoxError();
                    //if (error != null && error.getStatus() == )
                    showToast("Upload failed");
                } finally {
                    mDialog.dismiss();
                }
            }
        }.start();

    }

    private void uploadNewVersion(BoxFile boxFile) {
    }

    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
            }
        });
    }
}
