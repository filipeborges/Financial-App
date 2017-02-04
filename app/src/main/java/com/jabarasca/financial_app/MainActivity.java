package com.jabarasca.financial_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.util.Xml;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jabarasca.financial_app.dao.DatabaseAccess;
import com.jabarasca.financial_app.utils.SwipeDismissListViewTouchListener;
import com.jabarasca.financial_app.utils.Utilities;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

//Obs: Normal actionBar from Activity doesnt show hamburguer icon.
public class  MainActivity extends AppCompatActivity {

    public ActionBarDrawerToggle actionBarDrawerToggle;
    public View rightDrawerListView;
    public DrawerLayout drawerLayout;
    public Menu menu;
    public ImageView graphicBalanceImgView;
    public TextView actionBarTextView;
    public TextView amountSumTextView;
    public String actionBarFormattedDate = Utilities.getNowActionBarDate();
    public LayoutInflater inflater;
    public List<String> expenseAmountsList = new ArrayList<String>();
    public List<String> incomeAmountsList = new ArrayList<String>();
    public List<String> allAmountsList = new ArrayList<String>();
    public SparseIntArray codList = new SparseIntArray();
    public String OUT_OF_BOUNDS_LABEL = null;
    private final int EXPENSE_LISTVIEW_POSITION = 0, INCOME_LISTVIEW_POSITION = 1;
    private final int ANNUAL_ANALYSIS_POSITION = 0;
    private DatabaseAccess dbAccess;
    private boolean isAddButtonHided = false;
    private int activityRequestCode = 0;
    private int[] selectedDateForQuery = new int[4];
    private final int CONTAINS_DATA = 0;
    private final int YEAR = 1;
    private final int MONTH = 2;
    private final int DAY = 3;
    private MainActivity activity;

    public void saveAmount(String amountValue, int typeOfAmount, String titleName) {
        double amount;
        List<String> listToAdd;
        int typeOfSort;
        String format = "%.2f";

        if(typeOfAmount == AddAmountActivity.INCOME_AMOUNT) {
            amount = Double.parseDouble(amountValue);
            format = "+" + format;
            listToAdd = incomeAmountsList;
            typeOfSort = Utilities.INCOME_SORT;
        } else {
            amount = Double.parseDouble(amountValue) * -1;
            listToAdd = expenseAmountsList;
            typeOfSort = Utilities.EXPENSE_SORT;
        }

        String amountString = String.format(format, amount);
        dbAccess.saveAmount(amountString, Utilities.formatDbDateWithTimeFromDatePicker(
                selectedDateForQuery[DAY], selectedDateForQuery[MONTH],
                selectedDateForQuery[YEAR]), titleName
        );

        listToAdd.add(amountString);
        updateAmountsOnScreenWithActionBarDate(typeOfSort);
    }

    public void updateAmountsOnScreenWithActionBarDate(int typeOfSort) {
        String date = Utilities.formatDbDateFromActionBarDate(actionBarFormattedDate);
        List<String> queryAmountsList = dbAccess.getSpecificMonthlyAmounts(date);

        Utilities.sortAllAmountsList(allAmountsList, incomeAmountsList,
                expenseAmountsList, codList, queryAmountsList, typeOfSort);
        ListView listView = (ListView) findViewById(R.id.amountsListView);
        ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
        refreshGraphicAndAmountSum(allAmountsList);
    }

