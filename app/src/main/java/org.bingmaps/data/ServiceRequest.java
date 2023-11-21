package org.bingmaps.app.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ServiceRequest {
    public String ContentType;
    public RequestType RequestType;
    public URL requestUrl;
    public String Data;


    public ServiceRequest() {
    }

    public ServiceRequest(String url, RequestType requestType, String contentType) {
        try {
            this.requestUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.RequestType = requestType;
        this.ContentType = contentType;
    }

    public ServiceRequest(String url, RequestType requestType, String contentType, String data) {
        try {
            this.requestUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.RequestType = requestType;
        this.ContentType = contentType;
        this.Data = data;
    }


    public String execute() {
        String result = "";
        HttpURLConnection urlConnection = null;

        if (this.RequestType == org.bingmaps.app.data.RequestType.POST) {

            try {
                urlConnection = (HttpURLConnection) requestUrl.openConnection();
                urlConnection.setRequestMethod(this.RequestType.name());
                urlConnection.setRequestProperty("Accept", this.ContentType);
                urlConnection.setRequestProperty("Content-type", this.ContentType);
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setRequestProperty("charset", "utf-8");

                if (this.Data != null) {
                    byte[] postData = this.Data.getBytes(StandardCharsets.UTF_8);
                    urlConnection.setRequestProperty("Content-Length", Integer.toString(postData.length));
                    OutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
                    outputStream.write(postData);
                }

                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                result = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != urlConnection)
                    urlConnection.disconnect();
            }
        } else if (this.RequestType == org.bingmaps.app.data.RequestType.GET) {
            try {
                urlConnection = (HttpURLConnection) requestUrl.openConnection();
                urlConnection.setRequestMethod(this.RequestType.name());
                urlConnection.setRequestProperty("Accept", this.ContentType);
                urlConnection.setRequestProperty("Content-type", this.ContentType);
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                result = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != urlConnection)
                    urlConnection.disconnect();
            }
        }
        return result;
    }
}
