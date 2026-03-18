import com.github.gradle.node.npm.task.NpxTask

plugins {
    id("com.github.node-gradle.node") version "7.0.1"
}

node {
    version.set("20.11.0")
    npmVersion.set("10.2.4")
    download.set(true)
}

tasks.register<NpxTask>("dev") {
    command.set("next")
    args.set(listOf("dev"))
}

tasks.register<NpxTask>("build-npm") {
    command.set("next")
    args.set(listOf("build"))
}

tasks.register<NpxTask>("start") {
    command.set("next")
    args.set(listOf("start"))
}

tasks.register<NpxTask>("lint") {
    command.set("next")
    args.set(listOf("lint"))
}
