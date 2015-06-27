package com.example.xaradrim.benchmark_example.Tests;


import java.util.Random;


/**
 * Created by xaradrim on 5/29/15.
 * * updated by pdp on 26-Jun
 */
public class Cpu implements Testable {

   private boolean Testing;




    Cpu() {

    }

    @Override
    public void run() {


        int number;
        Random r = new Random();
        this.Testing = true;

        //runing the CPu for specified time


            //repeated benchmark-test loop
        System.out.println("runnning CPU loop");
            while (this.isTesting() ) {
                number = r.nextInt(100000);
                number++;
                number += 2;
                number -= 3;
                number *= 7;
                number /= 5;

                //System.out.println("The value in number for the cpu-> " + number + "\n");

            }

    }


    @Override
    public void stop_test() {
        this.Testing = false;
        System.out.println("terminated...!");

    }

    @Override
    public boolean isTesting() {
        return this.Testing;
    }


}
