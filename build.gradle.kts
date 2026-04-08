// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        // Dagger Hilt
        classpath(libs.hilt.android.gradle.plugin)
    }

}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // Dagger Hilt
    alias(libs.plugins.dagger.hilt) apply false
    alias(libs.plugins.kotlin.ksp) apply false
}