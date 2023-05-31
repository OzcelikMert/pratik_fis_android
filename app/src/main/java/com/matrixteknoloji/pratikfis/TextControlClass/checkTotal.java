/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/
package com.matrixteknoloji.pratikfis.TextControlClass;

import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class checkTotal {
    // 2A: * | AB: « | 23: # | 2C: , | 2E: . | 2B: + | 2D: -
    public static String TotalFilter = "(?:[\\x2A]|X|x|[\\xAB]|[\\x23]|[\\x2B]|)(?:[0-9]{5}|[0-9]{4}|[0-9]{3}|[0-9]{2}|[0-9])(?:[\\x2C]|[\\x2E])(?:[0-9]{2}|[0-9])";
    public checkTotal(){
        // Start Functions
    }

    public String ControlTotal(String Value, EditText TotalTextbox/*, EditText KDVTotalTextbox*/){
        String FiltersKDV  = "kdv, kdu, " + "kbv, kbu, " + "topkdv, topkdvn, topkdu, t0pkdu, t0pkdvn, t0pkdv, " + "tpkdv, " + "kdvtop, kdvt0p";
        // Clear Space Values
        Value = Value.replaceAll(" ", "");
        String TotalTextbox_Text = "";
        //String KDVTotalTextbox_Text = "";
        String NewValue = Value;
        // Get KDV Line Index
        int KDVTextIndex = GetKDVIndex(Value, FiltersKDV);
        // Get Values (Before And After)
        Object[][] BeforeTotalsData = GetTotalsBefore(Value, KDVTextIndex);
        Object[][] AfterTotalsData = GetTotalsAfter(Value, KDVTextIndex);
        // Get Max Values
        double BeforeMaxValue = GetMaxValue(BeforeTotalsData[0]);
        double AfterMaxValue = GetMaxValue(AfterTotalsData[0]);
        // Check Values
        if (BeforeMaxValue > AfterMaxValue){
            TotalTextbox_Text = String.valueOf(BeforeMaxValue);
            //KDVTotalTextbox_Text = (BeforeTotalsData[0][0].toString().equals(String.valueOf(BeforeMaxValue))) ? BeforeTotalsData[0][1].toString() : BeforeTotalsData[0][0].toString();
            NewValue = Value.substring(0, Integer.valueOf(BeforeTotalsData[1][0].toString()));
        }else if(AfterMaxValue > BeforeMaxValue){
            TotalTextbox_Text = String.valueOf(AfterMaxValue);
            //KDVTotalTextbox_Text = (AfterTotalsData[0][0].toString().equals(String.valueOf(AfterMaxValue))) ? AfterTotalsData[0][1].toString() : AfterTotalsData[0][0].toString();
            NewValue = Value.substring(0, Integer.valueOf(AfterTotalsData[1][0].toString()));
        }

        TotalTextbox.setText(TotalTextbox_Text);
        //KDVTotalTextbox.setText(KDVTotalTextbox_Text);
        return  NewValue;
    }

    // Value First Item Controller
    public static String PriceValue_DeleteFirstItem(String Value){
        // Control 1
        if((Value != null && !Value.equals("")) && (Value.substring(0, 1).equals("*") || Value.substring(0, 1).equals("X") || Value.substring(0, 1).equals("x") || Value.substring(0, 1).equals("«") || Value.substring(0, 1).equals("+") || Value.substring(0, 1).equals("#") || Value.substring(0, 1).equals("%"))) {
            Value = Value.substring(1);
        }

        return Value;
    }

    /* Get Values */
    // Get Totals Before
    private Object[][] GetTotalsBefore(String Value, Integer Index){
        // New Value
        String valueBefore = Value.substring(0, Index);
        // Create ArrayList
        ArrayList<Double> MoneyValue = new ArrayList<Double>();
        ArrayList<Integer> IndexValue = new ArrayList<Integer>();
        // Regex Control
        Pattern pattern = Pattern.compile(TotalFilter);
        Matcher matcher = pattern.matcher(valueBefore);
        while(matcher.find()){
            String moneyText = PriceValue_DeleteFirstItem(matcher.group());
            double moneyValue = Double.valueOf(moneyText.replaceAll(",", "."));
            double money = Double.valueOf(String.format(Locale.CANADA,"%.2f", moneyValue));
            MoneyValue.add(money);
            IndexValue.add(matcher.start());
        }
        // Check Values
        if (MoneyValue.size() > 1) {
            Double[] MoneyValue_LastTwo = {MoneyValue.get(MoneyValue.size() - 1), MoneyValue.get(MoneyValue.size() - 2)};
            Integer[] IndexValue_LastTwo = {IndexValue.get(IndexValue.size() - 2)};
            Object[][] MoneyAnd_MoneyIndex = {
                    MoneyValue_LastTwo,
                    IndexValue_LastTwo
            };
            return MoneyAnd_MoneyIndex;
        }else{
            Object[][] MoneyAnd_MoneyIndex = {
                    {0, 0},
                    {0}
            };
            return  MoneyAnd_MoneyIndex;
        }
    }
    // Get Totals After
    private Object[][] GetTotalsAfter(String Value, Integer Index){
        // New Value
        String valueBefore = Value.substring(Index);
        // Create ArrayList
        ArrayList<Double> MoneyValue = new ArrayList<Double>();
        ArrayList<Integer> IndexValue = new ArrayList<Integer>();
        // Regex Control
        Pattern pattern = Pattern.compile(TotalFilter);
        Matcher matcher = pattern.matcher(valueBefore);
        while(matcher.find()){
            String moneyText = PriceValue_DeleteFirstItem(matcher.group());
            double moneyValue = Double.valueOf(moneyText.replaceAll(",", "."));
            double money = Double.valueOf(String.format(Locale.CANADA,"%.2f", moneyValue));
            MoneyValue.add(money);
            IndexValue.add(Index + matcher.start());
        }
        // Check Values
        if (MoneyValue.size() > 1) {
            Double[] MoneyValue_LastTwo = {MoneyValue.get(0), MoneyValue.get(1)};
            Integer[] IndexValue_LastTwo = {IndexValue.get(0)};
            Object[][] MoneyAnd_MoneyIndex = {
                    MoneyValue_LastTwo,
                    IndexValue_LastTwo
            };
            return MoneyAnd_MoneyIndex;
        }else{
            Object[][] MoneyAnd_MoneyIndex = {
                    {0, 0},
                    {0}
            };
            return  MoneyAnd_MoneyIndex;
        }
    }
    /* end Get Values */

    // Learn KDV End Index
    public static int GetKDVIndex(String Value, String Filters){
        int returnEndIndex = 0;
        Filters = Filters.replaceAll(" ", "");
        String DataFilters[] = Filters.split(",");
        for (String Filter: DataFilters) {
            Pattern pattern = Pattern.compile(Filter);
            Matcher matcher = pattern.matcher(Value);
            if (matcher.find()){
                returnEndIndex = matcher.end();
                break;
            }
        }
        return returnEndIndex;
    }

    // Get Max Value
    public double GetMaxValue(Object array[]) {
        ArrayList<Double> list = new ArrayList<Double>();
        for (int i = 0; i < array.length; i++) {
            list.add(Double.valueOf(array[i].toString()));
        }
        return Collections.max(list);
    }
}
/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/
