plugins {
    id("java")
    id("dev.equo.p2deps") version "1.7.8"
}

group = "se.liu.ida.sas.pelab.text2vql"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {         
        url = uri("https://repo.eclipse.org/content/groups/releases/")
    }
}
p2deps {
  into("implementation", {
    p2repo("https://download.eclipse.org/acceleo/updates/releases/4.1/R202502130921/")
    install("org.eclipse.acceleo.query")
    install("org.eclipse.acceleo.aql")
  })
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    
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
    /*register<JavaExec>("validateOCL"){
        val mainRuntimeClasspath = sourceSets.main.map { it.runtimeClasspath }
        dependsOn(mainRuntimeClasspath)
        classpath(mainRuntimeClasspath)
        //workingDir("../../results")
        //environment("KEY","VALUE")
        mainClass.set("se.liu.ida.sas.pelab.text2vql.ocl.ValidateWithOCL")
        standardInput = System.`in`
        group = "text2vql"
        description = "Get matches for OCL Query"
    }*/
}
