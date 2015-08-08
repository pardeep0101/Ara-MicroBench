package com.example.xaradrim.benchmark_example.Tests;


import android.annotation.TargetApi;
import android.os.BatteryManager;
import android.os.Build;


import java.util.Random;


/**
 * Created by MAX R. BERRIOS
 * OSCAR LABS
 * on 5/29/15.
 */
public class Cpu implements Testable {

    private boolean Testing;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void run() {


        int number;
        BatteryManager b = new BatteryManager();
        Random r = new Random();
        this.Testing = true;


        System.out.println("Starting CPU \n");
        while (this.isTesting()) {

            long current = b.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
            long power = b.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER);

            System.out.println("Current(microAMP) : " + current + " \nPower(nanowatts/hour) : " + power + " \n");
            //long voltage = b.getLongProperty(BatteryManager.);
            number = r.nextInt(100000);
            number++;
            number += 2;
            number -= 3;
            number *= 7;
            number /= 5;
            System.out.println("The value in number for the cpu : " + number + "\n");
        }
    }


    @Override
    public void stop_test() {
        this.Testing = false;
    }

    @Override
    public boolean isTesting() {
        return this.Testing;
    }


}
