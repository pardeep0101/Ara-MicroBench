package com.example.xaradrim.benchmark_example;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.xaradrim.benchmark_example.Tests.ObserverCPU;
import com.example.xaradrim.benchmark_example.Tests.Test;
import com.example.xaradrim.benchmark_example.Tests.Testable;

import java.util.List;

public class MainActivity extends ActionBarActivity {

    private Test t = null;
    private TextView myText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (isProcessRunning("com.eembc.andebench")) {
//            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.eembc.andebench");
//            startActivity(launchIntent);
//
//        } else {
            t = new Test();
        //code added : Pardeep
            System.out.println("Observer attached");
            new ObserverCPU(t,"ProjectARA-CPU");

//        }
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

        System.out.println("clicked");
        if (B.isChecked()) {


            if (((CheckBox) findViewById(R.id.cpu_box)).isChecked()) {

                System.out.println("Im starting the cpu test");
                t.add_test(t.make_test("cpu"));


            }
            if (((CheckBox) findViewById(R.id.memory_box)).isChecked()) {
                System.out.println("Im starting the memory test");
                t.add_test(t.make_test("memory"));

            }

            System.out.println("Test Started.");
            t.start_test();


        } else {

            t.halt_execution();

        }
    }
    //Code added : Pardeep
    //listing other benchmark app running in bakcground, if any.
    void listingOB() {
        if (isProcessRunning("com.example.xaradrim.benchmark_example")) {
            System.out.println(" only Project ara is running? -> " );
        } else {
            System.out.println("AndEbenc is running> ->" + isProcessRunning("com.eembc.andebench"));
        }

    }

    boolean isProcessRunning(String processName) {
        if (processName == null)
            return false;

        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process : processes) {
            if (processName.equals(process.processName)) {
                return true;
            }
        }
        return false;
    }
}
