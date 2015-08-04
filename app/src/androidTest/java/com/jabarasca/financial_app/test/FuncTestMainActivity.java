package com.jabarasca.financial_app.test;

import junit.framework.Assert;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jabarasca.financial_app.MainActivity;
import com.jabarasca.financial_app.R;
import com.jabarasca.financial_app.utils.Utilities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

//OBS: Tests that uses ActivityInstrumentationTestCase2 are Functional Tests.
//OBS2: JUnit4 @Test annotation doesnt work with Gradle directly. Works only with Android Studio.
@RunWith(AndroidJUnit4.class)
public class FuncTestMainActivity extends ActivityInstrumentationTestCase2<MainActivity>{

    private MainActivity activity;

    public FuncTestMainActivity() {
        super(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        //This was previously injected by the platforms old InstrumentationTestRunner and now need to be done manually.
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        activity = getActivity();
    }

    @Test
    public void testInitialState() {
        ImageView bottomBarGraphicImgView = (ImageView)activity.findViewById(R.id.bottomBarGraphicImgView);
        Assert.assertTrue("Initial image graphic should be: \"graphic_neutral\".",
                bottomBarGraphicImgView.getDrawable().getConstantState().equals(activity.getResources().
                        getDrawable(R.drawable.graphic_neutral).getConstantState()));

        TextView initialBalanceTextView = (TextView)activity.findViewById(R.id.amountSumTextView);
        String initialBalance = initialBalanceTextView.getText().toString();
        if(initialBalance.contains(",")) {
            initialBalance = initialBalance.replace(",", ".");
        }
        Assert.assertEquals("Initial Balance should be: 0.00", initialBalance, "0.00");

        ListView amountsListView = (ListView)activity.findViewById(R.id.amountsListView);
        Assert.assertEquals("Amounts List View should not contain any amount.", 0, amountsListView.getCount());

        ViewGroup linearLayout = (ViewGroup)activity.getSupportActionBar().getCustomView();
        TextView actionBarDate = (TextView)linearLayout.getChildAt(0);
        Assert.assertEquals("ActionBar Date is not setted with the actual date.", Utilities.getFormattedActualDate(),
                actionBarDate.getText().toString());

        MenuItem addButton = activity.menu.findItem(R.id.addButton);
        Assert.assertTrue("Add button image should be: \"plus\".", addButton.getIcon().getConstantState().equals(
            activity.getResources().getDrawable(R.drawable.plus).getConstantState()));
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }
}