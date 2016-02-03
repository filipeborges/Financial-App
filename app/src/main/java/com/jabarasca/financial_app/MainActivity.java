package com.jabarasca.financial_app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.jabarasca.financial_app.dao.DatabaseAccess;
import com.jabarasca.financial_app.utils.SwipeDismissListViewTouchListener;
import com.jabarasca.financial_app.utils.Utilities;

//Obs: Normal actionBar from Activity doesnt show hamburguer icon.
public class MainActivity extends AppCompatActivity {

    public ActionBarDrawerToggle actionBarDrawerToggle;
    public View rightDrawerListView;
    public DrawerLayout drawerLayout;
    public Menu menu;
    public ImageView graphicBalanceImgView;
    public TextView actionBarTextView;
    public TextView amountSumTextView;
    public String actionBarFormattedDate = Utilities.getActionBarFormattedActualDate();
    public LayoutInflater inflater;
    public List<String> expenseAmountsList = new ArrayList<String>();
    public List<String> incomeAmountsList = new ArrayList<String>();
    public List<String> allAmountsList = new ArrayList<String>();
    public String OUT_OF_BOUNDS_LABEL = null;
    private final int EXPENSE_LISTVIEW_POSITION = 0;
    private final int INCOME_LISTVIEW_POSITION = 1;
    private DatabaseAccess dbAccess;

    public DialogInterface.OnClickListener addAmountDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            EditText amountEditText = (EditText) ((Dialog) dialog).findViewById(R.id.addAmountPopupEditText);

