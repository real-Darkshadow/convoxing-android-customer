import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.google.firebase.firebase.perf)
}

android {
    namespace = "com.convoxing.convoxing_customer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.convoxing.convoxing_customer"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        create("internationalDebug") {
            initWith(getByName("debug"))
            getProps("${rootDir}/app/config/dev-env.properties").forEach { (key, value) ->
                buildConfigField("String", key, value)
            }
        }

        getByName("debug") {
            getProps("${rootDir}/app/config/dev-env.properties").forEach { (key, value) ->
                buildConfigField("String", key, value)
            }
        }

        getByName("release") {
            getProps("${rootDir}/app/config/prod-env.properties").forEach { (key, value) ->
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
    implementation(libs.transport.api)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    /*GOOGLE-PLAY CORE*/
    implementation(libs.review.ktx)
    implementation(libs.app.update.ktx)
    implementation(libs.firebase.crashlytics)
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

    /* GOOGLE CREDENTIAL MANAGER*/
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    /*networking*/
    implementation(libs.gson)
    implementation(libs.converter.gson)
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)

    /*DATADOG*/
    implementation(libs.dd.sdk.android.logs)

    /* AMPLITUDE*/
    implementation(libs.analytics.android)

    /* LOTTIE*/
    implementation(libs.lottie)

    /* CARD STACK*/
    implementation(libs.cardstackview)


}
// Allow references to generated code
kapt {
    correctErrorTypes = true
}

fun getProps(filePath: String): Map<String, String> {
    val file = file(filePath)
    if (!file.exists()) {
        println("Warning: Properties file not found: $filePath")
        return emptyMap()
    }

    val properties = Properties().apply {
        file.inputStream().use { load(it) }
    }
    return properties.entries.associate { it.key.toString() to it.value.toString() }
}