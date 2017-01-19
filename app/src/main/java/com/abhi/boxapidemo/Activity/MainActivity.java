package com.abhi.boxapidemo.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.abhi.boxapidemo.AsynTask.UploadAsync;
import com.abhi.boxapidemo.Constants;
import com.abhi.boxapidemo.FileChooserActivity;
import com.abhi.boxapidemo.R;
import com.abhi.boxapidemo.Util.Util;
import com.box.androidsdk.content.BoxApiFile;
import com.box.androidsdk.content.BoxApiFolder;
import com.box.androidsdk.content.BoxConstants;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.auth.BoxAuthentication;
import com.box.androidsdk.content.listeners.ProgressListener;
import com.box.androidsdk.content.models.BoxDownload;
import com.box.androidsdk.content.models.BoxError;
import com.box.androidsdk.content.models.BoxFile;
import com.box.androidsdk.content.models.BoxFolder;
import com.box.androidsdk.content.models.BoxItem;
import com.box.androidsdk.content.models.BoxIteratorItems;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.requests.BoxRequestsFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;

public class MainActivity extends Activity implements BoxAuthentication.AuthListener{

    private static final int FILE_SELECT_CODE = 0;
    private static final int FILE_CHOOSER = 121;

    BoxSession mSession = null;
    BoxSession mOldSession = null;
    public ProgressDialog mDialog;
    private ArrayAdapter<BoxItem> mAdapter;

    private BoxApiFolder mFolderApi;
    private BoxApiFile mFileApi;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        configureClient();
        mAdapter = new BoxItemAdapter(this);

        initView();
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

    private void initView() {
        listView = (ListView)findViewById(R.id.listView);
        TextView emptyView = (TextView)findViewById(R.id.emptyTextView);
        listView.setEmptyView(emptyView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                BoxItem item = mAdapter.getItem(position);
                if(item != null)
                    downloadFile(item.getId(), item.getName());
            }
        });

    }


    /**
     * Set required config parameters. Use values from your application settings in the box developer console.
     */


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
    public void loadRootFolder() {
        mAdapter.clear();
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

        if(mAdapter != null){
            listView.setAdapter(mAdapter);
        }
    }
    private void uploadFile(final String path) {
        mDialog = ProgressDialog.show(this, getText(R.string.boxsdk_Please_wait), getText(R.string.boxsdk_Please_wait));
        File file = new File(path);
        UploadAsync upload = new UploadAsync(this, mSession,file);
        upload.execute();


    }

    private void uploadNewVersion(BoxFile boxFile) {
    }

    public void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void downloadFile(final String fileId, final String fileName){
        mDialog = ProgressDialog.show(MainActivity.this, getText(R.string.boxsdk_Please_wait), getText(R.string.boxsdk_Please_wait));
        new Thread(){
            @Override
            public void run() {
                OutputStream output = null;
                try {
                    File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "BoxFiles");
                    boolean success = true;
                    if (!folder.exists()) {
                        success = folder.mkdir();
                    }else{
                        success = true;
                    }
                    if(success)
                        output = new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator + "BoxFiles" + File.separator + fileName);

                    if(output != null) {
                        BoxApiFile fileApi = new BoxApiFile(mSession);
                        BoxDownload fileDownload = fileApi.getDownloadRequest(output, fileId)
                                // Optional: Set a listener to track download progress.
                                .setProgressListener(new ProgressListener() {
                                    @Override
                                    public void onProgressChanged(long numBytes, long totalBytes) {
                                        // Update a progress bar, etc.
                                        if(numBytes >= totalBytes){
                                            showToast("File Downloaded");
                                        }
                                    }
                                })
                                .send();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (BoxException e) {
                    e.printStackTrace();
                }finally {
                    mDialog.dismiss();
                }
            }
        }.start();

    }

    private void showFileChooser() {
        Intent intent = new Intent(this, FileChooserActivity.class);
        intent.setType("image/*|application/pdf");
        startActivityForResult(intent, FILE_CHOOSER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == FILE_CHOOSER) && (resultCode == RESULT_OK)) {
            String fileSelected = data.getStringExtra(Constants.KEY_FILE_SELECTED);
            if(fileSelected != null && !fileSelected.equalsIgnoreCase("") ){
                uploadFile(fileSelected);
            }else {
                showToast("File cannot be uploaded");
            }
        }
    }


    private class BoxItemAdapter extends ArrayAdapter<BoxItem> {
        public BoxItemAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BoxItem item = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.boxsdk_list_item, parent, false);
            }

            TextView name = (TextView) convertView.findViewById(R.id.name);
            name.setText(item.getName());

            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            if (item instanceof BoxFolder) {
                icon.setImageResource(R.drawable.boxsdk_icon_folder_yellow);
            } else {
                icon.setImageResource(R.drawable.boxsdk_generic);
            }

            return convertView;
        }

    }
}
