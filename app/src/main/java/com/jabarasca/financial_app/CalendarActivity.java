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
    public static final int CALENDAR_ACTIVITY_ID_REQUEST = 1;
    public static final String SELECTED_DATE_YEAR = "SELECTED_DATE_YEAR";
    public static final String SELECTED_DATE_MONTH = "SELECTED_DATE_MONTH";
    public static final String SELECTED_DATE_DAY = "SELECTED_DATE_DAY";
    public static final int DEFAULT_DAY = 10;

    public static final int DATE_PICKER_MAX_VALUE = 1;
    public static final int DATE_PICKER_MIN_VALUE = 2;

    private boolean isChartActivityRequest;

    private View.OnClickListener okListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            if(!isChartActivityRequest) {
                intent.putExtra(SELECTED_DATE_MONTH, datePicker.getMonth());
                intent.putExtra(SELECTED_DATE_DAY, DEFAULT_DAY); //Default day to meet the format: YYYY-MM-DD
            }
            intent.putExtra(SELECTED_DATE_YEAR, datePicker.getYear());
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
        Button okButton = (Button)findViewById(R.id.datePickerButton);
        okButton.setOnClickListener(okListener);
        isChartActivityRequest = getIntent()
                .getBooleanExtra(ChartActivity.CHART_ACTIVITY_REQUEST, false);

        if(isChartActivityRequest) {
            datePicker.findViewById(Resources.getSystem().getIdentifier("month", "id", "android"))
                    .setVisibility(View.GONE);
            //TODO: Verify default value from the intent.
            int currentYear = getIntent().getIntExtra(ChartActivity.CURRENT_YEAR, 0);
            //Only the year is important.
            datePicker.updateDate(currentYear, 0, DEFAULT_DAY);
        }

        //TODO: Maybe will be needed to receive presented date from MainActivity,
        // to set the datePicker to correct position with presented date.

        datePicker.setMinDate(dbAccess.getDatePickerValue(DATE_PICKER_MIN_VALUE));
        datePicker.setMaxDate(System.currentTimeMillis());
    }
}
