plugins {
    id ("com.android.application")
    id ("kotlin-android")
    id ("kotlin-parcelize")
}

android {
    namespace = "com.cexup.meet"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.cexup.meet"
        minSdk = 24
        targetSdk = 33
        versionCode = 2
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures{
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled   = true
            proguardFiles (getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}


dependencies {

    //Image Picker
    implementation ("com.github.dhaval2404:imagepicker:2.1")

    //datastore
    implementation ("androidx.datastore:datastore-preferences:1.0.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    //networking
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    //glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    implementation (fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    val agora_sdk_version = "4.1.1"

    // agora Rtm
    implementation ("io.agora.rtm:rtm-sdk:1.4.10")

    // case 1: full libs
    implementation ("io.agora.rtc:full-sdk:${agora_sdk_version}")
    implementation ("io.agora.rtc:full-screen-sharing:${agora_sdk_version}")


    // case 2: partial libs
    // implementation "io.agora.rtc:full-rtc-basic:${agora_sdk_version}"
    // implementation "io.agora.rtc:ains:${agora_sdk_version}"
    // implementation "io.agora.rtc:full-content-inspect:${agora_sdk_version}"
    // implementation "io.agora.rtc:full-virtual-background:${agora_sdk_version}"
    // implementation "io.agora.rtc:full-super-resolution:${agora_sdk_version}"
    // implementation "io.agora.rtc:spatial-audio:${agora_sdk_version}"
    // implementation "io.agora.rtc:audio-beauty:${agora_sdk_version}"
    // implementation "io.agora.rtc:clear-vision:${agora_sdk_version}"
    // implementation "io.agora.rtc:pvc:${agora_sdk_version}"
    // implementation "io.agora.rtc:screen-capture:${agora_sdk_version}"
    // implementation "io.agora.rtc:aiaec:${agora_sdk_version}"
    // implementation "io.agora.rtc:drm-loader:${agora_sdk_version}"
    // implementation "io.agora.rtc:drm:${agora_sdk_version}"
    // implementation "io.agora.rtc:full-vqa:${agora_sdk_version}"

    implementation ("androidx.core:core-ktx:1.8.0")
    implementation ("androidx.appcompat:appcompat:1.4.1")
    implementation ("com.google.android.material:material:1.5.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.3")
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.3")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.4.0")


}