// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.ksp.android) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    id("com.google.gms.google-services") version "4.6.0" apply false
}