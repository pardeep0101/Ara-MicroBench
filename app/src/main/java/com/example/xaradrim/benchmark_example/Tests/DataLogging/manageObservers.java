package com.example.xaradrim.benchmark_example.Tests.DataLogging;

/**
 * Created by Pardeep  on 6/26/15.
 * provides interface to the Testobserves to register with type of test they want to get notified for.
 */
public interface manageObservers {

    //add Testobservers to whom to send notifictaion
    public void addObserver(AttributeObserver tObserve, String listento);

    //removes Testobservers
    public void removeObserver(AttributeObserver tObserve);

    //notifies all the registered or added Test Observers.
    public void notifyObserver();

}
