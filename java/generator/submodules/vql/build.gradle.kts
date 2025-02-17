plugins {
    id("java")
    id("org.xtext.xtend") version "4.0.0"
}

//ext.xtextVersion = "2.12.0"

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
    implementation("org.eclipse.xtext:org.eclipse.xtext:2.36.0")
    implementation(project(":utilities"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.eclipse.emf:org.eclipse.emf.codegen.ecore:2.39.0")
    implementation("org.eclipse.collections:eclipse-collections:11.1.0")
    implementation("org.eclipse.viatra:org.eclipse.viatra.query.patternlanguage.emf:2.9.0")
    implementation("org.eclipse.viatra:org.eclipse.viatra.query.runtime:2.9.0")
    implementation("org.eclipse.viatra:org.eclipse.viatra.query.runtime.rete:2.9.0")
    implementation("org.eclipse.viatra:org.eclipse.viatra.query.runtime.localsearch:2.9.0")
    //implementation("org.eclipse.viatra:org.eclipse.viatra.query.runtime.base:2.8.2")
    //implementation("org.eclipse.viatra:org.eclipse.viatra.query.runtime.matchers:2.8.2")
    implementation("org.apache.commons:commons-csv:1.10.0")
    implementation("org.eclipse.xtext:org.eclipse.xtext.xbase:2.36.0")
    runtimeOnly("org.slf4j:log4j-over-slf4j:2.0.16")
    //implementation("log4j:log4j:1.2.17")
    //implementation("org.slf4j:slf4j-simple:1.7.36")
}

tasks.test {
    useJUnitPlatform()
}
tasks{
    register<JavaExec>("profileQueries"){
        val mainRuntimeClasspath = sourceSets.main.map { it.runtimeClasspath }
        dependsOn(mainRuntimeClasspath)
        classpath(mainRuntimeClasspath)
        mainClass.set("se.liu.ida.sas.pelab.text2vql.testing.profile.ProfileMain")
        standardInput = System.`in`
        group = "text2vql"
        description = "Run profiler. Expect environment variables: MODE,CSV,COL,OUT"
    }

    register<JavaExec>("compareQueryMatchSets"){
        val mainRuntimeClasspath = sourceSets.main.map { it.runtimeClasspath }
        dependsOn(mainRuntimeClasspath)
        classpath(mainRuntimeClasspath)
        workingDir("../../results")
        environment("META","../dataset_construction/test_metamodel/railway.ecore")
        environment("INPUT","ai/codellama-CodeLlama-7b-hf_lora.csv")
        environment("OUTPUT","eval/chatgpt_fs_random_eval_tmp.csv")
        environment("INSTANCEDIR","testmodels")
        environment("AI","<idx>_output")
        mainClass.set("se.liu.ida.sas.pelab.text2vql.testing.matchset.CSVBasedEvaluation")
        standardInput = System.`in`
        group = "text2vql"
        description = "Compare match sets of queries from CSV. Expect environment variables: META,INPUT,OUTPUT,INSTANCEDIR,AI"
    }

    register<JavaExec>("syntaxCheckVQL"){
        val mainRuntimeClasspath = sourceSets.main.map { it.runtimeClasspath }
        dependsOn(mainRuntimeClasspath)
        classpath(mainRuntimeClasspath)
        mainClass.set("se.liu.ida.sas.pelab.text2vql.vql.SyntaxCheckVQL")
        standardInput = System.`in`
        group = "text2vql"
        description = "Run syntax check on a database containing VQL queries"
    }
    register<JavaExec>("validateVQL"){
        val mainRuntimeClasspath = sourceSets.main.map { it.runtimeClasspath }
        dependsOn(mainRuntimeClasspath)
        classpath(mainRuntimeClasspath)
        mainClass.set("se.liu.ida.sas.pelab.text2vql.vql.ValidateWithVQL")
        standardInput = System.`in`
        group = "text2vql"
        description = "Get matches for VIATRA Query Language"
    }
}

