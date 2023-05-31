/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/
package com.matrixteknoloji.pratikfis.WebService;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

public class GetCompanyList {

    private String SOAP_ACTION = "http://tempuri.org/GetCompanyList";
    private String METHOD_NAME = "GetCompanyList";
    private String NAMESPACE = "http://tempuri.org/";

    public ArrayList<String> CoId = new ArrayList<String>();
    public ArrayList<String> CoDesc = new ArrayList<String>();

    public void Get(String UserName, String Password)
    {
        try {
            String URL = "";
            if(Values.ServiceSectionSelect.equals("smmmm")) URL = Values.DomainPrefix + Values.DomainName + Values.DomainExtension_smmmm + Values.DomainDirectory;
            else if(Values.ServiceSectionSelect.equals("other")) URL = Values.DomainPrefix + Values.DomainName + Values.DomainDirectory;

            CoId.clear();
            CoDesc.clear();

            Log.e("ServiceURL", "URL: " + URL);
            Object Result = "";
            // Get and Set Values
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            SoapObject Request2 = new SoapObject(NAMESPACE, "param");
            Request2.addProperty("UserName", UserName);
            Request2.addProperty("Password", Password);

            Request.addSoapObject(Request2);
            // Select Version
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.dotNet = true;
            // Set Request
            envelope.setOutputSoapObject(Request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            androidHttpTransport.debug = true;
            androidHttpTransport.call(SOAP_ACTION, envelope);
            Result = (Object) envelope.getResponse();

            // Get Services Values
            if (!Result.toString().equals("")){
                JSONArray GetCListArray =new JSONArray(Result.toString());
                for(int i=0; i < GetCListArray.length(); i++) {
                    JSONObject GetCListObject = (JSONObject) GetCListArray.get(i);
                    CoId.add(GetCListObject.getString("CoId"));
                    CoDesc.add(GetCListObject.getString("CoDesc"));
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

}
/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/