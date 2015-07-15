package com.example.xaradrim.benchmark_example.Tests.DataLogging;

/**
 * Created by Pardeep on 7/2/15.
 */
public abstract class ObserverTemplate implements AttributeObserver, Runnable  {


    abstract public void initializieFile();
    abstract public void startTest();
    abstract public void stopTest();
    abstract public void startObservation();



}

