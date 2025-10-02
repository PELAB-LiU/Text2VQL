plugins {
    id("java-library")
    id("de.undercouch.download") version "5.6.0"
}

import de.undercouch.gradle.tasks.download.Download

group = "se.liu.ida.sas.pelab.text2vql"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {         
        url = uri("https://repo.eclipse.org/content/groups/releases/")
    }
    maven {         
        url = uri("https://dist.wso2.org/maven2/")
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    //implementation(fileTree("libs/ocl") { include("*.jar") })
    api(fileTree(mapOf("dir" to "libs/ocl", "include" to listOf("*.jar"))))
    //api(fileTree("libs/ocl") { include("*.jar") })
    
    implementation(project(":utilities"))

    implementation("org.eclipse.emf:org.eclipse.emf.ecore:2.39.0")
    implementation("org.eclipse.emf:org.eclipse.emf.codegen.ecore:2.39.0")
    implementation("org.eclipse.xtext:org.eclipse.xtext:2.39.0")
    implementation("org.eclipse.emf:org.eclipse.emf.edit:2.23.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks{
    register<JavaExec>("validateOCL"){
        val mainRuntimeClasspath = sourceSets.main.map { it.runtimeClasspath }
        dependsOn(mainRuntimeClasspath)
        classpath(mainRuntimeClasspath)
        //workingDir("../../results")
        //environment("KEY","VALUE")
        mainClass.set("se.liu.ida.sas.pelab.text2vql.ocl.ValidateWithOCL")
        standardInput = System.`in`
        group = "text2vql"
        description = "Get matches for OCL Query"
    }
}


val downloadOCL by tasks.registering(Download::class) {
        src(listOf(
            "https://download.eclipse.org/modeling/mdt/ocl/builds/release/6.22.0/plugins/org.eclipse.ocl.ecore_3.22.0.v20240902-1518.jar",
            "https://download.eclipse.org/modeling/mdt/ocl/builds/release/6.22.0/plugins/org.eclipse.ocl.common_1.22.0.v20240902-1518.jar",
            "https://download.eclipse.org/modeling/mdt/ocl/builds/release/6.22.0/plugins/org.eclipse.ocl_3.22.0.v20240902-1518.jar",
            "https://download.eclipse.org/modeling/mdt/ocl/builds/release/6.22.0/plugins/lpg.runtime.java_2.0.17.v201004271640.jar",
            "https://download.eclipse.org/modeling/mdt/ocl/builds/release/6.22.0/plugins/org.eclipse.ocl.pivot_1.22.0.v20240902-1518.jar",
            "https://download.eclipse.org/modeling/mdt/ocl/builds/release/6.22.0/plugins/org.eclipse.ocl.examples_3.22.0.v20240902-1518.jar",

            "https://download.eclipse.org/modeling/mdt/ocl/builds/release/6.22.0/plugins/org.eclipse.ocl.xtext.completeocl_1.22.0.v20240902-1518.jar",
            "https://download.eclipse.org/modeling/mdt/ocl/builds/release/6.22.0/plugins/org.eclipse.ocl.xtext.essentialocl_1.22.0.v20240902-1518.jar",
            "https://download.eclipse.org/modeling/mdt/ocl/builds/release/6.22.0/plugins/org.eclipse.ocl.xtext.oclinecore_1.22.0.v20240902-1518.jar",
            "https://download.eclipse.org/modeling/mdt/ocl/builds/release/6.22.0/plugins/org.eclipse.ocl.xtext.base_1.22.0.v20240902-1518.jar",
            "https://download.eclipse.org/modeling/mdt/ocl/builds/release/6.22.0/plugins/org.eclipse.ocl.examples.standalone_2.22.0.v20240902-1518.jar",
            "https://download.eclipse.org/modeling/mdt/ocl/builds/release/6.22.0/plugins/org.eclipse.ocl.examples.emf.validation.validity_2.22.0.v20240902-1518.jar"

        ))
        dest("libs/ocl/")
        onlyIfModified(true)
    }
tasks.named("compileJava") { 
    dependsOn(downloadOCL) 
}