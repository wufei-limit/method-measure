package com.github.wind.methodmeasure;


/**
 * Created by jiaokang on 2021/11/8
 */
public class MethodMeasureConfig {
    private static String LOG_NAME = "";
    private static String METHOD_NAME = "i";

    public static String getLogName() {
        return LOG_NAME;
    }

    public static void setLogName(String logName) {
        if (null == logName || logName.length() == 0) {
            throw new RuntimeException("no logger set, please set logger for use");
        }
        LOG_NAME = logName.replace(".", "/");
        System.out.println("use logger "+LOG_NAME);
    }

    public static String getMethodName() {
        return METHOD_NAME;
    }

    public static void setMethodName(String methodName) {
        if (null == methodName || methodName.length() == 0)
            throw new RuntimeException("no method set, please set method for use");
//        Logger.info("use measure method: "+methodName);
        METHOD_NAME = methodName;
        System.out.println("use method "+METHOD_NAME);
    }
}
