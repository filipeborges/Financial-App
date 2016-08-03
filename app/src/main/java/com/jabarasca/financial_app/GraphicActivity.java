package com.jabarasca.financial_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class GraphicActivity extends AppCompatActivity {

    GraphicActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphic_layout);

        activity = this;
        setActionBarCustomView(R.layout.action_bar_text_layout);
        TextView actionBarTextView = (TextView)findViewById(R.id.actionBarTextView);
        actionBarTextView.setText(getString(R.string.graphic_activ_action_bar_title));
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

        getSupportActionBar().setCustomView(actionBarView, layoutParams);
    }
}
