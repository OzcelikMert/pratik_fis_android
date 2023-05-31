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
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.matrixteknoloji.pratikfis.SQLiteData.Data;
import com.matrixteknoloji.pratikfis.WebService.GetCompanyList;
import com.matrixteknoloji.pratikfis.WebService.Values;

import java.util.ArrayList;

public class settings extends AppCompatActivity {

    // Database
    Data data;

    ListView listView_Popup;
    Button change_company_btn, exit_btn, back_btn, cancelButton_Popup;
    TextView user_nameTextview, company_nameTextview, domain_nameTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        // Close Action Bar
        getSupportActionBar().hide();
        // end Close Action Bar

        data = new Data(this);
        // Text Views
        user_nameTextview = (TextView) findViewById(R.id.UserNameTextView);
        company_nameTextview = (TextView) findViewById(R.id.CompanyNameTextView);
        domain_nameTextview = (TextView) findViewById(R.id.domainNameTextView);

        // Buttons
        change_company_btn = (Button) findViewById(R.id.ChangeCompany_btn);
        exit_btn = (Button) findViewById(R.id.Exit_btn);
        back_btn = (Button) findViewById(R.id.Back_btn);

        domain_nameTextview.setText((Values.DomainName));
        user_nameTextview.setText(Values.UserName);
        company_nameTextview.setText(Values.CoDesc);

        change_company_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkConnected())
                    new asynTask().execute(Values.UserName, Values.Password);
                else
                    Toast.makeText(settings.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
        });

        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set Dialog
                AlertDialog.Builder dialog =
                        new AlertDialog.Builder(new ContextThemeWrapper(settings.this, R.style.Theme_AppCompat_DayNight_Dialog));

                dialog.setMessage(getString(R.string.exit_message))
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // OKAY Button
                                Values.UserName = "";
                                Values.Password = "";
                                Values.CoId = "";
                                Values.CoDesc = "";
                                data.DeleteData(Values.Id);
                                Values.Id = "";
                                dialog.dismiss();
                                GoLoginLayout();
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

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void GoLoginLayout(){
        Intent intent = new Intent(settings.this, login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private class asynTask extends AsyncTask<String,Void,Void> {

        ProgressDialog progressDialog = new ProgressDialog(new ContextThemeWrapper(settings.this, R.style.Theme_AppCompat_DayNight_Dialog));

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
                        GetSelectBox(getCompanyList.CoDesc, getCompanyList.CoId);
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
                            new AlertDialog.Builder(new ContextThemeWrapper(settings.this, R.style.Theme_AppCompat_DayNight_Dialog));

                    dialog.setMessage(getString(R.string.selected_message) + ":\n\""+ clickedItemName.toString() + "\"\n" + getString(R.string.confirm_question))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // OKAY Button
                                    Values.CoId = CoIdValues.get(Index);
                                    Values.CoDesc = clickedItemName.toString();
                                    if(data.UpdateData(Values.Id, Values.CoDesc, Values.CoId)){
                                        // Login Success
                                        company_nameTextview.setText(Values.CoDesc);
                                        main_layout.CompanyName.setText(Values.CoDesc);
                                        popupWindow.dismiss();
                                    }else{
                                        // Unknown Error
                                        Toast.makeText(settings.this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
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
            popupWindow.showAtLocation(Myview, Gravity.CENTER, 0, 0);
        } catch (Exception e){

        }
    }

    // Check Internet Connection
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/