plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.lightappcollege"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.lightappcollege"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("org.jsoup:jsoup:1.17.2")
    implementation ("androidx.cardview:cardview:1.0.0") // Добавьте эту строку, если CardView еще не используется
    implementation ("com.google.android.material:material:1.12.0") // Убедитесь, что у вас есть Material Design библиотека

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}