plugins {
    id("java")
    id("org.xtext.xtend") version "4.0.0"
}

group = "se.liu.ida.sas.pelab.text2vql"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.eclipse.epsilon:org.eclipse.epsilon.eol.engine:2.5.0")
    implementation("org.eclipse.epsilon:org.eclipse.epsilon.epl.engine:2.5.0")
    implementation("org.eclipse.epsilon:org.eclipse.epsilon.emc.emf:2.5.0")
    implementation(project(":utilities"))
}

tasks.test {
    useJUnitPlatform()
}

tasks{
    register<JavaExec>("validateEOL"){
        val mainRuntimeClasspath = sourceSets.main.map { it.runtimeClasspath }
        dependsOn(mainRuntimeClasspath)
        classpath(mainRuntimeClasspath)
        //workingDir("../../results")
        //environment("KEY","VALUE")
        mainClass.set("se.liu.ida.sas.pelab.text2vql.epsilon.ValidateWithEOL")
        standardInput = System.`in`
        group = "text2vql"
        description = "Get matches for Epsilon Query"
    }
}