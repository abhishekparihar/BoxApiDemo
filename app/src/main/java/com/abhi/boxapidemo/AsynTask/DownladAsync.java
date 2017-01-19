package com.abhi.boxapidemo.AsynTask;

import android.os.AsyncTask;

/**
 * Created by abhishek_p on 19-01-2017.
 */

public class DownladAsync extends AsyncTask<String,Long,String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(String... strings) {

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
