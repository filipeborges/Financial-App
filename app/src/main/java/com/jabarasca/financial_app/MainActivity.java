package com.jabarasca.financial_app;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jabarasca.financial_app.utils.Utilities;

//Obs: Normal actionBar from Activity doesnt show hamburguer icon.
public class MainActivity extends AppCompatActivity {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private View drawerRightListView;
    private DrawerLayout drawerLayout;
    private Menu menu;
    private TextView actionBarTextView;
    private String formattedDate = Utilities.getFormattedActualDate();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        setListViewItems(R.id.activityMainRightDrawer, addOptions, R.layout.action_bar_add_option, R.id.drawerAddOptTextView);
    }

    private void setListViewItems(int listViewId, String[] listViewOptions, int listItemLayoutId, int listItemTextViewId) {
        ListView listView = (ListView)findViewById(listViewId);

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
