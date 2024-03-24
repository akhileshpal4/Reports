package com.autoworld.Reports;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.log4testng.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TestListener implements ITestListener {

    private static Logger logger=Logger.getLogger(TestListener.class);
    private Map<String,String> allParameter=new HashMap<>();
    private Map<String,String> suiteParameter=new HashMap<>();
    private Map<String,String> localParameter=new HashMap<>();
    private ArrayList<String> fileList=new ArrayList<>();

    public TestListener(){}
    public Map<String,String> getAllParameter(){return allParameter;}
    public Map<String,String> getSuiteParameter(){return allParameter;}
    public Map<String,String> getLocalParameter(){return allParameter;}

    private static String getTestMethodName(ITestResult iTestResult){
        return iTestResult.getMethod().getConstructorOrMethod().getName();
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTestManager.startTest(result.getParameters()[0].toString().replaceAll("\"",""),result.getParameters()[1].toString().replaceAll("\"",""));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info(result.getName()+" Passed successfully!!");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.warn(getTestMethodName(result)+" failed");
        if(ExtentTestManager.getTest()!=null){
            if(result.getThrowable().toString().contains("java.lang.AssertionError")){
                String errMsg=result.getThrowable().getMessage();
                try {
                    ExtentTestManager.getTest().log(Status.FAIL,"Test Step Failed due to following error: "+errMsg.substring(0,errMsg.indexOf("expected")-1).trim(), MediaEntityBuilder.createScreenCaptureFromPath(takeScreenShot()).build());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {
                ExtentTestManager.getTest().log(Status.FAIL,"Test Step Failed: "+result.getThrowable());
            }
        }
    }

    protected static String takeScreenShot(){
        String timestamp=(new SimpleDateFormat("yyyyMMdd_HHmmss")).format(Calendar.getInstance().getTime());
        String imageName="c:\\temp\\"+timestamp+".png";
        BufferedImage image=null;

        try {
            Robot rb = new Robot();
            Rectangle rectangle=new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            image=rb.createScreenCapture(rectangle);
            ImageIO.write(image,"png",new File(imageName));
        }catch (AWTException | IOException e) {
            throw new RuntimeException(e);
        }
        return imageName;
    }
    @Override
    public void onTestSkipped(ITestResult result) {
        if(ExtentTestManager.getTest()!=null){
            ExtentTestManager.getTest().log(Status.SKIP,result.getName()+" execution got skipped");
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        ITestListener.super.onTestFailedButWithinSuccessPercentage(result);
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        ITestListener.super.onTestFailedWithTimeout(result);
    }

    @Override
    public void onStart(ITestContext context) {
        this.allParameter=context.getSuite().getXmlSuite().getAllParameters();
        this.suiteParameter=context.getSuite().getXmlSuite().getParameters();
        this.localParameter=context.getCurrentXmlTest().getLocalParameters();
    }

    @Override
    public void onFinish(ITestContext context) {
      ExtentConfiguration.getInstance().flush();
      ExtentTestManager.endTest();
     // compressDirectory("AutomationReports","AutomationReports.zip");
    }

    private void compressDirectory(String dir,String zipFile){
        File directory=new File(dir);
        getFileList(directory);
        try{
            FileOutputStream fos=new FileOutputStream(zipFile);
            try{
                ZipOutputStream zos=new ZipOutputStream(fos);

                try{
                    Iterator it= fileList.iterator();
                    while (it.hasNext()){
                        String filePath=(String)it.next();
                        System.out.println("Compressing: "+filePath);
                        String name=filePath.substring(directory.getAbsolutePath().length()+1);
                        ZipEntry zipEntry=new ZipEntry(name);
                        zos.putNextEntry(zipEntry);
                        try{
                            FileInputStream fis=new FileInputStream(filePath);
                            try{
                                byte[] buffer=new byte[1024];
                                while (true){
                                    int length;
                                    if((length=fis.read(buffer))<=0){
                                        zos.closeEntry();
                                        break;
                                    }
                                    zos.write(buffer,0,length);
                                }

                            }catch (Throwable th){
                                try {
                                    fis.close();
                                }catch (Throwable th1){
                                    th.addSuppressed(th1);
                                }
                                throw th;
                            }
                            fis.close();

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                }catch (Throwable th){
                    try {
                        zos.close();
                    } catch (Throwable th1) {
                        th.addSuppressed(th1);
                    }
                    throw th;
                }
                zos.close();
            }catch (Throwable th){
                try{
                    fos.close();
                }catch (Throwable th1){
                    th.addSuppressed(th1);
                }
                throw th;
            }
            fos.close();
        }catch (IOException e) {
           e.printStackTrace();
        }

    }

    private void getFileList(File directory){
        File[] files=directory.listFiles();
        if(files!=null && files.length>0){
            for(int i=0;i<files.length;i++){
                if(files[i].isFile()){
                    this.fileList.add(files[i].getAbsolutePath());
                }else {
                    getFileList(files[i]);
                }
            }
        }
    }
}
