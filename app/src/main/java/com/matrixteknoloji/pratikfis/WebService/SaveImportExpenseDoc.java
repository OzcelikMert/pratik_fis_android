/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/
package com.matrixteknoloji.pratikfis.WebService;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveImportExpenseDoc {

    // Crashes
    public byte NoAccess = 0x0057;
    public byte InvalidValue = 0x07d;
    public byte Successful = 0x00f;
    public byte Available = 0x01f;
    // end Crashes

    private String SOAP_ACTION = "http://tempuri.org/SaveImportExpenseDoc";
    private String METHOD_NAME = "SaveImportExpenseDoc";
    private String NAMESPACE = "http://tempuri.org/";
    // KDV = V.A.T
    public byte Set(String userName, String password, String PlugDate, String PlugNo, Double Total, String VD, String PaymentType, Double KDV_1, Double KDV_8, Double KDV_18, Integer CoId, Double Matrah_1, Double Matrah_8, Double Matrah_18)
    {
        try {
            String URL = "";
            if(Values.ServiceSectionSelect.equals("smmmm")) URL = Values.DomainPrefix + Values.DomainName + Values.DomainExtension_smmmm + Values.DomainDirectory;
            else if(Values.ServiceSectionSelect.equals("other")) URL = Values.DomainPrefix + Values.DomainName + Values.DomainDirectory;

            // Check Inputs
            if(!PlugDate.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})"))
                return InvalidValue;
            else if(!VD.matches("(?:[0-9]{11}|[0-9]{10})"))
                return InvalidValue;
            // end Check Inputs

            // Check Date
            final String OLD_FORMAT = "dd/MM/yyyy";
            final String NEW_FORMAT = "yyyy-MM-dd";

            SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
            Date date = sdf.parse(PlugDate);
            sdf.applyPattern(NEW_FORMAT);
            PlugDate = sdf.format(date);
            // end Check Date

            // Get and Set Values
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            SoapObject RequestParam = new SoapObject(NAMESPACE, "param");

            // User Info
            SoapObject RequestToken = new SoapObject(NAMESPACE, "Token");

            // User Info Properties
            // User Name
            PropertyInfo UserName = new PropertyInfo();
            UserName.setName("UserName");
            UserName.setType(String.class);
            UserName.setValue(userName);
            RequestToken.addProperty(UserName);

            // Password
            PropertyInfo Password = new PropertyInfo();
            Password.setName("Password");
            Password.setType(String.class);
            Password.setValue(password);
            RequestToken.addProperty(Password);
            // end User Info Properties

            // end User Info

            // Plug Info
            SoapObject RequestValue = new SoapObject(NAMESPACE, "Value");

            // Plug Info Properties
            // Date
            PropertyInfo tarih = new PropertyInfo();
            tarih.setName("tarih");
            tarih.setType(String.class);
            tarih.setValue(PlugDate);
            RequestValue.addProperty(tarih);

            // Plug No
            PropertyInfo fisno = new PropertyInfo();
            fisno.setName("fisno");
            fisno.setType(String.class);
            fisno.setValue(PlugNo);
            RequestValue.addProperty(fisno);

            // Total
            PropertyInfo toplam = new PropertyInfo();
            toplam.setName("toplam");
            toplam.setType(Double.class);
            toplam.setValue(Total.toString());
            RequestValue.addProperty(toplam);

            // VD
            PropertyInfo tckn_vkn = new PropertyInfo();
            tckn_vkn.setName("tckn_vkn");
            tckn_vkn.setType(String.class);
            tckn_vkn.setValue(VD);
            RequestValue.addProperty(tckn_vkn);

            // Payment Type
            PropertyInfo odeme_tipi = new PropertyInfo();
            odeme_tipi.setName("odeme_tipi");
            odeme_tipi.setType(String.class);
            odeme_tipi.setValue(PaymentType);
            RequestValue.addProperty(odeme_tipi);

            // KDV 1
            PropertyInfo kdv1 = new PropertyInfo();
            kdv1.setName("kdv1");
            kdv1.setType(Double.class);
            kdv1.setValue(KDV_1.toString());
            RequestValue.addProperty(kdv1);

            // KDV 8
            PropertyInfo kdv8 = new PropertyInfo();
            kdv8.setName("kdv8");
            kdv8.setType(Double.class);
            kdv8.setValue(KDV_8.toString());
            RequestValue.addProperty(kdv8);

            // KDV 18
            PropertyInfo kdv18 = new PropertyInfo();
            kdv18.setName("kdv18");
            kdv18.setType(Double.class);
            kdv18.setValue(KDV_18.toString());
            RequestValue.addProperty(kdv18);

            // Company Id
            PropertyInfo coId = new PropertyInfo();
            coId.setName("coId");
            coId.setType(Integer.class);
            coId.setValue(CoId);
            RequestValue.addProperty(coId);

            // KDV Base 1 (Matrah 1)
            PropertyInfo matrah1 = new PropertyInfo();
            matrah1.setName("matrah1");
            matrah1.setType(Double.class);
            matrah1.setValue(Matrah_1.toString());
            RequestValue.addProperty(matrah1);

            // KDV Base 8 (Matrah 8)
            PropertyInfo matrah8 = new PropertyInfo();
            matrah8.setName("matrah8");
            matrah8.setType(Double.class);
            matrah8.setValue(Matrah_8.toString());
            RequestValue.addProperty(matrah8);

            // KDV Base 18 (Matrah 18)
            PropertyInfo matrah18 = new PropertyInfo();
            matrah18.setName("matrah18");
            matrah18.setType(Double.class);
            matrah18.setValue(Matrah_18.toString());
            RequestValue.addProperty(matrah18);
            // end Plug Info Properties

            //end Plug Info


            RequestParam.addSoapObject(RequestToken);
            RequestParam.addSoapObject(RequestValue);

            Request.addSoapObject(RequestParam);

            // Select Version
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.dotNet = true;
            // Set Request
            envelope.setOutputSoapObject(Request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            androidHttpTransport.debug = true;
            androidHttpTransport.call(SOAP_ACTION, envelope);

            // Get Service Result
            Object Result = (Object) envelope.getResponse();

            Log.e("SameResult", "Result: " + Result + " | Filter: " + Result.toString().toLowerCase());
            // Check Services Result
            if (!Result.toString().equals("")){
                if(Result.toString().toLowerCase().equals("\"succeded\"")){
                    Log.e("SameResult", "Successful True");
                    return Successful;
                }else if(Result.toString().replaceAll(" ", "").toLowerCase().substring(0, 17).equals("\"hata:ilgilifişno")){
                    Log.e("SameResult", "Available True");
                    return Available;
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        Log.e("SameResult", "NoAccess True");
        return NoAccess;
    }

}
/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/