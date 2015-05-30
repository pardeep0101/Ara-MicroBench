package com.example.xaradrim.benchmark_example.Tests;

/**
 * Created by xaradrim on 5/30/15.
 */
public class Test {

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
        param = param.toLowerCase();

        switch (param){
            case "cpu":
                return (Testable)new Cpu();
        }


        return null ;
    }

    /**
     * To be implemented =D
     */
    public void logTest(){

    }
}
