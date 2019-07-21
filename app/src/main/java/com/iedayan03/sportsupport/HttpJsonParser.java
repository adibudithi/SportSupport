package com.iedayan03.sportsupport;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class HttpJsonParser {

    static JSONObject jObj = null;
    static String json = "";

    public JSONObject makeHttpRequest(String url) {
        try {

            URL urlObj = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)urlObj.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            InputStream inputStream = httpURLConnection.getInputStream();
            httpURLConnection.connect();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine())!= null) {
                sb.append(line + "\n");
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            json = sb.toString();
            jObj = new JSONObject(json);

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        return jObj;
    }
}