    public AdapterView.OnItemClickListener addMenuItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String title = "";
            switch (position) {
                case EXPENSE_LISTVIEW_POSITION:
                    title = getString(R.string.expense_title);
                    break;
                case INCOME_LISTVIEW_POSITION:
                    title = getString(R.string.income_title);
                    break;
            }
            Intent intent = new Intent(activity, AddAmountActivity.class);
            intent.putExtra(AddAmountActivity.KEY_ADD_AMOUNT_TITLE, title);
            intent.putExtra(AddAmountActivity.KEY_TOTAL_AMOUNT, amountSumTextView.getText().toString());
            activity.startActivityForResult(intent, AddAmountActivity.ADD_AMOUNT_ACTIVITY_CODE);
        }
    };

    public AdapterView.OnItemClickListener actBarDrawToggleItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final int CHART_ANALYSIS = 0;

            switch (position) {
                case CHART_ANALYSIS:
                    Intent intent = new Intent(activity, ChartActivity.class);
                    intent.putExtra(ChartActivity.KEY_CURRENT_DATE, actionBarFormattedDate);
                    activity.startActivityForResult(intent, ChartActivity.CHART_ACTIVITY_CODE);
                    break;
            }
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

        configureAmountsListView(R.id.amountsListView);
        setActionBarCustomView(R.layout.action_bar_text_layout);

        actionBarTextView = (TextView)findViewById(R.id.actionBarTextView);
        actionBarTextView.setText(actionBarFormattedDate);

        amountSumTextView = (TextView)findViewById(R.id.amountSumTextView);
        amountSumTextView.setText(Utilities.sumIncomeExpenseItems(allAmountsList,
                OUT_OF_BOUNDS_LABEL));

        setAmountListViewItems(R.id.amountsListView, allAmountsList,
                R.layout.amount_list_view_item_layout, R.id.amountItemTextView);

        configureAddMenuOptions();
        activity = this;
        actionBarDrawerToggle = getActionBarDrawerToogle();
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        String nowDate = Utilities.getNowDbDateWithoutTime();
        //selectedDateForQuery must containt data in Calendar format.
        selectedDateForQuery[YEAR] = Integer.parseInt(nowDate.substring(0,4));
        selectedDateForQuery[MONTH] = Integer.parseInt(nowDate.substring(5,7)) - 1;
        selectedDateForQuery[DAY] = Integer.parseInt(nowDate.substring(8,10));

        configureActBarDrawToogleOptions();
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        activityRequestCode = 0;
        if(resultCode == RESULT_OK) {
            activityRequestCode = requestCode;
            dbAccess = DatabaseAccess.getDBAccessInstance(getApplicationContext());
            switch(requestCode) {
                case ChartActivity.CHART_ACTIVITY_CODE:
                    drawerLayout.closeDrawers();
                case CalendarActivity.CALENDAR_ACTIVITY_CODE:
                    selectedDateForQuery[CONTAINS_DATA] = 1;
                    selectedDateForQuery[YEAR] = data.getIntExtra(Utilities.KEY_INTENT_YEAR, 0);
                    selectedDateForQuery[MONTH] = data.getIntExtra(Utilities.KEY_INTENT_MONTH, 0);
                    selectedDateForQuery[DAY] = data.getIntExtra(Utilities.KEY_INTENT_DAY, 0);
                    isAddButtonHided = !data.getBooleanExtra(Utilities.KEY_INTENT_COMPARE_DATE, false);
                    if(data.getBooleanExtra(Utilities.KEY_INTENT_COMPARE_DATE, false)) {
                        menu.findItem(R.id.addButton).setVisible(true);
                    } else {
                        menu.findItem(R.id.addButton).setVisible(false);
                    }
                    break;
                case AddAmountActivity.ADD_AMOUNT_ACTIVITY_CODE:
                    drawerLayout.closeDrawers();
                    int typeOfAmount = data.getIntExtra(AddAmountActivity
                            .KEY_TYPE_AMOUNT_RETURNED, 0);
                    String amount = data.getStringExtra(AddAmountActivity.KEY_AMOUNT_RETURNED);
                    String title = data.getStringExtra(AddAmountActivity.KEY_TITLE_NAME_RETURNED);
                    saveAmount(amount, typeOfAmount, title);
                    break;
            }
        } else {
            selectedDateForQuery[CONTAINS_DATA] = 0;
            if(requestCode == ChartActivity.CHART_ACTIVITY_CODE ||
                    requestCode == AddAmountActivity.ADD_AMOUNT_ACTIVITY_CODE) {
                drawerLayout.closeDrawers();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        dbAccess = DatabaseAccess.getDBAccessInstance(getApplicationContext());
        if(dbAccess.databaseCanWrite()) {
            switch(activityRequestCode) {
                case AmountDetailActivity.AMOUNT_DETAIL_ACTIV_CODE:
                case ChartActivity.CHART_ACTIVITY_CODE:
                case CalendarActivity.CALENDAR_ACTIVITY_CODE:
                    if(selectedDateForQuery[CONTAINS_DATA] == 1 &&
                            !Utilities.datePickerDateMatchesActBarDate(selectedDateForQuery[MONTH],
                                    selectedDateForQuery[YEAR], actionBarFormattedDate)
                    ) {
                        setAmountsFromDB(Utilities.formatDbDateFromDatePicker(selectedDateForQuery[DAY],
                                selectedDateForQuery[MONTH],
                                selectedDateForQuery[YEAR])
                        );
                        actionBarFormattedDate = String.format("%s/%s",
                                Utilities.getActionBarMonthFromDatePickerMonth(selectedDateForQuery[MONTH]),
                                String.valueOf(selectedDateForQuery[YEAR])
                        );
                        actionBarTextView.setText(actionBarFormattedDate);
                        selectedDateForQuery[CONTAINS_DATA] = 0;
                    }
                    break;
                default:
                    setAmountsFromDB(Utilities.formatDbDateFromActionBarDate(actionBarFormattedDate));
            }
        } else {
            showAlertDialogWithOk(getString(R.string.db_cant_write),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
        }
    }

    //Caution: This method needs to run very quickly.
    @Override
    protected void onPause() {
        dbAccess.closeDatabase();
        super.onPause();
    }

    @Override
    protected void onStop() {
        dbAccess.closeSqlStatements();
        super.onStop();
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

    public void showAlertDialogWithOk(String message, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.positive_button_message, okListener);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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
                        actionBarTextView.setClickable(false);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    }
                    else if(slideOffset <= 0.1) {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        menu.findItem(R.id.addButton).setIcon(R.drawable.plus);
                        actionBarTextView.setText(actionBarFormattedDate);
                        actionBarTextView.setClickable(true);
                    }
                }
                else {
                    if(slideOffset > 0.1) {
                        if(!isAddButtonHided) {
                            menu.findItem(R.id.addButton).setVisible(false);
                        }
                        actionBarTextView.setText(getString(R.string.menu_action_bar_title));
                        actionBarTextView.setClickable(false);
                    }
                    else if(slideOffset <= 0.1) {
                        if(!isAddButtonHided) {
                            menu.findItem(R.id.addButton).setVisible(true);
                        }
                        actionBarTextView.setText(actionBarFormattedDate);
                        actionBarTextView.setClickable(true);
                    }
                }
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };

        return actionBarDrawerToggle;
    }

    public void configureActBarDrawToogleOptions() {
        ListView leftDrawListView = (ListView)findViewById(R.id.activityMainLeftDrawerListView);
        leftDrawListView.setOnItemClickListener(actBarDrawToggleItemListener);

        List<String> leftDrawerOptionsList = new ArrayList<String>();
        leftDrawerOptionsList.add(getString(R.string.actbardrawtoggle_menu_option_1));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.left_drawer_menu_item_layout, R.id.leftMenuItemTextView,
                leftDrawerOptionsList) {
            @Override
            public View getView (int position, View convertView, ViewGroup parent) {
                if(convertView == null) {
                    LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.left_drawer_menu_item_layout, parent, false);
                }
                switch (position) {
                    case ANNUAL_ANALYSIS_POSITION:
                        ((ImageView)((RelativeLayout)convertView).getChildAt(0)).setImageResource(R.drawable.annual_analysis);
                        break;
                }
                return super.getView(position, convertView, parent);
            }
        };
        leftDrawListView.setAdapter(adapter);
    }

    public void configureAddMenuOptions() {
        List<String> addMenuOptionsList = new ArrayList<String>();
        addMenuOptionsList.add(getString(R.string.add_menu_option_1));
        addMenuOptionsList.add(getString(R.string.add_menu_option_2));

        ListView rightDrawListView = (ListView)findViewById(R.id.activityMainRightDrawerListView);
        rightDrawListView.setOnItemClickListener(addMenuItemListener);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.right_drawer_menu_item_layout, R.id.rightMenuItemTextView, addMenuOptionsList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null) {
                    LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.right_drawer_menu_item_layout, parent, false);
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
        rightDrawListView.setAdapter(adapter);
    }

    //Must be in format: YYYY-MM-DD
    public void setAmountsFromDB(String date) {
        List<String> queryAmountsList = dbAccess.getSpecificMonthlyAmounts(date);
        expenseAmountsList.clear();
        incomeAmountsList.clear();

        if(queryAmountsList.size() > 0) {
            for(int i = 0; i < queryAmountsList.size(); i ++) {
                String amountWithDate = queryAmountsList.get(i);
                String amount = amountWithDate.substring(0, amountWithDate.indexOf("&"));

                //Brazilian float number.
                if(Double.parseDouble(amount.replace(",",".")) > 0.0) {
                    incomeAmountsList.add(amount);
                } else {
                    expenseAmountsList.add(amount);
                }
            }
            Utilities.sortAllAmountsList(allAmountsList, incomeAmountsList,
                    expenseAmountsList, codList, queryAmountsList, Utilities.INCOME_EXPENSE_SORT);
            ListView amountsListView = (ListView)findViewById(R.id.amountsListView);
            ((ArrayAdapter)amountsListView.getAdapter()).notifyDataSetChanged();
        } else {
            allAmountsList.clear();
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

    private void setActionBarCustomView(int layoutId) {
        XmlPullParser parser = getResources().getXml(layoutId);
        while(true) {
            try {
                parser.next();
                if(parser.getEventType() == XmlPullParser.START_TAG) {
                    if(parser.getName().equals("LinearLayout")) {
                        break;
                    }
                }
            }catch (XmlPullParserException xmle) {
                xmle.printStackTrace();
            }catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        AttributeSet attrSet = Xml.asAttributeSet(parser);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(this, attrSet);

        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup actionBarView = (ViewGroup)inflater.inflate(layoutId, null);

        actionBarView.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, CalendarActivity.class);
                activity.startActivityForResult(intent, CalendarActivity
                        .CALENDAR_ACTIVITY_CODE);
            }
        });

        getSupportActionBar().setCustomView(actionBarView, layoutParams);
    }

    private void configureAmountsListView(int listViewId) {
        ListView listView = (ListView)findViewById(listViewId);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(activity, AmountDetailActivity.class);
                String date = dbAccess.getDateFromCod(codList.get(position));
                String title = dbAccess.getTitleFromCod(codList.get(position));
                intent.putExtra(AmountDetailActivity.KEY_INTENT_DATE, date);
                intent.putExtra(AmountDetailActivity.KEY_INTENT_AMOUNT_TITLE, title);
                intent.putExtra(AmountDetailActivity.KEY_INTENT_AMOUNT, allAmountsList.get(position));
                activity.startActivityForResult(intent, AmountDetailActivity.AMOUNT_DETAIL_ACTIV_CODE);
                return true;
            }
        });

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
                        String date = dbAccess.getDateFromCod(codList.get(reverseSortedPositions[0]));
                        dbAccess.removeAmount(amountToRemove, date);
                        int typeOfSort;
                        //If the element dismissed is Income.
                        if(reverseSortedPositions[0] < incomeElementsMaxPosition) {
                            incomeAmountsList.remove(amountToRemove);
                            typeOfSort = Utilities.INCOME_SORT;
                        } else {
                            expenseAmountsList.remove(amountToRemove);
                            typeOfSort = Utilities.EXPENSE_SORT;
                        }
                        updateAmountsOnScreenWithActionBarDate(typeOfSort);
                    }
                });

        listView.setOnTouchListener(swipeToDismissListViewListener);
        listView.setOnScrollListener(swipeToDismissListViewListener.makeScrollListener());
    }
}