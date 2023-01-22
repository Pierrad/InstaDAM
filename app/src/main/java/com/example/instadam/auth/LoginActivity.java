package com.example.instadam.auth;

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
import com.example.instadam.feed.FeedActivity;
import com.example.instadam.helpers.HTTPRequest;
import com.example.instadam.user.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * The LoginActivity allows to connect to our InstaDAM application.
 */
public class LoginActivity extends AppCompatActivity {

    private Utilities utilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("LoginActivity: ", "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        utilities = new Utilities(this.getBaseContext());

        EditText email = findViewById(R.id.emailAddress);
        EditText password = findViewById(R.id.password);
        Button showHidePassword = findViewById(R.id.showHidePassword);
        Button login = findViewById(R.id.signin);
        Button register = findViewById(R.id.signup);

        showHidePassword.setOnClickListener(click -> utilities.changeVisibility(password, showHidePassword));

        login.setOnClickListener(click -> login(email.getText().toString(), password.getText().toString()));

        register.setOnClickListener(click -> redirectToRegisterActivity());
    }

    /**
     * Called when the login button is pressed.
     *
     * Redirects to our home page if all is correct.
     * Otherwise displays an error message
     *
     * @param email  The email of the user.
     * @param password  The password of the user.
     */
    public void login(String email, String password) {
        Log.d("LoginActivity: ", "login(" + email + ", " + password + ")");

        TextView textError = findViewById(R.id.error);

        if (email.isEmpty()) {
            textError.setText(R.string.email_empty_error);
            return;
        }

        if (password.isEmpty()) {
            textError.setText(R.string.password_empty_error);
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        HTTPRequest request = new HTTPRequest(queue, getString(R.string.API_URL));

        Map<String, String> headers = new HashMap<>();
        Map<String, String> body = new HashMap<>();

        body.put("email", email);
        body.put("password", password);

        request.makeRequest(Request.Method.POST, "/v1/auth/login", headers, body, response -> {
                Log.d("LoginActivity: ", "login() -> rqstPost login OK");

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONObject user = jsonResponse.getJSONObject("user");
                    JSONObject tokens = jsonResponse.getJSONObject("tokens");
                    JSONObject accessToken = tokens.getJSONObject("access");
                    JSONObject refreshToken = tokens.getJSONObject("refresh");

                    if (tokens.has("access")) {
                        User.getInstance(LoginActivity.this).setId(user.getString("id"));
                        User.getInstance(LoginActivity.this).setEmail(user.getString("email"));
                        User.getInstance(LoginActivity.this).setUsername(user.getString("name"));
                        User.getInstance(LoginActivity.this).setAccessToken(accessToken.getString("token"));
                        User.getInstance(LoginActivity.this).setRefreshToken(refreshToken.getString("token"));

                        redirectToFeedActivity();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, error -> {
                Log.d("LoginActivity: ", "login() -> rqstPost login  NOT OK");

                try {
                    String responseBody = new String(error.networkResponse.data);
                    JSONObject data = new JSONObject(responseBody);

                    textError.setText(data.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        );
    }

    public void redirectToRegisterActivity() {
        Log.d("ProfileActivity: ", "redirectToRegisterActivity()");

        Intent intent = new Intent(this, RegisterActivity.class);
        this.startActivity(intent);
    }

    public void redirectToFeedActivity() {
        Log.d("ProfileActivity: ", "redirectToFeedActivity()");

        Intent intent = new Intent(LoginActivity.this, FeedActivity.class);
        LoginActivity.this.startActivity(intent);
    }

}