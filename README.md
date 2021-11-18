### MethodMeasure
一个简单的针对android 项目的Gradle插桩插件，用于衡量方法耗时。
插件会获取在方法执行开始时获取时间，并在结束时计算时间，并将类名与方法名传递给你，你需要声明一个用于接收这些信息的类，并声明接收的方法，方法参数是固定的三个，分别对应类名，方法名，方法耗时。如下所示
```
package com.example.myapplication;

import android.os.Process;
import android.util.Log;

/**
 * Created by wufei on 2021/11/8
 */
public class Logger {
    public static void i(String className,String methodName,long castTime){
        int pid = Process.myPid();
        String threadName = Thread.currentThread().getName();
        String log = "pid:" +
                "[" + pid + "]" +
                "thread:" +
                threadName +
                "[" +
                className +
                ":" +
                methodName +
                "]" +
                "运行耗时:" +
                castTime + "ms";
        Log.i("MyApplication", log);
    }

```
使用时，在`gradle.property`文件中指定对应的类名与方法名
```
# gradle.property

methodmeasure.logger=com.example.myapplication.Logger
methodmeasure.logmethd=i
# default log is false
methodmeasure.log=true
# default is false
methodmeasure.disable=false
```