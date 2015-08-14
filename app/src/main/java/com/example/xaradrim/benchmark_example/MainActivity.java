package com.example.xaradrim.benchmark_example;


import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.xaradrim.benchmark_example.Tests.DataLogging.AttributeGenerator;
import com.example.xaradrim.benchmark_example.Tests.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    public static String phoneType = "";
    // private ObserverMain external_observer = null; // to run observer separately

    String serviceStartedAt, serviceStoppedAt;
    TextView tv;
    private Test t = null;
   // private ObserverMain external_observer = null; // to run observer separately
    AttributeGenerator at1 = null;
    private String device_manufacturer, device_model;
    private Intent b, iIntent;
    private IntentFilter ifilter;
    protected PowerManager.WakeLock mWakeLock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t = new Test();

        //code added : Pardeep
        //creating the observer object to collect data
        at1 = AttributeGenerator.getInstance();

        device_manufacturer = Build.MANUFACTURER;
        device_model = Build.MODEL;
        if (device_manufacturer.equalsIgnoreCase("motorola")) {
            phoneType = "moto";
        }
        if (device_model.equalsIgnoreCase("Nexus S 4G")) {
            phoneType = "nexus";

        }
        if (device_manufacturer.equalsIgnoreCase("Samsung") && !(device_model.equalsIgnoreCase("Nexus S 4G"))) {
            phoneType = "samsung";
        }

        ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        b = this.registerReceiver(null, ifilter);
        tv = (TextView) findViewById(R.id.status);
        iIntent = new Intent(this, MyService.class);

        final PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "ARA APP");
        this.mWakeLock.acquire();
    }

    @Override
    public void onDestroy() {
        this.mWakeLock.release();
        super.onDestroy();
    }

    public int getVoltage()
    {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent b = this.registerReceiver(null, ifilter);
        return b.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // this are Xaradrim (  my additions ) to this activity lane
    // hopping it works and doesn't explode =D
    public String getSystemTime() {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.US);
        return (df.format(new Date()));
    }

    public void onClick(View view) {


        ToggleButton B = (ToggleButton) view.findViewById(R.id.runTest);


        // this is to selecting and running the actual test

        if (B.isChecked()) {


            if (((CheckBox) findViewById(R.id.cpu_box)).isChecked()) {
                String threads = ((EditText) findViewById(R.id.thread_number)).getText().toString();
                int number_of_threads = new Integer(threads);
                for(int i=0 ; i < number_of_threads ; i++){
                    t.add_test(t.make_test("cpu"));
                }


            }
            if (((CheckBox) findViewById(R.id.memory_box)).isChecked()) {
                //System.out.println("Im starting the memory test");
                t.add_test(t.make_test("memory"));
            }
            //Code added:Pardeep
            // check box controller for data capturing, running data capturing via service now.
            if (((CheckBox) findViewById(R.id.dcapture_box)).isChecked()) {
                iIntent.putExtra("ObserverType", (((CheckBox) findViewById(R.id.dcapture_box)).getText()).toString());
                iIntent.putExtras(b);
                serviceStartedAt = getSystemTime();
                tv.setText("Status: Started at" + serviceStartedAt);
            }
            t.start_test();
            startService(iIntent);
        } else {

            t.halt_execution();

            //Just in case if you want to stop the benchmarks but not the capturing part.
            if (((CheckBox) findViewById(R.id.dcapture_box)).isChecked()) {

                iIntent.putExtra("stopWork", true);
                stopService(iIntent);
                serviceStoppedAt = getSystemTime();
                tv.setText("Status: Started at " + serviceStartedAt + "; Completed at " + serviceStoppedAt);
            }


        }
    }

}


