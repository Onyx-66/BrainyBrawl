import java.util.Base64

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

fun publicConfig(name: String): String {
    val value = providers.environmentVariable(name).orElse(providers.gradleProperty(name)).getOrElse("")
    require(!value.startsWith("sb_secret_")) { "Only publishable client credentials are allowed" }
    if (name == "SUPABASE_PUBLISHABLE_KEY" && value.isNotBlank() && !value.startsWith("sb_publishable_")) {
        val parts = value.split('.')
        require(parts.size == 3) { "Invalid public client credential" }
        val claims = groovy.json.JsonSlurper().parseText(String(Base64.getUrlDecoder().decode(parts[1]))) as? Map<*, *>
        require(claims?.get("role") == "anon") { "Privileged credentials cannot be packaged" }
    }
    return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "") + "\""
}

android {
    namespace = "com.brainybrawl.app"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.brainybrawl.app"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "PRIVACY_POLICY_URL", publicConfig("PRIVACY_POLICY_URL"))
        buildConfigField("String", "TERMS_URL", publicConfig("TERMS_URL"))
        buildConfigField("String", "SUPABASE_URL", publicConfig("SUPABASE_URL"))
        buildConfigField("String", "SUPABASE_PUBLISHABLE_KEY", publicConfig("SUPABASE_PUBLISHABLE_KEY"))
    }

    val releaseStore=providers.environmentVariable("BRAWL_KEYSTORE_PATH").orNull
    if(releaseStore!=null) signingConfigs.create("production") {
        storeFile=file(releaseStore)
        storePassword=providers.environmentVariable("BRAWL_KEYSTORE_PASSWORD").get()
        keyAlias=providers.environmentVariable("BRAWL_KEY_ALIAS").get()
        keyPassword=providers.environmentVariable("BRAWL_KEY_PASSWORD").get()
    }
    buildTypes {
        release {
            if(releaseStore!=null)signingConfig=signingConfigs.getByName("production")
            optimization {
                enable = true
            }
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    sourceSets.getByName("androidTest").assets.directories.add("src/test/resources")
    bundle { language { enableSplit = false } }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidsvg)
    implementation(libs.navigation.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.coroutines.android)
    implementation(libs.serialization.json)
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.auth)
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.realtime)
    implementation(libs.supabase.functions)
    implementation(libs.ktor.okhttp)
    testImplementation(libs.coroutines.test)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

abstract class PrepareContentAssets : DefaultTask() {
    @get:InputDirectory abstract val contentDirectory: DirectoryProperty
    @get:InputDirectory abstract val artDirectory: DirectoryProperty
    @get:OutputDirectory abstract val outputDirectory: DirectoryProperty
    @get:javax.inject.Inject abstract val fs: FileSystemOperations
    @TaskAction fun prepare() {
        fs.sync {
            from(contentDirectory) { include("*.xml"); into("content") }
            from(artDirectory) { include("**/*.svg"); into("assets") }
            into(outputDirectory)
        }
    }
}
val syncContentAssets = tasks.register<PrepareContentAssets>("syncContentAssets") {
    contentDirectory.set(rootProject.layout.projectDirectory.dir("content"))
    artDirectory.set(rootProject.layout.projectDirectory.dir("assets"))
    outputDirectory.set(layout.buildDirectory.dir("generated/contentAssets"))
}
androidComponents.onVariants { variant ->
    variant.sources.assets?.addGeneratedSourceDirectory(syncContentAssets, PrepareContentAssets::outputDirectory)
}
android.testOptions.unitTests.all {
    it.systemProperty("brainybrawl.contentDir", rootProject.file("content").absolutePath)
}
