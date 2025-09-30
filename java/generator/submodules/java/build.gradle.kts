plugins {
    java
    id("org.xtext.xtend") version "4.0.0"
}

dependencies {
    compileOnly("org.eclipse.xtext:org.eclipse.xtext:2.36.0")

    implementation("org.eclipse.emf:org.eclipse.emf.ecore:2.39.0")
    implementation("org.eclipse.emf:org.eclipse.emf.ecore.xmi:2.39.0")
    implementation("org.eclipse.emf:org.eclipse.emf.ecore.change:2.17.0")
    implementation("org.eclipse.xtend:org.eclipse.xtend.core:2.39.0")
    
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

/*tasks{
    register<JavaExec>("evaluateJavaFile"){
        val mainRuntimeClasspath = sourceSets.main.map { it.runtimeClasspath }
        dependsOn(mainRuntimeClasspath)
        classpath(mainRuntimeClasspath)
        mainClass.set("se.liu.ida.sas.pelab.text2vql.java.JavaTester")
        standardInput = System.`in`
        group = "text2vql"
        description = ""
    }
}*/