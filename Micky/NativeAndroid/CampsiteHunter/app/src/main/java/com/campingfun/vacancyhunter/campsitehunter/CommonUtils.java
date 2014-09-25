package com.campingfun.vacancyhunter.campsitehunter;

import android.app.ProgressDialog;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wayliu on 9/1/2014.
 */
public class CommonUtils {
    public static String GetGoogleApiKey() {
        return "AIzaSyA7luvKRElU6chjTKqS7rtIGnXzx8uucCo";
    }

    public static String ActiveApiKey() {
        return "5sryncnakzru6a2euwn3mntm";
    }

    public static InputStream HttpGetResponse(String urlStr) throws IOException {
//        StringBuilder result = new StringBuilder();
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        return conn.getInputStream();
//        InputStreamReader in = new InputStreamReader(conn.getInputStream());
//        return in;
        // Load the results into a StringBuilder
//        int read;
//        char[] buff = new char[1024];
//        while ((read = in.read(buff)) != -1) {
//            result.append(buff, 0, read);
//        }
//
//        return result.toString();
    }
}
