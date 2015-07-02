package com.example.xaradrim.benchmark_example.Tests;

import java.util.ArrayList;

/**
 * Created by xaradrim on 5/30/15.
 */
public class Test {

    private ArrayList<Testable> testContainer;
    private ArrayList<Thread> bufferContainer;

    public Test() {
        testContainer = new ArrayList<Testable>();
        bufferContainer = new ArrayList<Thread>();
    }

    /**
     * make a tester based on the param string option
     * and return the tester object for it.
     *
     * @param param param must be a string one of the options to test
     * @return If found will return a object to fo the tester.
     * Otherwise it will return null for it couldnt find
     * a suitable option for testing.
     */
    public Testable make_test(String param) {
        param = param.toLowerCase();
        switch (param) {
            case "cpu":
                return (Testable) new Cpu();
        }
        return null;
    }

    public void add_test(Testable t) {
        if (t == null) return;

        this.testContainer.add(t);
        this.bufferContainer.add(new Thread(t));
    }

    public void start_test() {

        if (this.bufferContainer.isEmpty()) return;

        for (Thread t : this.bufferContainer) {
            t.start();
            //System.out.println("started");

        }
    }

    public void halt_execution() {

        if (this.testContainer.isEmpty()) return;

        for (Testable t : this.testContainer) {
            t.stop_test();
        }

        this.testContainer.clear();
        this.bufferContainer.clear();
    }

    /**
     * To be implemented =D
     */
    public void logTest() {

    }


}
