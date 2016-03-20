package com.jabarasca.financial_app;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.jabarasca.financial_app.dao.DatabaseAccess;

public class CalendarActivity extends Activity {

    private DatePicker datePicker;
    private DatabaseAccess dbAccess;
    private final long DEFAULT_MIN_DATE_PICKER = System.currentTimeMillis()-1000;
    private final long DEFAULT_MAX_DATE_PICKER = System.currentTimeMillis();
    public static final int CALENDAR_ACTIVITY_ID_REQUEST = 1;
    public static final String SELECTED_DATE_YEAR = "SELECTED_DATE_YEAR";
    public static final String SELECTED_DATE_MONTH = "SELECTED_DATE_MONTH";
    public static final String SELECTED_DATE_DAY = "SELECTED_DATE_DAY";
    private View.OnClickListener okListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.putExtra(SELECTED_DATE_YEAR, datePicker.getYear());
            intent.putExtra(SELECTED_DATE_MONTH, datePicker.getMonth());
            intent.putExtra(SELECTED_DATE_DAY, 10); //Default day to meet the format: YYYY-MM-DD
            setResult(RESULT_OK, intent);
            finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_layout);
        dbAccess = DatabaseAccess.getDBAcessInstance(getApplicationContext());
        datePicker = (DatePicker)findViewById(R.id.datePicker);
        datePicker.findViewById(Resources.getSystem().getIdentifier("day", "id", "android"))
                .setVisibility(View.GONE);
        datePicker.setMinDate(dbAccess.getDatePickerValue(dbAccess.getMinPickerSqlStmnt(),
                DEFAULT_MIN_DATE_PICKER));
        datePicker.setMaxDate(dbAccess.getDatePickerValue(dbAccess.getMaxPickerSqlStmnt(),
                DEFAULT_MAX_DATE_PICKER));

        Button okButton = (Button)findViewById(R.id.datePickerButton);
        okButton.setOnClickListener(okListener);
    }
}
