package com.abhi.boxapidemo.AsynTask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.abhi.boxapidemo.Activity.MainActivity;
import com.abhi.boxapidemo.R;
import com.box.androidsdk.content.BoxApiFile;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.listeners.ProgressListener;
import com.box.androidsdk.content.models.BoxFile;
import com.box.androidsdk.content.models.BoxSession;

import java.io.File;

/**
 * Created by abhishek_p on 19-01-2017.
 */

public class UploadAsync extends AsyncTask<String,String,Boolean> {
    Boolean success = false;
    MainActivity mActivity;
    BoxSession mSession;
    File mFile;
    ProgressDialog mDialog;


    public  UploadAsync(MainActivity activity, BoxSession session,File file){
        mActivity = activity;
        mSession = session;
        mFile = file;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        BoxApiFile fileApi = new BoxApiFile(mSession);
        try {

            BoxFile uploadedFile = fileApi.getUploadRequest(mFile, "0")
                    // Optional: By default the name of the file on the local file system will be used as the name on Box.
                    // However, you can set a different name for the file by configuring the request.
                    .setFileName(mFile.getName())
                    // Optional: Set a listener to track upload progress.
                    .setProgressListener(new ProgressListener() {
                        @Override
                        public void onProgressChanged(long numBytes, long totalBytes) {
                            Log.d("TAG", "onProgressChanged: numBytes== " + numBytes + "----------totalBytes== " + totalBytes);
                            if(numBytes >= totalBytes){
                                //loadRootFolder();
                                success = true;
                            }
                        }
                    })
                    .send();
        } catch (BoxException e) {
            e.printStackTrace();
            success = false;
        }

        return success;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        mActivity.mDialog.dismiss();
        if(aBoolean){
            mActivity.showToast("File Uploaded");
            mActivity.loadRootFolder();
        }else{
            mActivity.showToast("File Upload Fail");
        }
    }
}
