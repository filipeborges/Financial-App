package com.jabarasca.financial_app.utils;

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

    public static void setSortedAmountsList(List<String> allAmountsList, List<String> incomeAmountsList, List<String> expenseAmountsList,
                                            boolean isIncomeSorting) {
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

        if(isIncomeSorting) {
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

    public static String sumIncomeExpenseItems(List<String> allAmountsListItems, String outOfBoundsLabel) {
        final double INVALID_SIGNAL = 99.99;
        double totalSum = 0.0;
        if(!allAmountsListItems.isEmpty()) {
            for(int i = 0; i < allAmountsListItems.size(); i++) {
                totalSum += Double.parseDouble(allAmountsListItems.get(i).replace(",","."));
            }
        }

        //TODO: Improve this regex better, if possible, to detect more special cases.
        if(String.valueOf(totalSum).matches("\\-?[0-9]{1,6}\\.[0-9]+")) {
            balanceSignal = Math.signum(totalSum);

            if (totalSum > 0.0) {
                return "+" + String.format("%.2f", totalSum);
            } else {
                return String.format("%.2f", totalSum);
            }
        } else {
            //Value to enter on default on getBalanceGraphicResourceId().
            balanceSignal = INVALID_SIGNAL;
            return outOfBoundsLabel;
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
                //TODO: Return image for infinite value.
                return -1;
        }
    }

    private static String getFormattedMonth(int monthNumber) {
           switch (monthNumber) {
               case Calendar.JANUARY:
                   return "Jan";
               case Calendar.FEBRUARY:
                   return "Fev";
               case Calendar.MARCH:
                   return "Mar";
               case Calendar.APRIL:
                   return "Abr";
               case Calendar.MAY:
                   return "Mai";
               case Calendar.JUNE:
                   return "Jun";
               case Calendar.JULY:
                   return "Jul";
               case Calendar.AUGUST:
                   return "Ago";
               case Calendar.SEPTEMBER:
                   return "Set";
               case Calendar.OCTOBER:
                   return "Out";
               case Calendar.NOVEMBER:
                   return "Nov";
               case Calendar.DECEMBER:
                   return "Dez";
               default:
                   return "Inválido";
           }
    }
}
