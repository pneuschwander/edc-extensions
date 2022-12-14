plugins {
    id("java")
    id("checkstyle")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

val downloadArtifact: Configuration by configurations.creating {
    isTransitive = false
}


val identityHubVersion: String by project;
val registrationServiceVersion: String by project;
//dependencies {
//    downloadArtifact("org.eclipse.dataspaceconnector.identityhub:identity-hub-cli:${identityHubVersion}:all")
//    downloadArtifact("org.eclipse.dataspaceconnector.registrationservice:registration-service-cli:${registrationServiceVersion}:all")
//}

// task that downloads the RegSrv CLI and IH CLI
val getJars by tasks.registering(Copy::class) {
    outputs.upToDateWhen { false } //always download

    from(downloadArtifact)
        // strip away the version string
        .rename { s -> s.replace("-${identityHubVersion}", "")
            .replace("-${registrationServiceVersion}", "")
            .replace("-all", "")
        }
    into(layout.projectDirectory.dir("resources/cli-tools"))
}

// run the download jars task after the "jar" task
tasks{
    jar {
        finalizedBy(getJars)
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "checkstyle")

    checkstyle {
        toolVersion = "9.0"
        configFile = rootProject.file("resources/checkstyle-config.xml")
        configDirectory.set(rootProject.file("resources"))
        maxErrors = 0 // does not tolerate errors
    }

    repositories {
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://maven.iais.fraunhofer.de/artifactory/eis-ids-public/")
        }
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
//        maven {
//            url = uri("/libs")
//        }
    }
}