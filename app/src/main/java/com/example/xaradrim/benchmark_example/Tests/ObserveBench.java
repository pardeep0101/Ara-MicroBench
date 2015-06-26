package com.example.xaradrim.benchmark_example.Tests;

/**
 * Created by larsip-ubuntu-2 on 6/26/15.
 */
public interface ObserveBench {

    public void addObserver(TestObservers tObserve);

    public void removeObserver(TestObservers tObserve);

    public void notifyObserver();

}
