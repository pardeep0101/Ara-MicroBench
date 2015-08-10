package com.example.xaradrim.benchmark_example.Tests.DataLogging;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;

import com.example.xaradrim.benchmark_example.MainActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.NumberFormat;
import java.text.ParsePosition;

// moto- via API
// Samsung s6 both via API and ADB
// nexus s via ADB but from inside app only Voltage

/**
 * Created by Pardeep on 6/26/15.
 * Example CPU-observer for our CPU-benchmark
 */
public class ObserverMain extends ObserverTemplate {

    private static int count = 0;
    private static String samsung_Voltage_now = "/sys/class/power_supply/battery/voltage_now";
    private static String samsung_Current_now = "/sys/class/power_supply/battery/current_now";
    private static String samsung_Meminfo = "cat /proc/meminfo";
    //To run writing process as a thread. concurently to the benchmark
    Thread t;
    boolean threadRunning = false;
    CoreUtilization[] cu1 = new CoreUtilization[8];
    Thread[] th1 = new Thread[8];
    private String s = null, sf = "", sf1 = "", listenTo = "Capture data", fileName = null;
    private manageObservers ob1;
    private FileWriter fw;
    private BufferedWriter bw;
    private File file;
    private double volt = 0, curr = 0, power = 0;
    private long freeSize = 0L, totalSize = 0L, usedSize = -1L, sUsedSize = -1L;

    //update coming from update()
    private String testType = null;
    private boolean testStarted = false, testStopped = false;
    private float[] icore = new float[8];
    private String[] icpuFreq = new String[8];
    private int no_of_core = 0;

    //strings to get
    private Process process;
    private BufferedReader bufferedReader;
    private RandomAccessFile reader;

    private MainActivity ma = null;

    /*
    This constructor could be used to create object of this class and run observer directly
     */
    public ObserverMain(String testType, String name) {
        this.fileName = name;
        this.initializieFile();

        //if there is a need to run this class as a thread and get a file we can init update method from here
        //or we can imply comment this line and call each method independtly

        //this.update(testType, this.testStarted, this.testStopped, this.ma);

        //find the number of cores
        this.numberOfCore();
    }

    /*
    This contructor could be used to create objects binded with "AttributeGenerator" objec
     */
    public ObserverMain(manageObservers ob1, String name) {
        this.ob1 = ob1;
        this.fileName = name;
        this.ob1.addObserver(this, listenTo);
        this.initializieFile();

        //find the number of cores
        this.numberOfCore();
    }

    public static boolean isNumeric(String str) {
        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(str, pos);
        return str.length() == pos.getIndex();
    }

