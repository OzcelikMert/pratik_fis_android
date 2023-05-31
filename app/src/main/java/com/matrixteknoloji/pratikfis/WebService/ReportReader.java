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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class ReportReader {

    protected String InsertPassword = "26108920qwe*";

    public String Send_Read(String AccountName, String CoId, String Reader_Result, String Plug_Date, String Plug_No, String Plug_VAT_1, String Plug_VAT_8, String Plug_VAT_18, String Plug_VAT_Base_1, String Plug_VAT_Base_8, String Plug_VAT_Base_18, String Plug_Total, String Plug_VD) {
        String JsonResult = "error";
        try {

            // Json Parse
            String URL = "http://services.digigarson.net/uyumsoft/pratikfis/WebServices/functions/insert_wrong_read.php";

            // Login Info
            JSONObject postValues = new JSONObject();
            postValues.put("password", InsertPassword);

            // Report Info
            postValues.put("account_name", AccountName);
            postValues.put("co_id", CoId);
            postValues.put("result", Reader_Result);
            postValues.put("plug_date", Plug_Date);
            postValues.put("plug_no", Plug_No);
            postValues.put("plug_vat_1", Plug_VAT_1);
            postValues.put("plug_vat_8", Plug_VAT_8);
            postValues.put("plug_vat_18", Plug_VAT_18);
            postValues.put("plug_vat_base_1", Plug_VAT_Base_1);
            postValues.put("plug_vat_base_8", Plug_VAT_Base_8);
            postValues.put("plug_vat_base_18", Plug_VAT_Base_18);
            postValues.put("plug_total", Plug_Total);
            postValues.put("plug_vd", Plug_VD);

            // URL Connection
            java.net.URL url = new URL(URL);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            // Set POST Values
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            bufferedWriter.write(getPostDataString(postValues));
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            // end Set POST Values

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

            // Json Result
            if(!result.equals("")){
                JSONObject responseJSON = new JSONObject(result);
                JsonResult = responseJSON.getString("result");
            }
            // end Json Result

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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return JsonResult;
    }

    // Convert ArrayObject To String
    private String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator itr = params.keys();

        while(itr.hasNext()){

            String key = (String) itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}
/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/