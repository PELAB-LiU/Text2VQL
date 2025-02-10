plugins {
    id("java")
}

group = "se.liu.ida.sas.pelab.text2vql"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(fileTree("libs") { include("*.jar") })
    implementation(project(":utilities"))
    implementation("org.eclipse.emf:org.eclipse.emf.codegen.ecore:2.39.0")
    implementation("lpg.runtime:java:2.0.17-v201004271640")
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