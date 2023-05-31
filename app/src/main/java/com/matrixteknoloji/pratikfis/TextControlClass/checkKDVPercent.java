/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/
package com.matrixteknoloji.pratikfis.TextControlClass;

import android.util.Log;
import android.widget.CheckBox;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class checkKDVPercent {
    public checkKDVPercent(){
        // Start Functions
    }
    // Percent CheckBox Values
    private boolean percent_1 = false;
    private boolean percent_8 = false;
    private boolean percent_18 = false;
    private double KDV_percentValue_1 = 0.01;
    private double KDV_percentValue_8 = 0.08;
    private double KDV_percentValue_18 = 0.18;
    // Filter
    private String PercentFilter = "(?:[\\x25](?:[0-9]{2}|[0-9])|^(?:[0-9]{2}|[0-9])$)";

    public void ControlKDV(String Value, CheckBox checkBox_KDV_1, CheckBox checkBox_KDV_8, CheckBox checkBox_KDV_18){
        String[][] KDVFilterData = {
                /* % 01 */ {"01", "1", "O1", "Ol", "ol", "l"},
                /* % 08 */ {"08", "8", "O8", "o8"},
                /* % 18 */ {"18", "l8"}
        };
        Value = Value.replaceAll(" ", "");

        String[] KDVTexts = GetKDV(Value, PercentFilter, KDVFilterData);
        Double[] KDVValues = GetMoneys(Value, checkTotal.TotalFilter, KDVTexts.length);

        if(KDVTexts.length == KDVValues.length){
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(3);
            // Get All KDV
            for (int i = 0; i <= KDVTexts.length - 1; i++){
                double Calculate_KDV = 0;
                double Calculate_KDV_Base = 0;
                // KDV % 1
                for (String KDV_1 : KDVFilterData[0]) {
                    if(KDVTexts[i].equals(KDV_1)){
                        Calculate_KDV = (KDVValues[KDVValues.length - (i + 1)] / (KDV_percentValue_1 + 1.00)) * KDV_percentValue_1;
                        Calculate_KDV_Base = (KDVValues[KDVValues.length - (i + 1)]) - Calculate_KDV;
                        Log.e("CalculateKDV", "KDV: " + KDVTexts[i] + " | Value: " + KDVValues[KDVValues.length - (i + 1)] + " | KDV Percent: " + KDV_percentValue_1 + " | KDV Value: " + Calculate_KDV + " | KDV Base: " + Calculate_KDV_Base);
                        Values.KDV_1_Value += Calculate_KDV;
                        Values.KDV_1_Value_Base += Calculate_KDV_Base;
                        // Checked Control
                        if (!percent_1)
                            percent_1 = true;
                    }
                }
                // KDV % 8
                for (String KDV_8 : KDVFilterData[1]) {
                    if(KDVTexts[i].equals(KDV_8)){
                        Log.e("MinusValue", "Values: " + KDVValues[KDVValues.length - (i + 1)]);
                        Calculate_KDV = (KDVValues[KDVValues.length - (i + 1)] / (KDV_percentValue_8 + 1.00)) * KDV_percentValue_8;
                        Calculate_KDV_Base = (KDVValues[KDVValues.length - (i + 1)]) - Calculate_KDV;
                        Log.e("CalculateKDV", "KDV: " + KDVTexts[i] + " | Value: " + KDVValues[KDVValues.length - (i + 1)] + " | KDV Percent: " + KDV_percentValue_8 + " | KDV Value: " + Calculate_KDV + " | KDV Base: " + Calculate_KDV_Base);
                        Values.KDV_8_Value += Calculate_KDV;
                        Values.KDV_8_Value_Base += Calculate_KDV_Base;
                        // Checked Control
                        if (!percent_8)
                            percent_8 = true;
                    }
                }
                // KDV % 8
                for (String KDV_18 : KDVFilterData[2]) {
                    if(KDVTexts[i].equals(KDV_18)){
                        Calculate_KDV = (KDVValues[KDVValues.length - (i + 1)] / (KDV_percentValue_18 + 1.00)) * KDV_percentValue_18;
                        Calculate_KDV_Base = (KDVValues[KDVValues.length - (i + 1)]) - Calculate_KDV;
                        Log.e("CalculateKDV", "KDV: " + KDVTexts[i] + " | Value: " + KDVValues[KDVValues.length - (i + 1)] + " | KDV Percent: " + KDV_percentValue_18 + " | KDV Value: " + Calculate_KDV + " | KDV Base: " + Calculate_KDV_Base);
                        Values.KDV_18_Value += Calculate_KDV;
                        Values.KDV_18_Value_Base += Calculate_KDV_Base;
                        // Checked Control
                        if (!percent_18)
                            percent_18 = true;
                    }
                }
            }
        }else{
            Values.KDV_1_Value = 0;
            Values.KDV_8_Value = 0;
            Values.KDV_18_Value = 0;

            Values.KDV_1_Value_Base = 0;
            Values.KDV_8_Value_Base = 0;
            Values.KDV_18_Value_Base = 0;

            percent_1 = false;
            percent_8 = false;
            percent_18 = false;
        }

        checkBox_KDV_1.setChecked(percent_1);
        checkBox_KDV_8.setChecked(percent_8);
        checkBox_KDV_18.setChecked(percent_18);
        // end CheckBox Controller
    }

    // Get KDV Values
    private String[] GetKDV(String Value, String Filter, String[][] PercentFilter){
        ArrayList<String> KDVArrayList = new ArrayList<String>();
        Pattern pattern;
        Matcher matcher;
        Log.e("SimpleArray", "Filter: " + Filter);
        String[] ValueData = Value.split("\n");
        // Get KDV Values
        for (String LineValue : ValueData) {
            pattern = Pattern.compile(Filter);
            matcher = pattern.matcher(LineValue);
            if(matcher.find()){
                String FoundValue = checkTotal.PriceValue_DeleteFirstItem(matcher.group());
                Log.e("SimpleArray", "Found Value: " + FoundValue);
                // KDV % 1, %8, %18 (This is simple. Get All Values)
                for (int i = 0; i <= PercentFilter.length -1; i++) {
                    // Get Percent Filter Line Values
                    for (String KDVPercent : PercentFilter[i]) {
                        //Log.e("SimpleArray", "PercentFilter: " + KDVPercent);
                        if (FoundValue.equals(KDVPercent)) {
                            KDVArrayList.add(FoundValue);
                        }
                    }
                }
            }
        }
        // Set Array list to Array
        String[] KDVArray = KDVArrayList.toArray(new String[KDVArrayList.size()]);
        return  KDVArray;
    }
    // Get Money Values
    private Double[] GetMoneys(String Value, String Filter, Integer KDVDataCount){
        ArrayList<Double> MoneyArrayList = new ArrayList<Double>();
        Pattern pattern;
        Matcher matcher;
        String[] ValueData = Value.split("\n");
        // Get Money Values
        for (String LineValue : ValueData) {
            pattern = Pattern.compile(Filter);
            matcher = pattern.matcher(LineValue);
            if(matcher.find()){
                Log.e("GetMinusValues", "Values: " + matcher.group());
                Double DoubleLineValue = Double.valueOf(checkTotal.PriceValue_DeleteFirstItem(matcher.group().replaceAll(",", ".")));
                DoubleLineValue = Double.valueOf(String.format(Locale.CANADA,"%.2f", DoubleLineValue));
                MoneyArrayList.add(DoubleLineValue);
            }
        }
        // Get Money Array Last Values
        Log.e("SimpleArray", "Get Moneys Foreach end");
        Double[] MoneyArray = new Double[KDVDataCount];
        if(MoneyArrayList.size() >= KDVDataCount) {
            for (int i = 0; i < KDVDataCount; i++) {
                Log.e("SimpleArray", "Get Moneys Last For i: " + i);
                MoneyArray[i] = MoneyArrayList.get(MoneyArrayList.size() - (i + 1));
            }
        }
        Log.e("SimpleArray", "Get Moneys For end");
        return MoneyArray;
    }
}
/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/
