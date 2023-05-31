/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/
package com.matrixteknoloji.pratikfis;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.matrixteknoloji.pratikfis.TextControlClass.*;
import com.matrixteknoloji.pratikfis.WebService.ReportReader;
import com.matrixteknoloji.pratikfis.WebService.SaveImportExpenseDoc;
import com.matrixteknoloji.pratikfis.WebService.Values;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class main_layout extends AppCompatActivity {

    // Get Set
    // Bitmap
    private Bitmap privateBitmap;
    public Bitmap GetBitmap(){
        return  privateBitmap;
    }
    public void SetBitmap(Bitmap bitmap){
        privateBitmap = bitmap;
    }
    // end Get Set

    String results = null;
    Button snap_image, take_image, send_image, rotateButton_Popup, okButton_Popup, cancelButton_Popup, settings, Question_Button, wrongReadButton_Popup, helpButton_Popup, backButton_Popup;
    ImageView plugImage_Popup;
    EditText DateTextbox, PlugNoTextbox, TotalTextbox, VDTextbox, KDV_1_TextBox, KDV_8_TextBox, KDV_18_TextBox, KDV_1_TextBox_Base, KDV_8_TextBox_Base, KDV_18_TextBox_Base/*, TotalKDVTextBox*/;
    CheckBox KDV_Percent1, KDV_Percent8, KDV_Percent18;
    RadioButton CashRButon, CreditRButton;
    public static TextView CompanyName;

    String PaymentType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        // Close Action Bar
        getSupportActionBar().hide();
        // end Close Action Bar

        if(com.matrixteknoloji.pratikfis.WebService.Values.Id.length() < 1){
            Intent intent = new Intent(main_layout.this, login.class);
            startActivity(intent);
            finish();
        }

        CreateInput();

        // Set Values
        CompanyName.setText(com.matrixteknoloji.pratikfis.WebService.Values.CoDesc);
        // end Set Values

        InputEvents();

        // Close Keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // end Close Keyboard
    }

    // Inputs
    private void CreateInput(){

        snap_image = (Button) findViewById(R.id.openGalleryButton);
        take_image = (Button) findViewById(R.id.openCameraButton);
        send_image = (Button) findViewById(R.id.sendButton);
        settings = (Button) findViewById(R.id.SettingsButton);
        Question_Button = (Button)  findViewById(R.id.Question_Button);

        DateTextbox = (EditText) findViewById(R.id.dateTextbox);
        PlugNoTextbox = (EditText) findViewById(R.id.plugTextbox);
        TotalTextbox = (EditText) findViewById(R.id.totalTextbox);
        VDTextbox = (EditText) findViewById(R.id.VDTextbox);
        //TotalKDVTextBox = (EditText) findViewById(R.id.totalKDVTextbox);
        KDV_1_TextBox = (EditText) findViewById(R.id.KDV_1_TextBox);
        KDV_8_TextBox = (EditText) findViewById(R.id.KDV_8_TextBox);
        KDV_18_TextBox = (EditText) findViewById(R.id.KDV_18_TextBox);

        KDV_Percent1 = (CheckBox) findViewById(R.id.KDV_checkBox_1);
        KDV_Percent8 = (CheckBox) findViewById(R.id.KDV_checkBox_8);
        KDV_Percent18 = (CheckBox) findViewById(R.id.KDV_checkBox_18);

        KDV_1_TextBox_Base = (EditText) findViewById(R.id.KDV_1_TextBox_Base);
        KDV_8_TextBox_Base = (EditText) findViewById(R.id.KDV_8_TextBox_Base);
        KDV_18_TextBox_Base = (EditText) findViewById(R.id.KDV_18_TextBox_Base);

        CashRButon = (RadioButton) findViewById(R.id.cashRButton);
        CreditRButton = (RadioButton) findViewById(R.id.creditRButton);

        CompanyName = (TextView) findViewById(R.id.CompanyName);
    }

    private void InputEvents(){

        // Buttons Event
        snap_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), PICK_IMAGE_REQUEST);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(main_layout.this, settings.class);
                startActivity(intent);
            }
        });

        take_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        send_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Send_PlugInfo();
            }
        });
        // end Buttons Event

        // Checkboxes Event
        KDV_Percent1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked) {
                    KDV_Change_CheckBox(isChecked, KDV_1_TextBox, String.format(Locale.CANADA, "%.2f", com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_1_Value));
                    KDV_Change_CheckBox(isChecked, KDV_1_TextBox_Base, String.format(Locale.CANADA, "%.2f", com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_1_Value_Base));
                }
                else {
                    KDV_Change_CheckBox(isChecked, KDV_1_TextBox, getString(R.string.KDVText));
                    KDV_Change_CheckBox(isChecked, KDV_1_TextBox_Base, getString(R.string.KDV_base));
                }
            }

        });

        KDV_Percent8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked) {
                    KDV_Change_CheckBox(isChecked, KDV_8_TextBox, String.format(Locale.CANADA, "%.2f", com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_8_Value));
                    KDV_Change_CheckBox(isChecked, KDV_8_TextBox_Base, String.format(Locale.CANADA, "%.2f", com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_8_Value_Base));
                }
                else {
                    KDV_Change_CheckBox(isChecked, KDV_8_TextBox, getString(R.string.KDVText));
                    KDV_Change_CheckBox(isChecked, KDV_8_TextBox_Base, getString(R.string.KDV_base));
                }
            }

        });

        KDV_Percent18.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked) {
                    KDV_Change_CheckBox(isChecked, KDV_18_TextBox, String.format(Locale.CANADA, "%.2f", com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_18_Value));
                    KDV_Change_CheckBox(isChecked, KDV_18_TextBox_Base, String.format(Locale.CANADA, "%.2f", com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_18_Value_Base));
                }
                else {
                    KDV_Change_CheckBox(isChecked, KDV_18_TextBox, getString(R.string.KDVText));
                    KDV_Change_CheckBox(isChecked, KDV_18_TextBox_Base, getString(R.string.KDV_base));
                }
            }

        });
        // end CheckBoxes Event

        // Radio Buttons Event
        CashRButon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PaymentType = "nakit";
            }
        });

        CreditRButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PaymentType = "kredi kartı";
            }
        });
        // end Radio Buttons Event

        // TextView Events
        CompanyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(main_layout.this, settings.class);
                startActivity(intent);
            }
        });
        // end TextView Events

        // Image View Events
        Question_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Question_Mark();
            }
        });
        // end Image View Events

    }
    // end Inputs

    // declare a constant for camera activity result
    private final int PICK_IMAGE_REQUEST = 100;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {

            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

                Uri uri = data.getData();
                ImageRotate_Popup(uri);

            }else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK && currentPhotoPath != null) {
                File file = new File(currentPhotoPath);
                Uri uri = Uri.fromFile(file);
                ImageRotate_Popup(uri);
            }
        }catch (Exception Error){
            AlertDialog.Builder ADB = new AlertDialog.Builder(main_layout.this);
            ADB.setTitle(getString(R.string.error));
            ADB.setMessage(getString(R.string.error_getNullValue));
            Log.e("MyError", "getNullValue: " + Error.toString());
            ADB.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dispatchTakePictureIntent();
                }
            });
            ADB.show();
        }

    }

    public Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /* Async Tasks */

    // Loader Send
    private class PlugSend extends AsyncTask<String,Void,Void> {

        ProgressDialog progressDialog = new ProgressDialog(new ContextThemeWrapper(main_layout.this, R.style.Theme_AppCompat_DayNight_Dialog));

        @Override
        protected void onPreExecute() {
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.waiting));
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(final String... values) {
            try {

                final SaveImportExpenseDoc SaveDoc = new SaveImportExpenseDoc();
                final byte ResultValue = SaveDoc.Set(
                        values[0],
                        values[1],
                        values[2],
                        values[3],
                        Double.valueOf(values[4]),
                        values[5],
                        values[6],
                        Double.valueOf(values[7]),
                        Double.valueOf(values[8]),
                        Double.valueOf(values[9]),
                        Integer.valueOf(values[10]),
                        Double.valueOf(values[11]),
                        Double.valueOf(values[12]),
                        Double.valueOf(values[13])
                );

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(ResultValue == SaveDoc.Successful){
                            Toast.makeText(main_layout.this, getString(R.string.send_data_success), Toast.LENGTH_SHORT).show();
                            ResetInputs();
                        }
                        else if (ResultValue == SaveDoc.Available)
                            Toast.makeText(main_layout.this, getString(R.string.available_plug), Toast.LENGTH_SHORT).show();
                        else if (ResultValue == SaveDoc.InvalidValue)
                            Toast.makeText(main_layout.this, getString(R.string.wrong_values), Toast.LENGTH_SHORT).show();
                        else if (ResultValue == SaveDoc.NoAccess)
                            Toast.makeText(main_layout.this, getString(R.string.send_data_error), Toast.LENGTH_SHORT).show();
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

    // Loader Recognize
    private class ReadPlug extends AsyncTask<Bitmap,Void,Void> {

        ProgressDialog progressDialog = new ProgressDialog(new ContextThemeWrapper(main_layout.this, R.style.Theme_AppCompat_DayNight_Dialog));

        @Override
        protected void onPreExecute() {
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.waiting));
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(final Bitmap... values) {
            try {

                recognize(values[0]);

            } catch (Exception e){
                e.printStackTrace();
            }

            // Image Delete
            if(DeleteImage(currentPhotoPath)){
                // true
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    // Loader Recognize
    private String Wrong_Read_Result = "";
    private class Send_WrongRead extends AsyncTask<String,Void,Void> {

        ProgressDialog progressDialog = new ProgressDialog(new ContextThemeWrapper(main_layout.this, R.style.Theme_AppCompat_DayNight_Dialog));

        @Override
        protected void onPreExecute() {
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.waiting));
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(final String... values) {
            try {

                ReportReader rd = new ReportReader();
                Wrong_Read_Result = rd.Send_Read(
                        values[0],
                        values[1],
                        values[2],
                        values[3],
                        values[4],
                        values[5],
                        values[6],
                        values[7],
                        values[8],
                        values[9],
                        values[10],
                        values[11],
                        values[12]
                );

            } catch (Exception e){
                e.printStackTrace();
            }

            // Image Delete
            if(DeleteImage(currentPhotoPath)){
                // true
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            progressDialog = null;

            // Thank For Send Message
            android.app.AlertDialog.Builder dialog =
                    new android.app.AlertDialog.Builder(new ContextThemeWrapper(main_layout.this, R.style.Theme_AppCompat_DayNight_Dialog));

            if(Wrong_Read_Result.equals("ok"))
                dialog.setMessage(getString(R.string.wrong_read_thank_message)).setCancelable(true).setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

            else if(Wrong_Read_Result.equals("registered"))
                dialog.setMessage(getString(R.string.available_plug)).setCancelable(true).setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

            else
                dialog.setMessage(getString(R.string.send_data_error)).setCancelable(true).setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
        }
    }

    /* end Async Tasks */

    private void recognize(Bitmap bitmap) {

        try {
            bitmap = monoChrome(bitmap);
            TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
            if (!textRecognizer.isOperational()){
                Log.e("ERROR","Recognizer dependencies is not available yet");
            }
            else {
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<TextBlock> items = textRecognizer.detect(frame);
                StringBuilder stringBuilder = new StringBuilder();
                for(int i=0;i<items.size();++i){
                    TextBlock item = items.valueAt(i);
                    // Show Text
                    stringBuilder.append(item.getValue());
                    stringBuilder.append("\n");
                }
                String result = stringBuilder.toString();
                if (result.length() > 0){
                    results = result;
                    results = results.toLowerCase() + "\n\n\n-----------------------------------------------------------------------------------------------\n\n\n";
                    // Replace ASCII
                    result.replaceAll("Ì", "İ").replaceAll("Í", "İ").replaceAll("Î", "İ").replaceAll("Ï", "İ");
                    // Replace Lower Case
                    result = result.toLowerCase();
                    /*
                     ================
                     Controllers
                     ================
                    */
                    final String FinalResult = result;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Clear All Values
                            ResetInputs();

                            // Date Controller
                            checkDate cd = new checkDate();
                            String clear1_result = cd.ControlDate(FinalResult, DateTextbox);
                            Log.e("ClearResults", "Clear 1 Result: " + clear1_result);

                            // Plug No Controller
                            checkPlugNo cpn = new checkPlugNo();
                            cpn.ControlPlugNo(clear1_result, PlugNoTextbox);

                            // Total Value Controller
                            checkTotal ct = new checkTotal();
                            String clear2_result = ct.ControlTotal(clear1_result, TotalTextbox/*, TotalKDVTextBox*/);
                            Log.e("ClearResults", "Clear 2 Result: " + clear2_result);

                            // Percent KDV Controller
                            results += clear2_result;
                            checkKDVPercent ck = new checkKDVPercent();
                            ck.ControlKDV(clear2_result, KDV_Percent1, KDV_Percent8, KDV_Percent18);

                            // VD Controller
                            checkVD cvd = new checkVD();
                            cvd.ControlVD(FinalResult, VDTextbox) ;
                        }
                    });
                    /*
                     ================
                     end Controllers
                     ================
                    */
                }else{
                    results = "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

     void show_text(String results) {
        Intent intent = new Intent(main_layout.this, get_values.class);
        intent.putExtra("DATA",results);
        startActivity(intent);
    }


    // Take photo
    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.matrixteknoloji.pratikfis",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        }catch (Exception Error) {
            AlertDialog.Builder ADB = new AlertDialog.Builder(main_layout.this);
            ADB.setTitle(getString(R.string.error));
            ADB.setMessage(getString(R.string.error_getNullFile));
            Log.e("MyError", "getNullFile: " + Error.toString());
            ADB.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dispatchTakePictureIntent();
                }
            });
            ADB.show();
        }
    }

    String currentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "OCR_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    Bitmap monoChrome(Bitmap bitmap) {
        Bitmap bmpMonochrome = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmpMonochrome);
        ColorMatrix ma = new ColorMatrix();
        ma.setSaturation(0);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(ma));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bmpMonochrome;
    }

    Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmp3 = bmp1.copy(Bitmap.Config.ARGB_8888,true);//mutable copy
        Canvas canvas = new Canvas(bmp3 );
        canvas.drawBitmap(bmp2, new Matrix(), null);
        return bmp3 ;
    }

    private void ResetInputs(){
        // Reset Text boxes in item and clear checked check boxes

        // Edit Boxes
        DateTextbox.setText("");
        PlugNoTextbox.setText("");
        TotalTextbox.setText("");
        VDTextbox.setText("");
        KDV_1_TextBox.setText("");
        KDV_8_TextBox.setText("");
        KDV_18_TextBox.setText("");
       // TotalKDVTextBox.setText("");

        // Check Boxes
        KDV_Percent1.setChecked(false);
        KDV_Percent8.setChecked(false);
        KDV_Percent18.setChecked(false);

        // Radio Buttons
        CashRButon.setChecked(false);
        CreditRButton.setChecked(false);

        com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_18_Value = 0;
        com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_8_Value = 0;
        com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_1_Value = 0;

        com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_18_Value_Base = 0;
        com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_8_Value_Base = 0;
        com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_1_Value_Base = 0;

        com.matrixteknoloji.pratikfis.TextControlClass.Values.TotalValue_LineNumber = 0;

        com.matrixteknoloji.pratikfis.TextControlClass.Values.KDVLineIndex = 0;
        com.matrixteknoloji.pratikfis.TextControlClass.Values.DateIndex_end = 0;

        PaymentType = "";
        // end Resets and Clears
    }

    private void KDV_Change_CheckBox(boolean CheckBoxValue, EditText editText, String EditTextValue){
        editText.setText(EditTextValue);
        editText.setEnabled(CheckBoxValue);
    }

    // Get Image Rotate Popup
    public void ImageRotate_Popup(final Uri uri) {

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            SetBitmap(bitmap);
            // Create view
            final ViewGroup Myview = (ViewGroup) getLayoutInflater().inflate(R.layout.main_layout, null);

            // inflate the layout of the popup window
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            final View popupView = inflater.inflate(R.layout.image_rotate, null);

            // Get the popup window info
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            boolean focusable = true; // lets taps outside the popup also dismiss it
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

            // show the popup window
            popupWindow.showAtLocation(Myview, Gravity.CENTER, 0, 0);

            // Create Inputs
            rotateButton_Popup = (Button) popupView.findViewById(R.id.Rotate_btn);
            okButton_Popup = (Button) popupView.findViewById(R.id.Ok_btn);
            cancelButton_Popup = (Button) popupView.findViewById(R.id.Cancel_btn);
            plugImage_Popup = (ImageView) popupView.findViewById(R.id.Plug_image);
            // end Create Inputs

            plugImage_Popup.setImageBitmap(GetBitmap());

            // Input Events
            rotateButton_Popup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SetBitmap(RotateBitmap(GetBitmap(), 90));
                    plugImage_Popup.setImageBitmap(GetBitmap());
                }
            });

            okButton_Popup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new ReadPlug().execute(GetBitmap());
                    popupWindow.dismiss();
                }
            });

            cancelButton_Popup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Image Delete
                    if(DeleteImage(currentPhotoPath)){
                        // true
                    }
                    popupWindow.dismiss();
                }
            });

            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if(DeleteImage(currentPhotoPath)){
                        // true
                    }
                }
            });
            // end Inputs Events

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    // check Internet Connection
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    // Image Delete
    private boolean DeleteImage(String ImageSrc){
        if(ImageSrc != null){

            File fdelete = new File(ImageSrc);
            if (fdelete.exists()) {
                if (fdelete.delete()) {
                    return true;
                } else {
                    return false;
                }
            }

        }
        return false;
    }

    // Send Plug Info
    private void Send_PlugInfo(){

        if(isNetworkConnected()) {
            if (DateTextbox.getText().toString().replaceAll(" ", "").equals("") ||
                    PlugNoTextbox.getText().toString().replaceAll(" ", "").equals("") ||
                    /*|| TotalKDVTextBox.getText().toString().replaceAll(" ", "").equals("")*/
                    TotalTextbox.getText().toString().replaceAll(" ", "").equals("") ||
                    VDTextbox.getText().toString().replaceAll(" ", "").equals("") ||
                    PaymentType.equals("")
            ) {
                Toast.makeText(main_layout.this, getString(R.string.empty_places), Toast.LENGTH_SHORT).show();
            } else {
                // Set Dialog
                android.app.AlertDialog.Builder dialog =
                        new android.app.AlertDialog.Builder(new ContextThemeWrapper(main_layout.this, R.style.Theme_AppCompat_DayNight_Dialog));

                dialog.setMessage(getString(R.string.send_message))
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // OKAY Button
                                String Date = (DateTextbox.getText().toString().equals("")) ? "01/01/2000" : DateTextbox.getText().toString();
                                String PlugNo = (PlugNoTextbox.getText().toString().equals("")) ? "0" : PlugNoTextbox.getText().toString();
                                //String TotalKDV = (TotalKDVTextBox.getText().toString().equals("")) ? "0" : TotalKDVTextBox.getText().toString();
                                Double Total = Double.valueOf((TotalTextbox.getText().toString().equals("")) ? "0" : TotalTextbox.getText().toString());
                                Total = Double.valueOf(String.format(Locale.CANADA, "%.2f", Total));
                                String VD = (VDTextbox.getText().toString().equals("")) ? "0" : VDTextbox.getText().toString();

                                com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_1_Value = (KDV_1_TextBox.getText().toString().equals((getString(R.string.KDVText))) || KDV_1_TextBox.getText().toString().equals("") || !KDV_Percent1.isChecked()) ? 0.00 : Double.valueOf(String.format(Locale.CANADA, "%.2f", Double.valueOf(KDV_1_TextBox.getText().toString().replaceAll(" ", "").replaceAll("[\\x2C]", "."))));
                                com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_8_Value = (KDV_8_TextBox.getText().toString().equals((getString(R.string.KDVText))) || KDV_8_TextBox.getText().toString().equals("") || !KDV_Percent8.isChecked()) ? 0.00 : Double.valueOf(String.format(Locale.CANADA, "%.2f", Double.valueOf(KDV_8_TextBox.getText().toString().replaceAll(" ", "").replaceAll("[\\x2C]", "."))));
                                com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_18_Value = (KDV_18_TextBox.getText().toString().equals((getString(R.string.KDVText))) || KDV_18_TextBox.getText().toString().equals("") || !KDV_Percent18.isChecked()) ? 0.00 : Double.valueOf(String.format(Locale.CANADA, "%.2f", Double.valueOf(KDV_18_TextBox.getText().toString().replaceAll(" ", "").replaceAll("[\\x2C]", "."))));

                                com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_1_Value_Base = (KDV_1_TextBox_Base.getText().toString().equals((getString(R.string.KDV_base))) || KDV_1_TextBox_Base.getText().toString().equals("") || !KDV_Percent1.isChecked()) ? 0.00 : Double.valueOf(String.format(Locale.CANADA, "%.2f", Double.valueOf(KDV_1_TextBox_Base.getText().toString().replaceAll(" ", "").replaceAll("[\\x2C]", "."))));
                                com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_8_Value_Base = (KDV_8_TextBox_Base.getText().toString().equals((getString(R.string.KDV_base))) || KDV_8_TextBox_Base.getText().toString().equals("") || !KDV_Percent8.isChecked()) ? 0.00 : Double.valueOf(String.format(Locale.CANADA, "%.2f", Double.valueOf(KDV_8_TextBox_Base.getText().toString().replaceAll(" ", "").replaceAll("[\\x2C]", "."))));
                                com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_18_Value_Base = (KDV_18_TextBox_Base.getText().toString().equals((getString(R.string.KDV_base))) || KDV_18_TextBox_Base.getText().toString().equals("") || !KDV_Percent18.isChecked()) ? 0.00 : Double.valueOf(String.format(Locale.CANADA, "%.2f", Double.valueOf(KDV_18_TextBox_Base.getText().toString().replaceAll(" ", "").replaceAll("[\\x2C]", "."))));

                                new PlugSend().execute(
                                        Values.UserName,
                                        Values.Password,
                                        Date,
                                        PlugNo,
                                        String.valueOf(Total),
                                        VD,
                                        PaymentType,
                                        String.valueOf(com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_1_Value),
                                        String.valueOf(com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_8_Value),
                                        String.valueOf(com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_18_Value),
                                        Values.CoId,
                                        String.valueOf(com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_1_Value_Base),
                                        String.valueOf(com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_8_Value_Base),
                                        String.valueOf(com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_18_Value_Base)
                                );
                                //show_text(results);

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

        }
        else
            Toast.makeText(main_layout.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();

    }

    // Question Mark
    private void Question_Mark(){

        try {
            // Create view
            final ViewGroup Myview = (ViewGroup) getLayoutInflater().inflate(R.layout.main_layout, null);

            // inflate the layout of the popup window
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            final View popupView = inflater.inflate(R.layout.question_mark, null);

            // Get the popup window info
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            boolean focusable = true; // lets taps outside the popup also dismiss it
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

            // show the popup window
            popupWindow.showAtLocation(Myview, Gravity.CENTER, 0, 0);

            // Create Inputs
            wrongReadButton_Popup = (Button) popupView.findViewById(R.id.WrongRead_Btn);
            helpButton_Popup = (Button) popupView.findViewById(R.id.Help_Btn);
            backButton_Popup = (Button) popupView.findViewById(R.id.Back_btn);
            // end Create Inputs

            // Inputs Events
            wrongReadButton_Popup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Set Dialog
                    android.app.AlertDialog.Builder dialog =
                            new android.app.AlertDialog.Builder(new ContextThemeWrapper(main_layout.this, R.style.Theme_AppCompat_DayNight_Dialog));

                    dialog.setMessage(getString(R.string.wrong_read_question))
                            .setCancelable(true)
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // OKAY Button
                                    String Date = (DateTextbox.getText().toString().equals("")) ? "01/01/2000" : DateTextbox.getText().toString();
                                    String PlugNo = (PlugNoTextbox.getText().toString().equals("")) ? "0" : PlugNoTextbox.getText().toString();
                                    //String TotalKDV = (TotalKDVTextBox.getText().toString().equals("")) ? "0" : TotalKDVTextBox.getText().toString();
                                    Double Total = Double.valueOf((TotalTextbox.getText().toString().equals("")) ? "0" : TotalTextbox.getText().toString());
                                    Total = Double.valueOf(String.format(Locale.CANADA, "%.2f", Total));
                                    String VD = (VDTextbox.getText().toString().equals("")) ? "0" : VDTextbox.getText().toString();

                                    com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_1_Value = (KDV_1_TextBox.getText().toString().equals((getString(R.string.KDVText))) || KDV_1_TextBox.getText().toString().equals("") || !KDV_Percent1.isChecked()) ? 0.00 : Double.valueOf(String.format(Locale.CANADA, "%.2f", Double.valueOf(KDV_1_TextBox.getText().toString().replaceAll(" ", "").replaceAll("[\\x2C]", "."))));
                                    com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_8_Value = (KDV_8_TextBox.getText().toString().equals((getString(R.string.KDVText))) || KDV_8_TextBox.getText().toString().equals("") || !KDV_Percent8.isChecked()) ? 0.00 : Double.valueOf(String.format(Locale.CANADA, "%.2f", Double.valueOf(KDV_8_TextBox.getText().toString().replaceAll(" ", "").replaceAll("[\\x2C]", "."))));
                                    com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_18_Value = (KDV_18_TextBox.getText().toString().equals((getString(R.string.KDVText))) || KDV_18_TextBox.getText().toString().equals("") || !KDV_Percent18.isChecked()) ? 0.00 : Double.valueOf(String.format(Locale.CANADA, "%.2f", Double.valueOf(KDV_18_TextBox.getText().toString().replaceAll(" ", "").replaceAll("[\\x2C]", "."))));

                                    com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_1_Value_Base = (KDV_1_TextBox_Base.getText().toString().equals((getString(R.string.KDV_base))) || KDV_1_TextBox_Base.getText().toString().equals("") || !KDV_Percent1.isChecked()) ? 0.00 : Double.valueOf(String.format(Locale.CANADA, "%.2f", Double.valueOf(KDV_1_TextBox_Base.getText().toString().replaceAll(" ", "").replaceAll("[\\x2C]", "."))));
                                    com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_8_Value_Base = (KDV_8_TextBox_Base.getText().toString().equals((getString(R.string.KDV_base))) || KDV_8_TextBox_Base.getText().toString().equals("") || !KDV_Percent8.isChecked()) ? 0.00 : Double.valueOf(String.format(Locale.CANADA, "%.2f", Double.valueOf(KDV_8_TextBox_Base.getText().toString().replaceAll(" ", "").replaceAll("[\\x2C]", "."))));
                                    com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_18_Value_Base = (KDV_18_TextBox_Base.getText().toString().equals((getString(R.string.KDV_base))) || KDV_18_TextBox_Base.getText().toString().equals("") || !KDV_Percent18.isChecked()) ? 0.00 : Double.valueOf(String.format(Locale.CANADA, "%.2f", Double.valueOf(KDV_18_TextBox_Base.getText().toString().replaceAll(" ", "").replaceAll("[\\x2C]", "."))));

                                    // Set Values
                                    new Send_WrongRead().execute(
                                            Values.UserName,
                                            Values.CoId,
                                            results,
                                            Date,
                                            PlugNo,
                                            String.valueOf(com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_1_Value),
                                            String.valueOf(com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_8_Value),
                                            String.valueOf(com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_18_Value),
                                            String.valueOf(com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_1_Value_Base),
                                            String.valueOf(com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_8_Value_Base),
                                            String.valueOf(com.matrixteknoloji.pratikfis.TextControlClass.Values.KDV_18_Value_Base),
                                            String.valueOf(Total),
                                            VD
                                    );

                                    dialog.dismiss();
                                }
                            }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // CANCEL Button
                            dialog.dismiss();
                        }
                    });
                    // end Set Dialog
                    dialog.create().show();
                }
            });

            helpButton_Popup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.uyumsoft.com"));
                    startActivity(intent);
                }
            });

            backButton_Popup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
            // end Inputs Events

        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/