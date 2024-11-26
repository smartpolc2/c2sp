plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")

    kotlin("plugin.serialization") version "1.9.22"

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")

}

android {
    namespace = "com.isw.c2sp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.isw.c2sp"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation(platform("androidx.compose:compose-bom:2024.09.03"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("androidx.benchmark:benchmark-macro:1.3.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.firebase:firebase-database-ktx:21.0.0")
    implementation("com.google.firebase:firebase-common-ktx:21.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.03"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // maps compose
    implementation("com.google.maps.android:maps-compose:6.1.2")

    // google map services
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // google maps utils
    implementation("com.google.maps.android:android-maps-utils:3.8.2")

    //gson
    implementation ("com.google.code.gson:gson:2.11.0")

    //Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // navigation
    implementation ("androidx.navigation:navigation-compose:2.8.2")

    //firebase
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))


    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")


    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries
    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    //media
    //implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.media3:media3-exoplayer:1.4.1")
    implementation("androidx.media3:media3-ui:1.4.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.4.1")
    implementation("androidx.media3:media3-exoplayer-rtsp:1.4.1")

    // permissions
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")
}