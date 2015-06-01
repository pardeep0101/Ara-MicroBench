package com.example.xaradrim.benchmark_example;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ToggleButton;

import com.example.xaradrim.benchmark_example.Tests.Test;
import com.example.xaradrim.benchmark_example.Tests.Testable;

public class MainActivity extends ActionBarActivity {

    private Test t = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t = new Test();

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
                t.add_test(t.make_test("memory"));

            }

            t.start_test();




        }

        else
            {

                t.halt_execution();

            }
    }

}
