/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/
package com.matrixteknoloji.pratikfis;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.matrixteknoloji.pratikfis.SQLiteData.Data;
import com.matrixteknoloji.pratikfis.WebService.*;

import java.util.ArrayList;


public class login extends AppCompatActivity {

    // Database
    Data data;

    Button loginButton, cancelButton_Popup;
    EditText UserName, Password, ServiceDomain;
    TextView LoginMessage, VersionText;
    ListView listView_Popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Close Action Bar
        getSupportActionBar().hide();
        // end Close Action Bar

        UserName = (EditText) findViewById(R.id.UserNameTextbox);
        Password = (EditText) findViewById(R.id.UserPasswordTextbox);
        ServiceDomain = (EditText) findViewById(R.id.ServiceDomainName);

        loginButton = (Button) findViewById(R.id.LoginButton);

        LoginMessage = (TextView) findViewById(R.id.LoginMessageTextView);
        VersionText = (TextView) findViewById(R.id.VersionText);
        try {
            // Get Version
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            Values.App_Now_Version = packageInfo.versionName;
            // end Get Version
        }catch(PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        VersionText.setText(("V" + Values.App_Now_Version));

        // Check Network
        if(!isNetworkConnected()){
            ReloadMessage();
            return;
        }

        new CheckVersionAsyncTask().execute();

        // Buttons Event
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Values.DomainName = ServiceDomain.getText().toString().replaceAll( " ", "").toLowerCase();
                Values.UserName = UserName.getText().toString();
                Values.Password = Password.getText().toString();

                String CheckMessage = TextboxValuesControl(Values.UserName, Values.Password, Values.DomainName);
                if(CheckMessage.equals("")){
                    if(isNetworkConnected()){

                        new CheckUserInfos().execute(Values.UserName, Values.Password);

                    }
                    else
                        Toast.makeText(login.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }else{
                    LoginMessage.setText(CheckMessage);
                }
            }
        });

        // Close Keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // end Close Keyboard
    }

    // Checked Radio Buttons Event
    public void CheckedSectionsEvent(View view){
        boolean isChecked =((RadioButton) view).isChecked();
        // Check Tag Name is Null
        if(view.getTag() == null){
            return;
        }
        // Checked Control and Get Tag Name
        if(isChecked){
            String Section_TagName = view.getTag().toString();
            if(Section_TagName.equals("smmmm"))
                ServiceDomain.setHint(R.string.service_url);
            else if(Section_TagName.equals("other"))
                ServiceDomain.setHint(R.string.service_url_other);
            Values.ServiceSectionSelect = Section_TagName;
        }
    }
    // end Checked Radio Buttons Event

    private String TextboxValuesControl(String Value1, String Value2, String Value3){
        String ReturnValue = "";
        // Value 1 Control (User Name)
        if(Value1.replaceAll(" ", "").replaceAll("'", "").equals("")){
            ReturnValue += getString(R.string.empty_user_name) + "\n";
        }

        // Value 2 Control (Password)
        if(Value2.replaceAll(" ", "").replaceAll("'", "").equals("")){
            ReturnValue += getString(R.string.empty_user_password) + "\n";
        }

        // Value 3 Control (Service Domain Name)
        if(Value3.replaceAll(" ", "").replaceAll("'", "").equals("")){
            ReturnValue += getString(R.string.empty_service_domain) + "\n";
        }

        return ReturnValue;
    }

    // ASYNC Classes
    private class CheckUserInfos extends AsyncTask<String,Void,Void> {

        ProgressDialog progressDialog = new ProgressDialog(new ContextThemeWrapper(login.this, R.style.Theme_AppCompat_DayNight_Dialog));

        @Override
        protected void onPreExecute() {
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.waiting));
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... values) {
            try {
                final GetCompanyList getCompanyList = new GetCompanyList();

                getCompanyList.Get(
                        values[0],
                        values[1]
                );

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(getCompanyList.CoId.size() < 1){
                            LoginMessage.setText(getString(R.string.wrong_account_info));
                        }else{
                            LoginMessage.setText("");
                            if (Values.CoId.length() < 1){
                                GetSelectBox(getCompanyList.CoDesc, getCompanyList.CoId);
                            }else{
                                GoMainLayout();
                            }
                        }
                    }
                });

            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private class CheckVersionAsyncTask extends AsyncTask<String,Void,Void> {

        @Override
        protected void onPreExecute() {
            // Start
        }

        @Override
        protected Void doInBackground(String... values) {
            try {

                CheckVersion cv = new CheckVersion();
                cv.GetVersions();

            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Finish
            UpdateMessage();
        }
    }

    // end ASYNC Classes
    // Select box
    private void GetSelectBox(final ArrayList<String> CoDescValues, final ArrayList<String> CoIdValues){
        try {
            // Create view
            final ViewGroup Myview = (ViewGroup) getLayoutInflater().inflate(R.layout.login, null);

            // inflate the layout of the popup window
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            final View popupView = inflater.inflate(R.layout.companylist_selectbox, null);

            // Get the popup window info
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            boolean focusable = true; // lets taps outside the popup also dismiss it
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

            // Create Button and ListView Events
            listView_Popup = (ListView) popupView.findViewById(R.id.companyList);
            cancelButton_Popup = (Button) popupView.findViewById(R.id.Cancel_btn);

            // Set Elements Value
            listView_Popup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                    final Object clickedItemName = adapterView.getAdapter().getItem(index);
                    final Integer Index = index;
                    // Set Dialog
                    AlertDialog.Builder dialog =
                            new AlertDialog.Builder(new ContextThemeWrapper(login.this, R.style.Theme_AppCompat_DayNight_Dialog));

                    dialog.setMessage(getString(R.string.selected_message) + ":\n\""+ clickedItemName.toString() + "\"\n" + getString(R.string.confirm_question))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // OKAY Button
                                    Values.CoId = CoIdValues.get(Index);
                                    Values.CoDesc = clickedItemName.toString();

                                    data.DeleteAllData();
                                    if(data.InsertData(Values.UserName, Values.Password, Values.CoId, Values.CoDesc, Values.DomainName)){
                                        // Login Success
                                        Cursor res = data.GetData();
                                        if(res.moveToNext()){
                                            Values.Id = res.getString(0);
                                        }
                                        popupWindow.dismiss();
                                        GoMainLayout();
                                    }else{
                                        // Unknown Error
                                        Toast.makeText(login.this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                                    }
                                    dialog.dismiss();
                                }
                            }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // CANCEL Button
                                    dialog.dismiss();
                                }
                            });
                    dialog.create().show();
                }
            });

            cancelButton_Popup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindow.dismiss();
                }
            });

            // Set List
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    popupView.getContext(),
                    R.layout.companylist_selectbox_row,
                    R.id.companyName,
                    CoDescValues
            );
            listView_Popup.setAdapter(arrayAdapter);

            // show the popup window
            closeKeyboard();
            popupWindow.showAtLocation(Myview, Gravity.CENTER, 0, 0);
        } catch (Exception e){

        }
    }

    private void GoMainLayout(){
        Intent intent = new Intent(login.this, main_layout.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void closeKeyboard(){
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            assert methodManager != null;
            methodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // Check Internet Connection
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        assert cm != null;
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void ReloadMessage(){
        AlertDialog.Builder dialog =
                new AlertDialog.Builder(new ContextThemeWrapper(login.this, R.style.Theme_AppCompat_DayNight_Dialog));

        dialog.setMessage(getString(R.string.no_internet) + "\n" + getString(R.string.no_internet_check_message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.try_again), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Try Again Button
                        dialog.dismiss();
                        // Restart
                        Intent i = getBaseContext().getPackageManager().
                                getLaunchIntentForPackage(getBaseContext().getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // CANCEL Button
                finish();
                dialog.dismiss();
            }
        });
        dialog.create().show();
    }

    public void UpdateMessage(){

        // Check Version
        if(!Values.App_Now_Version.equals(Values.App_Version)){
            // Set Message
            AlertDialog.Builder dialog =
                    new AlertDialog.Builder(new ContextThemeWrapper(login.this, R.style.Theme_AppCompat_DayNight_Dialog));

            dialog.setMessage(getString(R.string.update_message) + "(V" + Values.App_Version + ")")
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.update), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Try Again Button
                            dialog.dismiss();
                            // Update
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            //http://play.google.com/store/apps/details?id=<package_name>
                            intent.setData(Uri.parse("market://details?id=com.matrixteknoloji.pratikfis"));
                            startActivity(intent);
                        }
                    }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    // Close Popup
                    finish();
                }
            });
            dialog.create().show();
            // end Set Message
        }else{
            // Auto Login
            data = new Data(this);
            // Account Control
            Cursor res = data.GetData();
            if(res.moveToNext()){
                if(!res.getString(0).equals("") && !res.getString(1).equals("") && !res.getString(2).equals("") && !res.getString(3).equals("") && !res.getString(4).equals("") && !res.getString(5).equals("")){
                    Values.Id = res.getString(0);
                    Values.UserName = res.getString(1);
                    Values.Password = res.getString(2);
                    Values.CoId = res.getString(3);
                    Values.CoDesc = res.getString(4);
                    Values.DomainName = res.getString(5);
                    ServiceDomain.setText(Values.DomainName);
                    UserName.setText(Values.UserName);
                    Password.setText(Values.Password);
                    new CheckUserInfos().execute(Values.UserName, Values.Password);
                }
            }
            // end Auto Login
        }
        // end Check Version
    }
}
/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/