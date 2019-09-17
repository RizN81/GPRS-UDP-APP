package com.example.dcmotorgprs;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TestSms extends Activity {

    EditText phone, ip;
    Button Start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        ip = (EditText) findViewById(R.id.init_ip);
        phone = (EditText) findViewById(R.id.Phoneno);

        phone.setText("8220883170");
        Start = (Button) findViewById(R.id.start);
        Start.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("8220883170", null, "hai java.",
                            null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again." + e,
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

    }
}
