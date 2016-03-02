package com.jabarasca.financial_app;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.jabarasca.financial_app.dao.DatabaseAccess;

//TODO: Make a method to calculate Max and Min Date of CalendarView;
//TODO: Implement Ok Button callback.
//TODO: Fix bug when user press back button on CalendarActivity.
public class CalendarActivity extends Activity {

    private DatePicker datePicker;
    private DatabaseAccess dbAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        dbAccess = DatabaseAccess.getDBAcessInstance(getApplicationContext());
        datePicker = (DatePicker)findViewById(R.id.datePicker);
        datePicker.findViewById(Resources.getSystem().getIdentifier("day", "id", "android"))
                .setVisibility(View.GONE);
        datePicker.setMinDate(dbAccess.getMinDatePickerValue());
    }
}
