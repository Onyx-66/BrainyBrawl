import java.util.Base64

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

fun publicConfig(name: String): String {
    if(providers.gradleProperty("isolatedQa").orNull=="true" && name.startsWith("SUPABASE_"))return "\"\""
    val localPublic = rootProject.file(".env").takeIf { it.isFile }?.readLines()?.firstOrNull { it.startsWith("$name=") }?.substringAfter('=')?.trim().orEmpty()
    val value = providers.environmentVariable(name).orElse(providers.gradleProperty(name)).getOrElse(localPublic)
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
        applicationId = if(providers.gradleProperty("isolatedQa").orNull=="true")"com.brainybrawl.app.qa" else "com.brainybrawl.app"
        minSdk = 26
        targetSdk = 37
        versionCode = 4
        versionName = "1.0.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "GOOGLE_AUTH_ENABLED", publicConfig("GOOGLE_AUTH_ENABLED"))
        buildConfigField("String", "DISCORD_AUTH_ENABLED", publicConfig("DISCORD_AUTH_ENABLED"))
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
    implementation(libs.androidx.exifinterface)
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
    implementation(libs.supabase.storage)
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
            from(artDirectory) { include("**/*.svg", "**/*.png", "**/*.jpg", "**/*.jpeg", "**/*.webp"); into("assets") }
            into(outputDirectory)
        }
        // Small locale packs keep opening an offline round fast on real phones.
        val factory=javax.xml.parsers.DocumentBuilderFactory.newInstance()
        val source=factory.newDocumentBuilder().parse(contentDirectory.file("question_round.xml").get().asFile)
        val items=source.documentElement.getElementsByTagName("item")
        val index=mutableListOf<String>()
        listOf("en","fr","ar").forEach { locale ->
            (0 until items.length).map { items.item(it) as org.w3c.dom.Element }
                .filter { it.getAttribute("locale")==locale }.chunked(50).forEachIndexed { number, batch ->
                    val document=factory.newDocumentBuilder().newDocument()
                    val root=document.createElement("content")
                    root.setAttribute("kind","question_round");root.setAttribute("schemaVersion","1");root.setAttribute("contentVersion",source.documentElement.getAttribute("contentVersion"))
                    document.appendChild(root);batch.forEach { root.appendChild(document.importNode(it,true)) }
                    val file="questions_${locale}_${number}.xml"
                    javax.xml.transform.TransformerFactory.newInstance().newTransformer().transform(javax.xml.transform.dom.DOMSource(document),javax.xml.transform.stream.StreamResult(outputDirectory.file("content/$file").get().asFile))
                    index.add("$locale\t$file\t${batch.size}")
                }
        }
        outputDirectory.file("content/questions-index.tsv").get().asFile.writeText(index.joinToString("\n"))
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
