package com.am.machinex;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    Button daily_machine_btn,machine_breakdown_btn;
    String usid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.appbarColor));
        }
        init();
    }

    public void init() {
        daily_machine_btn = (Button) findViewById(R.id.button_daily_machine);
        machine_breakdown_btn = (Button) findViewById(R.id.button_machine_breakdown);
        if (getIntent().getStringExtra("user_id") != null) {
            usid = getIntent().getStringExtra("user_id");
        }
        daily_machine_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent history_intent = new Intent(MenuActivity.this, MainActivity.class);
                history_intent.putExtra("user_id", usid);
                startActivity(history_intent);
            }
        });
        machine_breakdown_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent history_intent = new Intent(MenuActivity.this, MachineBreakdownActivity.class);
                history_intent.putExtra("user_id", usid);
                startActivity(history_intent);
            }
        });
    }

}
