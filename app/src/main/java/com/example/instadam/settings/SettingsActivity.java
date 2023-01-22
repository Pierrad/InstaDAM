package com.example.instadam.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.instadam.R;
import com.example.instadam.helpers.HTTPRequest;
import com.example.instadam.profile.ProfileActivity;
import com.example.instadam.user.User;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * ProfilePostsAdapter allows you to display and modify your nickname and
 * display your email address
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("SettingsActivity: ", "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        String oldPseudo = User.getInstance(SettingsActivity.this).getUsername();

        EditText pseudo = findViewById(R.id.pseudo);
        TextView email = findViewById(R.id.email);
        Button save = findViewById(R.id.save);

        pseudo.setText(oldPseudo);
        email.setText(User.getInstance(SettingsActivity.this).getEmail());

        save.setOnClickListener(click -> save(oldPseudo, pseudo.getText().toString()));
    }

    /**
     * Called when the save button is pressed.
     *
     * Redirects to our profile page if is correct.
     *
     * @param oldPseudo  The old pseudo of the user.
     * @param newPseudo  The new pseudo of the user.
     */
    public void save(String oldPseudo, String newPseudo) {
        Log.d("SettingsActivity: ", "save(" + oldPseudo + ", " + newPseudo + ")");

        if (oldPseudo.equals(newPseudo)) {
            Log.d("SettingsActivity: ", "save() -> oldPseudo == newPseudo");

            redirectToProfileActivity();
        } else {
            Log.d("SettingsActivity: ", "save() -> oldPseudo != newPseudo");

            RequestQueue queue = Volley.newRequestQueue(this);
            HTTPRequest request = new HTTPRequest(queue, getString(R.string.API_URL), User.getInstance(SettingsActivity.this).getAccessToken());

            Map<String, String> headers = new HashMap<>();
            Map<String, String> body = new HashMap<>();

            body.put("name", newPseudo);

            request.makeRequest(Request.Method.PATCH, "/v1/users/" + User.getInstance(SettingsActivity.this).getId(), headers, body, response -> {
                    Log.d("SettingsActivity: ", "save() -> rqstPatch updateUser OK");

                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        User.getInstance(SettingsActivity.this).setUsername(jsonResponse.getString("name"));

                        redirectToProfileActivity();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("SettingsActivity: ", "save() -> rqstPatch updateUser NOT OK, error: " + error)
            );
        }
    }

    public void redirectToProfileActivity() {
        Log.d("SettingsActivity: ", "redirectToProfileActivity()");

        Intent intent = new Intent(this, ProfileActivity.class);
        this.startActivity(intent);
    }

}