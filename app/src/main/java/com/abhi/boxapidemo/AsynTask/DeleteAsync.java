package com.abhi.boxapidemo.AsynTask;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.abhi.boxapidemo.Activity.MainActivity;
import com.box.androidsdk.content.BoxApiFile;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.models.BoxSession;

/**
 * Created by abhishek_p on 19-01-2017.
 */

public class DeleteAsync extends AsyncTask<String,String,Boolean>{
    MainActivity mActivity;
    BoxSession mSession;

    public DeleteAsync(MainActivity activity, BoxSession session){
        mActivity = activity;
        mSession = session;
    }
    @Override
    protected Boolean doInBackground(String... strings) {
        BoxApiFile fileApi = new BoxApiFile(mSession);
        try {
            fileApi.getDeleteRequest(strings[0])
                    .send();
        } catch (BoxException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if(aBoolean){
            Toast.makeText(mActivity, "File Deleted Successfully", Toast.LENGTH_SHORT).show();
            mActivity.loadRootFolder();
        }
    }
}
