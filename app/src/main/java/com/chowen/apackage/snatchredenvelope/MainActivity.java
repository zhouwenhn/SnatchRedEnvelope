package com.chowen.apackage.snatchredenvelope;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.chowen.apackage.snatchredenvelope.service.SnatchRedEnvelopeService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_access).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });

        ((ToggleButton) findViewById(R.id.btn_status)).setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (SnatchRedEnvelopeService.ALL) {
                    SnatchRedEnvelopeService.ALL = false;
                } else {
                    SnatchRedEnvelopeService.ALL = true;
                }
            }

        });
    }
}
