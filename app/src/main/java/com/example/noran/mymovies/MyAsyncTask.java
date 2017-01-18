package com.example.noran.mymovies;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MyAsyncTask extends AsyncTask<String, Void, String > {

    private final Context mContext;
    private AsyncTaskCallBack mCallBack;
    private ProgressBar mProgressbar;

    public MyAsyncTask(AsyncTaskCallBack callBack, ProgressBar  dialog, Context context){
        mCallBack = callBack;
        mProgressbar = dialog;
        mContext = context;
    }
    private String LOG_TAG = MyAsyncTask.class.getSimpleName();


    @Override
    protected void onPreExecute() {
        if(mProgressbar!=null)
            mProgressbar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... params) {


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String forecastJsonStr = null;


        try {

            URL url = new URL(params[0]);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {

                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = reader.readLine()) != null) {


                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            forecastJsonStr = buffer.toString();
            Log.v(LOG_TAG, "JSON String: " + forecastJsonStr);

            return forecastJsonStr;

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);

            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    @Override
    protected void onPostExecute(String result) {

        if (mProgressbar!=null)
            mProgressbar.setVisibility(View.GONE);

        if(result==null)
            Toast.makeText(mContext,"Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
        else {

            mCallBack.onTaskFinish(result);
        }



    }
}

