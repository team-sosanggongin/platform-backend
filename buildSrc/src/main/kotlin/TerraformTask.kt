import org.gradle.api.tasks.Input
import org.gradle.api.tasks.options.Option
import org.gradle.process.ExecOperations
import javax.inject.Inject
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Exec

abstract class TerraformTask : Exec() {

    @get:Input
    @set:Option(option = "command", description = "Terraform command (plan, apply, init, destroy)")
    var command: String = "plan"

    @get:Input
    @set:Option(option = "env", description = "Environment (local, stg, prod)")
    var env: String = "stg"

    override fun exec() {
        workingDir = project.file("${project.projectDir}/_infra/terraform")
        executable = "terraform"

        val tfArgs = mutableListOf(command)

        if (command != "init") {
            tfArgs.add("-var-file=env/$env.tfvars")

            if (command == "apply" || command == "destroy") {
                tfArgs.add("-auto-approve")
            }
        }

        args(tfArgs)

        super.exec()
    }
}