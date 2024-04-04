import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.wire)
}

val sha: String? = System.getenv("GITHUB_SHA")
val isCI: String? = System.getenv("CI")
val isSelfBuild = isCI.isNullOrEmpty() || !isCI.equals("true", ignoreCase = true)
val applicationVersionCode = property.versionCode
var applicationVersionName = property.versionName
val isPerVersion = property.isPreRelease
if (isPerVersion) {
    applicationVersionName += "-${property.preReleaseName}.${property.preReleaseVer}"
}
if (!isSelfBuild && !sha.isNullOrEmpty()) {
    applicationVersionName += "+${sha.substring(0, 7)}"
}

wire {
    sourcePath {
        srcDir("src/main/protos")
    }

    kotlin {
        android = true
    }
}

android {
    val packageName = "com.huanchengfly.tieba.post"
    buildToolsVersion = "34.0.0"
    compileSdk = 34
    namespace = packageName
    defaultConfig {
        applicationId = packageName
        minSdk = 21
        targetSdk = 34
        versionCode = applicationVersionCode
        versionName = applicationVersionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        manifestPlaceholders["is_self_build"] = "$isSelfBuild"
    }
    buildFeatures {
        compose = true
    }
    signingConfigs {
        if (property.keystore.file.isNotBlank()) {
            create("config") {
                storeFile = file(File(rootDir, property.keystore.file))
                storePassword = property.keystore.password
                keyAlias = property.keystore.key.alias
                keyPassword = property.keystore.key.password
                enableV1Signing = true
                enableV2Signing = true
                enableV3Signing = true
                enableV4Signing = true
            }
        }
    }
    buildTypes {
        all {
            val configName = if (signingConfigs.any { it.name == "config" }) {
                "config"
            } else {
                "debug"
            }
            signingConfig = signingConfigs.getByName(configName)
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            isDebuggable = false
            isJniDebuggable = false
            multiDexEnabled = true
        }
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        val buildDir = project.layout.buildDirectory.asFile.get().absolutePath
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$buildDir/compose_metrics"
        )
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=$buildDir/compose_metrics"
        )
        freeCompilerArgs += listOf(
            "-P", "plugin:androidx.compose.compiler.plugins.kotlin:stabilityConfigurationPath=" +
                    project.rootDir.absolutePath + "/compose_stability_configuration.txt"
        )
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "DebugProbesKt.bin"
        }
    }

    applicationVariants.configureEach {
        val variant = this
        outputs.configureEach {
            val fileName = "${variant.buildType.name}-${applicationVersionName}(${applicationVersionCode}).apk"
            (this as BaseVariantOutputImpl).outputFileName = fileName
        }
    }
}

dependencies {
    // Local Files
//    implementation fileTree(include: ["*.jar"], dir: "libs")

    implementation(libs.swiftzer.semver)
    implementation(libs.godaddy.color.picker)

    implementation(libs.airbnb.lottie)
    implementation(libs.airbnb.lottie.compose)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.collections.immutable)

    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)

    implementation(libs.compose.destinations.core)
    ksp(libs.compose.destinations.ksp)

    implementation(libs.androidx.navigation.compose)

    api(libs.wire.runtime)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.androidx.hilt.compiler)

    implementation(libs.accompanist.drawablepainter)
    implementation(libs.accompanist.insets.ui)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.placeholder.material)

    implementation(libs.sketch.core)
    implementation(libs.sketch.compose)
    implementation(libs.sketch.extensions.compose)
    implementation(libs.sketch.gif)
    implementation(libs.sketch.okhttp)

    implementation(libs.zoomimage.compose.sketch)

    implementation(platform(libs.compose.bom))
    androidTestImplementation(platform(libs.compose.bom))

    runtimeOnly(libs.compose.runtime.tracing)
    implementation(libs.compose.animation)
    implementation(libs.compose.animation.graphics)
    implementation(libs.compose.material)
    implementation(libs.compose.material.icons.core)
    // Optional - Add full set of material icons
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.ui.util)
//    implementation "androidx.compose.material3:material3"

    // Android Studio Preview support
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)

    // UI Tests
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugRuntimeOnly(libs.compose.ui.test.manifest)

    implementation(libs.androidx.constraintlayout.compose)

    implementation(libs.github.oaid)

    implementation(libs.jetbrains.annotations)

    // implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // AndroidX
    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.gridlayout)
    implementation(libs.androidx.palette)
    implementation(libs.androidx.window)
    implementation(libs.androidx.startup.runtime)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestRuntimeOnly(libs.androidx.test.runner)

    // Glide
    implementation(libs.glide.core)
    // ksp(libs.glide.ksp)
    implementation(libs.glide.okhttp3.integration)

    implementation(libs.google.material)

    implementation(libs.okhttp3.core)
    implementation(libs.retrofit2.core)
    implementation(libs.retrofit2.converter.wire)

    implementation(libs.google.gson)
    implementation(libs.litepal)
    implementation(libs.jaredrummler.colorpicker)

    implementation(libs.matisse)
    implementation(libs.xxpermissions)
    implementation(libs.immersionbar)

    implementation(libs.ucrop)

    implementation(libs.butterknife)
    kapt(libs.butterknife.compiler)

    implementation(libs.appcenter.analytics)
    implementation(libs.appcenter.crashes)
    implementation(libs.appcenter.distribute)
}
