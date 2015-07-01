package com.jabarasca.financial_app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Xml;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import com.jabarasca.financial_app.utils.Utilities;

//Obs: Normal actionBar from Activity doesnt show hamburguer icon.
public class MainActivity extends AppCompatActivity {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private View drawerRightListView;
    private DrawerLayout drawerLayout;
    private Menu menu;
    private TextView actionBarTextView;
    private String formattedDate = Utilities.getFormattedActualDate();
    private LayoutInflater inflater;
    private List<String> amountsDespezaList = new ArrayList<String>();
    private Comparator<String> descAmountComparator = new Comparator<String>() {
        @Override
        public int compare(String lhs, String rhs) {
            //For decreasing order: lhs > rhs == -1; lhs == rhs == 0; lhs < rhs == 1;
            return (int)Math.signum(Float.parseFloat(lhs) - Float.parseFloat(rhs));
        }
    };

    private DialogInterface.OnClickListener addDespezaDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_NEGATIVE) {
                drawerLayout.closeDrawers();
            }
            else {
                String[] amounts = null;

                /*XmlPullParser parser = getResources().getLayout(R.layout.amount_text_view);
                AttributeSet attributes = null;
                try {
                    //Parser must be in START_TAG state (In this state the attributes of the TAG was readed).
                    while(!(parser.next() == XmlPullParser.START_TAG)){}
                    attributes = Xml.asAttributeSet(parser);
                }catch(IOException e) {
                    e.printStackTrace();
                }catch(XmlPullParserException e) {
                    e.printStackTrace();
                }*/

                EditText despezaEditText = (EditText)((Dialog)dialog).findViewById(R.id.addDespezaEditText);
                double expenseAmout = Double.parseDouble(despezaEditText.getText().toString()) * -1;
                amountsDespezaList.add(String.format("%.2f", expenseAmout));

                if(amountsDespezaList.size() > 0) {
                    //Expense should be sorted in ascending order (negative number).
                    Collections.sort(amountsDespezaList, descAmountComparator);
                    amounts = new String[amountsDespezaList.size()];
                    for(int i = 0; i < amountsDespezaList.size(); i++) {
                        amounts[i] = amountsDespezaList.get(i);
                    }
                }
                else {
                    amounts = new String[1];
                    amounts[0] = amountsDespezaList.get(0);
                }

                ListView amountListView = (ListView)findViewById(R.id.amountListView);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.amount_text_view,
                        R.id.expanseNumberItem, amounts);
                amountListView.setAdapter(adapter);

                drawerLayout.closeDrawers();
            }
        }
    };

    private AdapterView.OnItemClickListener addItemListener = new AdapterView.OnItemClickListener() {
        private final int DESPEZA = 0;
        private final String DESPEZA_TITLE = "Adicionar Despeza";
        private final String DESPEZA_MESSAGE = "Valor:";
        private final String NEGATIVE_BUTTON_MESSAGE = "Cancelar";
        private final String POSITIVE_BUTTON_MESSAGE = "Ok";

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case DESPEZA:
                    //AlertDialog.Builder constructor must use Activity reference instead of Context.
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setTitle(DESPEZA_TITLE);
                    alertDialog.setMessage(DESPEZA_MESSAGE);
                    alertDialog.setNegativeButton(NEGATIVE_BUTTON_MESSAGE, addDespezaDialogListener);
                    alertDialog.setPositiveButton(POSITIVE_BUTTON_MESSAGE, addDespezaDialogListener);
                    alertDialog.setView(inflater.inflate(R.layout.add_despeza_popup, null));
                    alertDialog.show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        drawerLayout = (DrawerLayout)findViewById(R.id.activityMainDrawerLay);
        drawerRightListView = findViewById(R.id.activityMainRightDrawer);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.open_drawer, R.string.close_drawer) {

            @Override
            public void onDrawerSlide (View drawerView, float slideOffset) {
                if(drawerView.getId() == R.id.activityMainRightDrawer) {
                    if(slideOffset > 0.1) {
                        menu.findItem(R.id.plusButton).setIcon(R.drawable.minus);
                        actionBarTextView.setText(getString(R.string.add_options_title));
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    }
                    else if(slideOffset <= 0.1) {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        menu.findItem(R.id.plusButton).setIcon(R.drawable.add);
                        actionBarTextView.setText(formattedDate);
                    }
                }
                else {
                    if(slideOffset > 0.1) {
                        menu.findItem(R.id.plusButton).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                        actionBarTextView.setText(getString(R.string.menu_options_title));
                    }
                    else if(slideOffset <= 0.1) {
                        menu.findItem(R.id.plusButton).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                        actionBarTextView.setText(formattedDate);
                    }
                }

                super.onDrawerSlide(drawerView, slideOffset);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        getSupportActionBar().setCustomView(R.layout.action_bar_text);
        actionBarTextView = (TextView)findViewById(R.id.actionBarTextView);
        actionBarTextView.setText(formattedDate);

        //Set items on AddListView (activityMainRightDrawer).
        String[] addOptions = new String[]{getString(R.string.add_options_1),
                                           getString(R.string.add_options_2)};
        setListViewItems(R.id.activityMainRightDrawer, addOptions, R.layout.drawer_add_option_item, R.id.drawerAddOptTextView,
                addItemListener);
    }

    private void setListViewItems(int listViewId, String[] listViewOptions, int listItemLayoutId, int listItemTextViewId,
                                  AdapterView.OnItemClickListener itemListener) {

        ListView listView = (ListView)findViewById(listViewId);
        listView.setOnItemClickListener(itemListener);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                listItemLayoutId, listItemTextViewId, listViewOptions);

        listView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_action_bar, menu);
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
        else if(item.getItemId() == R.id.plusButton) {
            if(!drawerLayout.isDrawerOpen(drawerRightListView)) {
                drawerLayout.openDrawer(drawerRightListView);
            } else {
                drawerLayout.closeDrawer(drawerRightListView);
            }
        }

        return super.onOptionsItemSelected(item);
    }

}
