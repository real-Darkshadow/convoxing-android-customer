import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.convoxing.convoxing_customer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.convoxing.convoxing_customer"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        create("internationalDebug") {
            initWith(getByName("debug"))
            getProps("./app/config/dev-env.properties").forEach { (key, value) ->
                buildConfigField("String", key, value)
            }
        }

        getByName("debug") {
            getProps("./app/config/dev-env.properties").forEach { (key, value) ->
                buildConfigField("String", key, value)
            }
        }

        getByName("release") {
            getProps("./app/config/prod-env.properties").forEach { (key, value) ->
                buildConfigField("String", key, value)
            }
            isShrinkResources = false
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
        viewBinding = true
        buildConfig = true
    }

}


dependencies {

    /*CORE*/
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    /*GOOGLE-PLAY CORE*/
    implementation(libs.review.ktx)
    implementation(libs.app.update.ktx)
    implementation(libs.feature.delivery.ktx)
    implementation(libs.installreferrer)
    implementation(libs.play.services.auth)
    implementation(libs.play.services.auth.api.phone)
    implementation(libs.tink.android)

    /* FIREBASE SDK.*/
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.inappmessaging.display.ktx)
    implementation(libs.firebase.perf.ktx)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.config.ktx)
    implementation(libs.firebase.dynamic.links.ktx)

    /*HILT*/
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    /*LOTTIE ANIMATION*/
    implementation(libs.lottie)

    /*DATA STORE*/
    implementation(libs.androidx.datastore.preferences)

    /*networking*/
    implementation(libs.gson)
    implementation(libs.converter.gson)
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)

    /*DATADOG*/
    implementation(libs.dd.sdk.android.logs)

    /* AMPLITUDE*/
    implementation(libs.analytics.android)


}
// Allow references to generated code
kapt {
    correctErrorTypes = true
}

fun Project.getProps(path: String): Map<String, String> {
    val propsFile = file(path)
    if (!propsFile.exists()) {
        logger.warn("Properties file not found: $path")
        return emptyMap()
    }

    val properties = Properties()
    propsFile.inputStream().use { inputStream ->
        properties.load(inputStream)
    }

    // Return each property as a key-value pair in a Map
    return properties.entries.associate { (key, value) ->
        // Surround the property value with quotes if you want string-literal style
        key.toString() to "\"$value\""
    }
}