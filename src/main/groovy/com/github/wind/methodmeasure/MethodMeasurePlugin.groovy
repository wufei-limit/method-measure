package com.github.wind.methodmeasure

import com.android.build.gradle.AppExtension
import com.github.wind.methodmeasure.utils.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project

class MethodMeasurePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        Properties properties = new Properties()
        boolean disablePlugin = false
        boolean logEnable = false
        if (project.rootProject.file('gradle.properties').exists()) {
            properties.load(project.rootProject.file('gradle.properties').newDataInputStream())
            disablePlugin = Boolean.parseBoolean(properties.getProperty("methodmeasure.disable", "false"))
            logEnable = Boolean.parseBoolean(properties.getProperty("methodmeasure.log", "false"))
            def loggerName = properties.getProperty("methodmeasure.logger", "")
            def methodName = properties.getProperty("methodmeasure.logmethd", "")
            MethodMeasureConfig.setLogName(loggerName)
            MethodMeasureConfig.setMethodName(methodName)
        }
        if (disablePlugin) {
            Logger.error("已关闭method measure plugin.")
            return
        }
        Logger.debug = logEnable
        AppExtension extension = project.getProperties().get("android")
        if (extension != null) {
            Logger.info("method measure plugin 已加载")
            def transform = new MethodMeasureTransform()
            extension.registerTransform(transform)
        }
    }
}