package com.example.xaradrim.benchmark_example.Tests;

/**
 * Created by xaradrim on 5/29/15.
 */
public interface Testable extends Runnable {

    /**
     * This functions executes the testing of the components
     * this is a thread override function
     */
    public void run();

    /**
     *  Stop the testing ... it will stop if loop is running . Otherwise I
     *  cannot know if even needed at some point.
     */
    public void stop_test();

    /**
     * Tells if the test for this component is running
     * @return True if test is running false otherwise.
     */
    public boolean isTesting();
}
