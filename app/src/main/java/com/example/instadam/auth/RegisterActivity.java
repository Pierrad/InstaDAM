package com.example.instadam.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.instadam.R;
import com.example.instadam.feed.FeedActivity;
import com.example.instadam.helpers.HTTPRequest;

import java.util.HashMap;
import java.util.Map;

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
            if (!password0.getText().toString().equals(password1.getText().toString())) {
                // TODO
            } else {
                register(this.getBaseContext(), email.getText().toString(), pseudo.getText().toString(), password0.getText().toString());
            }
        });

        login.setOnClickListener(click -> {
            Intent intent = new Intent(this, LoginActivity.class);
            this.startActivity(intent);
        });
    }

    private void register(Context context, String email, String pseudo, String password) {
        RequestQueue queue = Volley.newRequestQueue(this);
        HTTPRequest request = new HTTPRequest(queue, getString(R.string.API_URL), getString(R.string.API_BEARER));

        Map<String, String> headers = new HashMap<>();
        Map<String, String> body = new HashMap<>();
        body.put("name", pseudo);
        body.put("email", email);
        body.put("password", password);

        request.makeRequest(Request.Method.POST, "/v1/auth/register", headers, body, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TODO
                        Intent intent = new Intent(context, FeedActivity.class);
                        context.startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO
                        error.printStackTrace();
                    }
                }
        );
    }

}