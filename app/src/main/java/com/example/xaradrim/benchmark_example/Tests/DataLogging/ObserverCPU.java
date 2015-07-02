package com.example.xaradrim.benchmark_example.Tests.DataLogging;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Created by Pardeep on 6/26/15.
 * Example CPU-observer for our CPU-benchmark
 */
public class ObserverCPU extends ObserverTemplate  {

    private manageObservers ob1;
    private String fileName = null, fpath = "/sdcard/charsticks/";
    private FileWriter fw;
    private BufferedWriter bw;
    private File file;
    private int count = 0;
    private float volt = 0, curr = 0, power = 0;
    private String s = null;
    private boolean isVolt = true;
    private String listenTo = "Capture CPU data";

    //update coming from CPU
    private String testType = null;
    private boolean testStarted = false, testStopped = false;

    //To run writing process as a thread. concurently to the benchmark
    Thread t;
    boolean threadRunning = false;

    /*
    This constructor could be used to create object of this class and run observer directly
     */
    public ObserverCPU(String testType, String name) {
        this.fileName = name;
        this.initializieFile();

        this.update(testType, this.testStarted, this.testStopped);


    }
    /*
    This contructor could be used to create objects binded with "AttributeGenerator" object
     */
    public ObserverCPU(manageObservers ob1, String name) {
        this.ob1 = ob1;
        this.fileName = name;
        this.ob1.addObserver(this,listenTo);
        this.initializieFile();
    }

    public void initializieFile() {
        fpath = this.fpath + this.fileName + ".txt";
        file = new File(fpath);
        // If file does not exists, then create it
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("file IO"+e);
        }
    }

    @Override
    public void update(String testType, boolean testStarted, boolean testStopped) {
        //System.out.println("parameteres rec:" + this.testType + " " + this.testStarted + " " + this.testStopped);
        this.testType = testType;
        this.testStarted = testStarted;
        this.testStopped = testStopped;

        if (this.testType.equalsIgnoreCase(listenTo) && this.testStarted && !(this.threadRunning)) {
            t = new Thread(this, "CPU_ObserverThread");
            t.start();
            System.out.println("Cpu observer: Thread started");
            this.threadRunning = true;
        }

        if (this.testStopped && this.threadRunning) {
            try {
                t.join();
                System.out.println("Cpu observer: Thread stopped");
                this.threadRunning = false;
                this.testStarted = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void startTest() {
        this.testStarted = true;
        this.testStopped = false;
        this.update(this.testType, this.testStarted, this.testStopped);
        //System.out.println("test started.......");
    }

    public void stopTest() {
        this.testStarted = false;
        this.testStopped = true;
        this.update(this.testType, this.testStarted, this.testStopped);
        //System.out.println("test stopped.......");
    }

    @Override
    public void run() {
        System.out.println("Collecting & writing data.." + this.testStarted);
        while (this.testStarted) {
            startObservation();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startObservation() {

        try {
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            //running ADB command lines
            Process process = Runtime.getRuntime().exec("cat /sys/class/power_supply/battery/voltage_now cat /sys/class/power_supply/battery/current_now");
            //Process process = Runtime.getRuntime().exec("cat /sys/class/power_supply/qpnp-dc/uevent ");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            //DataOutputStream toProcess = new DataOutputStream(process.getOutputStream());
            //System.out.println("reading"+bufferedReader.readLine());
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
}

