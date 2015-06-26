package com.example.xaradrim.benchmark_example.Tests;

import java.util.ArrayList;

/**
 * Created by xaradrim on 5/30/15.
 */
public class Test implements ObserveBench{

    private ArrayList<Testable> testContainer;
    private ArrayList<Thread> bufferContainer;
    private ArrayList<TestObservers> tObserve ;
    private String testType = null;
    private boolean testStarted = false,testStopped = true;
    public Test(){
        testContainer = new ArrayList<Testable>();
        bufferContainer = new ArrayList<Thread>();
        tObserve = new ArrayList<TestObservers>();
    }
    //
    /**
     * make a tester based on the param string option
     * and return the tester object for it.
     *
     * @param param
     * param must be a string one of the options to test
     *
     * @return
     *
     * If found will return a object to fo the tester.
     * Otherwise it will return null for it couldnt find
     * a suitable option for testing.
     *
     */
    public Testable make_test(String param){
        testType = param;
        param = param.toLowerCase();

        switch (param){
            case "cpu":
                return (Testable)new Cpu();
        }


        return null ;
    }

    public void add_test(Testable t){
        if (t == null )return ;

        this.testContainer.add(t);
        this.bufferContainer.add(new Thread(t));
    }

    public void start_test(){
        notifyObserver();
        if (this.bufferContainer.isEmpty()) return ;

        for (Thread t : this.bufferContainer){
            t.start();
            testStarted=true;testStopped=false;
            System.out.println("started");
            notifyObserver();
        }
    }
    public void halt_execution(){
        notifyObserver();
        if (this.testContainer.isEmpty()) return ;

        for (Testable t : this.testContainer){
            t.stop_test();testStarted=false;
            testStopped=true;
        }
        notifyObserver();
        this.testContainer.clear();
        this.bufferContainer.clear();
    }
    /**
     * To be implemented =D
     */
    public void logTest(){

    }

    @Override
    public void addObserver(TestObservers to) {
        System.out.println("observer added in test class");
        this.tObserve.add(to);

    }

    @Override
    public void removeObserver(TestObservers to) {
        int index = this.tObserve.indexOf(tObserve);
        this.tObserve.remove(index);
    }

    @Override
    public void notifyObserver() {
        System.out.println("notified ..");
        for(TestObservers to: this.tObserve){
            to.update(testType,testStarted, testStopped);
        }

    }
}
