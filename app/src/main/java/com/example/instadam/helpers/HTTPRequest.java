package com.example.instadam.helpers;

import android.util.Log;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

/**
 * The HTTPRequest utility class is a wrapper for the Volley library.
 */
public class HTTPRequest {

    private RequestQueue queue;
    private String apiUrl;
    private String apiBearer;

    public HTTPRequest(RequestQueue queue, String apiUrl) {
        this.queue = queue;
        this.apiUrl = apiUrl;
    }

    public HTTPRequest(RequestQueue queue, String apiUrl, String apiBearer) {
        this.queue = queue;
        this.apiUrl = apiUrl;
        this.apiBearer = apiBearer;
    }

    /**
     * Sends a REST request to the API.
     * @param method The HTTP method to use.
     * @param endpoint The endpoint to send the request to.
     * @param headers The headers to send with the request.
     * @param body The body to send with the request.
     * @param listener The listener to call when the request is successful.
     * @param errorListener The listener to call when the request fails.
     */
    public void makeRequest(int method, String endpoint, final Map<String, String> headers, final Map<String, String> body, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {
        String url = apiUrl + endpoint;

        StringRequest stringRequest = new StringRequest(method, url, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (headers != null) {
                    headers.put("Content-Type", "application/json");
                    if (apiBearer != null) {
                        headers.put("Authorization", "Bearer " + apiBearer);
                    }
                    return headers;
                }
                return super.getHeaders();
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                if (body != null) {
                    return new JSONObject(body).toString().getBytes();
                }
                return super.getBody();
            }
        };

        queue.add(stringRequest);
    }

    public void makeMultipartRequest(String endpoint, final Map<String, String> headers, final Map<String, String> formData, final InputStream image, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {
        String url = apiUrl + endpoint;

        MultipartRequest multipartRequest = new MultipartRequest(url, errorListener, listener, "image", image, formData, headers) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (apiBearer != null) {
                    headers.put("Authorization", "Bearer " + apiBearer);
                }
                return headers;
            }
        };

        queue.add(multipartRequest);
    }

}
