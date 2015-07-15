package com.example.xaradrim.benchmark_example;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ToggleButton;

import com.example.xaradrim.benchmark_example.Tests.DataLogging.AttributeGenerator;
import com.example.xaradrim.benchmark_example.Tests.DataLogging.ObserverCPU;
import com.example.xaradrim.benchmark_example.Tests.Test;


public class MainActivity extends ActionBarActivity {

    private Test t = null;
    private ObserverCPU external_observer = null; // to run observer separately
    AttributeGenerator at1 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t = new Test();
        //code added : Pardeep
        //creating the observer object to collect data
        at1 = AttributeGenerator.getInstance();
        String device_manufacturer = Build.MANUFACTURER;
        String device_model = Build.MODEL;

        System.out.println(device_manufacturer+" -> "+ device_model);
        if(device_manufacturer.contains("motorola")){

            Intent intent = getIntent();//"ACTION_BATTERY_CHANGED"

            String id = intent.getStringExtra(BatteryManager.EXTRA_VOLTAGE);
            System.out.println(id);

//
//                (this).registerReceiver(this.btr, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
//
        }

    }

    private BroadcastReceiver btr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int  voltage= intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);
            System.out.println("vol "+ voltage);

        }
    };

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


    public void onClick(View view) {

        ToggleButton B = (ToggleButton) view.findViewById(R.id.runTest);


        // this is to selecting and running the actual test

        if (B.isChecked()) {


            if (((CheckBox) findViewById(R.id.cpu_box)).isChecked()) {
                //System.out.println("Im starting the cpu test");
               t.add_test(t.make_test("cpu"));

            }
            if (((CheckBox) findViewById(R.id.memory_box)).isChecked()) {
                //System.out.println("Im starting the memory test");
                t.add_test(t.make_test("memory"));
            }
            //Code added:Pardeep
            // check box controller for data capturing
            if (((CheckBox) findViewById(R.id.dcapture_box)).isChecked()) {
                System.out.println("Clicked -> " + ((CheckBox) findViewById(R.id.dcapture_box)).getText());
                at1.addAttributeList((((CheckBox) findViewById(R.id.dcapture_box)).getText()).toString());
            }
            t.start_test();
            at1.prepareAttributes();

        } else {

            t.halt_execution();

            //Just in case if you want to stop the benchmarks but not the capturing part.
            if (((CheckBox) findViewById(R.id.dcapture_box)).isChecked()) {
                System.out.println("Clicked -> " + ((CheckBox) findViewById(R.id.dcapture_box)).getText());
                at1.emptyAttributeList();
            }


        }
    }

}