            if (!amountEditText.getText().toString().equals("") &&
                    !amountSumTextView.getText().toString().equals(OUT_OF_BOUNDS_LABEL)) {
                //"alertTitle" -> name, "id" -> defType, "android" -> package.
                TextView alertDialogTitle = (TextView)((Dialog) dialog).findViewById(getResources().getIdentifier("alertTitle", "id", "android"));
                double amount;

                //If its a income amount.
                if(alertDialogTitle.getText().equals(getString(R.string.income_title))) {
                    amount = Double.parseDouble(amountEditText.getText().toString());
                    String amountString = String.format("+%.2f", amount);
                    dbAccess.saveAmount(amountString, Utilities.getDBFormattedActualDate());
                    incomeAmountsList.add(amountString);
                    Utilities.sortAllAmountsList(allAmountsList, incomeAmountsList,
                            expenseAmountsList, Utilities.INCOME_SORT);
                } else {
                    amount = Double.parseDouble(amountEditText.getText().toString()) * -1;
                    String amountString = String.format("%.2f", amount);
                    dbAccess.saveAmount(amountString, Utilities.getDBFormattedActualDate());
                    expenseAmountsList.add(amountString);
                    Utilities.sortAllAmountsList(allAmountsList, incomeAmountsList,
                            expenseAmountsList, Utilities.EXPENSE_SORT);
                }

                ListView listView = (ListView) findViewById(R.id.amountsListView);
                ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
                refreshGraphicAndAmountSum(allAmountsList);
            }
            drawerLayout.closeDrawers();
        }
    };

    public AdapterView.OnItemClickListener addMenuItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //AlertDialog.Builder constructor must use Activity reference instead of Context.
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            String title;

            switch (position) {
                case EXPENSE_LISTVIEW_POSITION:
                    title = getString(R.string.expense_title);
                    break;
                case INCOME_LISTVIEW_POSITION:
                    title = getString(R.string.income_title);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid ListView position: "+position);
            }

            alertDialog.setNegativeButton(getResources().getString(R.string.negative_button_message), addAmountDialogListener);
            alertDialog.setPositiveButton(getResources().getString(R.string.positive_button_message), addAmountDialogListener);
            alertDialog.setTitle(title);
            alertDialog.setMessage(getString(R.string.income_expense_popup_message));
            alertDialog.setView(inflater.inflate(R.layout.add_amount_popup_layout, null));
            alertDialog.show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        OUT_OF_BOUNDS_LABEL = getString(R.string.out_of_bounds_label);
        inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        drawerLayout = (DrawerLayout)findViewById(R.id.activityMainDrawerLay);
        rightDrawerListView = findViewById(R.id.activityMainRightDrawerListView);
        graphicBalanceImgView = (ImageView)findViewById(R.id.bottomBarGraphicImgView);

        actionBarDrawerToggle = getActionBarDrawerToogle();
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        setSwipeToDismissAmountsListView(R.id.amountsListView);

        getSupportActionBar().setCustomView(R.layout.action_bar_text_layout);
        actionBarTextView = (TextView)findViewById(R.id.actionBarTextView);
        actionBarTextView.setText(actionBarFormattedDate);

        amountSumTextView = (TextView)findViewById(R.id.amountSumTextView);
        amountSumTextView.setText(Utilities.sumIncomeExpenseItems(allAmountsList,
                OUT_OF_BOUNDS_LABEL));

        setAmountListViewItems(R.id.amountsListView, allAmountsList,
                R.layout.amount_list_view_item_layout, R.id.amountItemTextView);

        configureAddMenu();
    }

    @Override
    protected void onResume() {
        dbAccess = new DatabaseAccess(getApplicationContext());
        setAmountsSaved();
        super.onResume();
    }

    //Caution: This method needs to run very quickly.
    @Override
    protected void onPause() {
        dbAccess.closeDatabase();
        super.onPause();
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

    public void refreshGraphicAndAmountSum(List<String> allAmountsList) {
        amountSumTextView.setText(Utilities.sumIncomeExpenseItems(allAmountsList, OUT_OF_BOUNDS_LABEL));
        graphicBalanceImgView.setImageResource(Utilities.getBalanceGraphicResourceId());
    }

    public ActionBarDrawerToggle getActionBarDrawerToogle() {
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
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

        return actionBarDrawerToggle;
    }

    public void configureAddMenu() {
        List<String> addMenuOptionsList = new ArrayList<String>();
        addMenuOptionsList.add(getString(R.string.add_menu_option_1));
        addMenuOptionsList.add(getString(R.string.add_menu_option_2));

        setAddMenuListViewItems(R.id.activityMainRightDrawerListView, addMenuOptionsList,
                R.layout.add_menu_item_layout, R.id.addMenuItemTextView, addMenuItemListener);
    }

    public void setAmountsSaved() {
        List<String> queryAmountsList = dbAccess.getSpecificDateAmounts(Utilities.getDBFormattedActualDate());

        if(queryAmountsList.size() > 0) {
            for(int i = 0; i < queryAmountsList.size(); i++) {
                String amount = queryAmountsList.get(i);
                //Brazilian float number.
                if(Double.parseDouble(amount.replace(",",".")) > 0.0) {
                    incomeAmountsList.add(amount);
                } else {
                    expenseAmountsList.add(amount);
                }
            }
            Utilities.sortAllAmountsList(allAmountsList, incomeAmountsList,
                    expenseAmountsList, Utilities.INCOME_EXPENSE_SORT);
            ListView amountsListView = (ListView)findViewById(R.id.amountsListView);
            ((ArrayAdapter)amountsListView.getAdapter()).notifyDataSetChanged();
        }
        refreshGraphicAndAmountSum(allAmountsList);
    }

    public void setAmountListViewItems(int listViewId, List<String> listViewItemsStrings, int listItemLayoutId,
                                         int listItemTextViewId) {
        ListView listView = (ListView)findViewById(listViewId);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                listItemLayoutId, listItemTextViewId, listViewItemsStrings) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null) {
                    LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.amount_list_view_item_layout, parent, false);
                }
                int incomeElementsMaxPosition = allAmountsList.size() - expenseAmountsList.size();

                if(position < incomeElementsMaxPosition) {
                    ((TextView)((RelativeLayout) convertView).getChildAt(0)).setTextColor(getResources().getColor(R.color.income_amount_color));
                } else {
                    ((TextView)((RelativeLayout) convertView).getChildAt(0)).setTextColor(getResources().getColor(R.color.expense_amount_color));
                }
                return super.getView(position, convertView, parent);
            }
        };
        listView.setAdapter(adapter);
    }

    public void setAddMenuListViewItems(int listViewId, List<String> listViewItemsStrings, final int listItemLayoutId,
                                         int listItemTextViewId, AdapterView.OnItemClickListener itemListener) {

        ListView listView = (ListView)findViewById(listViewId);
        listView.setOnItemClickListener(itemListener);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                listItemLayoutId, listItemTextViewId, listViewItemsStrings) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null) {
                    LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(listItemLayoutId, parent, false);
                }
                switch (position) {
                    case EXPENSE_LISTVIEW_POSITION:
                        ((ImageView)((RelativeLayout)convertView).getChildAt(0)).setImageResource(R.drawable.negative_plus);
                        break;
                    case INCOME_LISTVIEW_POSITION:
                        ((ImageView)((RelativeLayout)convertView).getChildAt(0)).setImageResource(R.drawable.plus);
                        break;
                }
                return super.getView(position, convertView, parent);
            }
        };
        listView.setAdapter(adapter);
    }

    private void setSwipeToDismissAmountsListView(int listViewId) {
        ListView listView = (ListView)findViewById(listViewId);

        SwipeDismissListViewTouchListener swipeToDismissListViewListener = new SwipeDismissListViewTouchListener(listView,
                new SwipeDismissListViewTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        return true;
                    }

                    @Override
                    public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                        int incomeElementsMaxPosition = allAmountsList.size() - expenseAmountsList.size();
                        String amountToRemove = allAmountsList.get(reverseSortedPositions[0]);
                        dbAccess.removeAmount(amountToRemove, Utilities.getDBFormattedActualDate());

                        //If the element dismissed is Income.
                        if(reverseSortedPositions[0] < incomeElementsMaxPosition) {
                            incomeAmountsList.remove(amountToRemove);
                        } else {
                            expenseAmountsList.remove(amountToRemove);
                        }
                        allAmountsList.remove(reverseSortedPositions[0]);
                        ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
                        refreshGraphicAndAmountSum(allAmountsList);
                    }
                });

        listView.setOnTouchListener(swipeToDismissListViewListener);
        listView.setOnScrollListener(swipeToDismissListViewListener.makeScrollListener());
    }
}