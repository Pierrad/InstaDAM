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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class LoginActivity extends AppCompatActivity {

    private Utilities utilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        utilities = new Utilities(this.getBaseContext());

        EditText email = findViewById(R.id.emailAddress);
        EditText password = findViewById(R.id.password);
        Button showHidePassword = findViewById(R.id.showHidePassword);
        Button login = findViewById(R.id.signin);
        Button register = findViewById(R.id.signup);

        showHidePassword.setOnClickListener(click -> {
            utilities.changeVisibility(password, showHidePassword);
        });

        login.setOnClickListener(click -> {
            login(this.getBaseContext(), email.getText().toString(), password.getText().toString());
        });

        register.setOnClickListener(click -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            this.startActivity(intent);
        });
    }

    public void login(Context context, String email, String password) {
        TextView textError = findViewById(R.id.error);

        if (email.isEmpty()) {
            textError.setText("L'adresse mail ne peut être vide.");
            return;
        }

        if (password.isEmpty()) {
            textError.setText("Le mot de passe ne peut être vide.");
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        HTTPRequest request = new HTTPRequest(queue, getString(R.string.API_URL), getString(R.string.API_BEARER));

        Map<String, String> headers = new HashMap<>();
        Map<String, String> body = new HashMap<>();

        body.put("email", email);
        body.put("password", password);

        request.makeRequest(Request.Method.POST, "/v1/auth/login", headers, body, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TODO
                        Intent intent = new Intent(context, FeedActivity.class);
                        context.startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject data = new JSONObject(responseBody);

                            textError.setText(data.getString("message"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

}