package com.jabarasca.financial_app.utils;

import java.util.Calendar;

/**
 * Created by filipe on 16/06/15.
 */
public class Utilities {

    public static String getFormattedActualDate() {
        Calendar actualDate = Calendar.getInstance();
        String actualMonth = Utilities.getFormattedMonth(actualDate.get(Calendar.MONTH));

        String actualFormattedDate = actualMonth + "/" +
                String.valueOf(actualDate.get(Calendar.YEAR));

        return actualFormattedDate;
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
                   return "Invalido";
           }
    }
}
