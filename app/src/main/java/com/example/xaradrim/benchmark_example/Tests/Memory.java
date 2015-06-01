package com.example.xaradrim.benchmark_example.Tests;

import android.app.ActivityManager;
import android.os.Debug;
import android.os.Parcelable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * Created by xaradrim on 5/31/15.
 */
public class Memory implements Testable {
    ArrayList<Byte []> Mem_control ;
    long TotalMem = (new ActivityManager.MemoryInfo()).availMem;
    long numChunks = 24 ;

    @Override
    public void run() {

    }

    @Override
    public void stop_test() {

    }

    @Override
    public boolean isTesting() {
        return false;
    }


    private void allocate(){
        long CHUNK_SIZE = this.TotalMem / this.numChunks;

        for(long i = CHUNK_SIZE ; i < this.TotalMem ; i += CHUNK_SIZE  ){
            this.Mem_control.add(new Byte[(int)CHUNK_SIZE]);
            make_delay();
        }
    }

    private void readToMemory(){

    }

    private void writeToMemory(){

    }

    private void make_delay(){
        long cpufreq = -1;
        RandomAccessFile reader;

        try {
           reader =  new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" ,"r");
           cpufreq = Long.parseLong(reader.readLine());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        for(int i = 0 ; i < cpufreq ; i++);
    }
}
