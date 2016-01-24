package com.jabarasca.financial_app.utils;

import com.jabarasca.financial_app.R;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Utilities {

    private static double balanceSignal = 0.0;

    //TODO: Refactor this method name to reflect actionbar date.
    public static String getFormattedActualDate() {
        Calendar actualDate = Calendar.getInstance();
        String actualMonth = Utilities.getFormattedMonth(actualDate.get(Calendar.MONTH));

        String actualFormattedDate = actualMonth + "/" +
                String.valueOf(actualDate.get(Calendar.YEAR));

        return actualFormattedDate;
    }

    //TODO: Refactor this method name to a more generic name.
    public static String getSaveDateFormatted() {
        Calendar actualDate = Calendar.getInstance();

        String day = String.valueOf(actualDate.get(Calendar.DAY_OF_MONTH));
        String month = Utilities.getSaveFormattedMonth(actualDate.get(Calendar.MONTH));
        String year = String.valueOf(actualDate.get(Calendar.YEAR));

        return year + "-" + month + "-" + day;
    }

    //TODO: Refactor this method.
    public static void setSortedAmountsList(List<String> allAmountsList, List<String> incomeAmountsList, List<String> expenseAmountsList,
                                            boolean isIncomeSorting, boolean isSortAll) {
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
        } else if(isSortAll) {
            Collections.sort(incomeAmountsList, incomeAmountComparator);
            Collections.sort(expenseAmountsList, expenseAmountComparator);
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

    private static String getSaveFormattedMonth(int month) {
        switch (month) {
            case Calendar.JANUARY:
                return "01";
            case Calendar.FEBRUARY:
                return "02";
            case Calendar.MARCH:
                return "03";
            case Calendar.APRIL:
                return "04";
            case Calendar.MAY:
                return "05";
            case Calendar.JUNE:
                return "06";
            case Calendar.JULY:
                return "07";
            case Calendar.AUGUST:
                return "08";
            case Calendar.SEPTEMBER:
                return "09";
            case Calendar.OCTOBER:
                return "10";
            case Calendar.NOVEMBER:
                return "11";
            case Calendar.DECEMBER:
                return "12";
            default:
                return "Inválido";
        }
    }

    //TODO: Refactor this method name to reflect the actionbar date.
    private static String getFormattedMonth(int month) {
           switch (month) {
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
