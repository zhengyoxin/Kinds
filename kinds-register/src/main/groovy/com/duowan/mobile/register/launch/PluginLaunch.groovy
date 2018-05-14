package com.duowan.mobile.register.launch

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.duowan.mobile.register.util.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project

class PluginLaunch implements Plugin<Project> {

    @Override
    void apply(Project project) {
        Logger.make(project)
        try {
            def android = project.extensions.getByType(AppExtension)
            android.applicationVariants.all { variant ->
                variant.buildConfigField 'String[]', 'FEATURE_INJECT_NAME_ARRAY', getAllProjectName(project)
                variant.javaCompileOptions.annotationProcessorOptions.arguments.put 'FeatureModuleName', project.getName()
                variant.javaCompileOptions.annotationProcessorOptions.arguments.put 'FeatureProjectName', project.rootProject.project.getName()
            }
        } catch (Exception ignored){

        }

        try {
            def library = project.extensions.getByType(LibraryExtension)
            library.libraryVariants.all { variant ->
                variant.buildConfigField 'String[]', 'FEATURE_INJECT_NAME_ARRAY', getAllProjectName(project)
                variant.javaCompileOptions.annotationProcessorOptions.arguments.put 'FeatureModuleName', project.getName()
                variant.javaCompileOptions.annotationProcessorOptions.arguments.put 'FeatureProjectName', project.rootProject.project.getName()
            }
        } catch (Exception ignored){

        }
    }

    static def getAllProjectName(Project project) {
        Logger.i("getAllProjectName")
        StringBuffer result = new StringBuffer("{")
        Set<String> keySet = project.rootProject.childProjects.keySet()
        for (int i = 0; i < keySet.size(); i++) {
            if (i > 0) {
                result.append(",")
            }
            result.append("\"").append(project.rootProject.project.getName()).append("\$\$").append(keySet[i]).append("\"")
        }
        result.append("}")
        return result.toString()
    }

}