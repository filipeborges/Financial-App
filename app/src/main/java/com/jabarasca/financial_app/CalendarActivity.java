package com.jabarasca.financial_app;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.jabarasca.financial_app.dao.DatabaseAccess;

//TODO: Implement Ok Button callback.
public class CalendarActivity extends Activity {

    private DatePicker datePicker;
    private DatabaseAccess dbAccess;
    private final long DEFAULT_MIN_DATE_PICKER = System.currentTimeMillis()-1000;
    private final long DEFAULT_MAX_DATE_PICKER = System.currentTimeMillis();
    public static final int CALENDAR_ACTIVITY_ID_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        dbAccess = DatabaseAccess.getDBAcessInstance(getApplicationContext());
        datePicker = (DatePicker)findViewById(R.id.datePicker);
        datePicker.findViewById(Resources.getSystem().getIdentifier("day", "id", "android"))
                .setVisibility(View.GONE);
        datePicker.setMinDate(dbAccess.getDatePickerValue(dbAccess.getMinPickerSqlStmnt(),
                DEFAULT_MIN_DATE_PICKER));
        datePicker.setMaxDate(dbAccess.getDatePickerValue(dbAccess.getMaxPickerSqlStmnt(),
                DEFAULT_MAX_DATE_PICKER));
    }
}
