// buildSrc/src/main/kotlin/terraform-conventions.gradle.kts

// 우리가 만든 TerraformTask 클래스를 사용합니다.
tasks.register<TerraformTask>("tfPlanStg") {
    group = "terraform"
    description = "Run terraform plan for staging"
    command = "plan"
    env = "stg"
}

tasks.register<TerraformTask>("tfInit") {
    group = "terraform"
    description = "Run terraform init"
    command = "init"
    env = "local"
}

tasks.register<TerraformTask>("tfApplyStg") {
    group = "terraform"
    description = "Run terraform apply for staging"
    command = "apply"
    env = "stg"
}

tasks.register<TerraformTask>("tfDestroyStg") {
    group = "terraform"
    description = "Run terraform destroy for staging"
    command = "destroy"
    env = "stg"
}
