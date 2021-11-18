### MethodMeasure
一个简单的针对android 项目的Gradle插桩插件，用于衡量方法耗时。
插件会在方法开始时获取时间，并在结束时计算时间，并将类名与方法名传递给你，你需要声明一个用于接收这些信息的类，并声明接收的方法，方法参数是固定的三个，分别对应类名，方法名，方法耗时。如下所示
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

#用于接收上面所述三个参数的类，需要全路径限定
methodmeasure.logger=com.example.myapplication.Logger
#类中用于接收参数的方法
methodmeasure.logmethd=i

#是否开启编译日志 default log is false
methodmeasure.log=true
# 是否关闭插件 default is false
methodmeasure.disable=false
```

#### 引入插件

在项目的根目录下导入插件
```
#project build.gradle

buildscript {
    repositories {
        mavenCentral() //引入maven central 仓库
    }
    dependencies {
        //添加插件
        classpath "io.github.wufei-limit:methodmeasure:1.0.0"
    }
}


```
#### 在指定模块启用
在你想要使用的module 的build.gradle文件中启用

```
plugins {
    id 'com.github.wind.methodmeasure'
}

```