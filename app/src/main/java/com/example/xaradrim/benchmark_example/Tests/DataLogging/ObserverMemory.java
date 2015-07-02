package com.example.xaradrim.benchmark_example.Tests.DataLogging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Pardeep on 7/2/15.
 */
public class ObserverMemory extends ObserverTemplate{


    private String listenTo="Capture Mem data";
    private manageObservers ob2;
    private String testType = null;
    private boolean testStarted = false, testStopped = false,threadRunning=false;

    private String fileName = null, fpath = "/sdcard/charsticks/";
    private FileWriter fw;
    private BufferedWriter bw;
    private File file;
    private String s = null;
    private int count = 0;

    private Thread t;
    ObserverMemory(manageObservers ob2, String name){
        this.ob2 = ob2;
        ob2.addObserver(this,listenTo);
        this.fileName=name;
        initializieFile();


    }


    @Override
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
    public void startTest() {

    }

    @Override
    public void stopTest() {

    }

    @Override
    public void startObservation() {
        getUsedMemorySize();
    }

    @Override
    public void update(String testType, boolean testStarted, boolean testStopped) {

        this.testType = testType;
        this.testStarted = testStarted;
        this.testStopped = testStopped;

        if (this.testType.equalsIgnoreCase(listenTo) && this.testStarted && !(this.threadRunning)) {
            System.out.println("memory captruing started");
            t = new Thread(this, "Mem: Thread Started");
            t.start(); this.threadRunning = true;
        }

        if (this.testStopped && this.threadRunning) {

            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("memory captruing stopped");

        }

    }

    @Override
    public void run() {
        while(this.testStarted) {
            //System.out.println("starting");
            getUsedMemorySize();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
private  long freeSize = 0L,totalSize = 0L, usedSize = -1L;

    public void getUsedMemorySize() {

        try {
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            Runtime info = Runtime.getRuntime();
            freeSize = info.freeMemory();
            totalSize = info.totalMemory();
            usedSize = totalSize - freeSize;

            bw.write(Integer.toString(count));

            bw.write(" " + (Long.toString(totalSize)));
            bw.write(" " + (Long.toString(freeSize)));
            bw.write(" " + (Long.toString(usedSize)));
            count++;
            bw.newLine();
            bw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
