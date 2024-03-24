package com.autoworld.Reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ExtentTestManager {

    private static ExtentReports extent;
    private static HashMap<Integer, ExtentTest> extentTestMap=new HashMap<>();
    private static Set<Integer> extendThreadList=new HashSet<>();

    private ExtentTestManager(){}
    private static synchronized int getCurrentThread(){
        return (int)Thread.currentThread().getId();
    }
    public static synchronized ExtentTest getTest(){
        return extentTestMap.get(getCurrentThread());
    }

    public static synchronized ExtentTest startTest(String testName,String desc){
        System.out.println("Start Test: "+Thread.currentThread().getId());
        extent=ExtentConfiguration.getInstance();
        ExtentTest test=extent.createTest(testName,desc);
        extendThreadList.add(getCurrentThread());
        extentTestMap.put(getCurrentThread(),test);
        return test;
    }

    public static synchronized void endTest(){
        System.out.println("End Test: "+Thread.currentThread().getId());
        extendThreadList.remove(getCurrentThread());
        if(!extentTestMap.isEmpty() && extendThreadList.isEmpty()){
            Iterator<Integer> it=extentTestMap.keySet().iterator();
            while (it.hasNext()){
                extent.removeTest(extentTestMap.get(it.next()));
            }
            System.out.println(extentTestMap);
        }

    }

}
