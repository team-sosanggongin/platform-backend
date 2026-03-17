import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

abstract class TerraformTask : DefaultTask() {

    @get:Input
    @set:Option(option = "command", description = "Terraform command to run (plan, apply, init)")
    var command: String = "plan"

    @get:Input
    @set:Option(option = "env", description = "Environment to use (stg, prod)")
    var env: String = "stg"

    @get:Input
    var isLocal: Boolean = false

    @TaskAction
    fun execute() {
        project.exec {
            workingDir("${project.projectDir}/infrastructure")
            executable("terraform")

            val tfArgs = mutableListOf(command)

            if (command != "init") {
                tfArgs.add("-var-file=env/$env.tfvars")
                if (command == "apply" || command == "destroy") {
                    tfArgs.add("-auto-approve")
                }
            }

            if (isLocal) {
                environment("AWS_ACCESS_KEY_ID", "test")
                environment("AWS_SECRET_ACCESS_KEY", "test")
                environment("AWS_DEFAULT_REGION", "ap-northeast-2")
            }

            args(tfArgs)
        }
    }
}