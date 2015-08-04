package com.jabarasca.financial_app.test;

import android.app.Activity;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityUnitTestCase;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.jabarasca.financial_app.MainActivity;
import com.jabarasca.financial_app.R;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TestMainActivity extends ActivityUnitTestCase<MainActivity> {

    private MainActivity activity;
    private Intent intent;

    public TestMainActivity() {
        super(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        //This was previously injected by the platforms old InstrumentationTestRunner and now need to be done manually.
        //The instrumentation needs to be the first instruction on the setUp() method.
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        super.setUp();
        intent = new Intent(getInstrumentation().getTargetContext(), MainActivity.class);
    }

    @Test
    public void testOnCreate() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                //This method need to be run in the UiThread.
                activity = startActivity(intent, null, null);
            }
        });

        Assert.assertNotNull("\"addAmountDialogListener\" is null.", activity.addAmountDialogListener);
        Assert.assertNotNull("\"addMenuItemListener\" is null.", activity.addMenuItemListener);
        Assert.assertNotNull("\"inflater\" is null.", activity.inflater);
        Assert.assertNotNull("\"drawerLayout\" is null.", activity.drawerLayout);
        Assert.assertNotNull("\"rightDrawerListView\" is null.", activity.rightDrawerListView);
        Assert.assertNotNull("\"graphicBalanceImgView\" is null.", activity.graphicBalanceImgView);
        Assert.assertNotNull("\"actionBarDrawerToggle\" is null.", activity.actionBarDrawerToggle);
        Assert.assertNotNull("\"actionBarTextView\" is null.", activity.actionBarTextView);
        Assert.assertNotNull("\"amountSumTextView\" is null.", activity.amountSumTextView);
    }

    @Test
    public void testOnCreateOptionsMenu() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                //This method need to be run in the UiThread.
                activity = startActivity(intent, null, null);
            }
        });
        PopupMenu popupMenu = new PopupMenu(getInstrumentation().getTargetContext(), null);
        activity.onCreateOptionsMenu(popupMenu.getMenu());

        Assert.assertNotNull("\"menu\" is null.", activity.menu);
    }

    @Test
    public void testSetAmountListViewItems() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                //This method need to be run in the UiThread.
                activity = startActivity(intent, null, null);
            }
        });

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}
        };
        activity.setAmountListViewItems(R.id.amountsListView, activity.allAmountsList, R.layout.amount_list_view_item_layout,
                R.id.amountItemTextView, itemClickListener);

        ListView listView = (ListView)activity.findViewById(R.id.amountsListView);
        Assert.assertEquals("\"OnItemClickListener\" is not setted.", itemClickListener, listView.getOnItemClickListener());
        Assert.assertNotNull("\"ArrayAdapter\" is not setted.", listView.getAdapter());

    }

    @Test
    public void testSetAddMenuListViewItems() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                //This method need to be run in the UiThread.
                activity = startActivity(intent, null, null);
            }
        });

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}
        };

        List<String> addMenuOptionsList = new ArrayList<String>();
        addMenuOptionsList.add("Haha");
        addMenuOptionsList.add("Kaka");

        activity.setAddMenuListViewItems(R.id.activityMainRightDrawerListView, addMenuOptionsList,
                R.layout.add_menu_item_layout, R.id.addMenuItemTextView, itemClickListener);

        ListView listView = (ListView)activity.findViewById(R.id.activityMainRightDrawerListView);
        Assert.assertEquals("\"OnItemClickListener\" is not setted.", itemClickListener, listView.getOnItemClickListener());
        Assert.assertNotNull("\"ArrayAdapter\" is not setted.", listView.getAdapter());

    }

    //ActivityUnitTestCase.startActivity calls setActivity prior to dispatching onCreate.
    //AppCompatActivity needs Theme.AppCompat theme.
    @Override
    protected void setActivity(Activity testActivity) {
        if (testActivity != null) {
            testActivity.setTheme(R.style.AppCustomTheme);
        }
        super.setActivity(testActivity);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
