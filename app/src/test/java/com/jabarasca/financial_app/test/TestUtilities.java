package com.jabarasca.financial_app.test;

import com.jabarasca.financial_app.utils.Utilities;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TestUtilities {
    private List<String> allAmountsList = new ArrayList<String>();
    private List<String> incomeAmountsList = new ArrayList<String>();
    private List<String> expenseAmountsList = new ArrayList<String>();

    private void setupTestSortAllAmountsList() {
        allAmountsList.clear();
        allAmountsList.add("+98.28");
        allAmountsList.add("-123.37");

        incomeAmountsList.clear();
        incomeAmountsList.add("+98.28");

        expenseAmountsList.clear();
        expenseAmountsList.add("-123.37");
    }

    @Test
    public void testSortAllAmountsList() {
        setupTestSortAllAmountsList();
        incomeAmountsList.add("+234.00");
        Utilities.sortAllAmountsList(allAmountsList, incomeAmountsList, expenseAmountsList, true);
        Assert.assertEquals(3, allAmountsList.size());
        Assert.assertEquals("+234.00", allAmountsList.get(0));
        Assert.assertEquals("+98.28", allAmountsList.get(1));
        Assert.assertEquals("-123.37", allAmountsList.get(2));

        setupTestSortAllAmountsList();
        expenseAmountsList.add("-98.11");
        Utilities.sortAllAmountsList(allAmountsList, incomeAmountsList, expenseAmountsList, false);
        Assert.assertEquals(3, allAmountsList.size());
        Assert.assertEquals("+98.28", allAmountsList.get(0));
        Assert.assertEquals("-123.37", allAmountsList.get(1));
        Assert.assertEquals("-98.11", allAmountsList.get(2));

        setupTestSortAllAmountsList();
        incomeAmountsList.add("+234.00");
        expenseAmountsList.clear();
        Utilities.sortAllAmountsList(allAmountsList, incomeAmountsList, expenseAmountsList, true);
        Assert.assertEquals(2, allAmountsList.size());
        Assert.assertEquals("+234.00", allAmountsList.get(0));
        Assert.assertEquals("+98.28", allAmountsList.get(1));

        setupTestSortAllAmountsList();
        expenseAmountsList.add("-98.11");
        incomeAmountsList.clear();
        Utilities.sortAllAmountsList(allAmountsList, incomeAmountsList, expenseAmountsList, false);
        Assert.assertEquals(2, allAmountsList.size());
        Assert.assertEquals("-123.37", allAmountsList.get(0));
        Assert.assertEquals("-98.11", allAmountsList.get(1));

        setupTestSortAllAmountsList();
        incomeAmountsList.add("+234.00");
        allAmountsList.clear();
        Utilities.sortAllAmountsList(allAmountsList, incomeAmountsList, expenseAmountsList, true);
        Assert.assertEquals(3, allAmountsList.size());
        Assert.assertEquals("+234.00", allAmountsList.get(0));
        Assert.assertEquals("+98.28", allAmountsList.get(1));
        Assert.assertEquals("-123.37", allAmountsList.get(2));

        setupTestSortAllAmountsList();
        expenseAmountsList.add("-98.11");
        allAmountsList.clear();
        Utilities.sortAllAmountsList(allAmountsList, incomeAmountsList, expenseAmountsList, false);
        Assert.assertEquals(3, allAmountsList.size());
        Assert.assertEquals("+98.28", allAmountsList.get(0));
        Assert.assertEquals("-123.37", allAmountsList.get(1));
        Assert.assertEquals("-98.11", allAmountsList.get(2));
    }

    @Test
    public void testSumIncomeExpenseItems() {
        List<String> stringNumbers = new ArrayList<String>();
        stringNumbers.add("25.00");
        stringNumbers.add("-19.00456");
        stringNumbers.add("4.340");
        stringNumbers.add("-100.00");
        stringNumbers.add("234.190");

        String result = null;
        result = Utilities.sumIncomeExpenseItems(stringNumbers);
        Assert.assertNotNull(result);

        stringNumbers.clear();
        result = Utilities.sumIncomeExpenseItems(stringNumbers);
        Assert.assertNotNull(result);
        if(result.contains(",")) {
            result = result.replace(",", ".");
        }
        double returnedValue = Double.parseDouble(result);
        result = String.format(Locale.US, "%.2f", returnedValue);
        Assert.assertEquals(String.format(Locale.US, "%.2f", 0.0), result);
    }
}
