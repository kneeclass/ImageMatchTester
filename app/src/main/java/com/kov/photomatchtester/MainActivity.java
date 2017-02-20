package com.kov.photomatchtester;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
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
import android.widget.ProgressBar;
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check permission
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

        Button uploadButton = (Button) findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((Button)findViewById(R.id.uploadButton)).setEnabled(false);
                ((Button)findViewById(R.id.button)).setEnabled(false);

                PhotoUploader photoUploader = new PhotoUploader(MainActivity.this);
                File file = new File(CapturedImageURL.getPath());

                photoUploader.execute(file);
            }
        });
            
    }

    public void PhotoUploadResult(String str){
        ((Button)findViewById(R.id.uploadButton)).setEnabled(true);
        ((Button)findViewById(R.id.button)).setEnabled(true);
        Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
    }

    private Bitmap GetImageForImageView(File file) throws IOException {
        Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

        ExifInterface exif =  new ExifInterface(file.getAbsolutePath());
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        Log.d("EXIF", "Exif: " + orientation);
        Matrix matrix = new Matrix();
        if (orientation == 6) {
            matrix.postRotate(90);
        }
        else if (orientation == 3) {
            matrix.postRotate(180);
        }
        else if (orientation == 8) {
            matrix.postRotate(270);
        }
        return Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                File file = new File(CapturedImageURL.getPath());
                if(!file.exists()){
                    Toast.makeText(this, "File does not exist", Toast.LENGTH_SHORT).show();
                    return;
                }
                ((ImageView)findViewById(R.id.imageView)).setImageBitmap(GetImageForImageView(file));
                ((Button)findViewById(R.id.uploadButton)).setVisibility(View.VISIBLE);
                ((Button)findViewById(R.id.button)).setText("Ta en ny bild");

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }



}
