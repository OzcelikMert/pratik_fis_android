/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/
package com.matrixteknoloji.pratikfis.TextControlClass;

import android.util.Log;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class checkPlugNo {
    public checkPlugNo(){
        // Start function
    }

    public void ControlPlugNo(String Value, EditText editText){
        Value = Value.replaceAll("\r", "");
        // Text Filter
        String PlugNoFiltersText = "ışno, ışn0, fışno, fışn0, fısno, fısn0, fıno, fın0, " + "işno, işn0, fişno, fişn0, fisno, fisn0, fino, fin0, " + "ı$no, ı$n0, fı$no, fı$n0, fı$ng, " + "i$no, i$n0, fi$no, fi$n0, fi$ng, " + "f15no, f15n0, "
                + "ış.no, ış.n0, fış.no, fış.n0, fıs.no, fıs.n0, fı.no, fı.n0, " + "iş.no, iş.n0, fiş.no, fiş.n0, fis.no, fis.n0, fi.no, fi.n0, " + "ı$.no, ı$.n0, fı$.no, fı$.n0, " + "i$.no, i$.n0, fi$.no, fi$.n0, " + "f15.no, f15.n0";
        // Number Filter
        String PlugNoFiltersNumber = "(?:[0-9]{6}|[0-9]{5}|[0-9]{4}|[0-9]{3}|[0-9]{2}|[0-9])";
        // Clear Hours Value Filter [\x2E]: . (dot)
        String ClearValue = Hours_ClearValue(Value,
                "([0-9]{2}:[0-9]{2}:[0-9]{2}), " +
                        "([0-9]{2}[\\x2E][0-9]{2}:[0-9]{2}), " +
                        "([0-9]{2}:[0-9]{2}[\\x2E][0-9]{2}), " +
                        "([0-9]{2}[\\x2E][0-9]{2}[\\x2E][0-9]{2}), " +
                        "([0-9]{2}:[0-9]{2}), " +
                        "([0-9]{2}[\\x2E][0-9]{2})");
        // end Clear Hours Value
        Log.e("PlugNo", "Value: " + ClearValue);
        // Get Plug No After Value
        String Setvalue = Get_PlugNoAfterValue(ClearValue, PlugNoFiltersNumber, PlugNoFiltersText);
        Log.e("PlugNo",  "Plug No After Control Text: " + Setvalue);
        if (!RegexControl(Setvalue, PlugNoFiltersNumber)){
            // Get New Value
            String NewValue = NewValue(ClearValue, Values.DateIndex_end);
            NewValue = MoneyKDV_ClearValue(NewValue, checkTotal.TotalFilter);
            Log.e("PlugNo", "New Value: " + NewValue);
            // Get Plug No
            Pattern PlugNoPattern = Pattern.compile(PlugNoFiltersNumber);
            Matcher PlugNoMatcher = PlugNoPattern.matcher(NewValue);
            if(PlugNoMatcher.find()){
                Setvalue = PlugNoMatcher.group();
                Log.e("PlugNo", "New Set Value: " + Setvalue);
            }
        }
        // Set Plug No
        Setvalue = ClearWrongValue(Setvalue);
        Log.e("PlugNo", "Clear Set Value: " + Setvalue);
        editText.setText(Setvalue);
    }

    /* #Private Functions */
    // Get Plug No After Value
    private String Get_PlugNoAfterValue(String Value, String Value_PlugNoFilter, String PlugNoFilters){
        // Control Plug No
        int PlugNoTextIndex_end = 0;
        // Plug No Text Control
        PlugNoFilters = PlugNoFilters.replaceAll(" ", "");
        String PlugNoFiltersData[] = PlugNoFilters.split(",");
        for (String PlugNoFilter: PlugNoFiltersData) {
            Pattern pattern = Pattern.compile(PlugNoFilter);
            Matcher matcher = pattern.matcher(Value);
            if (matcher.find()){
                PlugNoTextIndex_end = matcher.end();
                Log.e("PlugNo",  "Plug No Text: " + matcher.group() + " | Plug No Text End Index: " + PlugNoTextIndex_end);
                break;
            }
        }
        // Substring Control
        if ((!Value.equals("")) && (Value.substring(PlugNoTextIndex_end, (PlugNoTextIndex_end + 1)).equals(":") || Value.substring(PlugNoTextIndex_end, (PlugNoTextIndex_end + 1)).equals(";"))){
            PlugNoTextIndex_end++;
        }
        Value = (!Value.equals("") && Value != null) ? Value.substring((PlugNoTextIndex_end)) : "";
        // Plug No Second Control
        Pattern PlugNoPattern = Pattern.compile(Value_PlugNoFilter);
        Matcher PlugNoMatcher = PlugNoPattern.matcher(Value);
        if(PlugNoMatcher.find()){
            Value = PlugNoMatcher.group();
        }
        return Value;
    }
    // Get First Money Index
    private String NewValue(String Value, int endDateIndex){
        // Get Money Start Index
        int startMoneyIndex = 0;
        Pattern MoneyPattern = Pattern.compile(checkTotal.TotalFilter);
        Matcher MoneyMatcher = MoneyPattern.matcher(Value);
        if(MoneyMatcher.find()){
            startMoneyIndex = MoneyMatcher.start();
        }
        startMoneyIndex = (startMoneyIndex < endDateIndex) ? Value.length() : startMoneyIndex;
        String NewValue = Value.substring(endDateIndex, startMoneyIndex);
        return NewValue;
    }
    // Clear All Values
    private String MoneyKDV_ClearValue(String Value, String Money_Or_KDV_Filters){
        // Value in KDV or Money Clear
        Pattern MoneyOrKDV_pattern = Pattern.compile(Money_Or_KDV_Filters);
        Matcher MoneyOrKDV_matcher = MoneyOrKDV_pattern.matcher(Value);
        while (MoneyOrKDV_matcher.find()){
            String KDVorMoney = MoneyOrKDV_matcher.group();
            Log.e("PlugNo", "KDV or Money: " + KDVorMoney);
            KDVorMoney = checkTotal.PriceValue_DeleteFirstItem(KDVorMoney);
            Value = Value.replaceAll(KDVorMoney, "Deleted");
        }
        // end Value in KDV or Money Clear
        return Value;
    }
    // Hours Clear
    private String Hours_ClearValue(String Value, String HoursFilters){
        // Value in Hours Clear
        HoursFilters = HoursFilters.replaceAll(" ", "");
        String HoursFiltersData[] = HoursFilters.split(",");
        for (String HoursFilter : HoursFiltersData) {
            Pattern HoursPattern = Pattern.compile(HoursFilter);
            Matcher HoursMatcher = HoursPattern.matcher(Value);
            if (HoursMatcher.find()){
                Log.e("PlugNo", "Hours: " + HoursMatcher.group() + " | Filter: " + HoursFilter);
                Value = Value.replaceAll(HoursMatcher.group(), "Deleted");
            }
        }
        // End Value in Hours Clear
        return  Value;
    }
    // Clear Wrong Values
    private String ClearWrongValue(String Value){
        // Plug No Remove Wrong Values
        Value = Value.replaceAll("\n", "\r");
        Pattern SetValuePattern = Pattern.compile("(?:[\\s]|:|=|;|\\+).*");
        Matcher SetValueMatcher = SetValuePattern.matcher(Value);
        if (SetValueMatcher.find()){
            Value = Value.replaceAll(SetValueMatcher.group(), "");
        }
        return Value;
    }
    /* #Public Functions */
    // Regex Control
    public static boolean RegexControl(String Value, String RegexValue){
        Pattern pattern = Pattern.compile(RegexValue);
        Matcher matcher = pattern.matcher(Value);
        if (matcher.find()){
            return true;
        }else{
            return false;
        }
    }

}
/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/
