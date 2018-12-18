package com.example.adarsh.smstest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
public class RetrieveMessage extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newmsg);

        Intent sms_intent = getIntent();
        Bundle b = sms_intent.getExtras();
        TextView tv = (TextView) findViewById(R.id.txtview);
        if (b != null) {
            // Display SMS in the TextView
            tv.setText(b.getString("sms_str"));
        }
    }
}
