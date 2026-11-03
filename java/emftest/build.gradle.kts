plugins {
    java
}

group = "se.liu.ida.sas.pelab.text2vql"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.eclipse.org/content/groups/releases/")
    }
}

// Generated metamodel jars produced by java/ecore2jar
val jars = rootDir.resolve("../../dataset_construction/metamodels/2-jars")

dependencies {
    implementation("org.eclipse.emf:org.eclipse.emf.ecore:2.39.0")
    implementation("org.eclipse.emf:org.eclipse.emf.ecore.xmi:2.39.0")

    // metamodels/0-raw/repo-ecore-all/data/SOM-Research/jsonDiscoverer/jsondiscoverer.zoo/zoo/googlePlaces/source4/source4.ecore
    testImplementation(files(jars.resolve("7524_source4.jar")))

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}
