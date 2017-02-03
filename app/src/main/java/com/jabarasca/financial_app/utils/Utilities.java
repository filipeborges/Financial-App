package com.jabarasca.financial_app.utils;

import android.util.SparseIntArray;

import com.jabarasca.financial_app.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Utilities {

    private static double balanceSignal = 0.0;
    public static final int INCOME_SORT = 1;
    public static final int EXPENSE_SORT = 2;
    public static final int INCOME_EXPENSE_SORT = 3;
    public static final String KEY_INTENT_DAY = "com.jabarasca.financial_app.DAY";
    public static final String KEY_INTENT_MONTH = "com.jabarasca.financial_app.MONTH";
    public static final String KEY_INTENT_YEAR = "com.jabarasca.financial_app.YEAR";
    public static final String KEY_INTENT_COMPARE_DATE = "com.jabarasca.financial_app.COMPARE_DATE";

    public static String formatDbDateFromDatePicker(int datePickerDay, int datePickerMonth, int datePickerYear) {
        String day = String.valueOf(datePickerDay);
        day = datePickerDay < 10 ? "0" + day : day;
        return String.format("%d-%s-%s", datePickerYear,
                Utilities.getDBMonthFromDatePickerMonth(datePickerMonth), day);
    }

    //Returned format: YYYY-MM-DD HH:MM:SS
    public static String formatDbDateWithTimeFromDatePicker(int datePickerDay, int datePickerMonth,
                                                            int datePickerYear) {
        Calendar actualCalendar = Calendar.getInstance();
        int timeField = actualCalendar.get(Calendar.HOUR_OF_DAY);
        String hour = timeField < 10 ? "0" + timeField : String.valueOf(timeField);
        timeField = actualCalendar.get(Calendar.MINUTE);
        String minutes = timeField < 10 ? "0" + timeField : String.valueOf(timeField);
        timeField = actualCalendar.get(Calendar.SECOND);
        String seconds = timeField < 10 ? "0" + timeField : String.valueOf(timeField);
        String date = formatDbDateFromDatePicker(datePickerDay, datePickerMonth, datePickerYear);

        return String.format("%s %s:%s:%s", date, hour, minutes, seconds);
    }

    //Returned format: DD/MM/YYYY HH:MM:SS
    public static String formatHumanDateFromDbDate(String dbDate) {
        String year = dbDate.substring(0,4);
        String month = dbDate.substring(5,7);
        String day = dbDate.substring(8,10);
        String hour = dbDate.substring(11,13);
        String minutes = dbDate.substring(14,16);
        String seconds = dbDate.substring(17,19);

        return String.format("%s/%s/%s %s:%s:%s", day, month, year, hour, minutes, seconds);
    }

    public static String getNowActionBarDate() {
        Calendar actualDate = Calendar.getInstance();
        String actualMonth = Utilities.getActionBarMonthFromDatePickerMonth(actualDate.get(Calendar.MONTH));

        String actualFormattedDate = actualMonth + "/" +
                String.valueOf(actualDate.get(Calendar.YEAR));

        return actualFormattedDate;
    }

    public static long getLongValueFromDBDate(String dbDate) {
        //Only needed the YEAR and MONTH portions of the date.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        try {
            Date date = dateFormat.parse(dbDate);
            return date.getTime();
        } catch (ParseException e) {
            return System.currentTimeMillis();
        }
    }

    public static boolean datePickerDateMatchesActBarDate(int datePickerMonth,
                                                          int datePickerYear,
                                                          String pActBarDate) {
        String actBarDate = String.format("%s/%d",
                getActionBarMonthFromDatePickerMonth(datePickerMonth), datePickerYear);
        return pActBarDate.equals(actBarDate);
    }

    public static String getNowDbDateWithoutTime() {
        Calendar actualDate = Calendar.getInstance();
        return Utilities.formatDbDateFromDatePicker(actualDate.get(Calendar.DAY_OF_MONTH),
                actualDate.get(Calendar.MONTH), actualDate.get(Calendar.YEAR));
    }

    //Return date on format: YYYY-MM-DD
    public static String formatDbDateFromActionBarDate(String actionBarDateText) {
        String defaultDay = "10";
        return String.format("%s-%s-%s",
                actionBarDateText.substring(4),
                Utilities.getDBMonthFromActionBar(actionBarDateText.substring(0,3)),
                defaultDay);
    }

    private static void sortCodListValues(List<String> amountCodValues,
                                          List<String> orderedAllAmountsList,
                                          SparseIntArray codList) {
        String amountToCompare, currentListValue;
        int amountCod;
        codList.clear();
        for(int i = 0; i < amountCodValues.size(); i++) {
            currentListValue = amountCodValues.get(i);
            amountToCompare = currentListValue.substring(0, currentListValue.indexOf("&"));
            amountCod = Integer.parseInt(
                    currentListValue.substring(currentListValue.indexOf("&") + 1,
                                               currentListValue.length())
            );

            for(int a = 0; a < orderedAllAmountsList.size(); a++) {
                currentListValue = orderedAllAmountsList.get(a);
                //Doesnt have the key inserted.
                if(amountToCompare.equals(currentListValue) && codList.indexOfKey(a) < 0) {
                    codList.append(a, amountCod);
                    break;
                }
            }
        }
    }

    public static void sortAllAmountsList(List<String> allAmountsList,
                                          List<String> incomeAmountsList,
                                          List<String> expenseAmountsList,
                                          SparseIntArray codList,
                                          List<String> amountCodValues,
                                          int typeOfSort) {
        Comparator<String> expenseAmountComparator = new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                //For increasing order(negative number): lhs > rhs == -1; lhs == rhs == 0; lhs < rhs == 1;
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

        switch(typeOfSort) {
            case INCOME_SORT:
                Collections.sort(incomeAmountsList, incomeAmountComparator);
                break;
            case EXPENSE_SORT:
                Collections.sort(expenseAmountsList, expenseAmountComparator);
                break;
            default:
                Collections.sort(incomeAmountsList, incomeAmountComparator);
                Collections.sort(expenseAmountsList, expenseAmountComparator);
                break;
        }

        allAmountsList.clear();
        allAmountsList.addAll(incomeAmountsList);
        allAmountsList.addAll(expenseAmountsList);
        sortCodListValues(amountCodValues, allAmountsList, codList);
    }

