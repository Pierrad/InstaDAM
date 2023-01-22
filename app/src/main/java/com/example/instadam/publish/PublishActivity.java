package com.example.instadam.publish;

import static java.lang.Thread.sleep;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.instadam.R;
import com.example.instadam.feed.FeedActivity;
import com.example.instadam.helpers.HTTPRequest;
import com.example.instadam.user.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PublishActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_READ_MEDIA = 1000;
    private static final int PERMISSIONS_REQUEST_WRITE_MEDIA = 1001;
    private ImageView capturedImage;
    private Uri capturedImageUri;
    private String imageName;
    private ProgressBar publishProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        Button takeImage = findViewById(R.id.button_image);
        Button buttonSave = this.findViewById(R.id.button_save);
        Button buttonLoad = this.findViewById(R.id.button_load);
        publishProgressBar = findViewById(R.id.progressPublish);
        capturedImage = findViewById(R.id.captured_image);

        ActivityResultLauncher<Intent> cameraResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> onCameraResult(result.getResultCode()));

        ActivityResultLauncher<Intent> galleryResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> onGalleryResult(result.getResultCode(), result.getData()));

        takeImage.setOnClickListener(v -> {
            if (checkWritePermission()) {
                File photoFile = null;
                try {
                    photoFile = createTemporaryImageFile();
                } catch (IOException ex) {
                    Toast.makeText(this, getString(R.string.error_creating_file), Toast.LENGTH_SHORT).show();
                }
                if (photoFile != null) {
                    capturedImageUri = FileProvider.getUriForFile(this, getString(R.string.authority_provider), photoFile);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
                    cameraResultLauncher.launch(intent);
                } else {
                    Toast.makeText(this, getString(R.string.error_creating_file), Toast.LENGTH_SHORT).show();
                }
            } else {
                requestWritePermission();
            }
        });

        buttonLoad.setOnClickListener(v -> {
            if (checkReadPermission()) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryResultLauncher.launch(intent);
            } else {
                requestReadPermission();
            }
        });

        buttonSave.setOnClickListener(v -> {
            try {
                saveAndPublish();
            } catch (JSONException | FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_MEDIA && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            recreate();
        }
    }

    private void onCameraResult(int resultCode) {
        if (resultCode == RESULT_OK) {
            capturedImage.setImageURI(capturedImageUri);
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, getString(R.string.action_cancel), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.action_failed), Toast.LENGTH_LONG).show();
        }
    }

    private void onGalleryResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            capturedImage.setImageURI(data.getData());
            capturedImageUri = data.getData();
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, getString(R.string.action_cancel), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.action_failed), Toast.LENGTH_LONG).show();
        }
    }

    private void saveAndPublish() throws JSONException, FileNotFoundException {
        String name = ((EditText) findViewById(R.id.editTextName)).getText().toString();
        String description = ((EditText) findViewById(R.id.editTextDesc)).getText().toString();
        Bitmap bitmap = null;

        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(capturedImageUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (name.isEmpty() || description.isEmpty() || bitmap == null) {
            Toast.makeText(this, R.string.warning_fill_all_fields, Toast.LENGTH_LONG).show();
            return;
        }

        publishProgressBar.setVisibility(View.VISIBLE);
        publishProgressBar.setIndeterminate(true);
        saveImageIntoStorage(bitmap);
        publishPost(name, description);
    }

    public void saveImageIntoStorage(Bitmap bitmap) {
        if (checkWritePermission()) {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/InstaDAM");
            path.mkdirs();
            File file = new File(path, imageName + "image.jpg");
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            requestWritePermission();
        }
    }

    public void publishPost(String name, String description) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this);
        HTTPRequest request = new HTTPRequest(queue, getString(R.string.API_URL), User.getInstance(PublishActivity.this).getAccessToken());

        Map<String, String> headers = new HashMap<>();
        Map<String, String> body = new HashMap<>();

        body.put("name", name);
        body.put("description", description);
        JSONObject geolocation = new JSONObject();
        geolocation.put("latitude", User.getInstance(PublishActivity.this).getCurrentPosition().getLatitude());
        geolocation.put("longitude", User.getInstance(PublishActivity.this).getCurrentPosition().getLongitude());
        body.put("geolocation", geolocation.toString());

        InputStream imageStream;
        try {
            imageStream = getContentResolver().openInputStream(capturedImageUri);

            request.makeMultipartRequest("/v1/images", headers, body, imageStream, response -> {
                Toast.makeText(PublishActivity.this, "Image publiée", Toast.LENGTH_LONG).show();
                publishProgressBar.setVisibility(View.GONE);

                try {
                    sleep(1500);
                    redirectToFeedActivity();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, error -> Toast.makeText(PublishActivity.this, R.string.error_publishing_post, Toast.LENGTH_LONG).show());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private File createTemporaryImageFile() throws IOException {
        String imageFileName = getPrefixImageName();
        imageName = imageFileName;
        File storageDir = getExternalCacheDir();

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    private String getPrefixImageName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRENCH).format(new Date());
        return "JPEG_" + timeStamp + "_";
    }

    public boolean checkWritePermission() {
        return ContextCompat.checkSelfPermission(PublishActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkReadPermission() {
        return ContextCompat.checkSelfPermission(PublishActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestWritePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_MEDIA);
    }

    public void requestReadPermission() {
        ActivityCompat.requestPermissions(PublishActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_MEDIA);
    }

    public void redirectToFeedActivity() {
        Intent intent = new Intent(PublishActivity.this, FeedActivity.class);
        PublishActivity.this.startActivity(intent);
    }
}
