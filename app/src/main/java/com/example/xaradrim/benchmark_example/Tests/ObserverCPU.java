package com.example.xaradrim.benchmark_example.Tests;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Pardeep on 6/26/15.
 * Example CPU-observer for our CPU-benchmark
 *
 */
public class ObserverCPU implements TestObservers , Runnable{
    private ObserveBench ob1;
    private String funName = null;

    // File intiator
    FileWriter fw;
    BufferedWriter bw;
    File file;


    int count = 0;
    float volt = 0, curr = 0, power = 0;
    String s = null;
    boolean isVolt = true;


    //update coming from CPU
    private String testType = null;
    private boolean testStarted = false, testStopped = false;

    //To run writing process as a thread. concurently to the benchmark
    Thread t;
    boolean threadRunning = false;

    public ObserverCPU(ObserveBench ob1, String name) {
        this.ob1 = ob1;
        this.funName = name;
        this.ob1.addObserver(this);

        long unixTime = System.currentTimeMillis() / 1000L; //to create unique file everytime test runs *not used right now
        String fpath = "/sdcard/charsticks/" + "CPU-charLog" + ".txt";
        file = new File(fpath);

        // If file does not exists, then create it
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        t = new Thread(this, "CPU_ObserverThread");
    }

    @Override
    public void update(String testType, boolean testStarted, boolean testStopped) {
        this.testType = testType;
        this.testStarted = testStarted;
        this.testStopped = testStopped;

        //while (this.testType.equalsIgnoreCase("cpu") && this.testStarted) {
      if (this.testType.equalsIgnoreCase("cpu") && this.testStarted && !(this.threadRunning)) {
//
//           // startObservation();
//            //this.testType = testType;
//            this.testStarted = testStarted;
           System.out.println("Writing data...");
          threadRunning = true;
            t.start();
        }
        if(this.testStopped && this.threadRunning){
            try {
                t.join();
                System.out.println("Cpu observer Thread stopped");
                threadRunning = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void startObservation() {

            try {
                fw = new FileWriter(file.getAbsoluteFile(), true);
                bw = new BufferedWriter(fw);


                //running ADB command lines
                Process process = Runtime.getRuntime().exec("cat /sys/class/power_supply/battery/voltage_now cat /sys/class/power_supply/battery/current_now");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                //reading and writing output to file
                bw.write(Integer.toString(count));

                while ((s = bufferedReader.readLine()) != null) {

                    //extracting voltage and current
                    if (isVolt) {
                        volt = (Float.parseFloat(s)) / 1000000;
                        //System.out.println("voltage " + volt);
                        isVolt = false;

                        bw.write(" " + Double.toString(volt) + " ");
                    } else {
                        curr = (Float.parseFloat(s)) / 1000000;
                        //System.out.println("current " + curr);
                        isVolt = true;

                        bw.write(" " + Double.toString(curr) + " ");

                        power = volt * curr;
                    }
                }
                //getting power and writing to the file
                System.out.println("Power consumed " + power);

                bw.write(" " + Float.toString(power));
                count++;
                bw.newLine();


                bw.close();

            } catch (IOException e) {
                System.out.println("IOException occured..");
                e.printStackTrace();
            }


    }


    @Override
    public void run() {
        while (this.testStarted) {
            System.out.println("Caching data for writing..");
            startObservation();
        }

    }
}

