package com.example.instadam.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.instadam.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity {

    private static final int RETURN_TAKEPICTURE = 1;
    private Button btnTakePicture;
    private ImageView imgPrintPicture;
    private String picturePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
        initActivity();

        
    }


    private void initActivity() {
        btnTakePicture = (Button)findViewById(R.id.btnTakePicture);
        imgPrintPicture = (ImageView)findViewById(R.id.imgPrintPicture);
        createOnClickBtnTakePicture();
    }

    private void createOnClickBtnTakePicture(){
        btnTakePicture = (Button)findViewById(R.id.btnTakePicture);
        btnTakePicture.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
    }

    private void takePicture(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null) {
            String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File photoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                File photoFile = File.createTempFile("photo" +time, ".jpg", photoDir);
                picturePath = photoFile.getAbsolutePath();
                Uri photoUri = FileProvider.getUriForFile(CameraActivity.this,
                        CameraActivity.this.getApplicationContext().getPackageName()+".provider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, RETURN_TAKEPICTURE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RETURN_TAKEPICTURE && resultCode == RESULT_OK){
            Bitmap picture = BitmapFactory.decodeFile(picturePath);
            imgPrintPicture.setImageBitmap(picture);
        }

    }
}
