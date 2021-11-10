package com.example.murat.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Murat on 13.05.2017.
 */

public class secondActivity extends Activity {
    Button btnEkle;
    String nbz;
    EditText nabiz,isim,yas,tel;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        btnEkle=(Button) findViewById(R.id.buttonEkle);
        nabiz=(EditText)findViewById(R.id.nabiz);
        yas=(EditText)findViewById(R.id.yas);
        tel=(EditText)findViewById(R.id.tel);
        isim=(EditText)findViewById(R.id.isim);
        Intent intent=getIntent();
        nbz=intent.getStringExtra("nabiz");
        nabiz.setText("Nabız: "+nbz);
        btnEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNo = tel.getText().toString();
                String sms = "Ad Soyad: "+isim.getText().toString()+" Yaş: "+yas.getText().toString()+" "+nabiz.getText().toString();

                SmsManager smsManager = SmsManager.getDefault();

                smsManager.sendTextMessage(phoneNo, null, sms, null, null);
            }
        });


    }
}
