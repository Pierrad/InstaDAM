package com.example.instadam.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.instadam.R;
import com.example.instadam.feed.FeedActivity;
import com.example.instadam.helpers.HTTPRequest;
import com.example.instadam.user.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private Utilities utilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        utilities = new Utilities(this.getBaseContext());

        EditText email = findViewById(R.id.emailAddress);
        EditText pseudo = findViewById(R.id.pseudo);
        EditText password0 = findViewById(R.id.password0);
        EditText password1 = findViewById(R.id.password1);
        Button showHidePassword0 = findViewById(R.id.showHidePassword0);
        Button showHidePassword1 = findViewById(R.id.showHidePassword1);
        Button register = findViewById(R.id.signup);
        Button login = findViewById(R.id.signin);

        showHidePassword0.setOnClickListener(click ->
            utilities.changeVisibility(password0, showHidePassword0)
        );

        showHidePassword1.setOnClickListener(click ->
            utilities.changeVisibility(password1, showHidePassword1)
        );

        register.setOnClickListener(click -> {
            register(this.getBaseContext(), email.getText().toString(), pseudo.getText().toString(), password0.getText().toString(), password1.getText().toString());
        });

        login.setOnClickListener(click -> {
            Intent intent = new Intent(this, LoginActivity.class);
            this.startActivity(intent);
        });
    }

    private void register(Context context, String email, String pseudo, String password0, String password1) {
        TextView textError = findViewById(R.id.error);
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$";
        final String EMAIL_PATTERN = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

        if (email.isEmpty()) {
            textError.setText("L'adresse mail ne peut être vide.");
            return;
        }

        if (!Pattern.compile(EMAIL_PATTERN).matcher(email).matches()) {
            textError.setText("L'adresse mail est incorrecte.");
            return;
        }

        if (pseudo.isEmpty()) {
            textError.setText("Le pseudo ne peut être vide.");
            return;
        }

        if (password0.length() < 8) {
            textError.setText("Le mot de passe doit contenir 8 charactères minimum.");
            return;
        }

        if (password0.length() > 20) {
            textError.setText("Le mot de passe doit contenir 20 charactères maximum.");
            return;
        }

        if (!Pattern.compile(PASSWORD_PATTERN).matcher(password0).matches()) {
            textError.setText("Le mot de passe doit contenir 1 miniscule, 1 majuscule et 1 chiffre.");
            return;
        }

        if (!password0.equals(password1)) {
            textError.setText("Les 2 mots de passe doivent correspondres.");
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        HTTPRequest request = new HTTPRequest(queue, getString(R.string.API_URL), getString(R.string.API_BEARER));

        Map<String, String> headers = new HashMap<>();
        Map<String, String> body = new HashMap<>();

        body.put("name", pseudo);
        body.put("email", email);
        body.put("password", password0);

        request.makeRequest(Request.Method.POST, "/v1/auth/register", headers, body, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONObject user = jsonResponse.getJSONObject("user");
                            JSONObject tokens = jsonResponse.getJSONObject("tokens");
                            JSONObject accessToken = tokens.getJSONObject("access");
                            JSONObject refreshToken = tokens.getJSONObject("refresh");

                            Log.d("response", response);

                            if (tokens.has("access")) {
                                User.getInstance(RegisterActivity.this).setId(user.getString("id"));
                                User.getInstance(RegisterActivity.this).setEmail(user.getString("email"));
                                User.getInstance(RegisterActivity.this).setUsername(user.getString("name"));
                                User.getInstance(RegisterActivity.this).setAccessToken(accessToken.getString("token"));
                                User.getInstance(RegisterActivity.this).setRefreshToken(refreshToken.getString("token"));

                                redirectToFeedActivity();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject data = new JSONObject(responseBody);

                            if (data.getString("message").equals("Email already taken")) {
                                textError.setText("L'adresse mail est déjà prise.");
                            } else {
                                textError.setText(data.getString("message"));
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    public void redirectToFeedActivity() {
        Intent intent = new Intent(RegisterActivity.this, FeedActivity.class);
        RegisterActivity.this.startActivity(intent);
    }
}