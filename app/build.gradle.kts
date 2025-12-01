plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.cypher_events"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.cypher_events"
        minSdk = 24
        targetSdk = 36
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

    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/java")
        }
    }

}

dependencies {

    // Adds firebase to the app
    implementation(platform("com.google.firebase:firebase-bom:34.5.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
    implementation ("com.google.firebase:firebase-database")

    implementation("com.google.firebase:firebase-storage:20.3.0")

    // Add the specific SDKs you will use.
    //implementation("com.google.firebase:firebase-analytics")
    //implementation("com.google.firebase:firebase-auth")
    //implementation("com.google.firebase:firebase-firestore")
    //implementation("com.google.firebase:firebase-storage")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")


    implementation("androidx.navigation:navigation-fragment:2.8.3")
    implementation("androidx.navigation:navigation-ui:2.8.3")

    // ZXing for QR code scanning
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.5.2")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    testImplementation("junit:junit:4.13.2")
}

// Javadoc generation task - generates docs for domain models only (pure Java classes)
tasks.register<Javadoc>("generateJavadoc") {
    source = fileTree("src/main/java") {
        include("**/domain/model/Event.java")
        include("**/domain/model/Entrant.java")
        include("**/domain/model/Organizer.java")
        include("**/domain/model/Admin.java")
        include("**/domain/model/Notification.java")
        include("**/domain/model/NotificationLog.java")
        include("**/util/Result.java")
    }
    
    // Set destination directory
    setDestinationDir(file("${project.rootDir}/docs"))
    
    // Configure options
    options {
        this as StandardJavadocDocletOptions
        encoding = "UTF-8"
        charSet = "UTF-8"
        memberLevel = JavadocMemberLevel.PRIVATE
        links("https://docs.oracle.com/javase/8/docs/api/")
        
        // Suppress warnings
        addStringOption("Xdoclint:none", "-quiet")
    }
    
    // Handle failures gracefully
    isFailOnError = false
}
