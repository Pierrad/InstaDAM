package com.example.instadam.helpers;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class MultipartRequest extends Request<String> {

    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "---011000010111000001101001";

    private final Response.Listener<String> mListener;
    private final Map<String, String> mHeaders;

    private final Map<String, String> mFormData;
    private final InputStream mInputStream;
    private final String mImageName;

    public MultipartRequest(String url, Response.ErrorListener errorListener, Response.Listener<String> listener,
                            String inputStreamName, InputStream inputStream, Map<String, String> formData, Map<String, String> headers) {
        super(Method.POST, url, errorListener);

        mListener = listener;
        mHeaders = headers;
        mFormData = formData;
        mInputStream = inputStream;
        mImageName = inputStreamName;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        mHeaders.put("Content-Type", "multipart/form-data; boundary=" + boundary);
        return mHeaders;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            // populate text payload
            Map<String, String> params = getParams();
            if (params != null && params.size() > 0) {
                textParse(dos, params);
            }

            // populate data byte payload
            if (mInputStream != null) {
                dataParse(dos, mInputStream, mImageName);
            }

            // close multipart form data after text and file data
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Custom method handle data payload.
     *
     * @return Map data part label with data byte
     * @throws AuthFailureError
     */
    protected Map<String, String> getParams() throws AuthFailureError {
        return mFormData;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(new String(response.data, HttpHeaderParser.parseCharset(response.headers)),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    private void textParse(DataOutputStream dataOutputStream, Map<String, String> params) throws IOException {
        for (Map.Entry<String, String> entry : params.entrySet()) {
            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + lineEnd);
            dataOutputStream.writeBytes(lineEnd);
            dataOutputStream.writeBytes(entry.getValue() + lineEnd);
        }
    }

    private void dataParse(DataOutputStream dataOutputStream, InputStream image, String imageName) throws IOException {
        byte[] buffer = new byte[4096];
        int bytesRead;

        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + imageName + "\"; filename=\"" + imageName + "\"" + lineEnd);
        dataOutputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);

        while ((bytesRead = image.read(buffer)) != -1) {
            dataOutputStream.write(buffer, 0, bytesRead);
        }

        dataOutputStream.writeBytes(lineEnd);

        image.close();
    }

}