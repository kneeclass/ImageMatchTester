package com.kov.photomatchtester;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by daak on 2017-02-18.
 */

public class PhotoUploader extends AsyncTask<File, Integer, String> {

    private MainActivity _mainActivity;

    public PhotoUploader(MainActivity mainActivity){

        _mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(File... params) {

        MultipartBody.Builder requestBody = new MultipartBody.Builder();
        requestBody.setType(MultipartBody.FORM);
        requestBody.addFormDataPart("uploadname1", "testfile", RequestBody.create(MediaType.parse("image/jpeg"),params[0]));

        Request request = new Request.Builder()
                .url("http://requestb.in/tyjimsty")
                .post(requestBody.build())
                .build();
        Response response;
        OkHttpClient client = new OkHttpClient();
        try {
            response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        _mainActivity.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        _mainActivity.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
        _mainActivity.PhotoUploadResult(s);

    }
}
