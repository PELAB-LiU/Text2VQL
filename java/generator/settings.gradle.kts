rootProject.name = "generator"
plugins {
    id("tools.refinery.settings") version "0.1.3"
}
include(
        "refinery",
        "epsilon",
        "ocl",
        "vql",
        "utilities"
)
for (project in rootProject.children) {
    val projectName = project.name
    project.name = projectName
    project.projectDir = file("submodules/$projectName")
}

