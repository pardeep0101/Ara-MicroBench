package com.example.xaradrim.benchmark_example.Tests.DataLogging;

import java.util.ArrayList;

import android.os.Build;
import android.os.Build.VERSION;
import 	android.os.Build.VERSION_CODES;

/**
 * Created by Pardeep on 7/2/15.
 * Right now this class is just an interface of ObserverCPU.java to the main activity.
 * This class could come handy in future if we need to expand our model of capturing data or if we have separate observers running at the same time.
 */
public class AttributeGenerator implements Runnable, manageObservers{

    private static AttributeGenerator attributeGenerator = new AttributeGenerator();


    private String observeType;
    private boolean observeStarted =false, observeStopped=false;
    private int apiLevel = 0;
    private ArrayList<String> currentObserver = new ArrayList<String>();
    private ArrayList<AttributeObserver> tObserve;
    private ArrayList<String> observerTypeList = new ArrayList<String>();
    private ArrayList<String> runObservers = new ArrayList<String>();


    public AttributeGenerator(){
        tObserve=new ArrayList<AttributeObserver>();
        initializeObserver();
    }

    public static AttributeGenerator getInstance(){
        return attributeGenerator;
    }
    private void initializeObserver(){
        ObserverTemplate cpu = new ObserverCPU(this, "Attributes-CPU-observation-log");
        //ObserverTemplate mem = new ObserverMemory(this,"Attributes-MEM-observation-log");
    }

    public void addAttributeList(String observersName){
        this.currentObserver.add(observersName);
    }
    public void emptyAttributeList(){
        System.out.println("stopping attributes");

        for(int i=0;i<observerTypeList.size();i++) {
            this.observeType=observerTypeList.get(i);this.observeStarted=false;this.observeStopped=true;
        }
        notifyObserver();
        this.currentObserver.clear();
        this.runObservers.clear();

    }
    public void checkPlatform(){
        this.apiLevel = VERSION.SDK_INT;
        System.out.println(this.apiLevel);
    }

    public void prepareAttributes(){
        for(int i=0;i<currentObserver.size();i++) {
            for(int j =0; j<observerTypeList.size();j++) {
                if (currentObserver.get(i).contains(observerTypeList.get(j))) {
                    System.out.println("run observer for -> " + observerTypeList.get(j));
                    this.observeType = observerTypeList.get(j);
                    this.observeStarted = true;
                    this.observeStopped = false;
                    System.out.println(this.observeType + " " + this.observeStarted + " " + this.observeStopped);
                    notifyObserver();
                } else {
                    //System.out.println("observer for such is not available");
                }
            }
        }


    }





    @Override
    public void run() {

    }


    @Override
    public void addObserver(AttributeObserver tObserve, String observerType) {
        //System.out.println("observer added in test class");
        this.tObserve.add(tObserve);
        this.observerTypeList.add(observerType);
//        this.observerTypeList.add("Capture Mem data");
//        this.currentObserver.add("Capture Mem data");
    }

    @Override
    public void removeObserver(AttributeObserver tObserve) {

        int index = this.tObserve.indexOf(tObserve);
        this.tObserve.remove(index);
    }

    @Override
    public void notifyObserver() {
        for(AttributeObserver to: this.tObserve){
            to.update(this.observeType,this.observeStarted, this.observeStopped);
        }
    }
}
