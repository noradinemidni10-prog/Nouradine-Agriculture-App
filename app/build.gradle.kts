plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.nouradine.agriculture"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nouradine.agriculture"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        val assistantApiBaseUrl = providers.gradleProperty("nouradineApiBaseUrl").orElse("").get()
        buildConfigField("String", "ASSISTANT_API_BASE_URL", "\"$assistantApiBaseUrl\"")
    }

    buildFeatures { compose = true; buildConfig = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    testImplementation("junit:junit:4.13.2")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
