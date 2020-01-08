package com.huwochuxing.foundations;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.callme.platform.api.HttpConfigure;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HttpConfigure.init(this, "", "", "1");
    }
}
