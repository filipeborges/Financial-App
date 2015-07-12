package com.jabarasca.financial_app.utils;

import android.app.Activity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jabarasca.financial_app.R;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Utilities {

    private static double balanceSignal = 0.0;

    public static String getFormattedActualDate() {
        Calendar actualDate = Calendar.getInstance();
        String actualMonth = Utilities.getFormattedMonth(actualDate.get(Calendar.MONTH));

        String actualFormattedDate = actualMonth + "/" +
                String.valueOf(actualDate.get(Calendar.YEAR));

        return actualFormattedDate;
    }

    public static void setListViewItems(Activity activity, int listViewId, List<String> listViewItemsStrings, int listItemLayoutId,
                                        int listItemTextViewId, AdapterView.OnItemClickListener itemListener) {

        ListView listView = (ListView)activity.findViewById(listViewId);
        listView.setOnItemClickListener(itemListener);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity.getApplicationContext(),
                listItemLayoutId, listItemTextViewId, listViewItemsStrings);

        listView.setAdapter(adapter);
    }

    public static void sortAllAmountsList(List<String> allAmountsList, List<String> incomeAmountsList, List<String> expenseAmountsList,
                                                boolean isIncomeElement) {
        Comparator<String> expenseAmountComparator = new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                //For incresing order(negative number): lhs > rhs == -1; lhs == rhs == 0; lhs < rhs == 1;
                //Number will come with ',' for decimal separator (Brazilian Device). Need to replace ',' to '.' for parseFloat() to works.
                if(lhs.contains(",") && rhs.contains(",")) {
                    lhs = lhs.replace(',','.');
                    rhs = rhs.replace(',','.');
                }
                return (int)Math.signum(Float.parseFloat(lhs) - Float.parseFloat(rhs));
            }
        };

        Comparator<String> incomeAmountComparator = new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                if(lhs.contains(",") && rhs.contains(",")) {
                    lhs = lhs.replace(',','.');
                    rhs = rhs.replace(',','.');
                }
                return (int)Math.signum(Float.parseFloat(rhs) - Float.parseFloat(lhs));
            }
        };

        if(isIncomeElement) {
            Collections.sort(incomeAmountsList, incomeAmountComparator);
        } else {
            Collections.sort(expenseAmountsList, expenseAmountComparator);
        }

        if(!allAmountsList.isEmpty()) {
            allAmountsList.clear();
        }
        allAmountsList.addAll(incomeAmountsList);
        allAmountsList.addAll(expenseAmountsList);
    }

    public static String sumIncomeExpenseItems(List<String> allAmountsListItems) {
        double totalSum = 0.0;
        if(!allAmountsListItems.isEmpty()) {
            for(int i = 0; i < allAmountsListItems.size(); i++) {
                totalSum += Double.parseDouble(allAmountsListItems.get(i).replace(",","."));
            }
        }
        balanceSignal = Math.signum(totalSum);

        if(totalSum > 0) {
            return "+"+String.format("%.2f", totalSum);
        } else {
            return String.format("%.2f", totalSum);
        }
    }

    //Needs to call sumIncomeExpenseItems() first, to update the balanceSignal.
    public static int getBalanceGraphicResourceId() {
        switch((int)balanceSignal) {
            case 0:
                return R.drawable.graphic_neutral;
            case 1:
                return R.drawable.graphic_up;
            case -1:
                return R.drawable.graphic_down;
            default:
                return -1;
        }
    }

    private static String getFormattedMonth(int monthNumber) {
           switch (monthNumber) {
               case 1:
                   return "Jan";
               case 2:
                   return "Fev";
               case 3:
                   return "Mar";
               case 4:
                   return "Abr";
               case 5:
                   return "Mai";
               case 6:
                   return "Jun";
               case 7:
                   return "Jul";
               case 8:
                   return "Ago";
               case 9:
                   return "Set";
               case 10:
                   return "Out";
               case 11:
                   return "Nov";
               case 12:
                   return "Dez";
               default:
                   return "Inválido";
           }
    }
}
