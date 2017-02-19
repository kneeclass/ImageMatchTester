package com.kov.photomatchtester;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public Uri CapturedImageURL;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int permission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            PERMISSIONS_STORAGE,
                            REQUEST_EXTERNAL_STORAGE
                    );
                }


                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //outfile where we are thinking of saving it
                String storage = System.getenv("EXTERNAL_STORAGE");
                File file = new File(storage, "test.jpg");
                CapturedImageURL = Uri.fromFile(file);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, CapturedImageURL);

                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    setResult(RESULT_OK,takePictureIntent);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                }

            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Uri uri = null;

            if (data != null)  {
                uri = data.getData();
            }
            if (uri == null && CapturedImageURL != null)  {
                uri = Uri.fromFile(new File(CapturedImageURL.getPath()));
            }
            File file = new File(CapturedImageURL.getPath());

            if(!file.exists()){
                Toast.makeText(this, "File does not exist", Toast.LENGTH_SHORT).show();
                return;
            }

            PhotoUploader photoUploader = new PhotoUploader();
            try {
                String str = photoUploader.execute(file).get();
                Toast.makeText(this, str, Toast.LENGTH_LONG).show();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


        }
    }



}
