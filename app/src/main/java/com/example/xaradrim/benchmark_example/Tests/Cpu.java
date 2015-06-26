package com.example.xaradrim.benchmark_example.Tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
/**
 * Created by xaradrim on 5/29/15.
 * updated by pdp on 26-Jun
 */
public class Cpu  implements Testable {
    private boolean Testing;

    // File initiators.
    FileWriter fw;
    BufferedWriter bw;
    File file;
    int count=0;
    Cpu(){

        //appending timestamp in the file name
        long unixTime = System.currentTimeMillis() / 1000L;
        //file save at this location
        String fpath = "/sdcard/charsticks/" + "CPU-charLog" +unixTime+ ".txt";


        file= new File(fpath);
        // If file does not exists, then create it
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

        }
        catch (IOException e){
            System.out.println(e);
        }
    }

    @Override
    public void run() {


        int number;
        Random r = new Random();
        this.Testing = true;


        while (this.isTesting()) {
            number = r.nextInt(100000);
            number++;
            number += 2;
            number -= 3;
            number *= 7;
            number /= 5;
            System.out.println("The value in number for the cpu-> "+number+"\n");
            String s = null;
            try{

                //running ADB commands; Writing to the file in /sdcard/
                Process process = Runtime.getRuntime().exec("cat /sys/class/power_supply/battery/voltage_now cat /sys/class/power_supply/battery/current_now");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                fw = new FileWriter(file.getAbsoluteFile(), true);
                bw = new BufferedWriter(fw);

                bw.write(count);
                while( (s= bufferedReader.readLine())!=null ) {


                    bw.write(" "+s + " ");

                }
                count++;
                bw.newLine();
                bw.close();
            }
            catch (IOException e){
                System.out.println(e);
            }
        }
    }



    @Override
    public void stop_test() {

        this.Testing = false;System.out.println("terminated...!");
    }

    @Override
    public boolean isTesting() {
        return this.Testing;
    }
}
