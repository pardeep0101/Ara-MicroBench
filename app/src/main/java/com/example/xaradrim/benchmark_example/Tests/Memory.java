package com.example.xaradrim.benchmark_example.Tests;

import android.app.ActivityManager;

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
    boolean control = false;
    long numChunks = 24 ;

    @Override
    public void run() {
        this.control = false;
        // just adding something else
        while(this.control != true){
            this.writeToMemory(this.control);
            this.make_delay();
            this.readFromMemory(this.control);
            this.make_delay();
        }
    }

    @Override
    public void stop_test() {
        this.control = true;
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

    private void writeToMemory(boolean control){
        Byte temp = new Byte("1");
        for(Byte[] b : this.Mem_control ){
            for(int i = 0 ; i < b.length ; i++){
                if (control){
                    break;
                }

                b[i] = temp;
            }
        }
    }

    private void readFromMemory(boolean control){
        Byte temp;
        for(Byte[] b : this.Mem_control ){
            for(int i = 0 ; i < b.length ; i++){
                if (control){
                    break;
                }
                temp = b[i];
                System.out.println(temp);
            }
        }

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
