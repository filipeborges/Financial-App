package com.jabarasca.financial_app.utils;

import android.app.Activity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Calendar;
import java.util.List;

public class Utilities {

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
