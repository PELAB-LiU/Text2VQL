plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
}

group = "se.liu.ida.sas.pelab.text2vql"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://repo.eclipse.org/content/repositories/viatra2-releases/")
        }
        maven {
            url = uri("https://repo.eclipse.org/content/groups/releases/")
        }
    }
}