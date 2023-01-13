package com.example.instadam.camera;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.instadam.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity {
    private static final int REQUEST_ID_IMAGE_CAPTURE = 100;
    private static final int PERMISSIONS_REQUEST_READ_MEDIA = 1000;
    private ImageView imageView;
    private Button buttonSave;
    private Bitmap picture;
    private String pictureName = "_test.jpg";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //get widgets
        Button takeImage = findViewById(R.id.button_image);
        buttonSave = this.findViewById(R.id.button_save);
        Button buttonLoad = this.findViewById(R.id.button_load);
        imageView = findViewById(R.id.imageView);

        buttonSave.setAlpha(0.5f);

        //set listeners
        takeImage.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an implicit intent, for image capture.
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // Start camera and wait for the results.
                startActivityForResult(intent, REQUEST_ID_IMAGE_CAPTURE);
            }
        });


        buttonSave.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (picture!=null) {
                    //manage authorizations
                    int permissionCheck = ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_MEDIA);
                    } else {
                        saveToInternalStorage(picture);
                    }
                }
            }
        });

        buttonLoad.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //manage authorizations
                int permissionCheck = ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_MEDIA);
                } else {
                    loadImageFromStorage( getApplicationContext().getFilesDir().getPath());
                }
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_MEDIA:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    saveToInternalStorage(picture);
                }
                break;

            default:
                break;
        }
    }



    // When results returned
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ID_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                picture = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(picture);
                buttonSave.setAlpha(1f);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Action canceled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Action Failed", Toast.LENGTH_LONG).show();
            }
        }
    }



    private void saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/yourapp/app_data/imageDir
        //File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File directory = cw.getFilesDir();
        Log.d("DEBUG_FR","directory="+directory);
        // Create imageDir

        File file = new File(directory, pictureName);
        Log.d("DEBUG_FR","file="+file);


        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            Log.d("DEBUG_FR","fos="+fos);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
            Toast.makeText(this, "New picture saved", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            Log.d("DEBUG_FR", "ERROR: picture not saved");
            e.printStackTrace();
        }
    }


    private void loadImageFromStorage(String path)
    {

        try {
            File f=new File(path, pictureName);
            picture = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img = findViewById(R.id.imageView);
            img.setImageBitmap(picture);
            Toast.makeText(this, "Picture loaded", Toast.LENGTH_LONG).show();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            Log.d("DEBUG_FR", "ERROR: picture not loaded");
        }

    }

}
