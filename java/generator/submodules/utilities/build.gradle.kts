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
    compileOnly("org.eclipse.xtext:org.eclipse.xtext:2.36.0")
    implementation("org.eclipse.emf:org.eclipse.emf.codegen.ecore:2.39.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}