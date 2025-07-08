plugins {
    id("java")
    id("org.xtext.xtend") version "4.0.0"
} 

group = "se.liu.ida.sas.pelab.text2vql"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {         
        url = uri("https://github.com/yamtl/yamtl.github.io/raw/master/mvn-repo/snapshot-repo")
    }
}

dependencies {
    compileOnly("org.eclipse.xtext:org.eclipse.xtext:2.36.0")

    implementation(project(":utilities"))

    implementation("yamtl:yamtl:1.1.8")
    implementation("yamtl:yamtl.groovy:1.1.8")
    implementation("org.apache.groovy:groovy-all:4.0.27")
    implementation("org.eclipse.emf:org.eclipse.emf.ecore:2.39.0")
    implementation("org.eclipse.emf:org.eclipse.emf.ecore.xmi:2.39.0")//
    implementation("org.eclipse.emf:org.eclipse.emf.ecore.change:2.17.0")
    implementation("org.eclipse.xtend:org.eclipse.xtend.core:2.39.0")
    implementation("org.springframework.boot:spring-boot-starter-aop:3.5.3")
    runtimeOnly("org.aspectj:aspectjweaver:1.9.24")

    implementation("org.eclipse.emf:org.eclipse.emf.codegen.ecore:2.39.0")
    implementation("org.apache.commons:commons-csv:1.13.0")
    implementation("org.xerial:sqlite-jdbc:3.49.0.0")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}