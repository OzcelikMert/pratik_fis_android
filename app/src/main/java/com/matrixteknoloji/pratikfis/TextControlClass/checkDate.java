/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/
package com.matrixteknoloji.pratikfis.TextControlClass;

import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class checkDate {
    public checkDate(){
        // Start Function
    }

    public String ControlDate(String Value, EditText editText){
        String Setvalue = "";
        Value = Value.replaceAll(" ", "");
        String Filters = "(?:[0-9]{2}\\/[0-9]{2}\\/[0-9]{4}), (?:[0-9]{2}\\-[0-9]{2}\\-[0-9]{4}), (?:[0-9]{2}\\.[0-9]{2}\\.[0-9]{4})";
        Filters = Filters.replaceAll( " ", "");
        String DataFilters[] = Filters.split(",");
        for (String FilterValue : DataFilters) {
            Pattern pattern = Pattern.compile(FilterValue);
            Matcher matcher = pattern.matcher(Value);
            if (matcher.find()){
                Setvalue = matcher.group();
                // Zero '0' Control
                Setvalue = DateController_VariableType(Setvalue);
                // Set Index
                Values.DateIndex_end = matcher.end();
                break;
            }
            // end Control
        }

        editText.setText(Setvalue);
        return Value.replaceAll(Setvalue, "Deleted");
    }

    // Date Controller
    private String DateController_VariableType(String Value){

        // Convert Date Type "/" (ex: 06-01-2000 Convert to: 06/01/2000)
        Value = Value.substring(0, 2) + "/" + Value.substring(3, 5) + "/" + Value.substring(6, 10);

        // Check Day
        String DayFirst = Value.substring(0, 1);
        if (DayFirst.equals("8")){
            DayFirst = "0";
            Value = DayFirst + Value.substring(1);
        }

        // Check Month
        String MonthFirst = Value.substring(3, 4);
        if(MonthFirst.equals("8")){
            MonthFirst = "0";
            Value = Value.substring(0, 3) + MonthFirst + Value.substring(4);
        }

        // Check Year
        String YearSecond = Value.substring(7, 8);
        if(YearSecond.equals("8")){
            YearSecond = "0";
            Value = Value.substring(0, 7) + YearSecond + Value.substring(8);
        }

        return Value;
    }
}
/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/
