package com.autoworld.Reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Protocol;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.log4testng.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentConfiguration {

    private static ExtentReports extent;
    private static final String TIME_STAMP;
    private static final String WORKING_DIR;
    private static final String EXTENT_REPORTS_FOLDER;
    private static final String REPORT_NAME;
    private static final String EXTENT_REPORTS_PATH;
    private static Logger logger;

    static {
        TIME_STAMP=new SimpleDateFormat("dd.MM.yyy.HH.mm").format(new Date());
        WORKING_DIR=System.getProperty("user.dir");
        EXTENT_REPORTS_FOLDER=WORKING_DIR+"/AutomationReports";
        REPORT_NAME="ExtentReport_"+TIME_STAMP+"_"+Thread.currentThread().getId()+".html";
        EXTENT_REPORTS_PATH=EXTENT_REPORTS_FOLDER+ File.separator+REPORT_NAME;
        logger=Logger.getLogger(ExtentConfiguration.class);
    }
    private ExtentConfiguration(){}

    public static ExtentReports getInstance(){
        if(extent==null){
            createReportFolder();
            attachReporter();
        }
        return extent;
    }

    private static void createReportFolder(){
        File file=new File(EXTENT_REPORTS_FOLDER);
        if(!file.exists() && !file.mkdir()){
            logger.warn("Failed to create directory");
        }
    }

    private static ExtentSparkReporter initHtmlReporter(){
        ExtentSparkReporter htmlReporter=new ExtentSparkReporter(EXTENT_REPORTS_PATH);
        htmlReporter.config().setTheme(Theme.STANDARD);
        htmlReporter.config().setDocumentTitle(REPORT_NAME);
        htmlReporter.config().setEncoding("utf-8");
        htmlReporter.config().setReportName("Execution-Status");
        htmlReporter.config().setCss("css-string");
        htmlReporter.config().setJs("js-string");
        htmlReporter.config().setProtocol(Protocol.HTTPS);
        htmlReporter.config().setTimeStampFormat("MMM dd,yyyy HH:mm:ss");
        htmlReporter.config().setTimelineEnabled(true);
        return htmlReporter;
    }

    private static void attachReporter(){
        extent=new ExtentReports();
        extent.attachReporter(initHtmlReporter());
    }
}
