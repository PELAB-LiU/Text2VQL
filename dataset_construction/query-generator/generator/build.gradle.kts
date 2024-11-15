plugins {
    id("java")
    id("org.xtext.xtend") version "4.0.0"
    id("com.gradleup.shadow") version "8.3.5"
}

group = "se.liu.ida.sas.pelab.text2vql"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.eclipse.org/content/repositories/viatra2-releases/")
    }
    maven {
        url = uri("https://repo.eclipse.org/content/groups/releases/")
    }
}

dependencies {
    implementation(refinery.generator)
    implementation(fileTree("libs") { include("*.jar") })
    implementation("lpg.runtime.java:lpg.runtime.java:2.0.17.v201004271640")
    implementation("org.eclipse.viatra:org.eclipse.viatra.query.patternlanguage.emf:2.9.1")
    implementation("org.eclipse.emf:org.eclipse.emf.codegen.ecore:2.39.0")
    implementation("org.eclipse.epsilon:org.eclipse.epsilon.eol.engine:2.5.0")
    implementation("org.eclipse.epsilon:org.eclipse.epsilon.epl.engine:2.5.0")
    implementation("org.eclipse.epsilon:org.eclipse.epsilon.emc.emf:2.5.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}
tasks{
    register<JavaExec>("generateQueries"){
        val mainRuntimeClasspath = sourceSets.main.map { it.runtimeClasspath }
        dependsOn(mainRuntimeClasspath)
        classpath(mainRuntimeClasspath)
        mainClass.set("se.liu.ida.sas.pelab.text2vql.Main")
        standardInput = System.`in`
        group = "text2vql"
        description = "Generate queries"
    }
    register<JavaExec>("runGeneratorOnEcore"){
        val mainRuntimeClasspath = sourceSets.main.map { it.runtimeClasspath }
        dependsOn(mainRuntimeClasspath)
        classpath(mainRuntimeClasspath)
        mainClass.set("se.liu.ida.sas.pelab.text2vql.GeneratorMain")
        standardInput = System.`in`
        group = "text2vql"
        description = "Generate queries"
    }
}
tasks.test {
    useJUnitPlatform()
}