package com.example.xaradrim.benchmark_example.Tests;

/**
 * Created by Pardeep  on 6/26/15.
 * provides interface to the Testobserves to register with type of test they want to get notified for.
 */
public interface ObserveBench {

    //add Testobservers to whom to send notifictaion
    public void addObserver(TestObservers tObserve);

    //removes Testobservers
    public void removeObserver(TestObservers tObserve);

    //notifies all the registered or added Test Observers.
    public void notifyObserver();

}
