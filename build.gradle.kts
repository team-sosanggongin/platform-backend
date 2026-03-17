plugins {
    java
}

allprojects {
    group = "com.sosangongin"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    tasks.test {
        useJUnitPlatform()
    }
}

// build.gradle.kts

tasks.register<TerraformTask>("tfPlanStg") {
    group = "terraform"
    description = "Run terraform plan for staging"
    command = "plan"
    env = "stg"
}

tasks.register<TerraformTask>("tfApplyStg") {
    group = "terraform"
    description = "Run terraform apply for staging"
    command = "apply"
    env = "stg"
}

tasks.register<TerraformTask>("tfLocalPlan") {
    group = "terraform"
    description = "Run terraform plan on LocalStack"
    command = "plan"
    env = "stg"
    isLocal = true
}