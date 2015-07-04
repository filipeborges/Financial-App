package com.jabarasca.financial_app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.jabarasca.financial_app.utils.SwipeDismissListViewTouchListener;
import com.jabarasca.financial_app.utils.Utilities;

//Obs: Normal actionBar from Activity doesnt show hamburguer icon.
public class MainActivity extends AppCompatActivity {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private View rightDrawerListView;
    private DrawerLayout drawerLayout;
    private Menu menu;
    private TextView actionBarTextView;
    private String actionBarFormattedDate = Utilities.getFormattedActualDate();
    private LayoutInflater inflater;
    private List<String> expenseAmountsList = new ArrayList<String>();

    private Comparator<String> expenseAmountComparator = new Comparator<String>() {
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

    private DialogInterface.OnClickListener addExpenseDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            EditText expenseAmountEditText = (EditText) ((Dialog) dialog).findViewById(R.id.addExpensePopupEditText);

            if (!expenseAmountEditText.getText().toString().equals("")) {
                double expenseAmout = Double.parseDouble(expenseAmountEditText.getText().toString()) * -1;
                expenseAmountsList.add(String.format("%.2f", expenseAmout));

                //Not first expense added.
                if (expenseAmountsList.size() > 1) {
                    Collections.sort(expenseAmountsList, expenseAmountComparator);
                    ListView listView = (ListView) findViewById(R.id.amountsListView);
                    ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
                } else {
                    Utilities.setListViewItems(MainActivity.this, R.id.amountsListView, expenseAmountsList,
                            R.layout.expense_amount_list_view_item_layout, R.id.expenseAmountItemTextView, null);
                }

                drawerLayout.closeDrawers();
            } else {
                drawerLayout.closeDrawers();
            }
        }
    };

    private AdapterView.OnItemClickListener addMenuItemListener = new AdapterView.OnItemClickListener() {
        private final int EXPENSE = 0;

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case EXPENSE:
                    //AlertDialog.Builder constructor must use Activity reference instead of Context.
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setTitle(getResources().getString(R.string.expense_title));
                    alertDialog.setMessage(getResources().getString(R.string.expense_message));
                    alertDialog.setNegativeButton(getResources().getString(R.string.expense_negative_button_message), addExpenseDialogListener);
                    alertDialog.setPositiveButton(getResources().getString(R.string.expense_positive_button_message), addExpenseDialogListener);
                    alertDialog.setView(inflater.inflate(R.layout.add_expense_amount_popup_layout, null));
                    alertDialog.show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        drawerLayout = (DrawerLayout)findViewById(R.id.activityMainDrawerLay);
        rightDrawerListView = findViewById(R.id.activityMainRightDrawerListView);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.open_drawer, R.string.close_drawer) {

            @Override
            public void onDrawerSlide (View drawerView, float slideOffset) {
                if(drawerView.getId() == R.id.activityMainRightDrawerListView) {
                    if(slideOffset > 0.1) {
                        menu.findItem(R.id.addButton).setIcon(R.drawable.minus);
                        actionBarTextView.setText(getString(R.string.add_menu_action_bar_title));
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    }
                    else if(slideOffset <= 0.1) {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        menu.findItem(R.id.addButton).setIcon(R.drawable.plus);
                        actionBarTextView.setText(actionBarFormattedDate);
                    }
                }
                else {
                    if(slideOffset > 0.1) {
                        menu.findItem(R.id.addButton).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                        actionBarTextView.setText(getString(R.string.menu_action_bar_title));
                    }
                    else if(slideOffset <= 0.1) {
                        menu.findItem(R.id.addButton).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                        actionBarTextView.setText(actionBarFormattedDate);
                    }
                }

                super.onDrawerSlide(drawerView, slideOffset);
            }
        };

        setSwipeToDismissListView(R.id.amountsListView);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        getSupportActionBar().setCustomView(R.layout.action_bar_text_layout);
        actionBarTextView = (TextView)findViewById(R.id.actionBarTextView);
        actionBarTextView.setText(actionBarFormattedDate);

        List<String> addMenuOptionsList = new ArrayList<String>();
        addMenuOptionsList.add(getString(R.string.add_menu_option_1));
        addMenuOptionsList.add(getString(R.string.add_menu_option_2));

        Utilities.setListViewItems(this, R.id.activityMainRightDrawerListView, addMenuOptionsList,
                R.layout.add_menu_item_layout, R.id.addMenuItemTextView, addMenuItemListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_action_bar_items, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event.
        if(actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        else if(item.getItemId() == R.id.addButton) {
            if(!drawerLayout.isDrawerOpen(rightDrawerListView)) {
                drawerLayout.openDrawer(rightDrawerListView);
            } else {
                drawerLayout.closeDrawer(rightDrawerListView);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void setSwipeToDismissListView(int listViewId) {
        ListView listView = (ListView)findViewById(listViewId);

        SwipeDismissListViewTouchListener swipeToDismissListViewListener = new SwipeDismissListViewTouchListener(listView,
                new SwipeDismissListViewTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        return true;
                    }

                    @Override
                    public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                        expenseAmountsList.remove(reverseSortedPositions[0]);
                        ((ArrayAdapter)listView.getAdapter()).notifyDataSetChanged();
                    }
                });

        listView.setOnTouchListener(swipeToDismissListViewListener);
        listView.setOnScrollListener(swipeToDismissListViewListener.makeScrollListener());
    }

}
