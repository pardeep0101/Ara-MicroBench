package com.example.xaradrim.benchmark_example.Tests.DataLogging;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Pardeep on 7/29/15.
 */
public class CoreUtilization implements Runnable {

    int core = 0;
    float usage;
    RandomAccessFile reader;
    CoreUtilization(int core){
        this.core=core;
    }
    @Override
    public void run() {
        usage=  (readCore(core) * 100);
        //System.out.println("core usage for core "+core+" -> "+ usage);

    }
    public float getCoreUtilization() {
        return usage;
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

}