    //creates the directory and file to write in..
    public void initializieFile() {
        File sdCard = Environment.getExternalStorageDirectory();
        File file1 = new File(sdCard + "/Attribute-data");
        file1.mkdirs();

        this.fileName = this.fileName + ".txt";
        file = new File(sdCard, "/Attribute-data/" + this.fileName);
        // If file does not exists, then create it
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("file IO" + e);
        }
    }

    @Override
    public void update(String testType, boolean testStarted, boolean testStopped, Intent b) {
        //System.out.println("parameteres rec:" + this.testType + " " + this.testStarted + " " + this.testStopped);
        this.testType = testType;
        this.testStarted = testStarted;
        this.testStopped = testStopped;
        this.b = b;
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

    // set of methods to run in thread
    // start and stop test methods could be when running this cass independltly or in Unit testing ;) .
    public void startTest() {
        this.testStarted = true;
        this.testStopped = false;
        this.update(this.testType, this.testStarted, this.testStopped, this.b);
    }

    public void stopTest() {
        this.testStarted = false;
        this.testStopped = true;
        this.update(this.testType, this.testStarted, this.testStopped, this.b);
    }

    @Override
    public void run() {

        System.out.println("Collecting & writing data.." + this.testStarted);
        while (this.testStarted) {
            startObservation();
            // this sleep time is removed as 200ms sleep time is induced in readCoreUsage() for each core.

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void startObservation() {
        if (MainActivity.phoneType != null) {
            if (MainActivity.phoneType.contains("moto")) {
                //to get CPU stat i.e volt and current now via API
                getPowerStat_AAPI();

            } else if (MainActivity.phoneType.contains("samsung")) {
                //to get CPU stat i.e volt and current now via ADB
                getPowerStat_AAPI();
            } else {

                // System.out.println("No CPU stat data for Nexus ");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //memory usage of app and system
            getApplicationUsedMemorySize();
            getSystemUsedMemorySize();

            //getCoreFrequency();

            // CPU core usage in percentage

//            readCoreUsage(); //takes 200 ms for each core.

            readCoreUsage_Threadtest();

            writeToFile();

        } else {

            //if ma is not available then no voltage can be obtained via api so need to try with adb
            getPowerStat_ADB();
            //memory usage of app and system
            getApplicationUsedMemorySize();
            getSystemUsedMemorySize();

            //getCoreFrequency();

            // CPU core usage in percentage
//            readCoreUsage(); //takes 200 ms for each core.
            readCoreUsage_Threadtest();
            writeToFile();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void getPowerStat_AAPI() {


        BatteryManager b = new BatteryManager();

        {
            curr = ((Double.parseDouble(Integer.toString((b.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW))))) / 1000000);
            volt = (this.b.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)) / 1000;
            power = curr * volt;
//            System.out.println("Current(microAMP) : " + curr + " Power(W/Sec) : " + power + " \n");
        }
    }

    public void getPowerStat_ADB() {

        try {
            reader = new RandomAccessFile(samsung_Voltage_now, "r");
            s = reader.readLine();
            volt = (Double.parseDouble(s) / 1000000);
            reader = new RandomAccessFile(samsung_Current_now, "r");
            s = reader.readLine();
            curr = (Double.parseDouble(s) / 1000000);
            power = volt * curr;
//             System.out.println("Current(microAMP) -> " + curr + " Power(W/Sec) ->" + power + " \n");

        } catch (IOException e) {
            System.out.println("IOException occured..");
            e.printStackTrace();
        }finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void getApplicationUsedMemorySize() {

        try {
            Runtime info = Runtime.getRuntime();
            freeSize = (info.freeMemory()) / 1024;
            totalSize = (info.totalMemory()) / 1024;
            usedSize = totalSize - freeSize;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getSystemUsedMemorySize() {

        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
            s =reader.readLine();
            String [] ss = s.split(" +");
            sf=ss[1];
            s=reader.readLine();
            ss = s.split(" +");
            sf1=ss[1];
            sUsedSize = Integer.parseInt(sf.trim()) - Integer.parseInt(sf1.trim());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public void numberOfCore() {
        File folder = new File("/sys/devices/system/cpu/");
        File[] listOfFiles = folder.listFiles();
        no_of_core = 0;
        for (int i = 0; i < listOfFiles.length; i++) {
            for (int j = 0; j < 8; j++) {
                if (listOfFiles[i].getName().contains("cpu" + j)) {
                    //  System.out.println("Directory " + listOfFiles[i].getName());
                    no_of_core += 1;
                }
            }
        }


        }
    }

    // reads usage of each core available. right now set to 8.(missing automation based on no. of available cores)
    private void readCoreUsage() {
        int i = 0;
        while (i < no_of_core) {
            icore[i] = (readCore(i) * 100);
            readCpuFrequency(i);
            i += 1;
        }
//        for(float j : icore){
//            System.out.println("core usage" + j);
//        }


    }

    private void readCoreUsage_Threadtest() {
        int i = 0;

        while (i < no_of_core) {
            cu1[i] = new CoreUtilization(i);
            //new Thread(cu1[i]).start();
            th1[i] = new Thread(cu1[i]);
            th1[i].start();
            i += 1;
        }
        i = 0;
        waitForCoreUsageThreads();
        while (i < no_of_core) {
            icore[i] = cu1[i].getCoreUtilization();
            //System.out.println("core usage "+i +"-> "+ icore[i]);

            readCpuFrequency(i);
            i += 1;
        }

    }

    private void waitForCoreUsageThreads() {
        for (int i = 0; i < no_of_core; i++) {
            try {
                th1[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


    public void readCpuFrequency(int i) {
        try {
            String file = "/sys/devices/system/cpu/cpu";
            file = file + i;
            file = file + "/cpufreq/scaling_cur_freq";
            reader = new RandomAccessFile(file, "r");
            s = reader.readLine();
            icpuFreq[i] = s;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    //returns the core usage of ith core.
    private float readCore(int i) {

        try {
            reader = new RandomAccessFile("/proc/stat", "r");
            for (int ii = 0; ii < i + 1; ++ii) {
                reader.readLine();
            }
            String load = reader.readLine();
            if (load.contains("cpu")) {
                String[] toks = load.split(" +");
                long work1 = Long.parseLong(toks[1]) + Long.parseLong(toks[2]) + Long.parseLong(toks[3]);
                long total1 = Long.parseLong(toks[1]) + Long.parseLong(toks[2]) + Long.parseLong(toks[3]) +
                        Long.parseLong(toks[4]) + Long.parseLong(toks[5])
                        + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
                try {

                    Thread.sleep(200);
                } catch (Exception e) {
                }
                reader.seek(0);
                //skip to the line we need
                for (int ii = 0; ii < i + 1; ++ii) {
                    reader.readLine();
                }
                load = reader.readLine();

                if (load.contains("cpu")) {
                    reader.close();
                    toks = load.split(" +");

                    long work2 = Long.parseLong(toks[1]) + Long.parseLong(toks[2]) + Long.parseLong(toks[3]);
                    long total2 = Long.parseLong(toks[1]) + Long.parseLong(toks[2]) + Long.parseLong(toks[3]) +
                            Long.parseLong(toks[4]) + Long.parseLong(toks[5])
                            + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
                    return (float) (work2 - work1) / ((total2 - total1));
                } else {
                    reader.close();
                    return 0;
                }

            } else {
                reader.close();
                return 0;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    private void writeToFile() {
        try {
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write(count + "," + volt + "," + curr + "," + power + "," + totalSize + "," + freeSize + "," + usedSize + "," + sf.trim() + ","
                    + sf1.trim() + "," + sUsedSize);

            for (int i = 0; i < no_of_core; i++) {
                bw.write("," + icore[i]);
            }

            for (int i = 0; i < no_of_core; i++) {
                bw.write("," + icpuFreq[i]);
            }
            bw.newLine();
            bw.close();
            this.count++;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