//    TODO: Parameter outOfBoundsLabel never used
    public static String sumIncomeExpenseItems(List<String> allAmountsListItems, String outOfBoundsLabel) {
        final double INVALID_SIGNAL = 99.99;
        double totalSum = 0.0;
        if(!allAmountsListItems.isEmpty()) {
            for(int i = 0; i < allAmountsListItems.size(); i++) {
                totalSum += Double.parseDouble(allAmountsListItems.get(i).replace(",","."));
            }
        }

        if(String.valueOf(totalSum).matches("\\-?[0-9]{1,7}\\.[0-9]+")) {
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
                return R.drawable.graphic_infinite;
        }
    }

    public static String getDBMonthFromDatePickerMonth(int month) {
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

    private static String getDBMonthFromActionBar(String actionBarMonth) {
        if(actionBarMonth.equals("Jan")) {
            return "01";
        } else if(actionBarMonth.equals("Fev")) {
            return "02";
        } else if(actionBarMonth.equals("Mar")) {
            return "03";
        } else if(actionBarMonth.equals("Abr")) {
            return "04";
        } else if(actionBarMonth.equals("Mai")) {
            return "05";
        } else if(actionBarMonth.equals("Jun")) {
            return "06";
        } else if(actionBarMonth.equals("Jul")) {
            return "07";
        } else if(actionBarMonth.equals("Ago")) {
            return "08";
        } else if(actionBarMonth.equals("Set")) {
            return "09";
        } else if(actionBarMonth.equals("Out")) {
            return "10";
        } else if(actionBarMonth.equals("Nov")) {
            return "11";
        } else if(actionBarMonth.equals("Dez")) {
            return "12";
        } else {
            return actionBarMonth;
        }
    }

    public static String getActionBarMonthFromDatePickerMonth(int month) {
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
