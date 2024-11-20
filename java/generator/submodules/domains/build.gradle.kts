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
    implementation("org.eclipse.emf:org.eclipse.emf.codegen.ecore:2.39.0")
    implementation(project(":utilities"))
}

tasks.test {
    useJUnitPlatform()
}