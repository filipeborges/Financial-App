package com.jabarasca.financial_app;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.jabarasca.financial_app.dao.DatabaseAccess;
import com.jabarasca.financial_app.utils.Utilities;

public class CalendarActivity extends AppCompatActivity {

    private DatePicker datePicker;
    private DatabaseAccess dbAccess;
    public static final int CALENDAR_ACTIVITY_CODE = 1;
    public static final int DEFAULT_DAY = 10;

    public static final int DATE_PICKER_MAX_VALUE = 1;
    public static final int DATE_PICKER_MIN_VALUE = 2;

    private boolean isChartActivityRequest;

    private View.OnClickListener okListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            if(!isChartActivityRequest) {
                intent.putExtra(Utilities.KEY_INTENT_MONTH, datePicker.getMonth());
                String selectedDate = Utilities.formatDbDateFromDatePicker(datePicker.getDayOfMonth(),
                        datePicker.getMonth(), datePicker.getYear());
                String actualDate = Utilities.getNowDbDateWithoutTime();

                boolean isActualDate = selectedDate.substring(0,3).equals(actualDate.substring(0,3)) &&
                        selectedDate.substring(5,7).equals(actualDate.substring(5,7)) &&
                        selectedDate.substring(8,10).equals(actualDate.substring(8,10));
                intent.putExtra(Utilities.KEY_INTENT_COMPARE_DATE, isActualDate);

                //Default day to meet the format: YYYY-MM-DD
                int day = isActualDate ? Integer.parseInt(actualDate.substring(8,10)) : DEFAULT_DAY;
                intent.putExtra(Utilities.KEY_INTENT_DAY, day);
            }
            intent.putExtra(Utilities.KEY_INTENT_YEAR, datePicker.getYear());
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_layout);
        dbAccess = DatabaseAccess.getDBAccessInstance(getApplicationContext());
        datePicker = (DatePicker)findViewById(R.id.datePicker);

        datePicker.findViewById(Resources.getSystem().getIdentifier("day", "id", "android"))
                .setVisibility(View.GONE);
        Button okButton = (Button)findViewById(R.id.datePickerButton);
        okButton.setOnClickListener(okListener);
        isChartActivityRequest = getIntent()
                .getBooleanExtra(ChartActivity.KEY_CHART_ACTIVITY_REQUEST, false);

        if(isChartActivityRequest) {
            datePicker.findViewById(Resources.getSystem().getIdentifier("month", "id", "android"))
                    .setVisibility(View.GONE);
            int currentYear = getIntent().getIntExtra(ChartActivity.KEY_CURRENT_YEAR, 0);
            //Only the year is important.
            datePicker.updateDate(currentYear, 0, DEFAULT_DAY);
        }

        datePicker.setMinDate(dbAccess.getDatePickerValue(DATE_PICKER_MIN_VALUE));
        datePicker.setMaxDate(System.currentTimeMillis());
    }
}
