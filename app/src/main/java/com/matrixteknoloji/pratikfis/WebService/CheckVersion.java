/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/
package com.matrixteknoloji.pratikfis.WebService;

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
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CheckVersion {

    public void GetVersions() {
        try {
            String URL = "http://services.digigarson.net/uyumsoft/pratikfis/WebServices/CheckVersion.php";

            // URL Connection
            java.net.URL url = new URL(URL);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            // Get Values
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));

            String result="";
            String line="";

            while((line = bufferedReader.readLine())!= null) {
                result += line;
            }

            bufferedReader.close();
            inputStream.close();
            // end Get Values

            httpURLConnection.disconnect();
            // end URL Connection

            Log.e("CheckVersion", "Version Result: " + result);

            String app_Version = "1.0";
            String data_Version = "1.0";

            // Json Result
            if(!result.equals("")){
                JSONObject responseJSON = new JSONObject(result);
                app_Version = responseJSON.getString("app_version");
                data_Version = responseJSON.getString("data_version");
            }
            // end Json Result

            Values.App_Version = app_Version;
            Values.Data_Version = data_Version;

            Log.e("CheckVersion", "App: " + Values.App_Version + " | Data: " + Values.Data_Version);
        } catch (MalformedURLException e) {
            Log.e("CheckVersion", "Error-1 " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("CheckVersion", "Error-2 " + e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e("CheckVersion", "Error-3 " + e.getMessage());
            e.printStackTrace();
        }
    }
}
/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/