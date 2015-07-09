package com.example.xaradrim.benchmark_example.Tests.DataLogging;

import android.annotation.TargetApi;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;

// moto- via API
// Samsung s6 both via API and ADB
// nexus s via ADB but from inside app only

/**
 * Created by Pardeep on 6/26/15.
 * Example CPU-observer for our CPU-benchmark
 */
public class ObserverCPU extends ObserverTemplate {

    private manageObservers ob1;
    private String fileName = null, fpath = "/sdcard/charsticks/";
    private FileWriter fw;
    private BufferedWriter bw;
    private File file;
    private static int count = 0;
    private double volt=0, curr=0, power=0;

    private long freeSize = 0L, totalSize = 0L, usedSize = -1L;
    private String s = null;
    private boolean isVolt = true;
    private String listenTo = "Capture data";
    //update coming from CPU
    private String testType = null;
    private boolean testStarted = false, testStopped = false;
    private Process process;
    private BufferedReader bufferedReader;
    //To run writing process as a thread. concurently to the benchmark
    Thread t;
    boolean threadRunning = false;
    private String sf = "", sf1 = "";

    //strings to get

    private static String samsung_Voltage_now = "cat /sys/class/power_supply/battery/voltage_now";
    private static String samsung_Current_now = "cat /sys/class/power_supply/battery/current_now";
    private static String samsung_Meminfo = "cat /proc/meminfo";

    /*
    This constructor could be used to create object of this class and run observer directly
     */
    public ObserverCPU(String testType, String name) {
        this.fileName = name;
        this.initializieFile();

        this.update(testType, this.testStarted, this.testStopped);


    }

    /*
    This contructor could be used to create objects binded with "AttributeGenerator" objec
     */
    public ObserverCPU(manageObservers ob1, String name) {
        this.ob1 = ob1;
        this.fileName = name;
        this.ob1.addObserver(this, listenTo);
        this.initializieFile();
    }

        //creates the directory and file to write in..
    public void initializieFile() {
        File sdCard = Environment.getExternalStorageDirectory();
        File file1 = new File(sdCard + "/Attribute-data");
        file1.mkdirs();

        this.fileName=this.fileName + ".txt";
        file = new File(sdCard,"/Attribute-data/"+this.fileName);
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

// start and stop test methods could be when running this cass independltly or in Unit testing ;) .
    public void startTest() {
        this.testStarted = true;
        this.testStopped = false;
        this.update(this.testType, this.testStarted, this.testStopped);
    }

    public void stopTest() {
        this.testStarted = false;
        this.testStopped = true;
        this.update(this.testType, this.testStarted, this.testStopped);
    }

    @Override
    public void run() {

        System.out.println("Collecting & writing data.." + this.testStarted);
        while (this.testStarted) {
            startObservation();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // set of methods to run in thread

    @Override
    public void startObservation() {

        //to get CPU stat i.e volt and current now via ADB
        getCPUStat();

        //to get CPU stat i.e volt and current now via API
        getCPUStat_AAPI();

        //memory usage of app and system
        getApplicationUsedMemorySize();
        getSystemUsedMemorySize();

        // CPU core usage in percentage
        readCoreUsage();

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void getCPUStat_AAPI() {


        BatteryManager b = new BatteryManager();

         {
            long current = b.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
            long power = b.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER);
            System.out.println("Current(microAMP) : " + current + " Power(nanowatts/hour) : " + power + " \n");
        }
    }


    public void getCPUStat() {

        try {
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            //running ADB command lines


            process = Runtime.getRuntime().exec(samsung_Voltage_now + "  " + samsung_Current_now);

            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            bw.write(Integer.toString(count));
            while ((s = bufferedReader.readLine()) != null) {
                //extracting voltage and current
                if (isVolt) {
                    volt = (Double.parseDouble(s)/1000000);
                    isVolt = false;
                    bw.write(" " + volt);
                } else {
                    curr = Math.abs(Double.parseDouble(s)/1000000);
                    isVolt = true;
                    bw.write(" " + curr);
                }
            }
            power = volt * curr;
           // System.out.println("Current(microAMP) -> " + curr + " Power(picowatts/Second) ->" + power + " \n");

            bw.write(" " + Double.toString(power));
            bw.close();
        } catch (IOException e) {
            System.out.println("IOException occured..");
            e.printStackTrace();
        }
    }


    public void getApplicationUsedMemorySize() {

        try {
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            Runtime info = Runtime.getRuntime();
            freeSize = (info.freeMemory()) / 1024 ;
            totalSize = (info.totalMemory()) / 1024;
            usedSize = totalSize - freeSize;
            bw.write(" " + (Long.toString(totalSize)));
            bw.write(" " + (Long.toString(freeSize)));
            bw.write(" " + (Long.toString(usedSize)));


            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getSystemUsedMemorySize() {

        try {
            String sf = "", sf1 = "";
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            process = Runtime.getRuntime().exec(samsung_Meminfo);
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((s = bufferedReader.readLine()) != null) {
                if (s.contains("MemTotal:")) {
                    for (String ss : s.split(" ")) {
                        if (isNumeric(ss)) {
                            sf = ss;
                        }
                    }
                }
                if (s.contains("MemFree:")) {
                    for (String ss : s.split(" ")) {
                        if (isNumeric(ss)) {
                            sf1 = ss;
                        }
                    }
                }
            }
            bw.write(" " + sf.trim());
            bw.write(" " + sf1.trim());
            usedSize = Integer.parseInt(sf.trim()) - Integer.parseInt(sf1.trim());
            bw.write(" " + usedSize);
//            count++;
//            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static boolean isNumeric(String str) {
        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(str, pos);
        return str.length() == pos.getIndex();
    }
    // reads usage of each core available. right now set to 8.(missing automation based on no. of available cores)
    private void readCoreUsage() {
        try {
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);

            int i = 0;
            float icore;
        while (i < 8) {
            icore = (readCore(i) * 100);

            //System.out.println("usage:" + i + " ->" + icore);//readUsage());
            bw.write(" " + icore);
            i += 1;
        }

            count++;
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//returns the core usage of ith core.
    private float readCore(int i)
    {

        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            for(int ii = 0; ii < i + 1; ++ii)
            {
                reader.readLine();
            }
            String load = reader.readLine();


            if(load.contains("cpu"))
            {
                String[] toks = load.split(" +");



                long work1 = Long.parseLong(toks[1])+ Long.parseLong(toks[2]) + Long.parseLong(toks[3]);
                long total1 = Long.parseLong(toks[1])+ Long.parseLong(toks[2]) + Long.parseLong(toks[3]) +
                        Long.parseLong(toks[4]) + Long.parseLong(toks[5])
                        + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

                try
                {

                    Thread.sleep(200);
                }
                catch (Exception e) {}

                reader.seek(0);
                //skip to the line we need
                for(int ii = 0; ii < i + 1; ++ii)
                {
                    reader.readLine();
                }
                load = reader.readLine();

                if(load.contains("cpu"))
                {
                    reader.close();
                    toks = load.split(" +");

                    long work2 = Long.parseLong(toks[1])+ Long.parseLong(toks[2]) + Long.parseLong(toks[3]);
                    long total2 = Long.parseLong(toks[1])+ Long.parseLong(toks[2]) + Long.parseLong(toks[3]) +
                            Long.parseLong(toks[4]) + Long.parseLong(toks[5])
                            + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
                    return (float)(work2 - work1) / ((total2 - total1));
                }
                else
                {
                    reader.close();
                    return 0;
                }

            }
            else
            {
                reader.close();
                return 0;
            }

        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        return 0;
    }
}

