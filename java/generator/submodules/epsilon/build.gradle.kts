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