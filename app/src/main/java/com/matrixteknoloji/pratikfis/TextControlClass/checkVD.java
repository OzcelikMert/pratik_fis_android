/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/
package com.matrixteknoloji.pratikfis.TextControlClass;

import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class checkVD {
    public checkVD(){
        // Start Functions
    }

    public void ControlVD(String Value, EditText editText){
        String Setvalue = null;
        Value = Value.replaceAll(" ", "");
        // Control Total KDV
        String Filter = "(?:\\d{11}|\\d{10})";
        Pattern pattern = Pattern.compile(Filter);
        Matcher matcher = pattern.matcher(Value);
        if (matcher.find()){
                Setvalue = matcher.group();
        }
        Setvalue = (Setvalue != null) ? Setvalue : "";
        editText.setText(Setvalue);
    }
}
/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/
