/*
 * SPDX-FileCopyrightText: 2023 The Refinery Authors <https://refinery.tools/>
 *
 * SPDX-License-Identifier: EPL-2.0
 */

plugins {
	id("tools.refinery.gradle.java-application")
}

dependencies {
	implementation(project(":refinery-generator"))
	implementation(libs.jcommander)
	implementation(libs.slf4j.api)
}

application {
	mainClass.set("tools.refinery.generator.cli.Text2VQLTestGenerator")
}

tasks.shadowJar {
	// Silence Xtext warning.
	append("plugin.properties")


}
tasks {
	register<JavaExec>("generate Railway") {
		val mainRuntimeClasspath = sourceSets.main.map { it.runtimeClasspath }
		dependsOn(mainRuntimeClasspath)
		classpath(mainRuntimeClasspath)
		jvmArgs("-Xmx22G")
		mainClass.set("tools.refinery.generator.cli.Text2VQLTestGenerator")
		standardInput = System.`in`
		group = "run"
		description = "Start a Jetty web server serving the Xtext API without assets."
	}
}
