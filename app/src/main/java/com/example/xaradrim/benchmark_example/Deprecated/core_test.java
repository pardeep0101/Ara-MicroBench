package com.example.xaradrim.benchmark_example.Deprecated;

import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * Created by xaradrim on 5/27/15.
 */



public class core_test implements Runnable{


    public boolean TEST_RUNNING;

    public void run(){
        make_dummy_cpu_consumption();
    }

    public void make_dummy_cpu_consumption() {

        Random r = new Random();
        int counter = r.nextInt(100000);
        //System.out.println(counter);

        this.TEST_RUNNING = true;

        String s = " The value result by now in here for counter is : ";

        while(this.TEST_RUNNING) {
            counter = r.nextInt(100000);
            counter++;
            counter += 2;
            counter /= 5;
            //System.out.println(s+(float)counter);
            writeTo(s+(float)counter);
        }
        return;


    }

    public void stop_cpu_test(){
        this.TEST_RUNNING = false;
    }

    private void writeTo(String s){

        File log ;
        OutputStream w;

        try {
            //System.out.println("Hey im wrinting here!");
            // true is for appending text to file =D
            log = new File(Environment.getExternalStoragePublicDirectory("test"),"log_file.txt" );

            log.createNewFile();
            w = new FileOutputStream(log,true);
            w.write(s.getBytes());
            w.write("\n".getBytes());
            w.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}