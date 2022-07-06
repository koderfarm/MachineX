package com.am.machinex;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.Locale;

public class MachineBreakdownActivity extends AppCompatActivity {
    String usid;
    TextView start_time, stop_time;
    Time startnow = new Time();
    Time stopnow = new Time();
    Button btn_start_stop;
    boolean start = false;
    LocalTime time;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_breakdown);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.appbarColor));
        }
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void init() {
        startnow.setToNow();
        stopnow.setToNow();
        if (getIntent().getStringExtra("user_id") != null) {
            usid = getIntent().getStringExtra("user_id");
        }
        Spinner spinner = (Spinner) findViewById(R.id.spinparts_name);
        start_time = (TextView) findViewById(R.id.start_time);
        stop_time = (TextView) findViewById(R.id.stop_time);
        String[] years = {"abc", "cbd", "ewr", "ewrw"};
        ArrayAdapter<CharSequence> langAdapter = new ArrayAdapter<CharSequence>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, years);
        langAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(langAdapter);
        btn_start_stop = (Button) findViewById(R.id.btn_start_stop);
        btn_start_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clock();

            }
        });

    }

    private void clock() {
        final Handler hander = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                hander.post(new Runnable() {
                    @Override
                    public void run() {
                        getTime();
//                        clock();
                    }
                });
            }
        }).start();
    }

    String start_time_string, stop_time_string;

    void getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        if (!start) {
            start_time_string = dateFormat.format(new Date());
            start_time.setText(start_time_string);
            btn_start_stop.setText("Stop");
            start = true;
        } else if (start) {
            stop_time_string = dateFormat.format(new Date());
            stop_time.setText(stop_time_string);
            btn_start_stop.setText("Start");
            start = false;

        }
    }
}
