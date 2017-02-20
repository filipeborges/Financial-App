package com.jabarasca.financial_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jabarasca.financial_app.utils.Constant;
import com.jabarasca.financial_app.utils.Utilities;

public class AmountDetailActivity extends AppCompatActivity {

    public static final int AMOUNT_DETAIL_ACTIV_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount_detail_layout);

        TextView amountDetailTxtVw = (TextView)findViewById(R.id.amountDetailValue);
        TextView amountDetailDateTxtVw = (TextView)findViewById(R.id.amountDetailDateValue);
        TextView amountDetailTitleTxtVw = (TextView)findViewById(R.id.amountDetailTitleValue);

        String amountValue = getIntent().getStringExtra(Constant.KEY_INTENT_AMOUNT);
        if(amountValue.contains("-")) {
            amountDetailTxtVw.setTextColor(getResources().getColor(R.color.expense_amount_color));
        } else {
            amountDetailTxtVw.setTextColor(getResources().getColor(R.color.income_amount_color));
        }
        amountDetailTxtVw.setText(amountValue);
        String date = getIntent().getStringExtra(Constant.KEY_INTENT_DATE);
        if(date.contains("*")) {
            date = date.replace("*", "");
            findViewById(R.id.amountDetailPosDateValue).setVisibility(View.VISIBLE);
        }
        date = Utilities.formatHumanDateFromDbDate(date);

        amountDetailDateTxtVw.setText(date);
        String title = getIntent().getStringExtra(Constant.KEY_INTENT_TITLE);
        title = title == null ? getString(R.string.amount_without_title) : title;
        amountDetailTitleTxtVw.setText(title);

        Button amountDetailOkBtn = (Button)findViewById(R.id.amountDetailButton);
        amountDetailOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

    }
}
