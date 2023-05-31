/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/
package com.matrixteknoloji.pratikfis;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class get_values extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_values);

        // Close Action Bar
        getSupportActionBar().hide();
        // end Close Action Bar

        textView = (TextView) findViewById(R.id.textview_result);

        Bundle bundle = getIntent().getExtras();
        String result = bundle.getString("DATA");

        textView.setText(result);

    }
}
/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/

