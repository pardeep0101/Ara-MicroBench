package com.example.xaradrim.benchmark_example;


import android.app.ActivityManager;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ToggleButton;

import com.example.xaradrim.benchmark_example.Tests.DataLogging.AttributeGenerator;
import com.example.xaradrim.benchmark_example.Tests.DataLogging.ObserverCPU;
import com.example.xaradrim.benchmark_example.Tests.Test;
import android.os.BatteryManager;

public class MainActivity extends ActionBarActivity {

    private Test t = null;
    private ObserverCPU external_observer = null;
    AttributeGenerator at1 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t = new Test();
        //code added : Pardeep
        //creating the observer object to collect data
       // external_observer = new ObserverCPU("CPU", "CPU-observation-log");
        at1 = AttributeGenerator.getInstance();

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
            if (((CheckBox) findViewById(R.id.dcpu_box)).isChecked()) {
                System.out.println("Clicked" + ((CheckBox) findViewById(R.id.dcpu_box)).getText());
                at1.addAttributeList((((CheckBox) findViewById(R.id.dcpu_box)).getText()).toString());
                //at1.addAttributeList("test");
                //external_observer.startTest();
            }
            if (((CheckBox) findViewById(R.id.dmemory_box)).isChecked()) {
                System.out.println("Clicked" + ((CheckBox) findViewById(R.id.dmemory_box)).getText());
                at1.addAttributeList(((CheckBox) findViewById(R.id.dmemory_box)).getText().toString());
                //external_observer.startTest();
            }


            t.start_test();
            at1.prepareAttributes();

        } else {

            t.halt_execution();
            //external_observer.stopTest();
            at1.emptyAttributeList();

        }
    }

}
