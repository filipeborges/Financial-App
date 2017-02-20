package com.jabarasca.financial_app;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.jabarasca.financial_app.dao.DatabaseAccess;
import com.jabarasca.financial_app.utils.Constant;
import com.jabarasca.financial_app.utils.Utilities;

public class CalendarActivity extends AppCompatActivity {

    private DatePicker datePicker;
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
                intent.putExtra(Constant.KEY_INTENT_MONTH, datePicker.getMonth());
                int day = Utilities.datePickerDateMatchesActBarDate(datePicker.getMonth(),
                        datePicker.getYear(), Utilities.getNowActionBarDate())
                        ? Integer.parseInt(Utilities.getNowDbDateWithoutTime().substring(8,10))
                        : DEFAULT_DAY;
                intent.putExtra(Constant.KEY_INTENT_DAY, day);
            }
            intent.putExtra(Constant.KEY_INTENT_YEAR, datePicker.getYear());
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_layout);
        DatabaseAccess dbAccess = DatabaseAccess.getDBAccessInstance(getApplicationContext());
        datePicker = (DatePicker)findViewById(R.id.datePicker);
        datePicker.setMinDate(dbAccess.getDatePickerValue(DATE_PICKER_MIN_VALUE));
        datePicker.setMaxDate(Utilities.getLongValueFromDBDate(Utilities.getNowDbDateWithoutTime()));
        datePicker.findViewById(Resources.getSystem().getIdentifier("day", "id", "android"))
                .setVisibility(View.GONE);
        Button okButton = (Button)findViewById(R.id.datePickerButton);
        okButton.setOnClickListener(okListener);
        isChartActivityRequest = getIntent()
                .getBooleanExtra(Constant.KEY_INTENT_CHART_REQUEST, false);

        if(isChartActivityRequest) {
            datePicker.findViewById(Resources.getSystem().getIdentifier("month", "id", "android"))
                    .setVisibility(View.GONE);
            int currentYear = getIntent().getIntExtra(Constant.KEY_INTENT_YEAR, 0);
            //Only the year is important.
            datePicker.updateDate(currentYear, 0, DEFAULT_DAY);
        }
    }
}
