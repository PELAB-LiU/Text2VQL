plugins {
    id("java")
    id("org.xtext.xtend") version "4.0.0"
}

group = "se.liu.ida.sas.pelab.text2vql"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.eclipse.org/content/groups/releases/")
    }
}

dependencies {
    compileOnly("org.eclipse.xtext:org.eclipse.xtext:2.36.0")
    implementation(refinery.generator)
    implementation(project(":utilities"))
    implementation("org.eclipse.viatra:org.eclipse.viatra.query.patternlanguage.emf:2.9.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}
tasks{
    register<JavaExec>("generateQueries"){
        val mainRuntimeClasspath = sourceSets.main.map { it.runtimeClasspath }
        dependsOn(mainRuntimeClasspath)
        classpath(mainRuntimeClasspath)
        mainClass.set("se.liu.ida.sas.pelab.text2vql.refinery.Main")
        standardInput = System.`in`
        group = "text2vql"
        description = "Generate 300 seeded and 300 seedless instance models from Railway domain."
    }
    register<JavaExec>("generateAndPrintQueries"){
        val mainRuntimeClasspath = sourceSets.main.map { it.runtimeClasspath }
        dependsOn(mainRuntimeClasspath)
        classpath(mainRuntimeClasspath)
        mainClass.set("se.liu.ida.sas.pelab.text2vql.refinery.GeneratorMain")
        standardInput = System.`in`
        group = "text2vql"
        description = "Generate a VQL query over the Railway domain."
    }
}
tasks.test {
    useJUnitPlatform()
}