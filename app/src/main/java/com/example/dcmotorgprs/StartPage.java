package com.example.dcmotorgprs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class StartPage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        Button bt1Button = (Button) findViewById(R.id.button1);
        Button bt1Button1 = (Button) findViewById(R.id.button2);
        bt1Button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent in = new Intent(getApplicationContext(), Init.class);
                startActivity(in);
            }
        });
        bt1Button1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (Init.val1.length() == 0) {

                    Toast.makeText(getApplicationContext(),
                            "Please Initialize gprs", Toast.LENGTH_LONG).show();
                } else {


                    Intent in1 = new Intent(getApplicationContext(),
                            MainActivity.class);
                    startActivity(in1);
                }
            }
        });

    }

}
